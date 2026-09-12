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
    viewModel: SearchViewModel = hiltViewModel(), // Inject the new Search Brain
    onGenreClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    // Collect the live search results
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val genres = listOf(
        GenreCard("Discover Mix", Color(0xFFD8B4FE)),
        GenreCard("Pop", Color(0xFF86EFAC)),
        GenreCard("Indie", Color(0xFF60A5FA)),
        GenreCard("Rock", Color(0xFFF87171)),
        GenreCard("R&B", Color(0xFFF472B6)),
        GenreCard("Phonk", Color(0xFFA78BFA)),
        GenreCard("Synthwave", Color(0xFF2DD4BF)),
        GenreCard("Lo-Fi", Color(0xFFFBBF24))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09090B))
            .padding(16.dp)
    ) {
        Text(
            text = "Search",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp, top = 24.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.onSearchQueryChanged(it) // Fire the live search as they type!
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
            keyboardActions = KeyboardActions(
                onSearch = { focusManager.clearFocus() }
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // DYNAMIC UI SWITCH: If they are typing, show results. If empty, show grid.
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
                                    // Pass the artist name to the Feed to create a radio station for them
                                    onGenreClick(track.artist)
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
                                Text(
                                    text = track.artist,
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
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
                            color = if (genre.name == "Discover Mix") Color.Black else Color.White,
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