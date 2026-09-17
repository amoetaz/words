package com.moetaz.words.presentation.add

sealed interface AddWordIntent {
    data class OnWordChanged(val word: String) : AddWordIntent
    data class OnTranslationChanged(val index: Int, val translation: String) : AddWordIntent
    data class OnExampleChanged(val index: Int, val example: String) : AddWordIntent
    data object AddTranslationField : AddWordIntent
    data object AddExampleField : AddWordIntent
    data object SaveWord : AddWordIntent
}

data class AddWordState(
    val word: String = "",
    val translations: List<String> = listOf(""),
    val examples: List<String> = listOf(""),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)
