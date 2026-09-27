package com.moetaz.words.presentation.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moetaz.words.presentation.components.PrimaryActionButton
import com.moetaz.words.presentation.components.WordsCard
import com.moetaz.words.presentation.components.WordsTextField
import com.moetaz.words.presentation.components.WordsTopAppBar

@Composable
fun AddWordScreen(
    wordId: Long? = null,
    viewModel: AddWordViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(wordId) {
        if (wordId != null && wordId > 0) {
            viewModel.handleIntent(AddWordIntent.LoadWord(wordId))
        }
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onBack()
        }
    }

    val isEditing = state.wordId != null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            WordsTopAppBar(
                title = if (isEditing) "Edit Entry" else "Add Entry",
                centerTitle = true,
                actions = {
                    TextButton(
                        onClick = { viewModel.handleIntent(AddWordIntent.SaveWord) },
                        enabled = !state.isSaving
                    ) {
                        Text(
                            text = "SAVE",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
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
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    TextButton(onClick = onBack) {
                        Text(
                            text = "Cancel",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // English Word Input Field
                    WordsTextField(
                        value = state.word,
                        onValueChange = { viewModel.handleIntent(AddWordIntent.OnWordChanged(it)) },
                        label = "English Word (Required)"
                    )

                    // Phonetic Pronunciation Input Field
                    WordsTextField(
                        value = state.phonetic,
                        onValueChange = { viewModel.handleIntent(AddWordIntent.OnPhoneticChanged(it)) },
                        label = "Phonetic Pronunciation (e.g. /æsˈθɛtɪk/)"
                    )

                    // Definition Input Field
                    WordsTextField(
                        value = state.definition,
                        onValueChange = { viewModel.handleIntent(AddWordIntent.OnDefinitionChanged(it)) },
                        label = "English Definition",
                        singleLine = false,
                        minLines = 2
                    )

                    // Definition Translation Input Field
                    WordsTextField(
                        value = state.definitionTranslation,
                        onValueChange = { viewModel.handleIntent(AddWordIntent.OnDefinitionTranslationChanged(it)) },
                        label = "Definition Translation (Arabic)",
                        singleLine = false,
                        minLines = 2,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End)
                    )

                    // Arabic Translation Input Field
                    val mainTranslation = state.translations.firstOrNull() ?: ""
                    WordsTextField(
                        value = mainTranslation,
                        onValueChange = { text ->
                            viewModel.handleIntent(AddWordIntent.OnTranslationChanged(0, text))
                        },
                        label = "Arabic Translation (Required)",
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End)
                    )

                    // Examples Section Title
                    Text(
                        text = "Examples",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Examples Card List
                    state.examples.forEachIndexed { index, example ->
                        WordsCard(shape = RoundedCornerShape(16.dp)) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "•",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    OutlinedTextField(
                                        value = example.english,
                                        onValueChange = {
                                            viewModel.handleIntent(AddWordIntent.OnExampleEnglishChanged(index, it))
                                        },
                                        placeholder = { Text("English Example Sentence") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color.Transparent,
                                            unfocusedBorderColor = Color.Transparent,
                                            focusedContainerColor = MaterialTheme.colorScheme.background,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
                                        )
                                    )
                                    IconButton(
                                        onClick = {
                                            viewModel.handleIntent(AddWordIntent.RemoveExampleField(index))
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Example",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = example.arabic,
                                    onValueChange = {
                                        viewModel.handleIntent(AddWordIntent.OnExampleArabicChanged(index, it))
                                    },
                                    placeholder = { Text("المثال باللغة العربية") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedContainerColor = MaterialTheme.colorScheme.background,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
                                    )
                                )
                            }
                        }
                    }

                    // "+ Add Example" Button Card
                    WordsCard(
                        shape = RoundedCornerShape(16.dp),
                        onClick = { viewModel.handleIntent(AddWordIntent.AddExampleField) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Add Example",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (state.error != null) {
                        Text(
                            text = state.error!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    PrimaryActionButton(
                        text = if (isEditing) "Save Changes" else "Save Word",
                        onClick = { viewModel.handleIntent(AddWordIntent.SaveWord) },
                        isLoading = state.isSaving,
                        enabled = !state.isSaving
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
