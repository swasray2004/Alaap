package com.example.musicplayer.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface LyricsApiService {
    @GET("lyrics")
    suspend fun getLyrics(
        @Query("title") title: String,
        @Query("artist") artist: String
    ): LyricsResponse
}

data class LyricsResponse(val lyrics: String)