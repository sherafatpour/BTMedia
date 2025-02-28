package dev.egchoi.kmedia.cache.di

import dev.egchoi.kmedia.cache.CachingMediaFileLoader
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

internal actual fun createSettingModule(): Module = module {
    single { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) } bind ObservableSettings::class
}

internal actual fun getPlatformCacheModule(): Module = module {
    single {
        CachingMediaFileLoader(
            cacheSettings = get()
        )
    }
}