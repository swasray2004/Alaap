package com.example.musicplayer.data.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.versionedparcelable.VersionedParcelize

enum class SongCategory {
    WHATSAPP_AUDIO,
    DOWNLOADED,
    RECORDED,
    OTHER
}
@VersionedParcelize
@Entity(tableName = "songs")
data class Song(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,

    val sentiment: String? = null,
    val albumArtUri: String?,
    val path: String,

    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val dateAdded: Long = System.currentTimeMillis(),
    val category: SongCategory = SongCategory.OTHER
)
