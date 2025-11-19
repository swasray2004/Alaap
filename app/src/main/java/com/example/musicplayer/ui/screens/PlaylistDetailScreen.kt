package com.example.musicplayer.ui.screens

import android.R.attr.duration
import android.R.attr.progress
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.ui.components.MiniPlayer
import com.example.musicplayer.ui.viewmodel.MusicViewModel
import com.example.musicplayer.ui.viewmodel.PlaylistViewModel
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.ui.res.painterResource
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.AddSongsToPlaylistDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlistId: Long,
    onNavigateUp: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    playlistViewModel: PlaylistViewModel = hiltViewModel(),
    musicViewModel: MusicViewModel = hiltViewModel()
) {
    val playlists by playlistViewModel.playlists.collectAsState()
    val playlist = playlists.find { it.id == playlistId }
    val playlistSongs by playlistViewModel.currentPlaylistSongs.collectAsState()
    val currentSong by musicViewModel.currentSong.collectAsState()
    val isPlaying by musicViewModel.isPlaying.collectAsState()
    val progress by musicViewModel.progress.collectAsState()
    val duration by musicViewModel.duration.collectAsState()
    var showPlayerScreen by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddSongsDialog by remember { mutableStateOf(false) }

    // This LaunchedEffect ensures the playlist songs are loaded whenever the screen is shown or playlistId changes
    LaunchedEffect(playlistId) {
        playlist?.let { playlistViewModel.loadPlaylistSongs(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(playlist?.name ?: "Playlist") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddSongsDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Songs")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Playlist")
                    }
                }
            )
        },
        bottomBar = {
            if (currentSong != null) {
                MiniPlayer(
                    song = currentSong!!,
                    isPlaying = isPlaying,
                    onPlayPause = { musicViewModel.togglePlayPause() },
                    onExpand = { showPlayerScreen = true },
                    progress = progress, // Pass progress
                    duration = duration, // Pass duration
                    onSeek = { musicViewModel.seekTo((it * duration).toLong()) }, // Handle seek
                    modifier = Modifier.fillMaxWidth(),
                    musicViewModel = musicViewModel,
                )
            }
        }
    ) { padding ->
        if (playlistSongs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                // 🌄 Background Image
                Image(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No songs in this playlist")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showAddSongsDialog = true }) {
                        Text("Add Songs")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 80.dp) // For mini player
            ) {
                items(playlistSongs) { song ->
                    PlaylistSongItem(
                        song = song,
                        onClick = {
                            musicViewModel.playSong(song)
                            onNavigateToPlayer()
                        },
                        onRemove = {
                            playlist?.let { playlistViewModel.removeSongFromPlaylist(it.id, song) }
                        }
                    )
                }
            }
        }

        if (showDeleteDialog && playlist != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Playlist") },
                text = { Text("Are you sure you want to delete '${playlist.name}'?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            playlistViewModel.deletePlaylist(playlist)
                            showDeleteDialog = false
                            onNavigateUp()
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showAddSongsDialog) {
            // Dialog to browse and add songs to this playlist
            AddSongsToPlaylistDialog(
                playlist = playlist,
                allSongs = musicViewModel.songs.collectAsState().value,
                onDismiss = { showAddSongsDialog = false },
                onAddSong = { song ->
                    playlist?.let { playlistViewModel.addSongToPlaylist(it.id, song) }
                }
            )
        }
    }
}

@Composable
fun PlaylistSongItem(
    song: Song,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = song.albumArtUri ?: "https://via.placeholder.com/48",
            contentDescription = song.title,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(onClick = onClick) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Play")
        }

        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remove from playlist")
        }
    }
}
