package com.moetaz.words.presentation.settings

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moetaz.words.data.local.dto.WordDto
import com.moetaz.words.data.local.dto.WordsResponseDto
import com.moetaz.words.domain.model.Example
import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.model.WordMasteryStatus
import com.moetaz.words.domain.repository.WordRepository
import com.moetaz.words.ui.theme.AppThemeMode
import com.moetaz.words.ui.theme.ThemePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SettingsViewModel(
    private val repository: WordRepository,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        explicitNulls = false
        encodeDefaults = true
    }

    init {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        val currentLang = if (!currentLocales.isEmpty) currentLocales.get(0)?.language ?: "en" else "en"
        _state.update {
            it.copy(
                selectedLanguage = currentLang,
                selectedThemeMode = themePreferences.themeMode.value
            )
        }
    }

    fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ChangeLanguage -> setLanguage(intent.languageCode)
            is SettingsIntent.ChangeThemeMode -> setThemeMode(intent.themeMode)
            is SettingsIntent.ImportJsonContent -> importJson(intent.jsonContent)
            is SettingsIntent.ExportJson -> exportJson(intent.uri, intent.context)
            is SettingsIntent.DownloadExampleJson -> downloadExampleJson(intent.uri, intent.context)
            is SettingsIntent.ClearImportStatus -> {
                _state.update { it.copy(importMessage = null, importError = null) }
            }
            is SettingsIntent.ClearExportStatus -> {
                _state.update { it.copy(exportMessage = null, exportError = null) }
            }
        }
    }

    private fun setLanguage(languageCode: String) {
        _state.update { it.copy(selectedLanguage = languageCode) }
        val appLocales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocales)
    }

    private fun setThemeMode(mode: AppThemeMode) {
        themePreferences.setThemeMode(mode)
        _state.update { it.copy(selectedThemeMode = mode) }
    }

    private fun importJson(jsonContent: String) {
        viewModelScope.launch {
            _state.update { it.copy(isImporting = true, importMessage = null, importError = null) }
            try {
                val words = parseWordsFromJson(jsonContent)
                if (words.isNotEmpty()) {
                    words.forEach { word ->
                        repository.insertWord(word)
                    }
                    _state.update {
                        it.copy(
                            isImporting = false,
                            importMessage = "Successfully imported ${words.size} word(s)!"
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isImporting = false,
                            importError = "No words found in JSON file."
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isImporting = false,
                        importError = "Failed to parse JSON file: ${e.message}"
                    )
                }
            }
        }
    }

    private fun exportJson(uri: Uri, context: Context) {
        viewModelScope.launch {
            _state.update { it.copy(isExporting = true, exportMessage = null, exportError = null) }
            try {
                val words = repository.getWords().first()
                if (words.isNotEmpty()) {
                    val dtos = words.map { WordDto.fromWord(it) }
                    val response = WordsResponseDto(words = dtos)
                    val jsonString = json.encodeToString(response)
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(jsonString.toByteArray())
                    }
                    _state.update {
                        it.copy(
                            isExporting = false,
                            exportMessage = "Successfully exported ${words.size} word(s)!"
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isExporting = false,
                            exportError = "No words in database to export."
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isExporting = false,
                        exportError = "Failed to export JSON: ${e.message}"
                    )
                }
            }
        }
    }

    private fun downloadExampleJson(uri: Uri, context: Context) {
        viewModelScope.launch {
            _state.update { it.copy(isImporting = true, importMessage = null, importError = null) }
            try {
                val sampleWords = WordsResponseDto(
                    words = listOf(
                        WordDto(
                            word = "Eloquent",
                            translations = listOf("فصيح", "بليغ"),
                            examples = listOf(
                                Example(
                                    english = "She gave an eloquent speech.",
                                    arabic = "ألقيت خطبة بليغة."
                                )
                            ),
                            phonetic = "/ˈɛl.ə.kwənt/",
                            definition = "Fluent or persuasive in speaking or writing.",
                            definitionTranslation = "طلاقة أو إقناع في التحدث أو الكتابة.",

                        ),
                        WordDto(
                            word = "Meticulous",
                            translations = listOf("دقيق", "شديد العناية"),
                            examples = listOf(
                                Example(
                                    english = "He is meticulous about his work.",
                                    arabic = "إنه دقيق للغاية في عمله."
                                )
                            ),
                            phonetic = "/məˈtɪk.jə.ləs/",
                            definition = "Showing great attention to detail; very careful and precise.",
                            definitionTranslation = "إبداء اهتمام كبير بالتفاصيل؛ حريص ودقيق للغاية.",

                        )
                    )
                )
                val jsonString = json.encodeToString(sampleWords)
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(jsonString.toByteArray())
                }
                _state.update {
                    it.copy(
                        isImporting = false,
                        importMessage = "Example JSON file saved successfully!"
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isImporting = false,
                        importError = "Failed to save example JSON: ${e.message}"
                    )
                }
            }
        }
    }

    private fun parseWordsFromJson(jsonContent: String): List<Word> {
        val trimmed = jsonContent.trim()
        val dtos: List<WordDto> = if (trimmed.startsWith("[")) {
            json.decodeFromString<List<WordDto>>(trimmed)
        } else {
            json.decodeFromString<WordsResponseDto>(trimmed).words
        }
        return dtos.map { it.toEntity().toWord() }
    }
}
