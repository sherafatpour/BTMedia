package dev.egchoi.kmedia.analytics

interface PlaybackAnalyticsListener {
    fun onPlaybackCompleted(
        musicId: String,
        totalPlayTimeMs: Long,
        duration: Long
    )
}