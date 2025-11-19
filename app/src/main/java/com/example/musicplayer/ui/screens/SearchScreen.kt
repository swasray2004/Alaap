package com.example.musicplayer.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.BottomNavBar
import com.example.musicplayer.ui.components.SongItem
import com.example.musicplayer.ui.theme.Maroon30
import com.example.musicplayer.ui.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateUp: () -> Unit,
    musicViewModel: MusicViewModel,
    navController: NavHostController = rememberNavController()
) {
    val searchQuery by musicViewModel.searchQuery.collectAsState()
    val searchResults by musicViewModel.searchResults.collectAsState()
    val currentSong by musicViewModel.currentSong.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Songs") },
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
    ) { padding ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
            // 🌄 Background image
            Image(
                painter = painterResource(id = R.drawable.home),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
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
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searchResults) { song ->
                        SongItem(
                            song = song,
                            onClick = { musicViewModel.playSong(song) },
                            onFavoriteClick = { musicViewModel.toggleFavorite(song) },
                            showAddToPlaylistOption = false,
                            onAddToPlaylistClick = {}
                        )
                    }
                }
            }
        }
    }
}
