package com.example.musicplayer.service

import com.example.musicplayer.data.model.LastFmResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LastFmApiService {
    @GET("?method=track.search&format=json")
    suspend fun searchTrack(
        @Query("track") track: String,
        @Query("api_key") apiKey: String
    ): LastFmResponse
}
