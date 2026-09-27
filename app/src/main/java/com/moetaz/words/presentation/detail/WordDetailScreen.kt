package com.moetaz.words.presentation.detail

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moetaz.words.domain.model.Example
import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.model.WordMasteryStatus
import com.moetaz.words.presentation.components.SpeakButton
import com.moetaz.words.presentation.components.WordsCard
import com.moetaz.words.presentation.components.WordsTopAppBar
import com.moetaz.words.presentation.util.rememberTextToSpeech
import com.moetaz.words.ui.theme.WordsTheme

@Composable
fun WordDetailScreen(
    wordId: Long,
    viewModel: WordDetailViewModel,
    onEditWord: (Long) -> Unit = {},
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(wordId) {
        viewModel.handleIntent(WordDetailIntent.LoadWord(wordId))
    }

    WordDetailContent(
        wordId = wordId,
        state = state,
        onIntent = viewModel::handleIntent,
        onEditWord = onEditWord,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailContent(
    wordId: Long,
    state: WordDetailState,
    onIntent: (WordDetailIntent) -> Unit,
    onEditWord: (Long) -> Unit = {},
    onBack: () -> Unit
) {
    val tts = rememberTextToSpeech()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            WordsTopAppBar(
                title = "",
                onBack = onBack,
                actions = {
                    if (state.word != null) {
                        val word = state.word
                        IconButton(onClick = {
                            onIntent(WordDetailIntent.ToggleFavorite(!word.isFavorite))
                        }) {
                            Icon(
                                imageVector = if (word.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (word.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = { onEditWord(wordId) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Word",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.word != null) {
                val word = state.word
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header Section: Word & Pronunciation & Translation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = word.word,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                SpeakButton(
                                    onClick = { tts.speak(word.word) },
                                    iconSize = 24.dp
                                )
                            }
                            word.phonetic?.let { phoneticText ->
                                if (phoneticText.isNotBlank()) {
                                    Text(
                                        text = phoneticText,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        val mainTranslation = word.translations.firstOrNull() ?: ""
                        if (mainTranslation.isNotEmpty()) {
                            Text(
                                text = mainTranslation,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Mastery Status Section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Mastery Status",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            WordMasteryStatus.entries.forEachIndexed { index, status ->
                                SegmentedButton(
                                    selected = word.masteryStatus == status,
                                    onClick = {
                                        onIntent(WordDetailIntent.UpdateMasteryStatus(status))
                                    },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = WordMasteryStatus.entries.size
                                    )
                                ) {
                                    Text(
                                        text = status.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // Definition Card Section
                    if (!word.definition.isNullOrBlank() || !word.definitionTranslation.isNullOrBlank()) {
                        WordsCard {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Definition",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                word.definition?.let { defText ->
                                    if (defText.isNotBlank()) {
                                        Text(
                                            text = defText,
                                            fontSize = 15.sp,
                                            lineHeight = 22.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                                word.definitionTranslation?.let { defTransText ->
                                    if (defTransText.isNotBlank()) {
                                        Text(
                                            text = defTransText,
                                            fontSize = 15.sp,
                                            lineHeight = 22.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Additional Translations Card
                    if (word.translations.size > 1) {
                        WordsCard {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Translations",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                word.translations.drop(1).forEach { translation ->
                                    Text(
                                        text = "• $translation",
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Examples Card Section
                    if (word.examples.isNotEmpty()) {
                        WordsCard {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Examples",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                word.examples.forEach { example ->
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (example.english.isNotBlank()) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Text(
                                                    text = "•",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = example.english,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                        if (example.arabic.isNotBlank()) {
                                            Text(
                                                text = example.arabic,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.End
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun WordDetailContentPreview() {
    WordsTheme {
        WordDetailContent(
            wordId = 1L,
            state = WordDetailState(
                word = Word(
                    id = 1L,
                    word = "Eloquent",
                    phonetic = "/ˈɛl.ə.kwənt/",
                    definition = "Fluent or persuasive in speaking or writing.",
                    definitionTranslation = "فصيح أو بليغ في التحدث أو الكتابة.",
                    translations = listOf("فصيح", "بليغ"),
                    examples = listOf(Example(english = "An eloquent speech.", arabic = "خطاب فصيح.")),
                    masteryStatus = WordMasteryStatus.LEARNING,
                    isFavorite = true
                )
            ),
            onIntent = {},
            onEditWord = {},
            onBack = {}
        )
    }
}
