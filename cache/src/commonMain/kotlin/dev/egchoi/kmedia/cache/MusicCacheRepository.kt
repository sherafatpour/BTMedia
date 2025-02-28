package dev.egchoi.kmedia.cache

import kotlinx.coroutines.flow.Flow

interface MusicCacheRepository {
    val maxSizeMb: Flow<Int>
    suspend fun setMaxSizeMb(size: Int)

    val usedSizeBytes: Flow<Long?>

    val enableCache: Flow<Boolean>

    suspend fun setCacheEnable(enable: Boolean)

    suspend fun clearCache()

    suspend fun removeCachedMusic(vararg keys: String)

    suspend fun preCacheMusic(url: String, key: String)

    suspend fun checkMusicCached(key: String): Boolean
}