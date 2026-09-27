package com.moetaz.words.presentation.settings

import android.content.Context
import android.net.Uri
import com.moetaz.words.ui.theme.AppThemeMode

sealed interface SettingsIntent {
    data class ChangeLanguage(val languageCode: String) : SettingsIntent
    data class ChangeThemeMode(val themeMode: AppThemeMode) : SettingsIntent
    data class ImportJsonContent(val jsonContent: String) : SettingsIntent
    data class ExportJson(val uri: Uri, val context: Context) : SettingsIntent
    data class DownloadExampleJson(val uri: Uri, val context: Context) : SettingsIntent
    data object ClearImportStatus : SettingsIntent
    data object ClearExportStatus : SettingsIntent
}

data class SettingsState(
    val selectedLanguage: String = "en",
    val selectedThemeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val isImporting: Boolean = false,
    val isExporting: Boolean = false,
    val importMessage: String? = null,
    val importError: String? = null,
    val exportMessage: String? = null,
    val exportError: String? = null
)
