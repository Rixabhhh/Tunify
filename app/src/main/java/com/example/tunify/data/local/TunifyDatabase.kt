package com.example.tunify.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tunify.data.local.dao.TrackDao
import com.example.tunify.data.local.dao.VaultDao
import com.example.tunify.data.local.entity.TrackEntity
import com.example.tunify.data.local.entity.VaultEntity
import com.example.tunify.data.local.entity.VaultTrackCrossRef

@Database(
    entities = [
        TrackEntity::class,
        VaultEntity::class,
        VaultTrackCrossRef::class
    ],
    version = 2, // Bumped version number for schema change
    exportSchema = false
)
abstract class TunifyDatabase : RoomDatabase() {
    abstract val trackDao: TrackDao
    abstract val vaultDao: VaultDao // Registered the new DAO
}