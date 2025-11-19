package com.example.musicplayer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.Maroon30
import com.example.musicplayer.ui.theme.MusicPlayerTheme
import com.example.musicplayer.ui.theme.Pink80
import com.example.musicplayer.ui.viewmodel.MusicViewModel



@Composable
fun MiniPlayer(
    // ... MiniPlayer parameters ...
) {
    // ... MiniPlayer implementation ...
}
@Composable
fun MiniPlayer(
    song: Song,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    progress: Float, // Add progress state
    duration: Long, // Add duration
    onSeek: (Float) -> Unit, // Add seek callback
    musicViewModel: MusicViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onExpand: () -> Unit,

)

{
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onExpand),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Maroon30// This sets the background to black
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album art
            AsyncImage(
                model = song.albumArtUri ?: "https://via.placeholder.com/48",
                contentDescription = song.title,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Song info and progress
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Song title and artist
                Row(
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Column(
                        modifier = Modifier.weight(2f).padding(7.dp)
                    ) {
                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Progress slider
                Slider(
                    value = progress,
                    onValueChange = onSeek,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = Black,
                        activeTrackColor = Black,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f)
                    )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Play/Pause button
            IconButton(
                onClick = onPlayPause,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
        }
    }
}

