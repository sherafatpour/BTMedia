package dev.egchoi.kmedia.cache.di

import dev.egchoi.kmedia.cache.CacheSettings
import dev.egchoi.kmedia.cache.DefaultCacheSettings
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

internal expect fun createSettingModule(): Module

internal expect fun getPlatformCacheModule(): Module

val cacheModule = module {
    includes(createSettingModule())
    single<CacheSettings> { DefaultCacheSettings(get()) } bind CacheSettings::class
    includes(getPlatformCacheModule())
}