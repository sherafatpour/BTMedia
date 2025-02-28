package dev.egchoi.kmedia.di

import dev.egchoi.kmedia.cache.di.cacheModule
import dev.egchoi.kmedia.state.PlaybackStateManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

expect fun getPlatformPlaybackModule(): Module

val playbackModule = module {
    includes(cacheModule)

    single { CoroutineScope(SupervisorJob() + Dispatchers.IO) } bind CoroutineScope::class
    single { Dispatchers.Main } bind CoroutineDispatcher::class

    single { PlaybackStateManager }

    includes(getPlatformPlaybackModule())
}