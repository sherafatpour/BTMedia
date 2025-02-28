package dev.egchoi.kmedia.sample

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.egchoi.kmedia.KMedia

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    kMedia: KMedia
) {
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("KMedia Sample") })
            }
        ) { paddingValues ->
            MusicPlayerScreen(
                modifier = Modifier.padding(paddingValues),
                kMedia = kMedia
            )
        }
    }
}