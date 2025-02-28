package dev.egchoi.kmedia.util

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController

internal fun MediaController.getMediaItemIndex(mediaItem: MediaItem):Int? {
    repeat(this.mediaItemCount) { index ->
        val currentItem = this.getMediaItemAt(index)
        if (currentItem.mediaId == mediaItem.mediaId) return index
    }
    return null
}

internal val Player.mediaItems: List<MediaItem>
    get() {
        val items = mutableListOf<MediaItem>()
        for (i in 0 until mediaItemCount) {
            try {
                items.add(this.getMediaItemAt(i))
            } finally { }
        }

        return items
    }