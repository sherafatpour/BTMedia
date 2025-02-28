package dev.egchoi.kmedia.repository

import dev.egchoi.kmedia.model.PlaybackState
import dev.egchoi.kmedia.state.PlaybackStateManager
import kotlinx.coroutines.flow.Flow

internal class DefaultPlaybackStateRepository(
    playbackStateManager: PlaybackStateManager
) {
    val playbackState: Flow<PlaybackState> = playbackStateManager.flow
}