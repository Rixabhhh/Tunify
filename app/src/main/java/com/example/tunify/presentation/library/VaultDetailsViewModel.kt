package com.example.tunify.presentation.library

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tunify.data.local.entity.VaultEntity
import com.example.tunify.data.local.entity.VaultWithTracks
import com.example.tunify.data.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class VaultDetailsViewModel @Inject constructor(
    private val vaultRepository: VaultRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Automatically extracts the vaultId passed via Navigation Compose
    private val vaultId: Int = checkNotNull(savedStateHandle["vaultId"])

    val vaultDetails: StateFlow<VaultWithTracks?> = if (vaultId == -1) {
        // --- THE AFFINITY VAULT INTERCEPTOR ---
        vaultRepository.getLikedTracks().map { likedTracks ->
            VaultWithTracks(
                vault = VaultEntity(
                    vaultId = -1,
                    name = "Affinity Vault"
                ),
                tracks = likedTracks
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    } else {
        // --- NORMAL CUSTOM VAULTS ---
        vaultRepository.getVaultDetails(vaultId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }
}