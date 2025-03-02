package io.github.moonggae.kmedia.sample.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import io.github.moonggae.kmedia.sample.ui.component.animation.PlayingAnimation
import io.github.moonggae.kmedia.sample.ui.model.MusicImageStatus
import kmedia.kmedia_sample.generated.resources.Res
import kmedia.kmedia_sample.generated.resources.ncs_cover
import org.jetbrains.compose.resources.painterResource

@Composable
fun AnimationMusicImage(
    modifier: Modifier = Modifier,
    url: String?,
    status: MusicImageStatus = MusicImageStatus.NONE,
    placeholder: Painter = painterResource(Res.drawable.ncs_cover),
) {
    Box(modifier) {
        Image(
            painter = if (url != null) rememberAsyncImagePainter(
                model = url,
                placeholder = placeholder,
                error = placeholder
            ) else placeholder,
            contentDescription = "music cover",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
        )

        when (status) {
            MusicImageStatus.PLAYING -> PlayingAnimation(
                Modifier
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(8.dp)
            )

            MusicImageStatus.NONE -> {}
        }
    }
}