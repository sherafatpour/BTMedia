package io.github.moonggae.kmedia

import io.github.moonggae.kmedia.analytics.NoOpPlaybackAnalyticsListener
import io.github.moonggae.kmedia.analytics.PlaybackAnalyticsListener
import io.github.moonggae.kmedia.cache.CacheConfig
import io.github.moonggae.kmedia.cache.CacheStatusListener
import io.github.moonggae.kmedia.cache.MusicCacheRepository
import io.github.moonggae.kmedia.cache.NoOpCacheStatusListener
import io.github.moonggae.kmedia.controller.MediaPlaybackController
import io.github.moonggae.kmedia.di.IsolatedKoinContext
import io.github.moonggae.kmedia.model.PlaybackState
import io.github.moonggae.kmedia.state.PlaybackStateManager
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