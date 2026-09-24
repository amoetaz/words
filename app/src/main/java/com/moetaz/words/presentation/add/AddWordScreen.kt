package com.moetaz.words.presentation.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moetaz.words.ui.theme.PrimaryGradient
import com.moetaz.words.ui.theme.SurfaceCardLight
import com.moetaz.words.ui.theme.TealDark

@OptIn(ExperimentalMaterial3Api::class)
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
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Entry" else "Add Entry",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(
                            text = "Cancel",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.handleIntent(AddWordIntent.SaveWord) },
                        enabled = !state.isSaving
                    ) {
                        Text(
                            text = "SAVE",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
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
                    // English Word Input Field
                    OutlinedTextField(
                        value = state.word,
                        onValueChange = { viewModel.handleIntent(AddWordIntent.OnWordChanged(it)) },
                        label = { Text("English Word (Required)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealDark,
                            unfocusedBorderColor = TealDark.copy(alpha = 0.5f),
                            focusedContainerColor = SurfaceCardLight.copy(alpha = 0.5f),
                            unfocusedContainerColor = SurfaceCardLight.copy(alpha = 0.5f)
                        ),
                        singleLine = true
                    )

                    // Phonetic Pronunciation Input Field
                    OutlinedTextField(
                        value = state.phonetic,
                        onValueChange = { viewModel.handleIntent(AddWordIntent.OnPhoneticChanged(it)) },
                        label = { Text("Phonetic Pronunciation (e.g. /æsˈθɛtɪk/)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealDark,
                            unfocusedBorderColor = TealDark.copy(alpha = 0.5f),
                            focusedContainerColor = SurfaceCardLight.copy(alpha = 0.5f),
                            unfocusedContainerColor = SurfaceCardLight.copy(alpha = 0.5f)
                        ),
                        singleLine = true
                    )

                    // Definition Input Field
                    OutlinedTextField(
                        value = state.definition,
                        onValueChange = { viewModel.handleIntent(AddWordIntent.OnDefinitionChanged(it)) },
                        label = { Text("Definition") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealDark,
                            unfocusedBorderColor = TealDark.copy(alpha = 0.5f),
                            focusedContainerColor = SurfaceCardLight.copy(alpha = 0.5f),
                            unfocusedContainerColor = SurfaceCardLight.copy(alpha = 0.5f)
                        )
                    )

                    // Arabic Translation Input Field
                    val mainTranslation = state.translations.firstOrNull() ?: ""
                    OutlinedTextField(
                        value = mainTranslation,
                        onValueChange = { text ->
                            viewModel.handleIntent(AddWordIntent.OnTranslationChanged(0, text))
                        },
                        label = { Text("Arabic Translation (Required)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealDark,
                            unfocusedBorderColor = TealDark.copy(alpha = 0.5f),
                            focusedContainerColor = SurfaceCardLight.copy(alpha = 0.5f),
                            unfocusedContainerColor = SurfaceCardLight.copy(alpha = 0.5f)
                        ),
                        singleLine = true
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
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = SurfaceCardLight
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
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
                                            focusedContainerColor = Color.White.copy(alpha = 0.8f),
                                            unfocusedContainerColor = Color.White.copy(alpha = 0.6f)
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
                                        focusedContainerColor = Color.White.copy(alpha = 0.8f),
                                        unfocusedContainerColor = Color.White.copy(alpha = 0.6f)
                                    )
                                )
                            }
                        }
                    }

                    // "+ Add Example" Button Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                viewModel.handleIntent(AddWordIntent.AddExampleField)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SurfaceCardLight
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                                tint = TealDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Add Example",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TealDark
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

                    // Full-width Save Changes Gradient Pill Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(CircleShape)
                            .background(PrimaryGradient)
                            .clickable(
                                enabled = !state.isSaving,
                                onClick = { viewModel.handleIntent(AddWordIntent.SaveWord) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (isEditing) "Save Changes" else "Save Word",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
