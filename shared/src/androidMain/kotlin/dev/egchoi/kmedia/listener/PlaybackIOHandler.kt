package dev.egchoi.kmedia.listener

import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import dev.egchoi.kmedia.cache.CacheManager
import com.ccc.ncs.domain.repository.MusicRepository
import dev.egchoi.kmedia.model.MusicStatus
import dev.egchoi.kmedia.util.asMediaItem
import dev.egchoi.kmedia.util.isLocalFileExists
import dev.egchoi.kmedia.util.isNetworkSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid

class PlaybackIOHandler(
    private val scope: CoroutineScope,
    private val cacheManager: CacheManager,
    private val musicRepository: MusicRepository
): Player.Listener {
    private lateinit var player: Player

    fun attachTo(player: Player) {
        this.player = player
        player.addListener(this)
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        mediaItem?.let {
            scope.launch {
                handleMediaItemTransition(it)
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        if (error.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS) {
            scope.launch {
                handleNetworkMusicBadStatus()
            }
        }
    }

    private suspend fun handleMediaItemTransition(mediaItem: MediaItem) {
        if (cacheManager.enableCache) {

            if (mediaItem.isNetworkSource) {
                setupCacheListener(mediaItem)
                return
            }
        }

        if (!mediaItem.isNetworkSource && !mediaItem.isLocalFileExists) {
            handleMissingLocalFile(mediaItem)
        }
    }

    @OptIn(UnstableApi::class)
    suspend fun setupCacheListener(mediaItem: MediaItem) {
        cacheManager.observeCacheUpdate(mediaItem.mediaId).collect { isFullyCached ->
            musicRepository.updateMusicStatus(
                musicId = Uuid.parse(mediaItem.mediaId),
                status = if (isFullyCached) MusicStatus.FullyCached else MusicStatus.PartiallyCached
            )
        }
    }

    private suspend fun handleMissingLocalFile(mediaItem: MediaItem) {
        val currentMusicId = Uuid.parse(mediaItem.mediaId)
        musicRepository.updateMusicStatus(currentMusicId, MusicStatus.None)
        musicRepository.getMusic(currentMusicId).first()?.let { musicItem ->
            withContext(Dispatchers.Main) {
                player.replaceMediaItem(player.currentMediaItemIndex, musicItem.asMediaItem())
            }
        }
    }

    private suspend fun handleNetworkMusicBadStatus() {
        player.currentMediaItem?.let { mediaItem ->
            val wasPlaying = player.playWhenReady
            Log.d("TAG", "handleNetworkMusicBadStatus - player.playWhenReady: ${player.playWhenReady}")
            Log.d("TAG", "handleNetworkMusicBadStatus - player.playbackState: ${player.playbackState}")

            // 재생중:     true  STATE_IDLE
            // not 재생중: false STATE_IDLE

            musicRepository.deleteMusic(Uuid.parse(mediaItem.mediaId))
            withContext(Dispatchers.Main) {
                player.removeMediaItem(player.currentMediaItemIndex)
                if (wasPlaying) {
                    player.prepare()
                    player.play()
                }
            }
        }
    }
}