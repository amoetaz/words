package com.moetaz.words.presentation.settings

import com.moetaz.words.ui.theme.AppThemeMode

sealed interface SettingsIntent {
    data class ChangeLanguage(val languageCode: String) : SettingsIntent
    data class ChangeThemeMode(val themeMode: AppThemeMode) : SettingsIntent
    data class ImportJsonContent(val jsonContent: String) : SettingsIntent
    data object ClearImportStatus : SettingsIntent
}

data class SettingsState(
    val selectedLanguage: String = "en",
    val selectedThemeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val isImporting: Boolean = false,
    val importMessage: String? = null,
    val importError: String? = null
)
