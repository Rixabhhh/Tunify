package com.example.tunify.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class VaultWithTracks(
    @Embedded val vault: VaultEntity,
    @Relation(
        parentColumn = "vaultId", // Primary key in VaultEntity
        entityColumn = "id",      // Primary key in TrackEntity
        associateBy = Junction(
            value = VaultTrackCrossRef::class,
            parentColumn = "vaultId", // Column in the junction table matching the vault
            entityColumn = "trackId"  // Column in the junction table matching the track
        )
    )
    val tracks: List<TrackEntity>
)