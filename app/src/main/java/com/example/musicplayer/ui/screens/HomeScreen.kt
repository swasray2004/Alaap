package com.example.musicplayer.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material3.Card


import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings

import androidx.compose.material3.AlertDialog

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.musicplayer.data.model.Playlist
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.ui.components.AddToPlaylistDialog
import com.example.musicplayer.ui.components.BottomNavBar
import com.example.musicplayer.ui.components.CreatePlaylistDialog
import com.example.musicplayer.ui.components.MiniPlayer
import com.example.musicplayer.ui.components.SongItem
import com.example.musicplayer.ui.viewmodel.MusicViewModel
import com.example.musicplayer.ui.viewmodel.PlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToPlayer: () -> Unit,
    onNavigateToPlaylist: (Long) -> Unit,
    musicViewModel: MusicViewModel = hiltViewModel(),
    playlistViewModel: PlaylistViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val songs by musicViewModel.songs.collectAsState()
    val whatsappAudios by musicViewModel.whatsappAudios.collectAsState()
    val downloadedSongs by musicViewModel.downloadedSongs.collectAsState()
    val recordedAudios by musicViewModel.recordedAudios.collectAsState()
    val likedSongs by musicViewModel.likedSongs.collectAsState()
    val currentSong by musicViewModel.currentSong.collectAsState()
    val isPlaying by musicViewModel.isPlaying.collectAsState()
    val playlists by playlistViewModel.playlists.collectAsState()
    val searchQuery by musicViewModel.searchQuery.collectAsState()
    val currentDestination by navController.currentBackStackEntryAsState()
    val currentRoute = currentDestination?.destination?.route

    val isPlayerScreenVisible by remember {
        derivedStateOf {
            navController.currentDestination?.route == "player"
        }
    }
    var showPlayerScreen by remember { mutableStateOf(false) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var showAddToPlaylistDialog by remember { mutableStateOf(false) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Music Player") }
            )
        },
        bottomBar = {

                Column {
                    if (currentSong != null)
                        MiniPlayer(
                            song = currentSong!!,
                            isPlaying = isPlaying,
                            onPlayPause = { musicViewModel.togglePlayPause() },
                            onExpand = { showPlayerScreen = true },
                            modifier = Modifier.fillMaxWidth(),

                            )

                    BottomNavBar(navController = navController)
                }

        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreatePlaylistDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Create Playlist")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { musicViewModel.searchSongs(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search songs...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp) // Add padding for mini player and bottom nav
            ) {
                // Playlists Section
                item {
                    Text(
                        text = "Your Playlists",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(playlists) { playlist ->
                            PlaylistItem(
                                playlist = playlist,
                                onClick = { onNavigateToPlaylist(playlist.id) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                }

                // Liked Songs Section
                if (likedSongs.isNotEmpty()) {
                    item {
                        CategoryHeader(
                            title = "Liked Songs",
                            icon = Icons.Default.Favorite,
                            iconTint = Color.Red
                        )
                    }

                    items(likedSongs.take(5)) { song ->
                        SongItem(
                            song = song,
                            onClick = {
                                musicViewModel.playSong(song, likedSongs)
                                onNavigateToPlayer()
                            },
                            onFavoriteClick = { musicViewModel.toggleFavorite(song) },
                            onAddToPlaylistClick = {
                                selectedSong = song
                                showAddToPlaylistDialog = true
                            },
                            showAddToPlaylistOption = true
                        )
                    }

                    if (likedSongs.size > 5) {
                        item {
                            TextButton(
                                onClick = { navController.navigate("category/liked") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text("View All Liked Songs (${likedSongs.size})")
                            }
                        }
                    }

                    item {
                        Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    }
                }

                // WhatsApp Audios Section

                    item {
                        CategoryHeader(
                            title = "WhatsApp Audios",
                            icon = Icons.Default.Whatsapp,
                            iconTint = Color(0xFF25D366) // WhatsApp green
                        )
                    }

                    items(whatsappAudios.take(5)) { song ->
                        SongItem(
                            song = song,
                            onClick = {
                                musicViewModel.playSong(song, whatsappAudios)
                                onNavigateToPlayer()
                            },
                            onFavoriteClick = { musicViewModel.toggleFavorite(song) },
                            onAddToPlaylistClick = {
                                selectedSong = song
                                showAddToPlaylistDialog = true
                            },
                            showAddToPlaylistOption = true
                        )
                    }

                    if (whatsappAudios.size > 5) {
                        item {
                            TextButton(
                                onClick = { navController.navigate("category/whatsapp") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text("View All WhatsApp Audios (${whatsappAudios.size})")
                            }
                        }
                }
                item {
                    Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }

                // Downloaded Music Section
                if (downloadedSongs.isNotEmpty()) {
                    item {
                        CategoryHeader(
                            title = "Downloaded Music",
                            icon = Icons.Default.Download,
                            iconTint = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(downloadedSongs.take(5)) { song ->
                        SongItem(
                            song = song,
                            onClick = {
                                musicViewModel.playSong(song, downloadedSongs)
                                onNavigateToPlayer()
                            },
                            onFavoriteClick = { musicViewModel.toggleFavorite(song) },
                            onAddToPlaylistClick = {
                                selectedSong = song
                                showAddToPlaylistDialog = true
                            },
                            showAddToPlaylistOption = true
                        )
                    }

                    if (downloadedSongs.size > 5) {
                        item {
                            TextButton(
                                onClick = { navController.navigate("category/downloaded") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text("View All Downloaded Songs (${downloadedSongs.size})")
                            }
                        }
                    }

                    item {
                        Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    }
                }

                // Recorded Audios Section
                if (recordedAudios.isNotEmpty()) {
                    item {
                        CategoryHeader(
                            title = "Recorded Audios",
                            icon = Icons.Default.Mic,
                            iconTint = Color(0xFFE91E63) // Pink
                        )
                    }

                    items(recordedAudios.take(5)) { song ->
                        SongItem(
                            song = song,
                            onClick = {
                                musicViewModel.playSong(song, recordedAudios)
                                onNavigateToPlayer()
                            },
                            onFavoriteClick = { musicViewModel.toggleFavorite(song) },
                            onAddToPlaylistClick = {
                                selectedSong = song
                                showAddToPlaylistDialog = true
                            },
                            showAddToPlaylistOption = true
                        )
                    }

                    if (recordedAudios.size > 5) {
                        item {
                            TextButton(
                                onClick = { navController.navigate("category/recorded") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text("View All Recorded Audios (${recordedAudios.size})")
                            }
                        }
                    }

                    item {
                        Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    }
                }

                // All Songs Section
                item {
                    CategoryHeader(
                        title = "All Songs",
                        icon = Icons.Default.PlayArrow,
                        iconTint = MaterialTheme.colorScheme.primary
                    )
                }

                items(songs) { song ->
                    SongItem(
                        song = song,
                        onClick = {
                            musicViewModel.playSong(song)
                            onNavigateToPlayer()
                        },
                        onFavoriteClick = { musicViewModel.toggleFavorite(song) },
                        onAddToPlaylistClick = {
                            selectedSong = song
                            showAddToPlaylistDialog = true
                        },
                        showAddToPlaylistOption = true
                    )
                }
            }
        }

        if (showPlayerScreen) {
            PlayerScreen(
                onNavigateUp = { showPlayerScreen = false },
                musicViewModel = musicViewModel
            )

        }

        if (showCreatePlaylistDialog) {
            CreatePlaylistDialog(
                onDismiss = { showCreatePlaylistDialog = false },
                onCreatePlaylist = { name ->
                    playlistViewModel.createPlaylist(name)
                }
            )
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
    }
}

@Composable
fun CategoryHeader(
    title: String,
    icon: ImageVector,
    iconTint: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .clickable(onClick = onClick)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = playlist.coverArtUri ?: "https://via.placeholder.com/160",
                    contentDescription = playlist.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = onClick,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

