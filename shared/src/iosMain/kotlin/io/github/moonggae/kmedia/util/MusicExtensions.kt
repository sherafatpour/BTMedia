package io.github.moonggae.kmedia.util

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreMedia.CMTime
import platform.CoreMedia.CMTimeGetSeconds


@OptIn(ExperimentalForeignApi::class)
fun CValue<CMTime>.toSeconds(): Long = CMTimeGetSeconds(this).toLong()

@OptIn(ExperimentalForeignApi::class)
fun CValue<CMTime>.toMilliSeconds(): Long = (CMTimeGetSeconds(this) * 1000).toLong()