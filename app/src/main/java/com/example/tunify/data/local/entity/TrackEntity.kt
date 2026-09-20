package com.example.tunify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_tracks")
data class TrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val coverArtUrl: String,
    val obscurityScore: Int,
    val isLiked: Boolean = false, // We keep this for the core Affinity Vault
    val savedAt: Long = System.currentTimeMillis()
)