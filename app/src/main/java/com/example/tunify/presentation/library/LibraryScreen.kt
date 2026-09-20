package com.example.tunify.presentation.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tunify.data.local.entity.VaultEntity

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    onNavigateToAffinity: () -> Unit,
    onNavigateToVault: (Int) -> Unit
) {
    val vaults by viewModel.customVaults.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF060709))
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 100.dp) // padding for bottom nav & mini player
    ) {
        Text(
            text = "Your Library",
            color = Color.White,
            fontSize = 32.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- CORE AFFINITY VAULT ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable { onNavigateToAffinity() }
                .background(Brush.linearGradient(listOf(Color(0xFF2D1B4E), Color(0xFF100B1A))))
                .border(
                    width = 1.dp,
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFD8B4FE).copy(alpha = 0.3f), Color.Transparent),
                        radius = 400f
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Affinity",
                    tint = Color(0xFFD8B4FE),
                    modifier = Modifier.size(32.dp)
                )

                Column {
                    Text(
                        text = "Affinity Vault",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Auto-synced Liked Tracks",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Custom Vaults",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- CUSTOM PLAYLISTS GRID ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(vaults) { vault ->
                VaultGridItem(
                    vault = vault,
                    onClick = { onNavigateToVault(vault.vaultId) }
                )
            }
        }
    }
}

@Composable
private fun VaultGridItem(
    vault: VaultEntity,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.05f), Color.White.copy(alpha = 0.01f))))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Elegant placeholder using the first letter of the vault name
            Text(
                text = vault.name.take(1).uppercase(),
                color = Color.White.copy(alpha = 0.2f),
                fontSize = 48.sp,
                fontFamily = FontFamily.Serif
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = vault.name,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Tap to view tracks", // Placeholder until we wire the relational count query
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}