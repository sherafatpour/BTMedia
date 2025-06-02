package io.github.moonggae.kmedia.sample.designsystem.platform

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.moonggae.kmedia.sample.designsystem.util.LocalWindowInsetPadding

@Composable
actual fun playerScreenHeight(): Dp = LocalWindowInsetPadding.current.run {
    // Configuration.screenWidthDp and screenHeightDp sizes no longer exclude the system bars since sdk 35.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        LocalConfiguration.current.screenHeightDp.dp
    } else {
        LocalConfiguration.current.screenHeightDp.dp + top + bottom
    }
}