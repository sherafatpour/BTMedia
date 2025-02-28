package dev.egchoi.kmedia.util

import androidx.media3.common.Player
import dev.egchoi.kmedia.model.PlayingStatus

val Player.playingStatus: PlayingStatus
    get() = when {
        playbackState == Player.STATE_READY && playWhenReady -> PlayingStatus.PLAYING
        playbackState == Player.STATE_READY && !playWhenReady -> PlayingStatus.PAUSED
        playbackState == Player.STATE_BUFFERING -> PlayingStatus.BUFFERING
        playbackState == Player.STATE_IDLE -> PlayingStatus.IDLE
        playbackState == Player.STATE_ENDED -> PlayingStatus.ENDED
        else -> PlayingStatus.IDLE
    }