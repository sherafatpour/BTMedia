package dev.egchoi.kmedia.session

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemStatus
import platform.AVFoundation.AVPlayerStatusFailed
import platform.AVFoundation.AVPlayerStatusReadyToPlay
import platform.AVFoundation.AVPlayerStatusUnknown
import platform.Foundation.NSKeyValueObservingOptionInitial
import platform.Foundation.NSKeyValueObservingOptionNew

// 음악 아이템 상태 감시
class MusicStatusObserverManager(
    private val coroutineScope: CoroutineScope
) {
    private var currentItemStatusJob: kotlinx.coroutines.Job? = null

    fun observeItemStatus(item: AVPlayerItem, onReadyToPlay: () -> Unit, onPlaybackStateChanged: () -> Unit) {
        Napier.d("MusicStatusObserver setup")
        currentItemStatusJob?.cancel()

        currentItemStatusJob = coroutineScope.launch {
            item.observeKeyValueAsFlow<AVPlayerItemStatus>(
                "status",
                NSKeyValueObservingOptionInitial or NSKeyValueObservingOptionNew
            ).collect { status ->
                Napier.d("itemStatusObserver: $status")
                when (status) {
                    AVPlayerStatusUnknown -> Napier.d("unknown status")
                    AVPlayerStatusReadyToPlay -> {
                        Napier.d("ready to play")
                        onReadyToPlay()
                    }
                    AVPlayerStatusFailed -> Napier.d("failed, error: ${item.error}")
                }
                onPlaybackStateChanged()
            }
        }
    }

    fun cleanup() {
        Napier.d("MusicStatusObserver cleanup")

        currentItemStatusJob?.cancel()
        currentItemStatusJob = null
    }
}