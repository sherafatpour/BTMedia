package io.github.moonggae.kmedia.sample.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import io.github.moonggae.kmedia.sample.designsystem.util.LocalWindowInsetPadding
import io.github.moonggae.kmedia.sample.designsystem.util.rememberWindowInsetPadding

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = White,
    secondary = PurpleGrey80,
    tertiary = Pink80,

    surface = DarkBlue90,
    onSurface = LightBlue10,
    onSurfaceVariant = Grey50,
    surfaceContainer = DarkBlue80,
    surfaceContainerHigh = DarkBlue70,

    background = DarkBlue90,
    onBackground = LightBlue10,
    outline = Grey50,
    error = CoralRed
)

@Composable
fun NcsTheme(
    content: @Composable () -> Unit
) {
    PlatformStatusBar(true)

    val windowInsetPadding = rememberWindowInsetPadding()
    CompositionLocalProvider(LocalWindowInsetPadding provides windowInsetPadding) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            content = content
        )
    }
}