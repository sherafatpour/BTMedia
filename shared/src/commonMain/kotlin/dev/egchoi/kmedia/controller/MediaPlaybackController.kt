package dev.egchoi.kmedia.controller

import dev.egchoi.kmedia.model.Music
import dev.egchoi.kmedia.model.RepeatMode
import kotlin.uuid.Uuid

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

    fun removeMusics(vararg musicId: Uuid)

    fun updateCurrentPlaylistMusic(music: Music)

//    fun fastForward()
//
//    fun rewind()
}