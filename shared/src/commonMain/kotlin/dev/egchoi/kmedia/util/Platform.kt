package dev.egchoi.kmedia.util

internal enum class Platform {
    Android, iOS
}

internal expect fun getPlatform(): Platform