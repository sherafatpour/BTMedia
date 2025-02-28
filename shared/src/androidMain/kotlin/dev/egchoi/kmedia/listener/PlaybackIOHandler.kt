package dev.egchoi.kmedia.listener

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import dev.egchoi.kmedia.cache.CacheManager
import dev.egchoi.kmedia.cache.CacheStatusListener
import dev.egchoi.kmedia.util.isNetworkSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class PlaybackIOHandler(
    private val scope: CoroutineScope,
    private val cacheManager: CacheManager,
    private val cacheStatusListener: CacheStatusListener
): Player.Listener {
    private lateinit var player: Player

    fun attachTo(player: Player) {
        this.player = player
        player.addListener(this)
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        mediaItem?.let {
            scope.launch {
                handleMediaItemTransition(it)
            }
        }
    }

    private suspend fun handleMediaItemTransition(mediaItem: MediaItem) {
        if (cacheManager.enableCache) {
            if (mediaItem.isNetworkSource) {
                setupCacheListener(mediaItem)
                return
            }
        }
    }

    @OptIn(UnstableApi::class)
    suspend fun setupCacheListener(mediaItem: MediaItem) {
        cacheManager.observeCacheUpdate(mediaItem.mediaId).collect { isFullyCached ->
            cacheStatusListener.onCacheStatusChanged(
                musicId = mediaItem.mediaId,
                if (isFullyCached) CacheStatusListener.CacheStatus.FULLY_CACHED else CacheStatusListener.CacheStatus.PARTIALLY_CACHED
            )
        }
    }
}