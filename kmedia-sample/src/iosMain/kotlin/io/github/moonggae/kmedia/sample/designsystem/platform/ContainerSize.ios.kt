package io.github.moonggae.kmedia.sample.designsystem.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntSize
import platform.UIKit.UIScreen

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getContainerSize(): IntSize = LocalWindowInfo.current.containerSize.div(UIScreen.mainScreen.scale.toInt())