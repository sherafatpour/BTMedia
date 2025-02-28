package dev.egchoi.kmedia.analytics

import com.ccc.ncs.analytics.AnalyticsEvent
import com.ccc.ncs.analytics.AnalyticsHelper
import dev.egchoi.kmedia.analytics.ParamKeys.DURATION
import dev.egchoi.kmedia.analytics.ParamKeys.MUSIC_ID
import dev.egchoi.kmedia.analytics.ParamKeys.PLAY_TIME
import dev.egchoi.kmedia.analytics.Types.PLAYBACK_TIME

private object Types {
    const val PLAYBACK_TIME = "playback_time"
}

private object ParamKeys {
    const val MUSIC_ID = "music_id"
    const val PLAY_TIME = "play_time"
    const val DURATION = "duration"
}

internal fun AnalyticsHelper.logPlaybackTime(
    musicId: String, totalPlayTimeMs: Long, duration: Long
) {
    logEvent(
        AnalyticsEvent(
            type = PLAYBACK_TIME,
            extras = listOf(
                AnalyticsEvent.Param(
                    key = MUSIC_ID,
                    value = musicId
                ),
                AnalyticsEvent.Param(
                    key = PLAY_TIME,
                    value = "$totalPlayTimeMs"
                ),
                AnalyticsEvent.Param(
                    key = DURATION,
                    value = "$duration"
                )
            )
        )
    )
}