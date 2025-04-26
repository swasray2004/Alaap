package com.example.musicplayer.data.repository

import com.example.musicplayer.data.model.LastFmResponse


import com.example.musicplayer.service.LastFmApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LastFmRepository {
    private val apiService: LastFmApiService

    companion object {
        const val BASE_URL = "https://ws.audioscrobbler.com/2.0/"
        const val API_KEY = "47059e2afa37393f83df5e77985856b3"
    }

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(LastFmApiService::class.java)
    }

    suspend fun searchTracks(query: String): List<com.example.musicplayer.data.model.Track> {
        try {
            val response: LastFmResponse = apiService.searchTrack(query, API_KEY)
            println("API Response: ${response.results.trackmatches.track}")  // Log the result
            return response.results.trackmatches.track
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

}
