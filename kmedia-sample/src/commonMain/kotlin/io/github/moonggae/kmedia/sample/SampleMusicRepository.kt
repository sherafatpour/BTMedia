package io.github.moonggae.kmedia.sample

import io.github.moonggae.kmedia.model.Music

class SampleMusicRepository {
    fun getSampleMusicList(): List<Music> {
        return listOf(
            Music(
                id = "1",
                title = "Sample Track 1",
                artist = "Artist 1",
                coverUrl = "https://picsum.photos/id/1/300/300",
                uri = "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"
            ),
            Music(
                id = "2",
                title = "Sample Track 2",
                artist = "Artist 2",
                coverUrl = "https://picsum.photos/id/2/300/300",
                uri = "https://storage.googleapis.com/exoplayer-test-media-0/Jazz_In_Paris.mp3"
            ),
            Music(
                id = "3",
                title = "Sample Track 3",
                artist = "Artist 3",
                coverUrl = "https://picsum.photos/id/5/300/300",
                uri = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
            )
        )
    }
}