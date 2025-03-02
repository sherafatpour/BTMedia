# KMedia - KMP Music Player Library
[![Maven Central](https://img.shields.io/maven-central/v/io.github.moonggae/kmedia)](https://mvnrepository.com/artifact/io.github.moonggae/kmedia)


KMedia is a cross-platform music player library built with Kotlin Multiplatform (KMP). It provides a consistent API for music playback functionality across both Android and iOS.

## Key Features

- Supports Kotlin Multiplatform (Android, iOS)
- Consistent music playback experience with a unified API
- Media caching support
- Playlist management (add, remove, reorder)
- Shuffle and repeat mode support
- Playback state and position monitoring
- Background playback support
- Control Center (iOS) and Media Notification (Android) integration

## Setup

### Gradle Setup

For Kotlin Multiplatform, add the dependency below to your module's `build.gradle.kts` file:
```kotlin
sourceSets {
    commonMain.dependencies {
        implementation("io.github.moonggae:kmedia:$kmedia_version")
    }
}
```

### Android Setup

Initialize KMedia in your Android app's `MainActivity.kt`:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize KMedia
        val media = KMedia.builder()
            .cache(enabled = true, sizeInMb = 1024)
            .build(applicationContext)
        
        setContent {
            App(media)
        }
    }
}
```

Add the following permissions and service to your `AndroidManifest.xml`:

```xml
<!-- Permissions -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />

<!-- Service registration -->
<service android:name="io.github.moonggae.kmedia.session.PlaybackService"
    android:exported="false"
    android:foregroundServiceType="mediaPlayback">
    <intent-filter>
        <action android:name="androidx.media3.session.MediaSessionService" />
    </intent-filter>
</service>
```

### iOS Setup

Initialize KMedia in your iOS app's `MainViewController.kt`:

```kotlin
fun MainViewController() = ComposeUIViewController {
    val media = KMedia.Builder()
        .cache(enabled = true, sizeInMb = 1024)
        .build()

    App(media)
}
```

Add the following to your `Info.plist`:

```xml
<key>BGTaskSchedulerPermittedIdentifiers</key>
<array>
    <string>com.ccc.ncs.assetDownloadSession</string>
</array>
<key>UIBackgroundModes</key>
<array>
    <string>audio</string>
    <string>processing</string>
</array>
```

## Usage

### Basic Usage

KMedia can be used as follows:

```kotlin
// Create a KMedia instance
val media = KMedia.Builder()
    .cache(enabled = true, sizeInMb = 1024)
    .build(context)

// Get music list
val musics = SampleMusicRepository().getSampleMusicList()

// Start playback
media.player.playMusics(musics, startIndex = 0)

// Play/Pause
media.player.play()
media.player.pause()

// Previous/Next track
media.player.previous()
media.player.next()

// Seek to position
media.player.seekTo(positionMs = 30000)
```

### Monitoring Playback State

You can monitor the playback state through Flow:

```kotlin
val playbackState by media.playbackState.collectAsState()

// Check current playback status
when (playbackState.playingStatus) {
    PlayingStatus.PLAYING -> // Playing
    PlayingStatus.PAUSED -> // Paused
    PlayingStatus.BUFFERING -> // Buffering
    PlayingStatus.IDLE -> // Not ready
    PlayingStatus.ENDED -> // Playback completed
}

// Current position and duration
val position = playbackState.position
val duration = playbackState.duration
```

### Repeat and Shuffle Modes

```kotlin
// Set repeat mode
media.player.setRepeatMode(RepeatMode.REPEAT_MODE_ONE) // Repeat one
media.player.setRepeatMode(RepeatMode.REPEAT_MODE_ALL) // Repeat all
media.player.setRepeatMode(RepeatMode.REPEAT_MODE_OFF) // No repeat

// Set shuffle mode
media.player.setShuffleMode(true) // Enable shuffle
media.player.setShuffleMode(false) // Disable shuffle
```

### Cache Management

```kotlin
// Check cache usage
val cacheUsage by media.cache.usedSizeBytes.collectAsState(initial = 0L)

// Cache specific music
media.cache.preCacheMusic(url = "https://example.com/music.mp3", key = "music1")

// Check cache status
val isCached = media.cache.checkMusicCached("music1")

// Remove cache
media.cache.removeCachedMusic("music1")

// Clear all cache
media.cache.clearCache()
```

## UI Implementation

For music player UI implementation examples using Compose Multiplatform, please refer to the sample code.

## Additional Features

### CacheStatusListener

An interface for monitoring cache status changes. You can implement this to track caching progress.

```kotlin
interface CacheStatusListener {
    fun onCacheStatusChanged(musicId: String, status: CacheStatus)

    enum class CacheStatus {
        NONE,               // Not cached
        PARTIALLY_CACHED,   // Partially cached
        FULLY_CACHED        // Fully cached
    }
}
```

Usage example:

```kotlin
val cacheStatusListener = object : CacheStatusListener {
    override fun onCacheStatusChanged(musicId: String, status: CacheStatus) {
        when (status) {
            CacheStatus.NONE -> println("$musicId: No cache")
            CacheStatus.PARTIALLY_CACHED -> println("$musicId: Caching in progress")
            CacheStatus.FULLY_CACHED -> println("$musicId: Caching completed")
        }
    }
}

// Register listener when initializing KMedia
val media = KMedia.Builder()
    .cache(enabled = true, sizeInMb = 1024, listener = cacheStatusListener)
    .build(context)
```

### PlaybackAnalyticsListener

An interface for collecting playback analytics. You can track user playback patterns and statistics.

```kotlin
interface PlaybackAnalyticsListener {
    fun onPlaybackCompleted(
        musicId: String,
        totalPlayTimeMs: Long,
        duration: Long
    )
}
```

Usage example:

```kotlin
val analyticsListener = object : PlaybackAnalyticsListener {
    override fun onPlaybackCompleted(musicId: String, totalPlayTimeMs: Long, duration: Long) {
        // Process playback statistics
        val playPercentage = (totalPlayTimeMs.toFloat() / duration.toFloat()) * 100
        println("$musicId playback completed: $playPercentage% played ($totalPlayTimeMs ms / $duration ms)")
        
        // You can send analytics data to server here
    }
}

// Register listener when initializing KMedia
val media = KMedia.Builder()
    .analytics(analyticsListener)
    .build(context)
```