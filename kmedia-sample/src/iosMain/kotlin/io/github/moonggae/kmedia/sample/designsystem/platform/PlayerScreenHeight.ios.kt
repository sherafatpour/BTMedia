package io.github.moonggae.kmedia.sample.designsystem.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import platform.UIKit.UIScreen

@Composable
actual fun playerScreenHeight(): Dp = LocalWindowInfo.current.containerSize.div(UIScreen.mainScreen.scale.toInt()).run {
    height.dp
}