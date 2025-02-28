package dev.egchoi.kmedia.di

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import dev.egchoi.kmedia.cache.CacheManager
import dev.egchoi.kmedia.cache.MusicCacheRepository
import dev.egchoi.kmedia.controller.MediaPlaybackController
import dev.egchoi.kmedia.controller.PlatformMediaPlaybackController
import dev.egchoi.kmedia.custom.CustomLayoutUpdateListener
import dev.egchoi.kmedia.custom.MediaCustomLayoutHandler
import dev.egchoi.kmedia.listener.PlaybackIOHandler
import dev.egchoi.kmedia.listener.PlaybackStateHandler
import dev.egchoi.kmedia.repository.PlatformMusicCacheRepository
import dev.egchoi.kmedia.session.LibrarySessionCallback
import dev.egchoi.kmedia.util.PlaybackConstraint
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun getPlatformPlaybackModule(): Module = module {
    singleOf(::CacheManager)
    singleOf(::PlaybackStateHandler)
    singleOf(::PlaybackIOHandler)
    singleOf(::PlatformMediaPlaybackController) bind MediaPlaybackController::class
    singleOf(::PlatformMusicCacheRepository) bind MusicCacheRepository::class
    singleOf(::LibrarySessionCallback)
    singleOf(::MediaCustomLayoutHandler)
    singleOf(::CustomLayoutUpdateListener)

    single<PendingIntent> {
        PendingIntent.getActivity(
            androidContext(),
            1,
            Intent(androidContext(), get()).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra(PlaybackConstraint.EXTRA_NAME_EVENT, PlaybackConstraint.EVENT_NOTIFICATION_CLICK)
            },
            FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}