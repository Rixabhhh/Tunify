package com.example.tunify.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tunify.core.common.Resource
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
    private val repository: TrackRepository
) : ViewModel() {

    // Mutable state that we can update internally
    private val _feedState = MutableStateFlow<Resource<List<Track>>>(Resource.Success(emptyList()))

    // Immutable state that the Compose UI can observe
    val feedState: StateFlow<Resource<List<Track>>> = _feedState.asStateFlow()

    /**
     * Triggers a network request to fetch 30-second previews.
     */
    fun fetchDiscoveryFeed(query: String = "synthwave") {
        viewModelScope.launch {
            // Collect the Flow from the repository (Loading -> Success/Error)
            repository.searchTracks(query).collect { result ->
                _feedState.value = result
            }
        }
    }
}