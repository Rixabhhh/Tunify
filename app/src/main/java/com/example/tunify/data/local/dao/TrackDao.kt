package com.example.tunify.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.tunify.data.local.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    // @Upsert will Insert the track if it doesn't exist, or Update it if it does
    @Upsert
    suspend fun upsertTrack(track: TrackEntity)


    // Emits a live list of tracks where the user hit the Affinity button
    @Query("SELECT * FROM saved_tracks WHERE isLiked = 1 ORDER BY savedAt DESC")
    fun getLikedTracks(): Flow<List<TrackEntity>>

    
    // Used by the FeedScreen to instantly check if the currently playing song is already saved
    @Query("SELECT * FROM saved_tracks WHERE id = :trackId")
    fun getTrackById(trackId: String): Flow<TrackEntity?>

    @Query("DELETE FROM saved_tracks WHERE id = :trackId")
    suspend fun deleteTrackById(trackId: String)
}