package io.github.moonggae.kmedia.sample

import androidx.compose.ui.window.ComposeUIViewController
import io.github.moonggae.kmedia.KMedia

fun MainViewController() = ComposeUIViewController {
    val media = KMedia.Builder()
        .cache(enabled = true, sizeInMb = 1024)
        .build()

    App(media)
}