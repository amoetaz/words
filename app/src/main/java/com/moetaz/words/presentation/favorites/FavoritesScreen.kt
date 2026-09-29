package com.moetaz.words.presentation.favorites

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.model.WordMasteryStatus
import com.moetaz.words.presentation.components.MasteryStatusBadge
import com.moetaz.words.presentation.components.SpeakButton
import com.moetaz.words.presentation.components.WordsCard
import com.moetaz.words.presentation.components.WordsTopAppBar
import com.moetaz.words.presentation.util.rememberTextToSpeech
import com.moetaz.words.ui.theme.WordsTheme

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onWordClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    FavoritesContent(
        state = state,
        onIntent = viewModel::handleIntent,
        onWordClick = onWordClick,
        onBack = onBack
    )
}

@Composable
fun FavoritesContent(
    state: FavoritesState,
    onIntent: (FavoritesIntent) -> Unit,
    onWordClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    val tts = rememberTextToSpeech()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            WordsTopAppBar(
                title = "Favorite Words",
                onBack = onBack,
                containerColor = MaterialTheme.colorScheme.background
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar for Favorites
            PaddingValues(horizontal = 16.dp, vertical = 8.dp).let {
                Box(modifier = Modifier.padding(it)) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { query ->
                            onIntent(FavoritesIntent.OnSearchQueryChanged(query))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        placeholder = {
                            Text(
                                "Search favorites...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 15.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        onIntent(FavoritesIntent.OnSearchQueryChanged(""))
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear Search",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (state.favoriteWords.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No favorite words yet.\nTap the heart icon on any word to save it here!",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (state.filteredFavorites.isEmpty()) {
                    Text(
                        text = "No favorites found matching \"${state.searchQuery}\"",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = state.filteredFavorites,
                            key = { it.id }
                        ) { word ->
                            FavoriteWordItem(
                                word = word,
                                onClick = { onWordClick(word.id) },
                                onSpeakClick = { tts.speak(word.word) },
                                onToggleFavorite = {
                                    onIntent(FavoritesIntent.ToggleFavorite(word.id, !word.isFavorite))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteWordItem(
    word: Word,
    onClick: () -> Unit,
    onSpeakClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    WordsCard(
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = word.word,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    SpeakButton(
                        onClick = onSpeakClick,
                        iconSize = 18.dp,
                        modifier = Modifier.size(32.dp)
                    )
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (word.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Toggle Favorite",
                            tint = if (word.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                word.phonetic?.let {
                    Text(
                        text = it,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                val mainTranslation = word.translations.firstOrNull() ?: ""
                if (mainTranslation.isNotEmpty()) {
                    Text(
                        text = mainTranslation,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            MasteryStatusBadge(status = word.masteryStatus)
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun FavoritesContentPreview() {
    val sampleFavorites = listOf(
        Word(
            id = 1L,
            word = "Eloquent",
            phonetic = "/ˈɛl.ə.kwənt/",
            definition = "Fluent or persuasive in speaking or writing.",
            translations = listOf("فصيح", "بليغ"),
            masteryStatus = WordMasteryStatus.LEARNING,
            isFavorite = true
        )
    )

    WordsTheme {
        FavoritesContent(
            state = FavoritesState(
                favoriteWords = sampleFavorites
            ),
            onIntent = {},
            onWordClick = {},
            onBack = {}
        )
    }
}
