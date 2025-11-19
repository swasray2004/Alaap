package com.example.musicplayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.model.Playlist
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.data.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists

    private val _currentPlaylistSongs = MutableStateFlow<List<Song>>(emptyList())
    val currentPlaylistSongs: StateFlow<List<Song>> = _currentPlaylistSongs

    private val _currentPlaylist = MutableStateFlow<Playlist?>(null)
    val currentPlaylist: StateFlow<Playlist?> = _currentPlaylist

    init {
        viewModelScope.launch {
            playlistRepository.getAllPlaylists().collectLatest {
                _playlists.value = it
            }
        }
    }

    fun createPlaylist(name: String, coverArtUri: String? = null): Long {
        var playlistId = 0L
        viewModelScope.launch {
            playlistId = playlistRepository.createPlaylist(name, coverArtUri)
        }
        return playlistId
    }

    fun loadPlaylistSongs(playlist: Playlist) {
        _currentPlaylist.value = playlist
        viewModelScope.launch {
            playlistRepository.getSongsFromPlaylist(playlist.id).collectLatest {
                _currentPlaylistSongs.value = it
            }
        }
    }

    fun addSongToPlaylist(playlistId: Long, song: Song) {
        viewModelScope.launch {
            playlistRepository.addSongToPlaylist(playlistId, song.id)

            // Reload the playlist songs if this is the current playlist
            if (_currentPlaylist.value?.id == playlistId) {
                _currentPlaylist.value?.let { loadPlaylistSongs(it) }
            }
        }
    }

    fun removeSongFromPlaylist(playlistId: Long, song: Song) {
        viewModelScope.launch {
            playlistRepository.removeSongFromPlaylist(playlistId, song.id)
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlist)
        }
    }
}
