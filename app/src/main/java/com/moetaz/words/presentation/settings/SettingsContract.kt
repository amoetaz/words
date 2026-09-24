package com.moetaz.words.presentation.settings

sealed interface SettingsIntent {
    data class ChangeLanguage(val languageCode: String) : SettingsIntent
    data class ImportJsonContent(val jsonContent: String) : SettingsIntent
    data object ClearImportStatus : SettingsIntent
}

data class SettingsState(
    val selectedLanguage: String = "en",
    val isImporting: Boolean = false,
    val importMessage: String? = null,
    val importError: String? = null
)
