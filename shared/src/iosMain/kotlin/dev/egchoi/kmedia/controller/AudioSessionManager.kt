package dev.egchoi.kmedia.controller

import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive

// Audio Session Manager
class AudioSessionManager {
    @OptIn(ExperimentalForeignApi::class)
    fun setupAudioSession() {
        try {
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(AVAudioSessionCategoryPlayback, null)
            audioSession.setActive(true, null)
        } catch (e: Exception) {
            Napier.e("Error setting up audio session: ${e.message}")
        }
    }
}