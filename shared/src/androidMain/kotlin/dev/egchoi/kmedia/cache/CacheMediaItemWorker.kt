package dev.egchoi.kmedia.cache

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ccc.ncs.domain.repository.MusicRepository
import dev.egchoi.kmedia.model.MusicStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid

class CacheMediaItemWorker(
    private val cacheManager: CacheManager,
    private val musicRepository: MusicRepository,
    applicationContext: Context,
    workerParameters: WorkerParameters
): CoroutineWorker(applicationContext, workerParameters) {

    override suspend fun doWork(): Result {
        val url = inputData.getString(KEY_URL) ?: return Result.failure()
        val key = inputData.getString(KEY_CACHE_KEY) ?: return Result.failure()

        try {
            withContext(Dispatchers.Main) {
                cacheManager.preCacheMedia(url, key)
            }
            musicRepository.updateMusicStatus(Uuid.parse(key), MusicStatus.FullyCached)
            return Result.success()
        } catch (e: Exception) {
            musicRepository.updateMusicStatus(Uuid.parse(key), MusicStatus.None)
            return Result.failure()
        }
    }

    companion object {
        const val KEY_URL = "url"
        const val KEY_CACHE_KEY = "cache_key"
        const val WORK_TAG = "cache_media_item"
    }
}