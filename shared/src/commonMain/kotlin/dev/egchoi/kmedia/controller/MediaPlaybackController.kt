package dev.egchoi.kmedia.controller

import dev.egchoi.kmedia.model.Music
import dev.egchoi.kmedia.model.RepeatMode

interface MediaPlaybackController {
    fun setPosition(positionMs: Long)

    fun setRepeatMode(repeatMode: RepeatMode)

    fun setShuffleMode(isOn: Boolean)

    fun previous()

    fun next()

    fun play()

    fun pause()

    fun moveMediaItem(currentIndex: Int, newIndex: Int)

    fun seekTo(musicIndex: Int)

    fun setSpeed(speed: Float)

    fun prepare(musics: List<Music>, index: Int, positionMs: Long)

    fun playMusics(musics: List<Music>, startIndex: Int = 0)

    fun stop()

    fun appendMusics(musics: List<Music>)

    fun removeMusics(vararg musicId: String)

    fun release()

//    fun fastForward()
//
//    fun rewind()
}