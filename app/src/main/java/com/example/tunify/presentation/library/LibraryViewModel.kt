package com.example.tunify.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tunify.data.local.entity.VaultEntity
import com.example.tunify.data.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    vaultRepository: VaultRepository
) : ViewModel() {

    // Automatically fetches and observes the list of user-created vaults from Room
    val customVaults: StateFlow<List<VaultEntity>> =
        vaultRepository.getAllVaults()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // TODO: We will wire up the live track counts in the next step when we build the Vault Details screen
}