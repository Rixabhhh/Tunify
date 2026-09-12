package com.example.tunify.domain.model

/**
 * The core business model representing a song in the Tunify application.
 * This is a pure Kotlin data class, completely decoupled from Room or Retrofit.
 */
data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val albumName: String,
    val coverArtUrl: String,
    val previewUrl: String,      // The URL for the 30-second audio stream
    val durationSeconds: Int,    // Usually 30 for our specific use case
    val obscurityScore: Int      // Algorithmic rating from 0 (Mainstream) to 100 (Deep Underground)
)