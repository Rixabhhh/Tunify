package com.example.tunify.domain.repository

import com.example.tunify.core.common.Resource
import com.example.tunify.domain.model.Track
import com.example.tunify.domain.model.VibeAnalysis
import kotlinx.coroutines.flow.Flow

interface AiRepository {
    fun analyzeTrackVibe(track: Track): Flow<Resource<VibeAnalysis>>
}