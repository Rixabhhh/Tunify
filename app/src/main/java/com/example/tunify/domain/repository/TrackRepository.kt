package com.example.tunify.domain.repository

import com.example.tunify.core.common.Resource
import com.example.tunify.domain.model.Track
import kotlinx.coroutines.flow.Flow

/**
 * The strict contract for accessing track data.
 * It returns a Flow of Resource, meaning it emits state changes (Loading -> Success/Error)
 * over time, which Jetpack Compose will perfectly react to.
 */
interface TrackRepository {

    /**
     * Fetches a list of tracks based on a search query.
     */
    fun searchTracks(query: String): Flow<Resource<List<Track>>>

}