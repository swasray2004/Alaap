package com.example.musicplayer.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.musicplayer.data.local.SongDao
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.data.model.SongCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepository @Inject constructor(
    private val songDao: SongDao,

    @ApplicationContext private val context: Context
) {
    fun getAllSongs(): Flow<List<Song>> = songDao.getAllSongs()

    fun getWhatsAppAudios(): Flow<List<Song>> =
        songDao.getSongsByCategory(SongCategory.WHATSAPP_AUDIO)

    fun getDownloadedSongs(): Flow<List<Song>> = songDao.getSongsByCategory(SongCategory.DOWNLOADED)

    fun getRecordedAudios(): Flow<List<Song>> = songDao.getSongsByCategory(SongCategory.RECORDED)

    fun getLikedSongs(): Flow<List<Song>> = songDao.getFavoriteSongs()

    fun searchSongs(query: String): Flow<List<Song>> = songDao.searchSongs(query)

    suspend fun toggleFavorite(songId: Long, isFavorite: Boolean) {
        songDao.updateFavoriteStatus(songId, isFavorite)
    }

    suspend fun incrementPlayCount(songId: Long) {
        songDao.incrementPlayCount(songId)
    }

    suspend fun getSongById(songId: Long): Song? = withContext(Dispatchers.IO) {
        songDao.getSongById(songId)
    }

    suspend fun loadSongsFromDevice() {
        withContext(Dispatchers.IO) {
            val songs = mutableListOf<Song>()
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATE_ADDED
            )

            val selection =
                "${MediaStore.Audio.Media.IS_MUSIC} != 0 OR ${MediaStore.Audio.Media.MIME_TYPE} LIKE 'audio/%'"

            context.contentResolver.query(
                collection,
                projection,
                selection,
                null,
                "${MediaStore.Audio.Media.DATE_ADDED} DESC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val dateAddedColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn)
                    val artist = cursor.getString(artistColumn)
                    val album = cursor.getString(albumColumn)
                    val duration = cursor.getLong(durationColumn)
                    val path = cursor.getString(pathColumn)
                    val albumId = cursor.getLong(albumIdColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn)

                    val albumArtUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    )

                    // Determine the category based on file path
                    val category = determineCategory(path)

                    val song = Song(
                        id = id,
                        title = title,
                        artist = artist,
                        album = album,
                        duration = duration,
                        path = path,
                        albumArtUri = albumArtUri.toString(),
                        dateAdded = dateAdded * 1000, // Convert to milliseconds
                        category = category
                    )
                    songs.add(song)
                }
            }

            songDao.insertSongs(songs)
        }
    }

    private fun determineCategory(path: String): SongCategory {
        return when {
            // WhatsApp audio files are typically stored in WhatsApp/Media/WhatsApp Audio
            path.contains("WhatsApp/Media/WhatsApp Audio", ignoreCase = true) ||
                    path.contains(
                        "WhatsApp Audio",
                        ignoreCase = true
                    ) -> SongCategory.WHATSAPP_AUDIO

            // Downloaded files are typically in the Download directory
            path.contains("/Download/", ignoreCase = true) ||
                    path.contains(
                        Environment.DIRECTORY_DOWNLOADS,
                        ignoreCase = true
                    ) -> SongCategory.DOWNLOADED

            // Recorded audio files are typically in DCIM/Sound recordings or similar
            path.contains("/Recording", ignoreCase = true) ||
                    path.contains("/record", ignoreCase = true) ||
                    path.contains("/Voice Recorder", ignoreCase = true) ||
                    path.contains("/Sounds", ignoreCase = true) ||
                    path.contains("/DCIM/", ignoreCase = true) && path.endsWith(
                ".m4a",
                ignoreCase = true
            ) -> SongCategory.RECORDED

            // All other audio files
            else -> SongCategory.OTHER
        }
    }
}
