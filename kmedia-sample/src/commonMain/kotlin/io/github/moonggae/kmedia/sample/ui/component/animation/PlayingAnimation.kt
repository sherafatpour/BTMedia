package io.github.moonggae.kmedia.sample.ui.component.animation

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.ExperimentalCompottieApi
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.dynamic.rememberLottieDynamicProperties
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kmedia.kmedia_sample.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalCompottieApi::class, ExperimentalResourceApi::class)
@Composable
fun PlayingAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/lottie_playing_music.json").decodeToString()
        )
    }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = Compottie.IterateForever
    )

    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    val dynamicProperty = rememberLottieDynamicProperties {
        shapeLayer("**") {
            stroke("**") {
                colorFilter {
                    BlendModeColorFilter(
                        color = onSurfaceColor,
                        blendMode = BlendMode.SrcAtop
                    )
                }
            }
        }
    }

    Image(
        painter = rememberLottiePainter(
            composition = composition,
            progress = { progress },
            dynamicProperties = dynamicProperty
        ),
        contentDescription = "playing music animation",
        modifier = modifier
    )
}