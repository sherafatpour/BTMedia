package dev.egchoi.kmedia.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dev.egchoi.kmedia.model.MusicStatus
import dev.egchoi.kmedia.cache.CacheManager
import dev.egchoi.kmedia.cache.CacheMediaItemWorker
import dev.egchoi.kmedia.cache.MusicCacheRepository
import dev.egchoi.kmedia.controller.PlatformMediaPlaybackController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlin.uuid.Uuid
import com.ccc.ncs.domain.repository.MusicRepository

internal actual class PlatformMusicCacheRepository(
    private val cacheManager: CacheManager,
    private val musicRepository: MusicRepository,
    private val applicationContext: Context,
    private val playbackController: PlatformMediaPlaybackController
) : MusicCacheRepository {
    override val maxSizeMb: Flow<Int> = cacheManager.maxSizeMbFlow

    override suspend fun setMaxSizeMb(size: Int) {
        cacheManager.setMaxSize(size)
    }

    override val enableCache: Flow<Boolean> = cacheManager.enableCacheFlow

    @OptIn(ExperimentalCoroutinesApi::class)
    override val usedSizeBytes: Flow<Long?> = enableCache.flatMapLatest { enable ->
        if (enable) {
            flow<Long?> {
                while (true) {
                    emit(cacheManager.usedCacheBytes)
                    delay(1000)
                }
            }
        } else {
            flowOf(0L)
        }
    }

    override suspend fun setCacheEnable(enable: Boolean) {
        val wasEnabled = cacheManager.enableCache
        cacheManager.setCacheEnable(enable)

        if (!enable && wasEnabled) {
            clearCache()
            WorkManager.getInstance(applicationContext).cancelAllWorkByTag(CacheMediaItemWorker.WORK_TAG)
        }

        if (enable != wasEnabled) {
            playbackController.recreatePlayer()
        }
    }

    override suspend fun clearCache() {
        cacheManager.cleanCache()
        musicRepository.getMusicsByStatus(
            status = listOf(MusicStatus.FullyCached, MusicStatus.PartiallyCached, MusicStatus.Downloading)
        ).first()
            .forEach { cachedMusic ->
                musicRepository.updateMusicStatus(cachedMusic.id, MusicStatus.None)
            }
    }

    override suspend fun checkMusicCached(key: Uuid) = cacheManager.checkItemCached(key.toString()) ?: false

    override suspend fun preCacheMusic(url: String, key: Uuid) {
        val workData = workDataOf(
            CacheMediaItemWorker.KEY_URL to url,
            CacheMediaItemWorker.KEY_CACHE_KEY to key.toString()
        )

        val workRequest = OneTimeWorkRequestBuilder<CacheMediaItemWorker>()
            .setInputData(workData)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(CacheMediaItemWorker.WORK_TAG)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(
                "cache_${key}",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                workRequest
            )
    }

    override suspend fun removeCachedMusic(vararg keys: Uuid) {
        keys.forEach { key ->
            cacheManager.removeFile(key.toString())
            musicRepository.updateMusicStatus(key, MusicStatus.None)
        }
    }
}