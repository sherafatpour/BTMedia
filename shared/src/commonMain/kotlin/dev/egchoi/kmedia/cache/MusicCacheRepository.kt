package dev.egchoi.kmedia.cache

import kotlinx.coroutines.flow.Flow

interface MusicCacheRepository {
    val maxSizeMb: Int

    val usedSizeBytes: Flow<Long?>

    val enableCache: Boolean

    suspend fun clearCache()

    suspend fun removeCachedMusic(vararg keys: String)

    suspend fun preCacheMusic(url: String, key: String)

    suspend fun checkMusicCached(key: String): Boolean
}