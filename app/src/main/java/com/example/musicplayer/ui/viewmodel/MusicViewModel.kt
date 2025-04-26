package com.example.musicplayer.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.data.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val player: ExoPlayer
) : ViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _whatsappAudios = MutableStateFlow<List<Song>>(emptyList())
    val whatsappAudios: StateFlow<List<Song>> = _whatsappAudios.asStateFlow()

    private val _downloadedSongs = MutableStateFlow<List<Song>>(emptyList())
    val downloadedSongs: StateFlow<List<Song>> = _downloadedSongs.asStateFlow()

    private val _recordedAudios = MutableStateFlow<List<Song>>(emptyList())
    val recordedAudios: StateFlow<List<Song>> = _recordedAudios.asStateFlow()

    private val _likedSongs = MutableStateFlow<List<Song>>(emptyList())
    val likedSongs: StateFlow<List<Song>> = _likedSongs.asStateFlow()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _shuffleMode = MutableStateFlow(false)
    val shuffleMode: StateFlow<Boolean> = _shuffleMode.asStateFlow()

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val _searchResults = MutableStateFlow<List<Song>>(emptyList())
    val searchResults: StateFlow<List<Song>> = _searchResults.asStateFlow()

    private val _buffering = MutableStateFlow(false)
    val buffering: StateFlow<Boolean> = _buffering.asStateFlow()

    init {
        loadInitialData()
        setupPlayerListener()
        startProgressUpdates()
        player.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                updateCurrentSong()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY && _currentSong.value == null) {
                    updateCurrentSong()
                }
            }
        })
    }



    private fun loadInitialData() {
        viewModelScope.launch {
            songRepository.getAllSongs().collectLatest { _songs.value = it }
        }
        viewModelScope.launch {
            songRepository.getWhatsAppAudios().collectLatest { _whatsappAudios.value = it }
        }
        viewModelScope.launch {
            songRepository.getDownloadedSongs().collectLatest { _downloadedSongs.value = it }
        }
        viewModelScope.launch {
            songRepository.getRecordedAudios().collectLatest { _recordedAudios.value = it }
        }
        viewModelScope.launch {
            songRepository.getLikedSongs().collectLatest { _likedSongs.value = it }
        }
    }

    private fun setupPlayerListener() {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> _buffering.value = true
                    Player.STATE_READY -> {
                        _buffering.value = false
                        _isPlaying.value = player.playWhenReady
                        // Ensure current song is set when player is ready
                        if (_currentSong.value == null) {
                            updateCurrentSongFromPlayer()
                        }
                    }
                    Player.STATE_ENDED -> _isPlaying.value = false
                    Player.STATE_IDLE -> _isPlaying.value = false
                }
                updateProgress()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateCurrentSongFromPlayer()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying && player.playbackState == Player.STATE_READY
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _shuffleMode.value = shuffleModeEnabled
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                _repeatMode.value = repeatMode
            }
        })
    }

    private fun updateCurrentSongFromPlayer() {
        player.currentMediaItem?.mediaId?.toLongOrNull()?.let { songId ->
            _songs.value.find { it.id == songId }?.let { song ->
                _currentSong.value = song
                viewModelScope.launch {
                    songRepository.incrementPlayCount(songId)
                }
            }
        }
    }
    internal fun updateCurrentSong() {
        viewModelScope.launch {
            player.currentMediaItem?.mediaId?.toLongOrNull()?.let { songId ->
                val allSongs = _songs.value + _whatsappAudios.value +
                        _downloadedSongs.value + _recordedAudios.value
                allSongs.find { it.id == songId }?.let { song ->
                    _currentSong.value = song
                }
            }
        }
    }

    private fun startProgressUpdates() {
        viewModelScope.launch {
            while (true) {
                updateProgress()
                delay(500)
            }
        }
    }

    fun playSong(song: Song, songList: List<Song> = _songs.value) {
        val songIndex = songList.indexOfFirst { it.id == song.id }
        if (songIndex == -1) return

        // Set current song immediately before preparing player
        _currentSong.value = song

        player.stop()
        player.clearMediaItems()
        songList.forEach { s ->
            player.addMediaItem(createMediaItem(s))
        }
        player.seekTo(songIndex, 0L)
        player.prepare()
        player.playWhenReady = true
    }




    fun play() {
        player.playWhenReady = true
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            if (player.playbackState == Player.STATE_ENDED) {
                player.seekTo(0)
            }
            player.play()
        }
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
        updateProgress()
    }

    fun skipToNext() {
        if (player.hasNextMediaItem()) {
            player.seekToNextMediaItem()
        } else if (player.repeatMode == Player.REPEAT_MODE_ALL) {
            player.seekTo(0, 0L)
        }
    }

    fun skipToPrevious() {
        if (player.currentPosition > 5000) { // If more than 5 seconds played, restart song
            player.seekTo(0)
        } else if (player.hasPreviousMediaItem()) {
            player.seekToPreviousMediaItem()
        }
    }

    fun toggleShuffle() {
        player.shuffleModeEnabled = !player.shuffleModeEnabled
    }

    fun toggleRepeatMode() {
        player.repeatMode = when (player.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
            else -> Player.REPEAT_MODE_OFF
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            songRepository.toggleFavorite(song.id, !song.isFavorite)
            // Update current song if it's the one being toggled
            if (_currentSong.value?.id == song.id) {
                _currentSong.update { it?.copy(isFavorite = !song.isFavorite) }
            }
            // Update all song lists
            updateSongInLists(song.id) { it.copy(isFavorite = !it.isFavorite) }
        }
    }

    private fun updateSongInLists(songId: Long, transform: (Song) -> Song) {
        _songs.update { it.map { song -> if (song.id == songId) transform(song) else song } }
        _whatsappAudios.update { it.map { song -> if (song.id == songId) transform(song) else song } }
        _downloadedSongs.update { it.map { song -> if (song.id == songId) transform(song) else song } }
        _recordedAudios.update { it.map { song -> if (song.id == songId) transform(song) else song } }
        _likedSongs.update { it.map { song -> if (song.id == songId) transform(song) else song } }
    }

    fun searchSongs(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                _searchResults.value = emptyList()
            } else {
                // Search across all song sources
                val allSongs = _songs.value +
                        _whatsappAudios.value +
                        _downloadedSongs.value +
                        _recordedAudios.value +
                        _likedSongs.value

                _searchResults.value = allSongs.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.artist.contains(query, ignoreCase = true) ||
                            it.album.contains(query, ignoreCase = true)
                }
            }
        }
    }

    fun loadSongsFromDevice() {
        viewModelScope.launch {
            songRepository.loadSongsFromDevice()
        }
    }

    private fun updateProgress() {
        if (player.duration > 0) {
            _progress.value = player.currentPosition.toFloat() / player.duration
            _duration.value = player.duration
        }
    }


    private fun createMediaItem(song: Song): MediaItem {
        return MediaItem.Builder()
            .setMediaId(song.id.toString())
            .setUri(Uri.parse(song.path))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artist)
                    .setAlbumTitle(song.album)
                    .setArtworkUri(song.albumArtUri?.let { Uri.parse(it) })
                    .build()
            )
            .build()
    }

    override fun onCleared() {
        super.onCleared()
        // Don't release the player here as it's managed by the service
    }
}