package dev.egchoi.kmedia.model

import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Music(
    val id: Uuid,
    val title: String,
    val artist: String,
    val releaseDate: LocalDate,
    val dataUrl: String,
    val coverThumbnailUrl: String,
    val coverUrl: String,
    val detailUrl: String,
    val status: MusicStatus = MusicStatus.None,
    val localUri: String? = null,
)

val Music.playingUrl: String
    get() = when {
        status == MusicStatus.Downloaded && localUri != null -> localUri
        else -> dataUrl
    }