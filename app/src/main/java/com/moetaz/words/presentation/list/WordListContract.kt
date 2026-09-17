package com.moetaz.words.presentation.list

import com.moetaz.words.domain.model.Word

sealed interface WordListIntent {
    data object LoadWords : WordListIntent
    data class OnWordClick(val id: Long) : WordListIntent
    data object OnAddWordClick : WordListIntent
}

data class WordListState(
    val words: List<Word> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
