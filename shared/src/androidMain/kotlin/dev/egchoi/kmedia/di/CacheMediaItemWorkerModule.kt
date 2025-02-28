package dev.egchoi.kmedia.di

import dev.egchoi.kmedia.cache.CacheMediaItemWorker
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val cacheMediaItemWorkerModule = module {
    worker { params ->
        CacheMediaItemWorker(
            cacheManager = get(),
            cacheStatusListener = get(),
            applicationContext = get(),
            workerParameters = params.get()
        )
    }
}