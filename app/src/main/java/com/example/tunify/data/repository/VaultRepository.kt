package com.example.tunify.data.repository

import com.example.tunify.data.local.dao.TrackDao
import com.example.tunify.data.local.dao.VaultDao
import com.example.tunify.data.local.entity.TrackEntity
import com.example.tunify.data.local.entity.VaultEntity
import com.example.tunify.data.local.entity.VaultTrackCrossRef
import com.example.tunify.domain.model.Track
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultRepository @Inject constructor(
    private val trackDao: TrackDao,
    private val vaultDao: VaultDao
) {
    // --- CORE AFFINITY LOGIC ---
    suspend fun saveTrackState(track: Track, isLiked: Boolean) {
        if (!isLiked) {
            trackDao.deleteTrackById(track.previewUrl)
        } else {
            val entity = TrackEntity(
                id = track.previewUrl,
                title = track.title,
                artist = track.artist,
                coverArtUrl = track.coverArtUrl,
                obscurityScore = track.obscurityScore,
                isLiked = true,
                savedAt = System.currentTimeMillis()
            )
            trackDao.upsertTrack(entity)
        }
    }

    // --- CUSTOM VAULT LOGIC ---
    fun getAllVaults(): Flow<List<VaultEntity>> = vaultDao.getAllVaults()

    suspend fun createNewVault(name: String) {
        vaultDao.createVault(VaultEntity(name = name))
    }

    fun getVaultIdsForTrack(trackId: String): Flow<List<Int>> {
        return vaultDao.getVaultIdsForTrack(trackId)
    }
    // Retrieves a specific vault and all the tracks mapped to it
    fun getVaultDetails(vaultId: Int): Flow<com.example.tunify.data.local.entity.VaultWithTracks> {
        return vaultDao.getVaultDetails(vaultId)
    }
    // Fetches all tracks marked as liked for the Affinity Vault
    fun getLikedTracks(): kotlinx.coroutines.flow.Flow<List<com.example.tunify.data.local.entity.TrackEntity>> {
        return trackDao.getLikedTracks()
    }

    suspend fun toggleTrackInVault(track: Track, vaultId: Int, isCurrentlySaved: Boolean) {
        // First, ensure the track actually exists in the tracks table before linking it
        val entity = TrackEntity(
            id = track.previewUrl,
            title = track.title,
            artist = track.artist,
            coverArtUrl = track.coverArtUrl,
            obscurityScore = track.obscurityScore,
            isLiked = false // Keep false, we aren't changing the Affinity state here
        )
        trackDao.upsertTrack(entity)

        // Then, link or unlink it in the junction table
        val crossRef = VaultTrackCrossRef(vaultId, track.previewUrl)
        if (isCurrentlySaved) {
            vaultDao.removeTrackFromVault(crossRef)
        } else {
            vaultDao.addTrackToVault(crossRef)
        }
    }
}