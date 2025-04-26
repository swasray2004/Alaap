package com.example.musicplayer.data.model
import com.google.gson.annotations.SerializedName

data class LastFmResponse(
    val results: Results
)

data class Results(
    val trackmatches: TrackMatches
)

data class TrackMatches(
    val track: List<Track>
)

data class Track(
    val name: String,
    val artist: String,
    val url: String,
    val imageUrl:List<TrackImage> = emptyList()
)
data class TrackImage(
    val url: String,
    val size: String
)