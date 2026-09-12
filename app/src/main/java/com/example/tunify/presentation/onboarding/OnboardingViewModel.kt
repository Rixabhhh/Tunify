package com.example.tunify.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tunify.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    // Preloaded list of discovery genres for the cold start
    val availableGenres = listOf(
        "Synthwave", "Phonk", "Lo-Fi", "Indie Rock",
        "Cyberpunk", "Ambient", "R&B", "Midwest Emo",
        "Techno", "Classical", "Hip Hop", "Jazz"
    )

    private val _selectedGenres = MutableStateFlow<Set<String>>(emptySet())
    val selectedGenres: StateFlow<Set<String>> = _selectedGenres.asStateFlow()

    fun toggleGenre(genre: String) {
        val current = _selectedGenres.value.toMutableSet()
        if (current.contains(genre)) {
            current.remove(genre)
        } else {
            current.add(genre)
        }
        _selectedGenres.value = current
    }

    fun completeOnboarding(onFinished: () -> Unit) {
        viewModelScope.launch {
            // Save selected genres and mark onboarding as true
            userPreferences.saveGenres(_selectedGenres.value)
            userPreferences.saveOnboardingState(true)
            onFinished()
        }
    }
}