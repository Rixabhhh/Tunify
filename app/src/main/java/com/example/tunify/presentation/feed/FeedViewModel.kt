package com.example.tunify.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tunify.core.common.Resource
import com.example.tunify.data.local.UserPreferences
import com.example.tunify.data.local.entity.VaultEntity
import com.example.tunify.data.repository.VaultRepository
import com.example.tunify.domain.model.Track
import com.example.tunify.domain.model.VibeAnalysis
import com.example.tunify.domain.repository.AiRepository
import com.example.tunify.domain.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val trackRepository: TrackRepository,
    private val userPreferences: UserPreferences,
    private val vaultRepository: VaultRepository,
    private val aiRepository: AiRepository // <-- ADD THIS
) : ViewModel() {

    // --- AI VIBE CHECKER STATE ---
    private val _vibeState = MutableStateFlow<Resource<VibeAnalysis>?>(null)
    val vibeState: StateFlow<Resource<VibeAnalysis>?> = _vibeState.asStateFlow()
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // --- 2. ADD VAULT STATE LOGIC ---
    val customVaults: StateFlow<List<VaultEntity>> = vaultRepository.getAllVaults()
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeTrackVaultIds = MutableStateFlow<Set<Int>>(emptySet())
    val activeTrackVaultIds: StateFlow<Set<Int>> = _activeTrackVaultIds.asStateFlow()

    fun checkVaultsForTrack(trackId: String) {
        viewModelScope.launch {
            vaultRepository.getVaultIdsForTrack(trackId).collect { ids ->
                _activeTrackVaultIds.value = ids.toSet()
            }
        }
    }
    private val _currentTrackIndex = MutableStateFlow(0)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex.asStateFlow()

    private val _currentCrateTitle = MutableStateFlow("DISCOVER MIX")
    val currentCrateTitle: StateFlow<String> = _currentCrateTitle.asStateFlow()

    fun nextTrack() {
        if (_currentTrackIndex.value < _tracks.value.size - 1) {
            _currentTrackIndex.value += 1
        }
    }

    fun createVault(name: String) {
        viewModelScope.launch { vaultRepository.createNewVault(name) }
    }

    fun onVaultToggled(track: Track, vaultId: Int, isCurrentlySaved: Boolean) {
        viewModelScope.launch { vaultRepository.toggleTrackInVault(track, vaultId, isCurrentlySaved) }
    }

    fun onLikeToggled(track: Track, isCurrentlyLiked: Boolean) {
        viewModelScope.launch { vaultRepository.saveTrackState(track, isLiked = !isCurrentlyLiked) }
    }
    init {
        loadPersonalizedFeed()
    }

    private fun loadPersonalizedFeed() {
        viewModelScope.launch {
            userPreferences.savedGenres.collect { genres ->
                val activeGenre = if (genres.isNotEmpty()) genres.random() else "pop"
                fetchNewCrate(activeGenre)
            }
        }
    }

    // --- ADD THIS WITH YOUR OTHER STATES ---
    private val _playTrigger = MutableStateFlow(0L)
    val playTrigger: StateFlow<Long> = _playTrigger.asStateFlow()

    // --- UPDATE THIS FUNCTION ---
    fun playVaultTracks(vaultName: String, vaultTracks: List<Track>, startIndex: Int) {
        _isLoading.value = false
        _errorMessage.value = null
        _currentCrateTitle.value = vaultName.uppercase()
        _tracks.value = vaultTracks
        _currentTrackIndex.value = startIndex

        // FORCE THE AUDIO ENGINE TO REACT TO THE CLICK
        _playTrigger.value = System.currentTimeMillis()
    }

    fun fetchNewCrate(query: String) {
        viewModelScope.launch {
            trackRepository.searchTracks(query).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _isLoading.value = true
                        _errorMessage.value = null
                    }
                    is Resource.Success -> {
                        _isLoading.value = false
                        val fetchedTracks = resource.data ?: emptyList()
                        _tracks.value = fetchedTracks

                        // NEW: Reset the index and update the crate title
                        _currentTrackIndex.value = 0
                        _currentCrateTitle.value = query.uppercase()

                        if (fetchedTracks.isEmpty()) {
                            _errorMessage.value = "No tracks found for this genre."
                        }
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = resource.message ?: "Failed to connect. Check your internet."
                    }
                }
            }
        }
    }
    fun analyzeCurrentTrack(track: Track) {
        viewModelScope.launch {
            aiRepository.analyzeTrackVibe(track).collect { resource ->
                _vibeState.value = resource
            }
        }
    }

    fun clearVibeState() {
        _vibeState.value = null
    }
}