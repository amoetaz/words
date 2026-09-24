package com.moetaz.words.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moetaz.words.R
import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.model.WordMasteryStatus
import com.moetaz.words.presentation.util.rememberTextToSpeech
import com.moetaz.words.ui.theme.HeaderGradient
import com.moetaz.words.ui.theme.PrimaryGradient
import com.moetaz.words.ui.theme.SurfaceCardLight

@Composable
fun WordListScreen(
    viewModel: WordListViewModel,
    onWordClick: (Long) -> Unit,
    onAddWordClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onFlashcardsClick: () -> Unit = {},
    onQuizClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val tts = rememberTextToSpeech()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(PrimaryGradient)
                    .clickable(onClick = onAddWordClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Word",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(HeaderGradient)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White
                            )
                        }

                        Text(
                            text = stringResource(R.string.vocabulary_list),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row {
                            IconButton(onClick = onFlashcardsClick) {
                                Icon(
                                    imageVector = Icons.Default.Style,
                                    contentDescription = "Flashcards",
                                    tint = Color.White
                                )
                            }
                            IconButton(onClick = onQuizClick) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = "Quiz",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Search Field
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { query ->
                            viewModel.handleIntent(WordListIntent.OnSearchQueryChanged(query))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        placeholder = {
                            Text(
                                "Search words...",
                                color = Color.Gray,
                                fontSize = 15.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = Color.Gray
                            )
                        },
                        trailingIcon = {
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        viewModel.handleIntent(WordListIntent.OnSearchQueryChanged(""))
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear Search",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        },
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.95f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.90f),
                            disabledContainerColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
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
                } else if (state.error != null) {
                    Text(
                        text = state.error ?: "Unknown Error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (state.words.isEmpty()) {
                    Text(
                        text = "No words added yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (state.filteredWords.isEmpty()) {
                    Text(
                        text = "No words found matching \"${state.searchQuery}\"",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.filteredWords) { word ->
                            WordItem(
                                word = word,
                                onClick = { onWordClick(word.id) },
                                onSpeakClick = { tts.speak(word.word) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WordItem(
    word: Word,
    onClick: () -> Unit,
    onSpeakClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceCardLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                    IconButton(
                        onClick = onSpeakClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speak",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    if (word.isFavorite) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorite",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
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

            val badgeColor = when (word.masteryStatus) {
                WordMasteryStatus.LEARNING -> MaterialTheme.colorScheme.tertiaryContainer
                WordMasteryStatus.REVIEWING -> MaterialTheme.colorScheme.secondaryContainer
                WordMasteryStatus.MASTERED -> MaterialTheme.colorScheme.primaryContainer
            }

            val badgeTextColor = when (word.masteryStatus) {
                WordMasteryStatus.LEARNING -> MaterialTheme.colorScheme.onTertiaryContainer
                WordMasteryStatus.REVIEWING -> MaterialTheme.colorScheme.onSecondaryContainer
                WordMasteryStatus.MASTERED -> MaterialTheme.colorScheme.onPrimaryContainer
            }

            Surface(
                color = badgeColor,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = word.masteryStatus.name,
                    color = badgeTextColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
