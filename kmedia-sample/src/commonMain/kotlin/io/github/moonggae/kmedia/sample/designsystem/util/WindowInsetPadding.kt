package io.github.moonggae.kmedia.sample.designsystem.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LocalWindowInsetPadding = compositionLocalOf {
    WindowInsetPadding(0.dp, 0.dp)
}

data class WindowInsetPadding(
    val top: Dp,
    val bottom: Dp
)

@Composable
fun rememberWindowInsetPadding(): WindowInsetPadding {
    val density = LocalDensity.current

    val topPadding = with(density) { WindowInsets.safeDrawing.getTop(density).toDp() }
    val bottomPadding = with(density) { WindowInsets.safeDrawing.getBottom(density).toDp() }

    return remember(topPadding, bottomPadding) {
        WindowInsetPadding(
            top = topPadding,
            bottom = bottomPadding
        )
    }
}