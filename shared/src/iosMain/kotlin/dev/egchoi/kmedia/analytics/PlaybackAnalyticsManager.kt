package dev.egchoi.kmedia.analytics

import com.ccc.ncs.analytics.AnalyticsHelper
import dev.egchoi.kmedia.model.Music
import dev.egchoi.kmedia.util.toMilliSeconds
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.addPeriodicTimeObserverForInterval
import platform.AVFoundation.asset
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.rate
import platform.AVFoundation.removeTimeObserver
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.darwin.NSEC_PER_SEC

internal class PlaybackAnalyticsManager(
    private val player: AVPlayer,
    private val analyticsHelper: AnalyticsHelper,
    private val coroutineScope: CoroutineScope
) {
    private var currentMusic: Music? = null
    private var periodicTimeObserver: Any? = null
    private var lastPosition: Long = 0
    private var accumulatedPlayTime: Long = 0
    private var currentDuration: Long = 0

    @OptIn(ExperimentalForeignApi::class)
    fun startTracking(music: Music) {
        if (currentMusic?.id != music.id) {
            logCurrentPlayTime()
            resetTracking()
        }

        currentMusic = music
        lastPosition = player.currentTime().toMilliSeconds()
        setupPeriodicTimeObserver()

        player.currentItem?.asset?.loadValuesAsynchronouslyForKeys(listOf("duration")) {
            currentDuration = player.currentItem?.duration?.toMilliSeconds() ?: 0L
            Napier.d("Duration updated: $currentDuration")
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun setupPeriodicTimeObserver() {
        val interval = CMTimeMakeWithSeconds(0.01, NSEC_PER_SEC.toInt()) // 10ms 간격
        periodicTimeObserver = player.addPeriodicTimeObserverForInterval(interval, null) { time ->
            val currentPosition = time.toMilliSeconds()

            if (kotlin.math.abs(currentPosition - lastPosition) > 500) {
                Napier.d("Seek detected: $lastPosition -> $currentPosition")
            }
            else if (player.rate > 0) {
                accumulatedPlayTime += (currentPosition - lastPosition)
            }

            lastPosition = currentPosition
        }
    }

    fun stopTracking() {
        logCurrentPlayTime()
        resetTracking()
    }

    private fun logCurrentPlayTime() {
        val music = currentMusic ?: return

        if (accumulatedPlayTime > 0) {
            Napier.d("Logging playback time: ${accumulatedPlayTime}ms for ${music.id}, duration: $currentDuration")
            analyticsHelper.logPlaybackTime(music.id.toString(), accumulatedPlayTime, currentDuration)
        }
    }

    private fun resetTracking() {
        periodicTimeObserver?.let {
            player.removeTimeObserver(it)
            periodicTimeObserver = null
        }
        currentMusic = null
        lastPosition = 0
        accumulatedPlayTime = 0
        currentDuration = 0
    }
}