package com.moetaz.words.presentation.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordScreen(
    viewModel: AddWordViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Word") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = state.word,
                onValueChange = { viewModel.handleIntent(AddWordIntent.OnWordChanged(it)) },
                label = { Text("Word") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Translations", style = MaterialTheme.typography.titleMedium)
            state.translations.forEachIndexed { index, translation ->
                TextField(
                    value = translation,
                    onValueChange = {
                        viewModel.handleIntent(AddWordIntent.OnTranslationChanged(index, it))
                    },
                    label = { Text("Translation ${index + 1}") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            TextButton(onClick = { viewModel.handleIntent(AddWordIntent.AddTranslationField) }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Translation")
            }

            Text("Example Sentences", style = MaterialTheme.typography.titleMedium)
            state.examples.forEachIndexed { index, example ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = example.english,
                        onValueChange = {
                            viewModel.handleIntent(AddWordIntent.OnExampleEnglishChanged(index, it))
                        },
                        label = { Text("Example (English) ${index + 1}") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = example.arabic,
                        onValueChange = {
                            viewModel.handleIntent(AddWordIntent.OnExampleArabicChanged(index, it))
                        },
                        label = { Text("Example (Arabic) ${index + 1}") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            TextButton(onClick = { viewModel.handleIntent(AddWordIntent.AddExampleField) }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Example")
            }

            if (state.error != null) {
                Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { viewModel.handleIntent(AddWordIntent.SaveWord) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save Word")
                }
            }
        }
    }
}
