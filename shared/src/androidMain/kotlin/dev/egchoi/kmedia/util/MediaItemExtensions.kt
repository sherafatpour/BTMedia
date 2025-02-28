package dev.egchoi.kmedia.util

import android.net.Uri
import androidx.annotation.OptIn
import androidx.core.net.toFile
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import dev.egchoi.kmedia.model.Music
import dev.egchoi.kmedia.model.MusicStatus

@OptIn(UnstableApi::class)
internal fun Music.asMediaItem(): MediaItem = MediaItem.Builder()
    .setUri(if (status == MusicStatus.Downloaded) localUri else dataUrl)
    .setCustomCacheKey(id.toString())
    .setMimeType(MimeTypes.AUDIO_MPEG)
    .setMediaId(id.toString())
    .setMediaMetadata(
        MediaMetadata
            .Builder()
            .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
            .setArtist(artist)
            .setTitle(title)
            .setArtworkUri(Uri.parse(coverUrl))
            .setIsPlayable(true)
            .setIsBrowsable(true)
            .build()
    ).build()

val MediaItem.isNetworkSource: Boolean
    get() {
        val uriScheme = this.localConfiguration?.uri?.scheme?.lowercase()
        return uriScheme == "http" || uriScheme == "https"
    }

val MediaItem.isLocalFileExists: Boolean
    get() = try {
        localConfiguration?.uri?.toFile()?.exists() ?: false
    } catch (e: IllegalArgumentException) {
        false
    }