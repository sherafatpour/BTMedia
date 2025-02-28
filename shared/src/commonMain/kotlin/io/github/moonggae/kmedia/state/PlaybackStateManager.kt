package io.github.moonggae.kmedia.state

import io.github.moonggae.kmedia.model.PlaybackState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal object PlaybackStateManager {
    private val _playbackState = MutableStateFlow(PlaybackState())
    val flow: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    var playbackState: PlaybackState
        get() = _playbackState.value
        set(value) {
            _playbackState.update { value }
        }
}