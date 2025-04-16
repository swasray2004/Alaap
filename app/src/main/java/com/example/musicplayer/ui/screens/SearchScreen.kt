package com.example.musicplayer.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musicplayer.ui.components.SongItem
import com.example.musicplayer.ui.viewmodel.MusicViewModel
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateUp: () -> Unit,
    musicViewModel: MusicViewModel
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
                }
            )
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


            // Search results
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(searchResults) { song ->
                    SongItem(
                        song = song,
                        onClick = { musicViewModel.playSong(song) },
                        onFavoriteClick = { musicViewModel.toggleFavorite(song) },
                        showAddToPlaylistOption = false,
                       onAddToPlaylistClick =  {}
                    )
                }
            }
        }
    }
}