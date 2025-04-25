package com.example.musicplayer.ui.screens

import android.R.attr.duration
import android.R.attr.progress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.data.model.SongCategory
import com.example.musicplayer.ui.components.AddToPlaylistDialog
import com.example.musicplayer.ui.components.CreatePlaylistDialog
import com.example.musicplayer.ui.components.MiniPlayer
import com.example.musicplayer.ui.components.SongItem
import com.example.musicplayer.ui.viewmodel.MusicViewModel
import com.example.musicplayer.ui.viewmodel.PlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    category: SongCategory,
    categoryTitle: String,

    onNavigateUp: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    musicViewModel: MusicViewModel = hiltViewModel(),
    playlistViewModel: PlaylistViewModel = hiltViewModel()
) {
    val songs = when {
        categoryTitle == "Liked Songs" -> musicViewModel.likedSongs.collectAsState().value
        category == SongCategory.WHATSAPP_AUDIO -> musicViewModel.whatsappAudios.collectAsState().value
        category == SongCategory.DOWNLOADED -> musicViewModel.downloadedSongs.collectAsState().value
        category == SongCategory.RECORDED -> musicViewModel.recordedAudios.collectAsState().value
        else -> musicViewModel.songs.collectAsState().value
    }


    val currentSong by musicViewModel.currentSong.collectAsState()
    val isPlaying by musicViewModel.isPlaying.collectAsState()
    val playlists by playlistViewModel.playlists.collectAsState()
    val progress by musicViewModel.progress.collectAsState()
    val duration by musicViewModel.duration.collectAsState()
    val navController = rememberNavController()
    var showPlayerScreen by remember { mutableStateOf(false) }
    var showAddToPlaylistDialog by remember { mutableStateOf(false) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var selectedSong by remember { mutableStateOf<com.example.musicplayer.data.model.Song?>(null) }
    val currentDestination by navController.currentBackStackEntryAsState()
    val currentRoute = currentDestination?.destination?.route


    val isPlayerScreenVisible by remember {
        derivedStateOf {
            navController.currentDestination?.route == "player"
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(categoryTitle) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {

            if (currentRoute != "player") {
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
        }
    ) { padding ->
        if (songs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No $categoryTitle found",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 80.dp) // For mini player
            ) {
                items(songs) { song ->
                    SongItem(
                        song = song,
                        onClick = {
                            musicViewModel.playSong(song, songs)
                            onNavigateToPlayer()
                        },
                        onFavoriteClick = { musicViewModel.toggleFavorite(song) },
                        onAddToPlaylistClick = {
                            selectedSong = song
                            showAddToPlaylistDialog = true
                        }
                    )
                }
            }
        }

        if (showAddToPlaylistDialog && selectedSong != null) {
            AddToPlaylistDialog(
                song = selectedSong!!,
                playlists = playlists,
                onDismiss = { showAddToPlaylistDialog = false },
                onAddToPlaylist = { playlist ->
                    selectedSong?.let { playlistViewModel.addSongToPlaylist(playlist.id, it) }
                    showAddToPlaylistDialog = false
                },
                onCreateNewPlaylist = {
                    showAddToPlaylistDialog = false
                    showCreatePlaylistDialog = true
                }
            )
        }

        if (showPlayerScreen) {
            PlayerScreen(
                onNavigateUp = { showPlayerScreen = false },
                musicViewModel = musicViewModel
            )
        }

        if (showCreatePlaylistDialog && selectedSong != null) {
            CreatePlaylistDialog(
                onDismiss = { showCreatePlaylistDialog = false },
                onCreatePlaylist = { name ->
                    val playlistId = playlistViewModel.createPlaylist(name)
                    selectedSong?.let { playlistViewModel.addSongToPlaylist(playlistId, it) }
                    showCreatePlaylistDialog = false
                }
            )
        }
    }
}
