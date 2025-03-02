package io.github.moonggae.kmedia.cache

interface CacheStatusListener {
    fun onCacheStatusChanged(musicId: String, status: CacheStatus)

    enum class CacheStatus {
        NONE,
        PARTIALLY_CACHED,
        FULLY_CACHED
    }
}

internal class NoOpCacheStatusListener: CacheStatusListener {
    override fun onCacheStatusChanged(musicId: String, status: CacheStatusListener.CacheStatus) {}
}