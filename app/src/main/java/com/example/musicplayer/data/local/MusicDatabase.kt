package com.example.musicplayer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.musicplayer.data.model.Playlist
import com.example.musicplayer.data.model.PlaylistSongCrossRef
import com.example.musicplayer.data.model.Song

@Database(
    entities = [Song::class, Playlist::class, PlaylistSongCrossRef::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
}