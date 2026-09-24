package com.moetaz.words.presentation.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moetaz.words.data.local.dto.WordsResponseDto
import com.moetaz.words.domain.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class SettingsViewModel(
    private val repository: WordRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    init {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        val currentLang = if (!currentLocales.isEmpty) currentLocales.get(0)?.language ?: "en" else "en"
        _state.update { it.copy(selectedLanguage = currentLang) }
    }

    fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ChangeLanguage -> setLanguage(intent.languageCode)
            is SettingsIntent.ImportJsonContent -> importJson(intent.jsonContent)
            is SettingsIntent.ClearImportStatus -> {
                _state.update { it.copy(importMessage = null, importError = null) }
            }
        }
    }

    private fun setLanguage(languageCode: String) {
        _state.update { it.copy(selectedLanguage = languageCode) }
        val appLocales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocales)
    }

    private fun importJson(jsonContent: String) {
        viewModelScope.launch {
            _state.update { it.copy(isImporting = true, importMessage = null, importError = null) }
            try {
                val response = json.decodeFromString<WordsResponseDto>(jsonContent)
                val words = response.words.map { it.toEntity().toWord() }
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
}
