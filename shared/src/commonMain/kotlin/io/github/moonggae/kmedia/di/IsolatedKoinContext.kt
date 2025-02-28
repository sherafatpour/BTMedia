package io.github.moonggae.kmedia.di

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.koinApplication

internal object IsolatedKoinContext {
    private lateinit var koinApp: KoinApplication

    fun init(module: Module) {
        koinApp = koinApplication {
            modules(playbackModule, module)
        }
    }

    val koin: Koin by lazy { koinApp.koin }
}