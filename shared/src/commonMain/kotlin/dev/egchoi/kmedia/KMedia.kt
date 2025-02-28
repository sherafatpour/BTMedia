package dev.egchoi.kmedia

import dev.egchoi.kmedia.analytics.NoOpPlaybackAnalyticsListener
import dev.egchoi.kmedia.analytics.PlaybackAnalyticsListener
import dev.egchoi.kmedia.cache.CacheConfig
import dev.egchoi.kmedia.cache.CacheStatusListener
import dev.egchoi.kmedia.cache.MusicCacheRepository
import dev.egchoi.kmedia.cache.NoOpCacheStatusListener
import dev.egchoi.kmedia.di.IsolatedKoinContext
import dev.egchoi.kmedia.controller.MediaPlaybackController
import dev.egchoi.kmedia.model.PlaybackState
import dev.egchoi.kmedia.state.PlaybackStateManager
import dev.egchoi.kmedia.util.Platform
import dev.egchoi.kmedia.util.getPlatform
import kotlinx.coroutines.flow.Flow
import org.koin.dsl.bind
import org.koin.dsl.module


class KMedia private constructor(
    internal val context: Any, // Context for Android
) {
    val player: MediaPlaybackController by IsolatedKoinContext.koin.inject()
    val cache: MusicCacheRepository by IsolatedKoinContext.koin.inject()
    val playbackState: Flow<PlaybackState> = PlaybackStateManager.flow

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
                module {
                    if (getPlatform() == Platform.Android) {
                        single { context }
                    }
                    single { cacheSettings }
                    single { analyticsListener ?: NoOpPlaybackAnalyticsListener() } bind PlaybackAnalyticsListener::class
                    single { cacheStatusListener ?: NoOpCacheStatusListener() } bind CacheStatusListener::class
                }
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