package io.github.moonggae.kmedia.sample.ui.component

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.moonggae.kmedia.sample.designsystem.component.ListItemCard
import io.github.moonggae.kmedia.sample.designsystem.component.ListItemCardDefaults
import io.github.moonggae.kmedia.sample.designsystem.component.ListItemCardStyle
import io.github.moonggae.kmedia.sample.model.SampleMusic
import io.github.moonggae.kmedia.sample.ui.model.MusicImageStatus
import kmedia.kmedia_sample.generated.resources.Res
import kmedia.kmedia_sample.generated.resources.ncs_cover
import org.jetbrains.compose.resources.painterResource


@Composable
fun MusicCard(
    modifier: Modifier = Modifier,
    item: SampleMusic,
    selected: Boolean = false,
    isPlaying: Boolean = false,
    style: ListItemCardStyle = ListItemCardDefaults.listItemCardStyle.medium(),
    unSelectedBackgroundColor: Color = Color.Transparent,
    titlePrefix: (@Composable () -> Unit)? = null,
    onClick: ((String) -> Unit)? = null,
    onLongClick: ((String) -> Unit)? = null,
    suffix: @Composable () -> Unit = {}
) {
    val imageStatus = remember(isPlaying, item.cacheStatus) {
        when {
            isPlaying -> MusicImageStatus.PLAYING
            else -> MusicImageStatus.NONE
        }
    }

    ListItemCard(
        prefix = {
            AnimationMusicImage(
                url = item.coverUrl,
                placeholder = painterResource(Res.drawable.ncs_cover),
                status = imageStatus,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(4.dp))
            )
        },
        labelPrefix = titlePrefix,
        label = item.title,
        description = item.artist,
        suffix = suffix,
        style = style,
        color = ListItemCardDefaults.listItemCardColors(
            backgroundColor = if (selected) MaterialTheme.colorScheme.primary else unSelectedBackgroundColor,
            labelColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            descriptionColor = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            moreIconColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onClick?.invoke(item.id) },
                onLongClick = { onLongClick?.invoke(item.id) }
            )
    )
}