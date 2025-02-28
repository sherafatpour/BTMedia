package io.github.moonggae.kmedia.custom

import androidx.media3.common.Player
import androidx.media3.session.MediaLibraryService.MediaLibrarySession

internal class CustomLayoutUpdateListener(
    private val customLayoutHandler: MediaCustomLayoutHandler
): Player.Listener {
    private var currentSession: MediaLibrarySession? = null

    fun attachTo(session: MediaLibrarySession, player: Player) {
        currentSession = session
        player.addListener(this)
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        updateCustomLayout()
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        updateCustomLayout()
    }

    private fun updateCustomLayout() {
        currentSession?.let { session ->
            session.setCustomLayout(customLayoutHandler.createCustomLayout(session))
        }
    }
}