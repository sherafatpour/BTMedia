package dev.egchoi.kmedia.cache.di

import dev.egchoi.kmedia.cache.CacheManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal actual fun getPlatformCacheModule(): Module = module {
    singleOf(::CacheManager)
}