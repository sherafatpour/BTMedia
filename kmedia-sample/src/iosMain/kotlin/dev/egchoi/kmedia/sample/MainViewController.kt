package dev.egchoi.kmedia.sample

import androidx.compose.ui.window.ComposeUIViewController
import dev.egchoi.kmedia.KMedia

fun MainViewController() = ComposeUIViewController {
    val media = KMedia.Builder()
        .cache(enabled = true, sizeInMb = 1024)
        .build(0)

    App(media)
}