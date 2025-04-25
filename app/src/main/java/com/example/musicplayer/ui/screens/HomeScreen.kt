package com.example.musicplayer.ui.screens

import PlaylistItem
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.*
import com.example.musicplayer.ui.theme.Maroon30
import com.example.musicplayer.ui.viewmodel.AuthViewModel
import com.example.musicplayer.ui.viewmodel.MusicViewModel
import com.example.musicplayer.ui.viewmodel.PlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNavigateToPlayer: () -> Unit,
    onNavigateToPlaylist: (Long) -> Unit,
    onNavigateToProfile: () -> Unit,
    musicViewModel: MusicViewModel = hiltViewModel(),
    playlistViewModel: PlaylistViewModel = hiltViewModel(),
    viewModel: AuthViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val likedSongs by musicViewModel.likedSongs.collectAsState()
    val currentSong by musicViewModel.currentSong.collectAsState()
    val isPlaying by musicViewModel.isPlaying.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val playlists by playlistViewModel.playlists.collectAsState()
    val searchQuery by musicViewModel.searchQuery.collectAsState()
    val progress by musicViewModel.progress.collectAsState()
    val duration by musicViewModel.duration.collectAsState()

    val isPlayerScreenVisible by remember {
        derivedStateOf {
            navController.currentDestination?.route == "player"
        }
    }

    var showPlayerScreen by remember { mutableStateOf(false) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }

    val scrollState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alaap") },
                colors = TopAppBarDefaults.topAppBarColors(Maroon30),
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            tint=Color.White,
                            contentDescription = "Profile"
                        )
                    }
                }
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
                        progress = progress, // Pass progress
                        duration = duration, // Pass duration
                        onSeek = { musicViewModel.seekTo((it * duration).toLong()) }, // Handle seek
                        modifier = Modifier.fillMaxWidth()
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
        Box(modifier = Modifier.fillMaxSize()) {

            // 🌄 Background Image
            Image(
                painter = painterResource(id = R.drawable.home),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                state = scrollState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    // Greeting with user's name
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                        Text(
                            text = "Hello, ${currentUser?.displayName ?: "User"}!",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "What would you like to listen to today?",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                            )
                        )
                    }
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
                }

                if (likedSongs.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxWidth()
                                .clickable { navController.navigate("category/liked") }
                                .animateContentSize(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3F3)),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Liked Songs",
                                    tint = Color.Red,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "Liked Songs",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        "${likedSongs.size} songs",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CategoryCard("WhatsApp Audios", Icons.Default.Whatsapp, Color(0xFF25D366)) {
                                navController.navigate("category/whatsapp")
                            }
                            CategoryCard("Downloaded Music", Icons.Default.Download, Color(0xFF3F51B5)) {
                                navController.navigate("category/downloaded")
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CategoryCard("Recorded Audios", Icons.Default.Mic, Color(0xFFE91E63)) {
                                navController.navigate("category/recorded")
                            }
                            CategoryCard("All Songs", Icons.Default.PlayArrow, Color(0xFF9C27B0)) {
                                navController.navigate("category/all")
                            }
                        }
                    }
                }


                item {
                    Text(
                        text = "Your Playlists",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(playlists) { _, playlist ->
                            PlaylistItem(
                                playlist = playlist,
                                onClick = { onNavigateToPlaylist(playlist.id) },
                                modifier = Modifier.animateItemPlacement()
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
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
                    onCreatePlaylist = { name -> playlistViewModel.createPlaylist(name) }
                )
            }
        }
    }
}






