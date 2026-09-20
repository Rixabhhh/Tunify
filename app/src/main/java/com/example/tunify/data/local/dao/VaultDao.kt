package com.example.tunify.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.tunify.data.local.entity.VaultEntity
import com.example.tunify.data.local.entity.VaultTrackCrossRef
import com.example.tunify.data.local.entity.VaultWithTracks
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createVault(vault: VaultEntity)

    // Gets all vaults for the Library grid and Bottom Sheet
    @Query("SELECT * FROM vaults ORDER BY createdAt DESC")
    fun getAllVaults(): Flow<List<VaultEntity>>

    // Links a track to a vault
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToVault(crossRef: VaultTrackCrossRef)

    // Unlinks a track from a vault
    @Delete
    suspend fun removeTrackFromVault(crossRef: VaultTrackCrossRef)

    // Returns a specific vault AND all of its mapped tracks for the details screen
    @Transaction
    @Query("SELECT * FROM vaults WHERE vaultId = :vaultId")
    fun getVaultDetails(vaultId: Int): Flow<VaultWithTracks>

    // Used by the Bottom Sheet to show the glowing neon checkmarks for already-saved vaults
    @Query("SELECT vaultId FROM vault_track_join WHERE trackId = :trackId")
    fun getVaultIdsForTrack(trackId: String): Flow<List<Int>>
}