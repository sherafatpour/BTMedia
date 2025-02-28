package dev.egchoi.kmedia.cache.di

import android.content.Context
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import dev.egchoi.kmedia.cache.CacheManager
import dev.egchoi.kmedia.cache.CacheSettings
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun createSettingModule(): Module = module {
    single {
        SharedPreferencesSettings(
            get<Context>().getSharedPreferences(CacheSettings.PREFERENCES_NAME, Context.MODE_PRIVATE)
        )
    } bind ObservableSettings::class
}

internal actual fun getPlatformCacheModule(): Module = module {
    single {
        CacheManager(
            context = get(),
            dataStore = get()
        )
    }
}