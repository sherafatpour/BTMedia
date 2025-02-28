package dev.egchoi.kmedia.di

import com.ccc.ncs.domain.MediaPlaybackController
import com.ccc.ncs.domain.repository.MusicCacheRepository
import dev.egchoi.kmedia.controller.PlatformMediaPlaybackController
import dev.egchoi.kmedia.repository.PlatformMusicCacheRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun getPlatformPlaybackModule(): Module = module {
    singleOf(::PlatformMusicCacheRepository) bind MusicCacheRepository::class
    singleOf(::PlatformMediaPlaybackController) bind MediaPlaybackController::class
}