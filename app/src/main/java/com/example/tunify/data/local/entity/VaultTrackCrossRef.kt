package com.example.tunify.data.local.entity

import androidx.room.Entity
import androidx.room.Index

// This table simply maps a Track ID to a Vault ID.
@Entity(
    tableName = "vault_track_join",
    primaryKeys = ["vaultId", "trackId"],
    indices = [Index("trackId")]
)
data class VaultTrackCrossRef(
    val vaultId: Int,
    val trackId: String
)