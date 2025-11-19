package com.example.musicplayer.data.local

import androidx.room.*
import com.example.musicplayer.data.model.Playlist
import com.example.musicplayer.data.model.PlaylistSongCrossRef
import com.example.musicplayer.data.model.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY name ASC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Transaction
    @Query("SELECT * FROM songs INNER JOIN playlist_song_cross_ref ON songs.id = playlist_song_cross_ref.songId WHERE playlist_song_cross_ref.playlistId = :playlistId")
    fun getSongsFromPlaylist(playlistId: Long): Flow<List<Song>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)  // Use IGNORE to prevent duplicates
    suspend fun insertPlaylistSongCrossRef(crossRef: PlaylistSongCrossRef)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Query("DELETE FROM playlist_song_cross_ref WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)
}

