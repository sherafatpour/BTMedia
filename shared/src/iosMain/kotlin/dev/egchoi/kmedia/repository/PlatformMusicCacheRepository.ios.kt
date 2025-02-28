package dev.egchoi.kmedia.repository

import dev.egchoi.kmedia.cache.CacheSettings
import dev.egchoi.kmedia.cache.CacheStatusListener
import dev.egchoi.kmedia.cache.CachingMediaFileLoader
import dev.egchoi.kmedia.cache.MusicCacheRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import platform.Foundation.NSURL

internal actual class PlatformMusicCacheRepository(
    private val fileLoader: CachingMediaFileLoader,
    private val cacheStatusListener: CacheStatusListener,
    private val cacheSettings: CacheSettings,
    private val coroutineScope: CoroutineScope,
) : MusicCacheRepository {
    override val maxSizeMb: Flow<Int> = cacheSettings.getStorageMbSizeFlow()

    override suspend fun setMaxSizeMb(size: Int) {
        cacheSettings.setStorageMbSize(size)
    }

    override val enableCache: Flow<Boolean> = cacheSettings.getEnableCacheFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val usedSizeBytes: Flow<Long?> = enableCache.flatMapLatest { enable ->
        if (enable) {
            flow<Long?> {
                while (true) {
                    val size = fileLoader.getTotalCachedBytes()
                    emit(size)
                    delay(1000)
                }
            }
        } else {
            flowOf(0L)
        }
    }

    override suspend fun setCacheEnable(enable: Boolean) {
        cacheSettings.setEnableCache(enable)
        if (!enable) {
            clearCache()
        }
    }

    override suspend fun clearCache() {
        val allCachedIds = fileLoader.getAllCachedIds()
        fileLoader.cleanup()
        allCachedIds.forEach { key ->
            cacheStatusListener.onCacheStatusChanged(key, CacheStatusListener.CacheStatus.NONE)
        }
    }

    override suspend fun removeCachedMusic(vararg keys: String) {
        keys.forEach { key ->
            fileLoader.removeCacheById(key)
            cacheStatusListener.onCacheStatusChanged(key, CacheStatusListener.CacheStatus.NONE)
        }
    }

    override suspend fun preCacheMusic(url: String, key: String) {
        val nsUrl = NSURL(string = url)

        fileLoader.cacheFile(
            url = nsUrl,
            musicId = key,
            onCompletion = {
                coroutineScope.launch {
                    cacheStatusListener.onCacheStatusChanged(key, CacheStatusListener.CacheStatus.FULLY_CACHED)
                }
            },
            onFail = {
                coroutineScope.launch {
                    cacheStatusListener.onCacheStatusChanged(key, CacheStatusListener.CacheStatus.NONE)
                }
            }
        )
    }

    override suspend fun checkMusicCached(key: String): Boolean {
        return fileLoader.hasCachedFileById(key)
    }
}