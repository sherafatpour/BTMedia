package dev.egchoi.kmedia

import dev.egchoi.kmedia.analytics.NoOpPlaybackAnalyticsListener
import dev.egchoi.kmedia.analytics.PlaybackAnalyticsListener
import dev.egchoi.kmedia.cache.CacheConfig
import dev.egchoi.kmedia.cache.CacheStatusListener
import dev.egchoi.kmedia.cache.MusicCacheRepository
import dev.egchoi.kmedia.cache.NoOpCacheStatusListener
import dev.egchoi.kmedia.controller.MediaPlaybackController
import dev.egchoi.kmedia.di.IsolatedKoinContext
import dev.egchoi.kmedia.model.PlaybackState
import dev.egchoi.kmedia.state.PlaybackStateManager
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.module.Module


class KMedia private constructor(
    internal val context: Any, // Context for Android
) {
    val player: MediaPlaybackController by lazy { IsolatedKoinContext.koin.inject<MediaPlaybackController>().value }
    val cache: MusicCacheRepository by lazy { IsolatedKoinContext.koin.inject<MusicCacheRepository>().value }
    val playbackState: StateFlow<PlaybackState> = PlaybackStateManager.flow

    class Builder {
        private var cacheEnabled: Boolean = false
        private var cacheSize: Int = 1024 // MB
        private var analyticsListener: PlaybackAnalyticsListener? = null
        private var cacheStatusListener: CacheStatusListener? = null

        fun cache(enabled: Boolean, sizeInMb: Int = 1024, listener: CacheStatusListener? = null) = apply {
            this.cacheEnabled = enabled
            this.cacheSize = sizeInMb
            this.cacheStatusListener = listener
        }

        fun analytics(listener: PlaybackAnalyticsListener) = apply {
            this.analyticsListener = listener
        }

        fun build(context: Any): KMedia {
            val cacheSettings = CacheConfig(
                enable = cacheEnabled,
                sizeMB = cacheSize,
            )

            IsolatedKoinContext.init(
                kmediaModule(
                    context,
                    cacheSettings,
                    analyticsListener ?: NoOpPlaybackAnalyticsListener(),
                    cacheStatusListener ?: NoOpCacheStatusListener()
                )
            )

            return KMedia(
                context = context
            )
        }
    }

    companion object {
        fun builder() = Builder()
    }
}

expect fun kmediaModule(
    context: Any,
    cacheConfig: CacheConfig,
    playbackAnalyticsListener: PlaybackAnalyticsListener,
    cacheStatusListener: CacheStatusListener
): Module