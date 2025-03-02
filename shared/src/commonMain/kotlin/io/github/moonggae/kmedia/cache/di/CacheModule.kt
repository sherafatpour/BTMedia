package io.github.moonggae.kmedia.cache.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun getPlatformCacheModule(): Module

internal val cacheModule = module {
    includes(getPlatformCacheModule())
}