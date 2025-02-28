package io.github.moonggae.kmedia.controller

import io.github.moonggae.kmedia.model.Music
import io.github.moonggae.kmedia.model.RepeatMode
import io.github.moonggae.kmedia.util.reorder

// Playlist Manager
class PlaylistManager {
    private val playlist = mutableListOf<Music>()
    var currentIndex = 0
        private set
    private val shuffleManager = ShuffleManager()

    var repeatMode = RepeatMode.REPEAT_MODE_OFF
        private set
    var isShuffleOn = false
        private set

    fun getCurrentMusic(): Music? = playlist.getOrNull(currentIndex)
    fun getPlaylist(): List<Music> = playlist.toList()

    fun updatePlaylist(musics: List<Music>, startIndex: Int) {
        playlist.clear()
        playlist.addAll(musics)
        currentIndex = startIndex.coerceIn(0, playlist.lastIndex)
        if (isShuffleOn) {
            shuffleManager.updateShuffleIndices(currentIndex, playlist.size)
        }
    }

    fun appendMusics(musics: List<Music>) {
        val startIndex = playlist.size
        val newMusics = musics - playlist.toSet()
        playlist.addAll(newMusics)

        if (isShuffleOn) {
            shuffleManager.addNewIndices(startIndex, newMusics.size)
        }
    }

    fun removeMusic(musicId: String): Music? {
        val removeIndex = playlist.indexOfFirst { it.id == musicId }
        if (removeIndex < 0) return null

        val removedMusic = playlist[removeIndex]
        val nextMusic = if (isShuffleOn) {
            val nextShuffledIndex = shuffleManager.getNextIndex(currentIndex, repeatMode)
                ?: shuffleManager.getPreviousIndex(currentIndex)
            nextShuffledIndex?.let { playlist.getOrNull(it) }
        } else {
            playlist.getOrNull(removeIndex + 1) ?: playlist.getOrNull(removeIndex - 1)
        }

        playlist.removeAt(removeIndex)

        if (isShuffleOn) {
            shuffleManager.removeIndex(removeIndex)
        }

        if (removeIndex == currentIndex) {
            currentIndex = nextMusic?.let { playlist.indexOf(it) } ?: 0
        } else if (removeIndex < currentIndex) {
            currentIndex--
        }

        return nextMusic
    }

    fun setShuffleMode(isOn: Boolean) {
        if (this.isShuffleOn != isOn) {
            this.isShuffleOn = isOn
            if (isOn) {
                shuffleManager.updateShuffleIndices(currentIndex, playlist.size)
            }
        }
    }

    fun setRepeatMode(mode: RepeatMode) {
        this.repeatMode = mode
    }

    fun moveMediaItem(fromIndex: Int, toIndex: Int) {
        val currentMusicId = playlist[currentIndex].id
        playlist.reorder(fromIndex, toIndex)
        currentIndex = playlist.indexOfFirst { it.id == currentMusicId }
    }

    fun getNextIndex(): Int? = when {
        playlist.isEmpty() -> null
        isShuffleOn -> shuffleManager.getNextIndex(currentIndex, repeatMode)
        else -> when {
            currentIndex == playlist.lastIndex &&
                    repeatMode == RepeatMode.REPEAT_MODE_OFF -> null
            currentIndex == playlist.lastIndex -> 0
            else -> currentIndex + 1
        }
    }

    fun getPreviousIndex(): Int? = when {
        isShuffleOn -> shuffleManager.getPreviousIndex(currentIndex)
        currentIndex > 0 -> currentIndex - 1
        else -> null
    }

    fun setCurrentIndex(index: Int) {
        if (index in 0..playlist.lastIndex) {
            currentIndex = index
        }
    }

    fun clear() {
        playlist.clear()
        currentIndex = 0
        repeatMode = RepeatMode.REPEAT_MODE_OFF
        isShuffleOn = false
        shuffleManager.clear()
    }

    fun hasNext(): Boolean = when {
        isShuffleOn -> currentIndex < playlist.lastIndex
        else -> currentIndex < playlist.lastIndex
    } || repeatMode == RepeatMode.REPEAT_MODE_ALL

    fun hasPrevious(): Boolean = currentIndex > 0
}