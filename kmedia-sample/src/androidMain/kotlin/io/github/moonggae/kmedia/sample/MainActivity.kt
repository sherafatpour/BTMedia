package io.github.moonggae.kmedia.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.moonggae.kmedia.KMedia

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