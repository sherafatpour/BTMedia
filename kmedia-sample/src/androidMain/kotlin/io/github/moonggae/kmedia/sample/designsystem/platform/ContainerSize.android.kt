package io.github.moonggae.kmedia.sample.designsystem.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntSize

@Composable
actual fun getContainerSize(): IntSize =
    IntSize(
        width = LocalConfiguration.current.screenWidthDp,
        height = LocalConfiguration.current.screenHeightDp
    )