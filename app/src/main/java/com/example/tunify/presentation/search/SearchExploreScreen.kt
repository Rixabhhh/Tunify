package com.example.tunify.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

data class GenreCard(val name: String, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchExploreScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onGenreClick: (String) -> Unit,
    onTrackClick: (com.example.tunify.domain.model.Track, List<com.example.tunify.domain.model.Track>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Expanded modern and regional genres
    val genres = listOf(
        GenreCard("Discover Mix", Color(0xFFD8B4FE)), // Neon Purple
        GenreCard("Made For You", Color(0xFFF472B6)), // Pink
        GenreCard("Fresh Finds", Color(0xFF4ADE80)),  // Vibrant Green
        GenreCard("Trending", Color(0xFFFBBF24)),     // Gold
        GenreCard("Bollywood", Color(0xFFF87171)),    // Red
        GenreCard("Punjabi", Color(0xFF60A5FA)),      // Blue
        GenreCard("Ghazal Hindi", Color(0xFFA78BFA)), // Soft Purple
        GenreCard("Pop", Color(0xFF86EFAC)),
        GenreCard("Hip-hop", Color(0xFFEAB308)),
        GenreCard("K-Pop", Color(0xFFF43F5E)),
        GenreCard("Indie", Color(0xFF38BDF8)),
        GenreCard("Rock", Color(0xFFEF4444)),
        GenreCard("R&B", Color(0xFFFB923C)),
        GenreCard("Phonk", Color(0xFF8B5CF6)),
        GenreCard("Party", Color(0xFF14B8A6)),
        GenreCard("Love", Color(0xFFF43F5E)),
        GenreCard("Synthwave", Color(0xFF2DD4BF)),
        GenreCard("Lo-Fi", Color(0xFFFCD34D))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09090B))
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp, bottom = 80.dp) // Added bottom padding to clear the nav bar
    ) {
        Text(
            text = "Search",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.onSearchQueryChanged(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            placeholder = { Text("Artists, songs, or podcasts", color = Color.DarkGray, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Black) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (searchQuery.isNotBlank()) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFD8B4FE))
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(searchResults) { track ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    focusManager.clearFocus()
                                    onTrackClick(track, searchResults)


                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = track.title,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(text = track.artist, color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        } else {
            Text(
                text = "Browse all",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(genres) { genre ->
                    Box(
                        modifier = Modifier
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(genre.color)
                            .clickable { onGenreClick(genre.name) }
                            .padding(12.dp)
                    ) {
                        Text(
                            text = genre.name,
                            color = if (genre.name in listOf("Discover Mix", "Trending", "Fresh Finds", "Pop", "Synthwave", "Lo-Fi", "Party")) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.align(Alignment.TopStart)
                        )
                    }
                }
            }
        }
    }
}