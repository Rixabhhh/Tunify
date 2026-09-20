package com.example.tunify.presentation.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tunify.data.local.entity.VaultEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StashBottomSheet(
    vaults: List<VaultEntity>,
    activeVaultIds: Set<Int>,
    isCurrentlyLiked: Boolean,
    onDismiss: () -> Unit,
    onToggleAffinity: () -> Unit,
    onToggleVault: (Int, Boolean) -> Unit,
    onCreateNewVault: (String) -> Unit
) {
    var showNewVaultDialog by remember { mutableStateOf(false) }
    var newVaultName by remember { mutableStateOf("") }

    // --- NEW: OPTIMISTIC STATE ---
    // This allows the checkmarks to appear/disappear instantly on tap without waiting for the DB
    var localLikedState by remember(isCurrentlyLiked) { mutableStateOf(isCurrentlyLiked) }
    var localActiveVaultIds by remember(activeVaultIds) { mutableStateOf(activeVaultIds) }

    if (showNewVaultDialog) {
        AlertDialog(
            onDismissRequest = { showNewVaultDialog = false },
            containerColor = Color(0xFF1E1E24),
            title = { Text("Initialize Vault", color = Color.White, fontFamily = FontFamily.Serif) },
            text = {
                OutlinedTextField(
                    value = newVaultName,
                    onValueChange = { newVaultName = it },
                    placeholder = { Text("Vault Name", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFD8B4FE),
                        cursorColor = Color(0xFFD8B4FE)
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newVaultName.isNotBlank()) {
                        onCreateNewVault(newVaultName)
                        showNewVaultDialog = false
                        newVaultName = ""
                    }
                }) { Text("Create", color = Color(0xFFD8B4FE)) }
            },
            dismissButton = {
                TextButton(onClick = { showNewVaultDialog = false }) { Text("Cancel", color = Color.Gray) }
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF09090B),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(40.dp, 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            )
        }
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)) {
            Text("Save to Vault", color = Color.White, fontSize = 24.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            // --- 1. NEW VAULT BUTTON ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { showNewVaultDialog = true }
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(Color(0xFFD8B4FE).copy(alpha = 0.5f), Color.Transparent)),
                        RoundedCornerShape(16.dp)
                    )
                    .background(Color.White.copy(alpha = 0.03f))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFD8B4FE).copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = "New", tint = Color(0xFFD8B4FE))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Initialize New Vault", color = Color(0xFFD8B4FE), fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 2. PERMANENT AFFINITY VAULT ---
            VaultRowItem(
                name = "Affinity Vault",
                subtitle = "Auto-synced Liked Tracks",
                icon = { Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFD8B4FE)) },
                iconBackground = Brush.linearGradient(listOf(Color(0xFF2D1B4E), Color(0xFF100B1A))),
                isSelected = localLikedState,
                onClick = {
                    localLikedState = !localLikedState
                    onToggleAffinity()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- 3. CUSTOM VAULTS ---
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vaults) { vault ->
                    val isSavedHere = localActiveVaultIds.contains(vault.vaultId)
                    VaultRowItem(
                        name = vault.name,
                        subtitle = "Custom Vault",
                        icon = { Text(vault.name.take(1).uppercase(), color = Color.White.copy(alpha = 0.5f), fontSize = 20.sp, fontFamily = FontFamily.Serif) },
                        iconBackground = Brush.linearGradient(listOf(Color.DarkGray, Color.Black)),
                        isSelected = isSavedHere,
                        onClick = {
                            // Instantly update the local UI state for the tick
                            localActiveVaultIds = if (isSavedHere) {
                                localActiveVaultIds - vault.vaultId
                            } else {
                                localActiveVaultIds + vault.vaultId
                            }
                            // Fire the database event in the background
                            onToggleVault(vault.vaultId, isSavedHere)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun VaultRowItem(
    name: String, subtitle: String, icon: @Composable () -> Unit,
    iconBackground: Brush, isSelected: Boolean, onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBackground), contentAlignment = Alignment.Center) { icon() }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(subtitle, color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
            }
        }
        if (isSelected) {
            Icon(Icons.Default.Check, contentDescription = "Saved", tint = Color(0xFFD8B4FE), modifier = Modifier.size(24.dp))
        } else {
            Box(modifier = Modifier
                .size(24.dp)
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)))
        }
    }
}