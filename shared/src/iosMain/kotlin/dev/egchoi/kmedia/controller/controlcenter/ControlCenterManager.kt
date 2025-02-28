package dev.egchoi.kmedia.controller.controlcenter

import dev.egchoi.kmedia.model.Music
import dev.egchoi.kmedia.model.PlaybackState

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