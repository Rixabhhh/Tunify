package com.example.tunify.data.remote.dto

data class ItunesResponse(
    val results: List<ItunesTrackDto>
)

data class ItunesTrackDto(
    val trackId: Long,
    val trackName: String?,
    val collectionName: String?, // <-- ADD THIS LINE
    val trackTimeMillis: Long?,
    val artistName: String?,
    val artworkUrl100: String?,
    val previewUrl: String?

)