package com.example.musicplayer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SongCategory {
    WHATSAPP_AUDIO,
    DOWNLOADED,
    RECORDED,
    OTHER
}

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val albumArtUri: String?,
    val path: String,
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val dateAdded: Long = System.currentTimeMillis(),
    val category: SongCategory = SongCategory.OTHER,

)
