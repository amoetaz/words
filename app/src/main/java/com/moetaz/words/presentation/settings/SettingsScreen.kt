package com.moetaz.words.presentation.settings

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moetaz.words.presentation.components.PrimaryActionButton
import com.moetaz.words.presentation.components.SelectableOptionRow
import com.moetaz.words.presentation.components.WordsCard
import com.moetaz.words.presentation.components.WordsTopAppBar
import com.moetaz.words.ui.theme.AppThemeMode
import com.moetaz.words.ui.theme.WordsTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    SettingsContent(
        state = state,
        onIntent = viewModel::handleIntent,
        onBack = onBack
    )
}

@Composable
fun SettingsContent(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val jsonPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { fileUri ->
            try {
                val content = context.contentResolver.openInputStream(fileUri)?.use { stream ->
                    stream.bufferedReader().readText()
                }
                if (!content.isNullOrBlank()) {
                    onIntent(SettingsIntent.ImportJsonContent(content))
                }
            } catch (_: Exception) {
                onIntent(SettingsIntent.ImportJsonContent(""))
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            WordsTopAppBar(
                title = "Settings",
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Theme Mode Selection Section
            WordsCard {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Theme / المظهر",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SelectableOptionRow(
                            label = "System Default / النظام الافتراضي",
                            selected = state.selectedThemeMode == AppThemeMode.SYSTEM,
                            onSelect = {
                                onIntent(SettingsIntent.ChangeThemeMode(AppThemeMode.SYSTEM))
                            }
                        )

                        SelectableOptionRow(
                            label = "Light / فاتح",
                            selected = state.selectedThemeMode == AppThemeMode.LIGHT,
                            onSelect = {
                                onIntent(SettingsIntent.ChangeThemeMode(AppThemeMode.LIGHT))
                            }
                        )

                        SelectableOptionRow(
                            label = "Dark / داكن",
                            selected = state.selectedThemeMode == AppThemeMode.DARK,
                            onSelect = {
                                onIntent(SettingsIntent.ChangeThemeMode(AppThemeMode.DARK))
                            }
                        )
                    }
                }
            }

            // Language Selection Section
            WordsCard {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Language / اللغة",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SelectableOptionRow(
                            label = "English",
                            selected = state.selectedLanguage == "en",
                            onSelect = {
                                onIntent(SettingsIntent.ChangeLanguage("en"))
                            }
                        )

                        SelectableOptionRow(
                            label = "العربية (Arabic)",
                            selected = state.selectedLanguage == "ar",
                            onSelect = {
                                onIntent(SettingsIntent.ChangeLanguage("ar"))
                            }
                        )
                    }
                }
            }

            // JSON Import Section
            WordsCard {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Import Words",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "Select a JSON file containing vocabulary words to import them directly into your database.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    PrimaryActionButton(
                        text = "Choose JSON File",
                        onClick = { jsonPickerLauncher.launch("application/json") },
                        isLoading = state.isImporting,
                        enabled = !state.isImporting
                    )

                    state.importMessage?.let { message ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = message,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                TextButton(
                                    onClick = {
                                        onIntent(SettingsIntent.ClearImportStatus)
                                    }
                                ) {
                                    Text("Dismiss", color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                        }
                    }

                    state.importError?.let { errorMessage ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = errorMessage,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = {
                                        onIntent(SettingsIntent.ClearImportStatus)
                                    }
                                ) {
                                    Text("Dismiss", color = MaterialTheme.colorScheme.onErrorContainer)
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
private fun SettingsContentPreview() {
    WordsTheme {
        SettingsContent(
            state = SettingsState(
                selectedLanguage = "en",
                selectedThemeMode = AppThemeMode.SYSTEM
            ),
            onIntent = {},
            onBack = {}
        )
    }
}
