package io.github.moonggae.kmedia.sample.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import io.github.moonggae.kmedia.sample.designsystem.component.ListItemCardDefaults
import io.github.moonggae.kmedia.sample.designsystem.icon.MusicMinus
import io.github.moonggae.kmedia.sample.designsystem.icon.NcsIcons
import io.github.moonggae.kmedia.sample.model.SampleMusic
import io.github.moonggae.kmedia.sample.ui.component.ReorderableMusicList
import io.github.moonggae.kmedia.sample.ui.util.conditional
import io.github.moonggae.kmedia.sample.ui.util.toggleElement

@Composable
fun PlayerMenuPlaylistTabView(
    modifier: Modifier = Modifier,
    musics: List<SampleMusic>,
    onMusicOrderChanged: (prevIndex: Int, currentIndex: Int) -> Unit,
    currentMusic: SampleMusic?,
    onPlayItem: (Int) -> Unit,
    onDelete: (List<String>) -> Unit,
    nestedScrollConnection: NestedScrollConnection? = null
) {
    var isSelectMode by remember { mutableStateOf(false) }
    var selectedMusicIds by remember(isSelectMode) { mutableStateOf(listOf<String>()) }

    Box(modifier) {
        ReorderableMusicList(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxSize()
                .conditional(nestedScrollConnection != null) {
                    nestedScroll(nestedScrollConnection!!)
                },
            musics = musics,
            selectMusicIds = selectedMusicIds,
            playingMusicId = currentMusic?.id,
            cardStyle = ListItemCardDefaults.listItemCardStyle.medium(),
            itemBackground = MaterialTheme.colorScheme.surfaceContainer,
            selectedItemBackground = MaterialTheme.colorScheme.primary,
            onMusicOrderChanged = onMusicOrderChanged,
            onClick = {
                if (isSelectMode) {
                    musics.getOrNull(it)?.id?.let { musicId ->
                        selectedMusicIds = selectedMusicIds.toggleElement(musicId)
                    }
                } else {
                    onPlayItem(it)
                }
            }
        )

        val floatingButtonModifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 24.dp, end = 24.dp)


        when {
            !isSelectMode -> DeleteModeFloatingButton(
                modifier = floatingButtonModifier,
                onClick = { isSelectMode = true }
            )

            isSelectMode && selectedMusicIds.isEmpty() -> CancelFloatingButton(
                modifier = floatingButtonModifier,
                onClick = { isSelectMode = false }
            )

            isSelectMode && selectedMusicIds.isNotEmpty() -> DeleteFloatingButton(
                modifier = floatingButtonModifier,
                onClick = {
                    onDelete(selectedMusicIds)
                    isSelectMode = false
                }
            )
        }
    }
}

@Composable
fun DeleteModeFloatingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            imageVector = NcsIcons.MusicMinus,
            contentDescription = "Delete musics button",
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun CancelFloatingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(imageVector = NcsIcons.Close, contentDescription = "Cancel button")
    }
}

@Composable
fun DeleteFloatingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.error
    ) {
        Icon(
            imageVector = NcsIcons.Delete,
            contentDescription = "Delete button",
        )
    }
}