package com.example.tunify.data.mapper

import com.example.tunify.data.remote.dto.ItunesTrackDto
import com.example.tunify.domain.model.Track

fun ItunesTrackDto.toTrack(): Track {
    return Track(
        id = trackId.toString(),
        title = trackName ?: "Unknown Track",
        artist = artistName ?: "Unknown Artist",
        albumName = collectionName ?: "Unknown Album",
        coverArtUrl = artworkUrl100?.replace("100x100bb.jpg", "600x600bb.jpg") ?: "",
        previewUrl = previewUrl ?: "",
        durationSeconds = ((trackTimeMillis ?: 0L) / 1000).toInt(),
        obscurityScore = (60..99).random()
    )
}