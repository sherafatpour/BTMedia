package io.github.moonggae.kmedia.di

import io.github.moonggae.kmedia.cache.di.cacheModule
import io.github.moonggae.kmedia.state.PlaybackStateManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

internal expect fun getPlatformPlaybackModule(): Module

internal val playbackModule = module {
    includes(cacheModule)

    single { CoroutineScope(SupervisorJob() + Dispatchers.IO) } bind CoroutineScope::class
    single { Dispatchers.Main } bind CoroutineDispatcher::class

    single { PlaybackStateManager }

    includes(getPlatformPlaybackModule())
}