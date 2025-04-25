package com.example.musicplayer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,

    val createdAt: Long = System.currentTimeMillis(),
    val coverArtUri: String? = null
)
