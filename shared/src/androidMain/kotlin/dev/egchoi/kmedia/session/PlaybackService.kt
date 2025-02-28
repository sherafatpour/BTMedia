package dev.egchoi.kmedia.session

import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import dev.egchoi.kmedia.cache.CacheManager
import dev.egchoi.kmedia.custom.CustomLayoutUpdateListener
import dev.egchoi.kmedia.listener.PlaybackAnalyticsEventListener
import dev.egchoi.kmedia.listener.PlaybackIOHandler
import dev.egchoi.kmedia.listener.PlaybackStateHandler
import dev.egchoi.kmedia.util.mediaItems
import org.koin.android.ext.android.inject

@OptIn(UnstableApi::class)
class PlaybackService : MediaLibraryService() {
    private var player: ExoPlayer? = null
        get() {
            if (field == null || field?.isReleased == true) {
                field = createPlayer()
            }
            return field
        }

    lateinit var session: MediaLibrarySession

    private val cacheManager: CacheManager by inject()
    private val playbackStateHandler: PlaybackStateHandler by inject()
    private val playbackIOHandler: PlaybackIOHandler by inject()
    private val playbackAnalyticsEventListener: PlaybackAnalyticsEventListener by inject()
    private val customLayoutUpdateListener: CustomLayoutUpdateListener by inject()
    private val sessionCallback: LibrarySessionCallback by inject()
    private val sessionActivity: PendingIntent by inject()

    private fun createPlayer(): ExoPlayer {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        val renderersFactory = DefaultRenderersFactory(this)
            .forceEnableMediaCodecAsynchronousQueueing()

        val builder = ExoPlayer.Builder(this, renderersFactory)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)

        if (cacheManager.enableCache) {
            cacheManager.getProgressiveMediaSourceFactory(applicationContext)?.let { mediaSourceFactory ->
                builder.setMediaSourceFactory(mediaSourceFactory)
            }
        }

        return builder.build().apply {
            playbackStateHandler.attachTo(this)
            playbackIOHandler.attachTo(this)
            playbackAnalyticsEventListener.attach(this)
        }
    }

    private fun recreatePlayer() {
        val currentPosition = player?.currentPosition ?: 0
        val currentMediaItems = player?.mediaItems?.toList() ?: emptyList()
        val currentMediaItemIndex = player?.currentMediaItemIndex ?: 0
        val wasPlaying = player?.isPlaying ?: false
        val repeatMode = player?.repeatMode ?: Player.REPEAT_MODE_OFF
        val shuffleModeEnabled = player?.shuffleModeEnabled ?: false

        // 기존 player만 해제하고 새로운 player 생성
        player?.release()
        player = null

        // 새로운 player 생성하고 기존 세션의 player를 교체
        val newPlayer = createPlayer()
        session.player = newPlayer
        player = newPlayer

        customLayoutUpdateListener.attachTo(session, newPlayer)

        player?.apply {
            if (currentMediaItems.isNotEmpty()) {
                setMediaItems(currentMediaItems, currentMediaItemIndex, currentPosition)
                this.repeatMode = repeatMode
                this.shuffleModeEnabled = shuffleModeEnabled
                prepare()
                if (wasPlaying) {
                    play()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        session = MediaLibrarySession
            .Builder(this, player!!, sessionCallback)
            .setSessionActivity(sessionActivity)
            .build()

        player?.let {
            customLayoutUpdateListener.attachTo(session, it)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_RECREATE_PLAYER -> {
                recreatePlayer()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        session.player.release()
        session.release()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        session.player.pause()
        session.player.stop()
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession = session

    companion object {
        const val ACTION_RECREATE_PLAYER = "dev.egchoi.kmedia.action.RECREATE_PLAYER"
    }
}
