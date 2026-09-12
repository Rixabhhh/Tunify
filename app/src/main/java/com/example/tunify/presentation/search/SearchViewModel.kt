package com.example.tunify.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tunify.core.common.Resource
import com.example.tunify.domain.model.Track
import com.example.tunify.domain.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val trackRepository: TrackRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Track>>(emptyList())
    val searchResults: StateFlow<List<Track>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        searchJob?.cancel() // Cancel the previous search if they are still typing fast

        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // Debounce: Wait half a second before hitting the API

            trackRepository.searchTracks(query).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _isLoading.value = false
                        _searchResults.value = resource.data ?: emptyList()
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                    }
                }
            }
        }
    }
}