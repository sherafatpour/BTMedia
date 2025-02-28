package dev.egchoi.kmedia.util

import kotlin.uuid.Uuid

fun Uuid.Companion.tryParse(uuidString: String): Uuid? = try {
    parse(uuidString)
} catch (_: Exception) {
    null
}