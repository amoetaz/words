package com.moetaz.words.presentation.add

import com.moetaz.words.domain.model.Example

sealed interface AddWordIntent {
    data class LoadWord(val id: Long) : AddWordIntent
    data class OnWordChanged(val word: String) : AddWordIntent
    data class OnPhoneticChanged(val phonetic: String) : AddWordIntent
    data class OnDefinitionChanged(val definition: String) : AddWordIntent
    data class OnDefinitionTranslationChanged(val definitionTranslation: String) : AddWordIntent
    data class OnTranslationChanged(val index: Int, val translation: String) : AddWordIntent
    data class OnExampleEnglishChanged(val index: Int, val english: String) : AddWordIntent
    data class OnExampleArabicChanged(val index: Int, val arabic: String) : AddWordIntent
    data class RemoveExampleField(val index: Int) : AddWordIntent
    data object AddTranslationField : AddWordIntent
    data object AddExampleField : AddWordIntent
    data object SaveWord : AddWordIntent
}

data class AddWordState(
    val wordId: Long? = null,
    val word: String = "",
    val phonetic: String = "",
    val definition: String = "",
    val definitionTranslation: String = "",
    val translations: List<String> = listOf(""),
    val examples: List<Example> = listOf(Example("", "")),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)
