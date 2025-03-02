package io.github.moonggae.kmedia.analytics

interface PlaybackAnalyticsListener {
    fun onPlaybackCompleted(
        musicId: String,
        totalPlayTimeMs: Long,
        duration: Long
    )
}

internal class NoOpPlaybackAnalyticsListener: PlaybackAnalyticsListener {
    override fun onPlaybackCompleted(musicId: String, totalPlayTimeMs: Long, duration: Long) {}
}