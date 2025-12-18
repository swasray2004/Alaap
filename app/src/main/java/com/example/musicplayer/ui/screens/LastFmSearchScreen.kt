package com.example.musicplayer.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.BottomNavBar
import com.example.musicplayer.ui.theme.Black
import com.example.musicplayer.ui.theme.Maroon30
import com.example.musicplayer.ui.viewmodel.MusicViewModel
import com.example.musicplayer.viewmodel.LastFmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LastFmSearchScreen(
    onNavigateUp: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    musicViewModel: MusicViewModel = viewModel(),
    lastFmViewModel: LastFmViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val tracks by lastFmViewModel.tracks.collectAsState()
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Tracks from Last.fm") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Maroon30),
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Image(
                painter = painterResource(id = R.drawable.lastfm),
                contentDescription = "Last.fm Background",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        lastFmViewModel.search(query)
                    },
                    placeholder = { Text("Search for a track") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (tracks.isEmpty()) {
                    Text(
                        text = "No tracks found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(tracks) { track ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { /* Add playback or navigation */
                                        // 1. Set the current song in your music viewmodel


                                        // 2. Start playback
                                        musicViewModel.play()

                                        // 3. Navigate to player screen
                                        onNavigateToPlayer()
                                    },
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(containerColor = Black)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(track.imageUrl),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .padding(end = 12.dp),
                                        contentScale = ContentScale.Crop
                                    )

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = track.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "by ${track.artist}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
