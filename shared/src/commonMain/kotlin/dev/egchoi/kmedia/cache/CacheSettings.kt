package dev.egchoi.kmedia.cache

import kotlinx.coroutines.flow.Flow

interface CacheSettings {
    fun getStorageMbSizeFlow(): Flow<Int>
    fun getEnableCacheFlow(): Flow<Boolean>
    fun getStorageMbSize(): Int
    fun setStorageMbSize(size: Int)
    fun getEnableCache(): Boolean
    fun setEnableCache(enable: Boolean)

    companion object {
        internal const val PREFERENCES_NAME = "cache_data"
        internal const val CACHE_STORAGE_SIZE_KEY = "CACHE_STORAGE_SIZE_KEY"
        internal const val DEFAULT_STORAGE_MB = 1024
        internal const val CACHE_STORAGE_ENABLE_KEY = "CACHE_STORAGE_ENABLE_KEY"
        internal const val DEFAULT_CACHE_ENABLE = true
    }
}