package io.github.moonggae.kmedia.compose

actual abstract class PlatformContext private constructor() {
    companion object {
        val INSTANCE = object : PlatformContext() {}
    }
}