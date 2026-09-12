package com.example.tunify.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onNavigateToFeed: () -> Unit
) {
    val selectedGenres by viewModel.selectedGenres.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09090B)) // Deep immersive dark background
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header Section
        Column {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "TUNIFY",
                color = Color(0xFFD8B4FE),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Pick your frequency.",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Select genres to calibrate your discovery algorithm.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }

        // Genre Grid Section
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            items(viewModel.availableGenres) { genre ->
                val isSelected = selectedGenres.contains(genre)

                Box(
                    modifier = Modifier
                        .height(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFFD8B4FE) else Color(0xFF18181B))
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color(0xFFD8B4FE) else Color(0xFF27272A),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.toggleGenre(genre) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = genre,
                        color = if (isSelected) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Bottom Action Button
        Button(
            onClick = { viewModel.completeOnboarding(onNavigateToFeed) },
            enabled = selectedGenres.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD8B4FE),
                disabledContainerColor = Color(0xFF27272A)
            )
        ) {
            Text(
                text = if (selectedGenres.isEmpty()) "Select at least 1" else "Enter Crate",
                color = if (selectedGenres.isEmpty()) Color.Gray else Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}