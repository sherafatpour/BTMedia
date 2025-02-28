package dev.egchoi.kmedia.session

import io.github.aakira.napier.Napier
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.currentItem
import platform.AVFoundation.removeTimeObserver
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue.Companion.mainQueue

// 음악 종료 감시
class MusicCompleteTimeObserverManager(private val player: AVPlayer) {
    private var observer: Any? = null

    fun setup(onMusicCompleted: () -> Unit) {
        NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = player.currentItem,
            queue = mainQueue
        ) {
            Napier.d("onMusicCompleted")
            onMusicCompleted()
        }
    }

    fun cleanup() {
        Napier.d("MusicCompleteTimeObserver cleanup")

        observer?.let {
            player.removeTimeObserver(it)
            observer = null
        }
    }
}