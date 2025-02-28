package io.github.moonggae.kmedia.cache

import io.github.aakira.napier.Napier
import platform.AVFoundation.*
import platform.Foundation.*
import kotlinx.cinterop.*
import platform.darwin.NSObject

data class DownloadInfo(
    val url: String,
    val musicId: String? = null,  // 추가된 musicId 필드
    val task: AVAssetDownloadTask,
    val onFail: (() -> Unit)?,
    val onComplete: () -> Unit
)

@OptIn(ExperimentalForeignApi::class)
class CachingMediaFileLoader(
    private val cacheConfig: CacheConfig
) {
    private val fileManager = NSFileManager.defaultManager
    private val session: AVAssetDownloadURLSession
    private val downloadTasks = mutableMapOf<Long, DownloadInfo>()
    private val defaults = NSUserDefaults.standardUserDefaults
    private val lastAccessKey = "io.github.moonggae.kmedia.lastAccess"
    private val urlToIdMapKey = "io.github.moonggae.kmedia.urlToIdMap"

    init {
        session = createDownloadSession()
    }

    private fun createDownloadSession(): AVAssetDownloadURLSession {
        val configuration = NSURLSessionConfiguration.backgroundSessionConfigurationWithIdentifier(
            "io.github.moonggae.kmedia.assetDownloadSession"
        )
        return AVAssetDownloadURLSession.sessionWithConfiguration(
            configuration = configuration,
            assetDownloadDelegate = createDownloadDelegate(),
            delegateQueue = NSOperationQueue.mainQueue
        )
    }

    private fun handleDownloadCompletion(task: NSURLSessionTask, error: NSError?) {
        val taskId = task.taskIdentifier.toLong()
        if (error != null) {
            Napier.e("Download failed: ${error.localizedDescription}")
            downloadTasks[taskId]?.let { downloadInfo ->
                cleanupFailedDownload(NSURL(string = downloadInfo.url))
                downloadInfo.onFail?.invoke()
            }
        } else {
            downloadTasks[taskId]?.let { downloadInfo ->
                // ID가 있으면 매핑 저장
                downloadInfo.musicId?.let { musicId ->
                    saveUrlToIdMapping(downloadInfo.url, musicId)
                }
                downloadInfo.onComplete()
            }
        }
        downloadTasks.remove(taskId)
    }

    // URL과 ID 매핑 저장
    private fun saveUrlToIdMapping(url: String, id: String) {
        val map = getUrlToIdMap().toMutableMap()
        map[url] = id
        defaults.setObject(map, urlToIdMapKey)
    }

    // URL과 ID 매핑 가져오기
    private fun getUrlToIdMap(): Map<String, String> {
        return defaults.dictionaryForKey(urlToIdMapKey) as? Map<String, String> ?: mapOf()
    }

    // ID로 URL 찾기
    fun getUrlById(id: String): String? {
        return getUrlToIdMap().entries.find { it.value == id }?.key
    }

    // ID에 해당하는 캐시 파일이 있는지 확인
    fun hasCachedFileById(id: String): Boolean {
        return getUrlById(id)?.let { url ->
            hasCachedFile(NSURL(string = url))
        } ?: false
    }

    // ID로 캐시 파일 제거
    fun removeCacheById(id: String) {
        getUrlById(id)?.let { url ->
            removeCache(NSURL(string = url))
            // 매핑에서도 제거
            val map = getUrlToIdMap().toMutableMap()
            map.entries.removeAll { it.value == id }
            defaults.setObject(map, urlToIdMapKey)
        }
    }

    private fun createDownloadDelegate() = object : NSObject(), AVAssetDownloadDelegateProtocol {
        override fun URLSession(
            session: NSURLSession,
            task: NSURLSessionTask,
            didCompleteWithError: NSError?,
        ) {
            handleDownloadCompletion(task, didCompleteWithError)
        }
    }

    val cacheDirectory: NSURL?
        get() {
            return fileManager.URLsForDirectory(
                NSCachesDirectory,
                NSUserDomainMask
            ).firstOrNull() as? NSURL
        }

    private fun updateLastAccess(url: NSURL) {
        val urlString = url.absoluteString ?: return
        val accessMap = defaults.dictionaryForKey(lastAccessKey) as? Map<String, Double> ?: mapOf()
        val updatedMap = accessMap + (urlString to NSDate.date().timeIntervalSince1970)
        defaults.setObject(updatedMap, lastAccessKey)
    }

    private fun getCacheFileURL(originalURL: NSURL): NSURL? {
        val extension = originalURL.pathExtension ?: "mp3"
        val fileName = "${originalURL.lastPathComponent?.replace(".$extension", "")}_cached.$extension"
        return cacheDirectory?.URLByAppendingPathComponent(fileName)
    }

    fun hasCachedFile(url: NSURL): Boolean {
        getCacheFileURL(url)?.path?.let { path ->
            return fileManager.fileExistsAtPath(path)
        }
        return false
    }

    fun cacheFile(
        url: NSURL,
        musicId: String,
        onCompletion: () -> Unit,
        onFail: () -> Unit
    ) {
        if (!cacheConfig.enable) {
            onFail()
            return
        }

        val cacheFileURL = getCacheFileURL(url) ?: run {
            onFail()
            return
        }

        checkAndCleanupCacheIfNeeded()

        if (hasCachedFile(url)) {
            updateLastAccess(url)
            url.absoluteString?.let { urlString ->
                saveUrlToIdMapping(urlString, musicId)
            }
            onCompletion()
            return
        }

        if (url.absoluteString?.let { urlString ->
                downloadTasks.values.any { it.url == urlString }
            } == true
        ) {
            onFail()
            return
        }

        val streamingAsset = AVURLAsset(
            url, mapOf(
                AVURLAssetPreferPreciseDurationAndTimingKey to true
            )
        )

        val downloadTask = session.assetDownloadTaskWithURLAsset(
            URLAsset = streamingAsset,
            destinationURL = cacheFileURL,
            options = null
        )

        downloadTask?.let { task ->
            val taskId = task.taskIdentifier.toLong()
            url.absoluteString?.let { absoluteString ->
                downloadTasks[taskId] = DownloadInfo(
                    url = absoluteString,
                    musicId = musicId,
                    task = task,
                    onFail = onFail,
                    onComplete = onCompletion
                )
            }
            task.resume()
        } ?: run {
            onFail()
        }
    }

    fun loadFileWithCaching(
        url: NSURL,
        musicId: String,
        onCompleteCaching: () -> Unit,
        onGotAsset: (AVURLAsset?) -> Unit,
    ) {
        if (!cacheConfig.enable) {
            val streamingAsset = AVURLAsset(
                url, mapOf(
                    AVURLAssetPreferPreciseDurationAndTimingKey to true
                )
            )
            onGotAsset(streamingAsset)
            return
        }

        val cacheFileURL = getCacheFileURL(url) ?: return
        checkAndCleanupCacheIfNeeded()

        if (hasCachedFile(url)) {
            updateLastAccess(url)
            url.absoluteString?.let { urlString ->
                saveUrlToIdMapping(urlString, musicId)
            }
            val cachedAsset = AVURLAsset(
                cacheFileURL, mapOf(
                    AVURLAssetPreferPreciseDurationAndTimingKey to true
                )
            )
            onCompleteCaching()
            onGotAsset(cachedAsset)
            return
        }

        val streamingAsset = AVURLAsset(
            url, mapOf(
                AVURLAssetPreferPreciseDurationAndTimingKey to true
            )
        )

        Napier.d("onGotAsset: start caching", tag="loadFileWithCaching")
        onGotAsset(streamingAsset)

        if (url.absoluteString?.let { urlString ->
                downloadTasks.values.any { it.url == urlString }
            } == true
        ) {
            return
        }

        val downloadTask = session.assetDownloadTaskWithURLAsset(
            URLAsset = streamingAsset,
            destinationURL = cacheFileURL,
            options = null
        )

        downloadTask?.let { task ->
            val taskId = task.taskIdentifier.toLong()
            url.absoluteString?.let { absoluteString ->
                downloadTasks[taskId] = DownloadInfo(
                    url = absoluteString,
                    musicId = musicId,  // musicId 추가
                    task = task,
                    onFail = null,
                    onComplete = onCompleteCaching
                )
            }
            task.resume()
        }
    }

    private fun checkAndCleanupCacheIfNeeded() {
        val maxBytes = cacheConfig.sizeMB * 1024 * 1024L
        val currentSize = getTotalCachedBytes()

        if (currentSize > maxBytes) {
            cleanupLeastRecentlyUsed(currentSize - maxBytes)
        }
    }

    fun getTotalCachedBytes(): Long {
        return cacheDirectory?.path?.let { path ->
            var totalSize = 0L
            fileManager.contentsOfDirectoryAtPath(path, null)?.forEach { filename ->
                (filename as? String)?.let { name ->
                    val filePath = "$path/$name"
                    totalSize += (fileManager.attributesOfItemAtPath(filePath, null)
                        ?.get(NSFileSize) as? NSNumber)?.longValue ?: 0L
                }
            }
            totalSize
        } ?: 0L
    }

    private fun cleanupLeastRecentlyUsed(bytesToFree: Long) {
        cacheDirectory?.path?.let { path ->
            val accessMap = defaults.dictionaryForKey(lastAccessKey) as? Map<String, Double> ?: return
            val files = fileManager.contentsOfDirectoryAtPath(path, null)
                ?.mapNotNull { filename ->
                    (filename as? String)?.let { name ->
                        val filePath = "$path/$name"
                        val attrs = fileManager.attributesOfItemAtPath(filePath, null)
                        val urlString = accessMap.entries.find { name.contains(it.key) }?.key
                        FileInfo(
                            path = filePath,
                            size = (attrs?.get(NSFileSize) as? NSNumber)?.longValue ?: 0L,
                            lastAccess = urlString?.let { accessMap[it] } ?: 0.0
                        )
                    }
                }
                ?.sortedBy { it.lastAccess }

            var freedBytes = 0L
            files?.forEach { fileInfo ->
                if (freedBytes < bytesToFree) {
                    fileManager.removeItemAtPath(fileInfo.path, null)
                    freedBytes += fileInfo.size
                }
            }
        }
    }

    private fun cleanupFailedDownload(url: NSURL?) {
        url?.let { originalUrl ->
            getCacheFileURL(originalUrl)?.let { cacheUrl ->
                try {
                    if (fileManager.fileExistsAtPath(cacheUrl.path!!)) {
                        fileManager.removeItemAtURL(cacheUrl, error = null)
                    }
                } catch (e: Exception) {
                    Napier.e("Failed to cleanup cache file", e)
                }
            }
        }
    }

    fun cancelDownload(url: NSURL) {
        url.absoluteString?.let { urlString ->
            downloadTasks.entries
                .firstOrNull { it.value.url == urlString }
                ?.let { entry ->
                    entry.value.task.cancel()
                    downloadTasks.remove(entry.key)
                }
        }
    }

    // ID로 다운로드 취소
    fun cancelDownloadById(id: String) {
        downloadTasks.entries
            .firstOrNull { it.value.musicId == id }
            ?.let { entry ->
                entry.value.task.cancel()
                downloadTasks.remove(entry.key)
            }
    }

    fun removeCache(url: NSURL) {
        getCacheFileURL(url)?.let { cacheUrl ->
            try {
                if (fileManager.fileExistsAtPath(cacheUrl.path!!)) {
                    fileManager.removeItemAtURL(cacheUrl, error = null)
                }

                // URL-ID 매핑에서도 제거
                url.absoluteString?.let { urlString ->
                    val map = getUrlToIdMap().toMutableMap()
                    map.remove(urlString)
                    defaults.setObject(map, urlToIdMapKey)
                }
            } catch (e: Exception) {
                Napier.e("Failed to remove cache file", e)
            }
        }
    }

    private data class FileInfo(
        val path: String,
        val size: Long,
        val lastAccess: Double
    )

    fun cleanup() {
        downloadTasks.values.forEach { it.task.cancel() }
        downloadTasks.clear()

        // URL-ID 매핑 초기화
        defaults.setObject(mapOf<String, String>(), urlToIdMapKey)

        if (cacheDirectory?.path != null) {
            try {
                fileManager.removeItemAtPath(cacheDirectory?.path!!, null)
            } catch (e: Exception) {
                Napier.e("Failed to cleanup cache directory", e)
            }
        }
    }

    // ID에 해당하는 모든 캐시된 ID 가져오기
    fun getAllCachedIds(): List<String> {
        return getUrlToIdMap().values.distinct()
    }
}