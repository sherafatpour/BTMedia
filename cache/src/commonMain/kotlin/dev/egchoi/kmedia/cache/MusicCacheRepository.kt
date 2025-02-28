package dev.egchoi.kmedia.cache

import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface MusicCacheRepository {
    val maxSizeMb: Flow<Int>
    suspend fun setMaxSizeMb(size: Int)

    val usedSizeBytes: Flow<Long?>

    val enableCache: Flow<Boolean>

    suspend fun setCacheEnable(enable: Boolean)

    suspend fun clearCache()

    @OptIn(ExperimentalUuidApi::class)
    suspend fun removeCachedMusic(vararg keys: Uuid)

    suspend fun preCacheMusic(url: String, key: Uuid)

    suspend fun checkMusicCached(key: Uuid): Boolean
}