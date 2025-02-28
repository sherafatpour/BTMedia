package dev.egchoi.kmedia.cache

import io.github.aakira.napier.Napier
import platform.AVFoundation.*
import platform.Foundation.*
import kotlinx.cinterop.*
import platform.darwin.NSObject

data class DownloadInfo(
    val url: String,
    val task: AVAssetDownloadTask,
    val onFail: (() -> Unit)?,
    val onComplete: () -> Unit
)

@OptIn(ExperimentalForeignApi::class)
class CachingMediaFileLoader(
    private val cacheSettings: CacheSettings
) {
    private val fileManager = NSFileManager.defaultManager
    private val session: AVAssetDownloadURLSession
    private val downloadTasks = mutableMapOf<Long, DownloadInfo>()
    private val defaults = NSUserDefaults.standardUserDefaults
    private val lastAccessKey = "dev.egchoi.kmedia.lastAccess"

    init {
        session = createDownloadSession()
    }

    private fun createDownloadSession(): AVAssetDownloadURLSession {
        val configuration = NSURLSessionConfiguration.backgroundSessionConfigurationWithIdentifier(
            "dev.egchoi.kmedia.assetDownloadSession"
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
                downloadInfo.onComplete()
            }
        }
        downloadTasks.remove(taskId)
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
        onCompletion: () -> Unit,
        onFail: () -> Unit
    ) {
        if (!cacheSettings.getEnableCache()) {
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
                downloadTasks[taskId] = DownloadInfo(absoluteString, task, onFail, onCompletion)
            }
            task.resume()
        } ?: run {
            onFail()
        }
    }

    fun loadFileWithCaching(
        url: NSURL,
        onCompleteCaching: () -> Unit,
        onGotAsset: (AVURLAsset?) -> Unit,
    ) {
        if (!cacheSettings.getEnableCache()) {
            val streamingAsset = AVURLAsset(
                url, mapOf(
                    AVURLAssetPreferPreciseDurationAndTimingKey to true
                )
            )
            Napier.d("onGotAsset: cache disabled", tag="loadFileWithCaching")
            onGotAsset(streamingAsset)
            return
        }

        val cacheFileURL = getCacheFileURL(url) ?: return
        checkAndCleanupCacheIfNeeded()

        if (hasCachedFile(url)) {
            updateLastAccess(url)
            val cachedAsset = AVURLAsset(
                cacheFileURL, mapOf(
                    AVURLAssetPreferPreciseDurationAndTimingKey to true
                )
            )
            Napier.d("onGotAsset: already cached", tag="loadFileWithCaching")
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
                    task = task,
                    onFail = null,
                    onComplete = onCompleteCaching
                )
            }
            task.resume()
        }
    }

    private fun checkAndCleanupCacheIfNeeded() {
        val maxBytes = cacheSettings.getStorageMbSize() * 1024 * 1024L
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

    fun removeCache(url: NSURL) {
        getCacheFileURL(url)?.let { cacheUrl ->
            try {
                if (fileManager.fileExistsAtPath(cacheUrl.path!!)) {
                    fileManager.removeItemAtURL(cacheUrl, error = null)
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
        if (cacheDirectory?.path != null) {
            try {
                fileManager.removeItemAtPath(cacheDirectory?.path!!, null)
            } catch (e: Exception) {
                Napier.e("Failed to cleanup cache directory", e)
            }
        }
    }
}