package io.github.moonggae.kmedia.controller.controlcenter

import io.github.moonggae.kmedia.model.Music
import io.github.moonggae.kmedia.model.PlaybackState

internal class ControlCenterManager(
    private val commandCenter: MediaCommandCenter,
    private val infoCenter: MediaInfoCenter
) {
    fun start() {
        commandCenter.setupCommands()
        infoCenter.setupInitInfo()
    }

    fun updatePlaybackState(music: Music, playbackState: PlaybackState) = infoCenter.updateInfo(music ,playbackState)

    fun release() {
        commandCenter.cleanup()
        infoCenter.cleanup()
    }
}