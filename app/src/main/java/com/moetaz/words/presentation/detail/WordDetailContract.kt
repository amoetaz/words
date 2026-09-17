package com.moetaz.words.presentation.detail

import com.moetaz.words.domain.model.Word

sealed interface WordDetailIntent {
    data class LoadWord(val id: Long) : WordDetailIntent
}

data class WordDetailState(
    val word: Word? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
