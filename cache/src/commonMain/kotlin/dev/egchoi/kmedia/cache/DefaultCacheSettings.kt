package dev.egchoi.kmedia.cache

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getBooleanFlow
import com.russhwolf.settings.coroutines.getIntFlow
import kotlinx.coroutines.flow.Flow


@OptIn(ExperimentalSettingsApi::class)
class DefaultCacheSettings(
    private val settings: ObservableSettings
) : CacheSettings {
    override fun getStorageMbSizeFlow(): Flow<Int> = settings.getIntFlow(
        key = CacheSettings.CACHE_STORAGE_SIZE_KEY,
        defaultValue = CacheSettings.DEFAULT_STORAGE_MB
    )

    override fun getEnableCacheFlow(): Flow<Boolean> = settings.getBooleanFlow(
        key = CacheSettings.CACHE_STORAGE_ENABLE_KEY,
        defaultValue = CacheSettings.DEFAULT_CACHE_ENABLE
    )

    override fun getStorageMbSize(): Int = settings.getInt(
        key = CacheSettings.CACHE_STORAGE_SIZE_KEY,
        defaultValue = CacheSettings.DEFAULT_STORAGE_MB
    )

    override fun setStorageMbSize(size: Int) {
        settings.putInt(CacheSettings.CACHE_STORAGE_SIZE_KEY, size)
    }

    override fun getEnableCache(): Boolean = settings.getBoolean(
        key = CacheSettings.CACHE_STORAGE_ENABLE_KEY,
        defaultValue = CacheSettings.DEFAULT_CACHE_ENABLE
    )

    override fun setEnableCache(enable: Boolean) {
        settings.putBoolean(CacheSettings.CACHE_STORAGE_ENABLE_KEY, enable)
    }
}