package com.example.musicplayer.data.repository

import com.example.musicplayer.data.local.PlaylistDao
import com.example.musicplayer.data.model.Playlist
import com.example.musicplayer.data.model.PlaylistSongCrossRef
import com.example.musicplayer.data.model.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
    private val playlistDao: PlaylistDao
) {
    fun getAllPlaylists(): Flow<List<Playlist>> = playlistDao.getAllPlaylists()

    fun getSongsFromPlaylist(playlistId: Long): Flow<List<Song>> =
        playlistDao.getSongsFromPlaylist(playlistId)

    suspend fun createPlaylist(name: String, coverArtUri: String? = null): Long {
        val playlist = Playlist(name = name, coverArtUri = coverArtUri)
        return playlistDao.insertPlaylist(playlist)
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
        // Check if the song is already in the playlist to avoid duplicates
        val crossRef = PlaylistSongCrossRef(playlistId = playlistId, songId = songId)
        playlistDao.insertPlaylistSongCrossRef(crossRef)
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        playlistDao.removeSongFromPlaylist(playlistId, songId)
    }

    suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(playlist)
    }
}
