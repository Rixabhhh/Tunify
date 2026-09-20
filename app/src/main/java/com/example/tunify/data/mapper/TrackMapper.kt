package com.example.tunify.data.mapper

import com.example.tunify.data.local.entity.TrackEntity
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

// NEW: Maps database entities back into playable tracks
fun TrackEntity.toTrack(): Track {
    return Track(
        id = this.id,
        title = this.title,
        artist = this.artist,
        albumName = "Unknown Album", // Fallback, not stored in DB
        coverArtUrl = this.coverArtUrl,
        previewUrl = this.id,        // We saved the previewUrl as the primary key ID
        durationSeconds = 30,        // Default duration for our use case
        obscurityScore = this.obscurityScore
    )
}