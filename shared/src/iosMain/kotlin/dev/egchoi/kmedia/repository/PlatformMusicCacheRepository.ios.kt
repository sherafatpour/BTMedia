package dev.egchoi.kmedia.repository

import com.ccc.ncs.domain.repository.MusicRepository
import dev.egchoi.kmedia.cache.CacheSettings
import dev.egchoi.kmedia.cache.CachingMediaFileLoader
import dev.egchoi.kmedia.cache.MusicCacheRepository
import dev.egchoi.kmedia.model.MusicStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import platform.Foundation.NSURL
import kotlin.uuid.Uuid

internal actual class PlatformMusicCacheRepository(
    private val fileLoader: CachingMediaFileLoader,
    private val musicRepository: MusicRepository,
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
        fileLoader.cleanup()
        musicRepository.getMusicsByStatus(
            status = listOf(MusicStatus.FullyCached, MusicStatus.PartiallyCached)
        ).first()
            .forEach { cachedMusic ->
                musicRepository.updateMusicStatus(cachedMusic.id, MusicStatus.None)
            }
    }

    override suspend fun removeCachedMusic(vararg keys: Uuid) {
        keys.forEach { key ->
            musicRepository.getMusic(key).first()?.let { music ->
                val url = NSURL(string = music.dataUrl)
                fileLoader.removeCache(url)
                musicRepository.updateMusicStatus(key, MusicStatus.None)
            }
        }
    }

    override suspend fun preCacheMusic(url: String, key: Uuid) {
        val nsUrl = NSURL(string = url)

        fileLoader.cacheFile(
            url = nsUrl,
            onCompletion = {
                coroutineScope.launch {
                    musicRepository.updateMusicStatus(key, MusicStatus.FullyCached)
                }
            },
            onFail = {
                coroutineScope.launch {
                    musicRepository.updateMusicStatus(key, MusicStatus.None)
                }
            }
        )
    }

    override suspend fun checkMusicCached(key: Uuid): Boolean {
        return musicRepository.getMusic(key).first()?.let { music ->
            val url = NSURL(string = music.dataUrl)
            fileLoader.hasCachedFile(url)
        } ?: false
    }
}