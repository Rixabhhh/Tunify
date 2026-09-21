package com.example.tunify.presentation.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tunify.core.common.Resource
import com.example.tunify.domain.model.VibeAnalysis
import com.example.tunify.ui.theme.EditorialSerif // Importing your custom font

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VibeBottomSheet(
    vibeState: Resource<VibeAnalysis>?,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Only show the sheet if the state is not null
    if (vibeState != null) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color(0xFF151020), // Dark cinematic background
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 32.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color(0xFF333333), RoundedCornerShape(4.dp))
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                when (vibeState) {
                    is Resource.Loading -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFFD8B4FE))
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Analyzing sonic profile...",
                                color = Color.White,
                                fontFamily = EditorialSerif,
                                fontSize = 20.sp,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                    is Resource.Success -> {
                        val vibe = vibeState.data
                        if (vibe != null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // 1. The Kicker
                                Text(
                                    text = "THE VIBE",
                                    color = Color(0xFFD8B4FE),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 2.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                // 2. The Primary Mood (Serif)
                                Text(
                                    text = vibe.mood,
                                    color = Color.White,
                                    fontFamily = EditorialSerif,
                                    fontSize = 36.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 40.sp
                                )
                                Spacer(modifier = Modifier.height(24.dp))

                                // 3. The Genre Pill
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFFD8B4FE).copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(50.dp)
                                        )
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = vibe.genreBlend.uppercase(),
                                        color = Color(0xFFD8B4FE),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // 4. The Aesthetic (Serif Italic)
                                Text(
                                    text = vibe.aesthetic,
                                    color = Color.White,
                                    fontFamily = EditorialSerif,
                                    fontSize = 24.sp,
                                    fontStyle = FontStyle.Italic,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                // 5. The Description
                                Text(
                                    text = vibe.description,
                                    color = Color(0xFFA0A0A0),
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Analysis Interrupted",
                                color = Color(0xFFF87171),
                                fontFamily = EditorialSerif,
                                fontSize = 28.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = vibeState.message ?: "Could not verify track safety.",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}