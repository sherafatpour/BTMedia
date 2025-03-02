package io.github.moonggae.kmedia.sample.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle

@Composable
actual fun PlatformStatusBar(isDark: Boolean) {
    DisposableEffect(isDark) {
        val style = if (isDark) {
            UIStatusBarStyleLightContent
        } else {
            UIStatusBarStyleDarkContent
        }
        UIApplication.sharedApplication.setStatusBarStyle(style, animated = true)
        onDispose {}
    }
}