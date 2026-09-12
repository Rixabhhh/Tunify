package com.example.tunify.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    // These are the exact "keys" we will use to find the data in the database
    private companion object {
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val SAVED_GENRES = stringSetPreferencesKey("saved_genres")
    }

    // --- READING DATA (Returns a continuous stream of data) ---
    val hasCompletedOnboarding: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[HAS_COMPLETED_ONBOARDING] ?: false // Default is false
    }

    val savedGenres: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[SAVED_GENRES] ?: emptySet() // Default is an empty set
    }

    // --- WRITING DATA (Suspend functions because saving takes time) ---
    suspend fun saveOnboardingState(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[HAS_COMPLETED_ONBOARDING] = completed
        }
    }

    suspend fun saveGenres(genres: Set<String>) {
        dataStore.edit { preferences ->
            preferences[SAVED_GENRES] = genres
        }
    }
}