package com.example.tunify.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tunify.core.common.Resource
import com.example.tunify.data.local.UserPreferences
import com.example.tunify.domain.model.Track
import com.example.tunify.domain.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val trackRepository: TrackRepository,
    private val userPreferences: UserPreferences // Injecting the local memory
) : ViewModel() {

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPersonalizedFeed()
    }

    private fun loadPersonalizedFeed() {
        viewModelScope.launch {
            // Read the saved genres from DataStore
            userPreferences.savedGenres.collect { genres ->
                // V1 Algorithm: Pick a random genre from their favorites to create a dynamic crate.
                // If the set is empty (fallback), default to "pop"
                val activeGenre = if (genres.isNotEmpty()) genres.random() else "pop"

                fetchNewCrate(activeGenre)
            }
        }
    }

    // Changed from private to public so the Search Screen can trigger it
    fun fetchNewCrate(query: String) {
        viewModelScope.launch {
            trackRepository.searchTracks(query).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _isLoading.value = false
                        _tracks.value = resource.data ?: emptyList()
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        // TODO: Handle Error State
                    }
                }
            }
        }
    }
    }
