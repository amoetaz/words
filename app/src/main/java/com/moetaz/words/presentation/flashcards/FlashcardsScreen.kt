package com.moetaz.words.presentation.flashcards

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.moetaz.words.domain.model.WordMasteryStatus
import com.moetaz.words.presentation.util.rememberTextToSpeech

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    viewModel: FlashcardsViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val tts = rememberTextToSpeech()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Flashcards Practice") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.words.isEmpty()) {
                Text(
                    text = "No words available for practice.\nAdd some words first!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                val currentWord = state.words[state.currentIndex]

                val rotation by animateFloatAsState(
                    targetValue = if (state.isFlipped) 180f else 0f,
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                    label = "CardFlipAnimation"
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Card ${state.currentIndex + 1} of ${state.words.size}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 16.dp)
                            .graphicsLayer {
                                rotationY = rotation
                                cameraDistance = 12 * density
                            }
                            .clickable { viewModel.handleIntent(FlashcardsIntent.FlipCard) },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (rotation <= 90f) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = currentWord.word,
                                            style = MaterialTheme.typography.headlineLarge,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                        IconButton(onClick = { tts.speak(currentWord.word) }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = "Pronounce",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    currentWord.phonetic?.let {
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(32.dp))
                                    Text(
                                        text = "Tap to reveal answer 🔄",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .graphicsLayer { rotationY = 180f }
                                        .fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = currentWord.translations.joinToString(", "),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Center
                                    )

                                    currentWord.definition?.let {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    currentWord.definitionTranslation?.let {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    if (currentWord.examples.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "Example: ${currentWord.examples.first().english}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.secondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.handleIntent(
                                    FlashcardsIntent.MarkMastery(currentWord.id, WordMasteryStatus.LEARNING)
                                )
                            }
                        ) {
                            Text("Again")
                        }

                        Button(
                            onClick = {
                                viewModel.handleIntent(
                                    FlashcardsIntent.MarkMastery(currentWord.id, WordMasteryStatus.REVIEWING)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        ) {
                            Text("Review")
                        }

                        Button(
                            onClick = {
                                viewModel.handleIntent(
                                    FlashcardsIntent.MarkMastery(currentWord.id, WordMasteryStatus.MASTERED)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Mastered")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.handleIntent(FlashcardsIntent.PreviousCard) }) {
                            Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = "Previous")
                        }

                        IconButton(onClick = {
                            viewModel.handleIntent(
                                FlashcardsIntent.ToggleFavorite(currentWord.id, !currentWord.isFavorite)
                            )
                        }) {
                            Icon(
                                imageVector = if (currentWord.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (currentWord.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(onClick = { viewModel.handleIntent(FlashcardsIntent.NextCard) }) {
                            Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Next")
                        }
                    }
                }
            }
        }
    }
}
