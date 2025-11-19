package com.example.musicplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.model.Track
import com.example.musicplayer.data.repository.LastFmRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LastFmViewModel : ViewModel() {
    private val repository = LastFmRepository()
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> get() = _tracks

    fun search(query: String) {
        viewModelScope.launch {
            try {
                val result = repository.searchTracks(query)
                _tracks.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
