package io.github.moonggae.kmedia.compose

import androidx.compose.runtime.staticCompositionLocalOf

actual val LocalPlatformContext = staticCompositionLocalOf { PlatformContext.INSTANCE }