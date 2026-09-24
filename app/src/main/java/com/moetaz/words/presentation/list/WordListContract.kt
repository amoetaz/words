package com.moetaz.words.presentation.list

import com.moetaz.words.domain.model.Word

sealed interface WordListIntent {
    data object LoadWords : WordListIntent
    data class OnSearchQueryChanged(val query: String) : WordListIntent
    data class OnWordClick(val id: Long) : WordListIntent
    data object OnAddWordClick : WordListIntent
}

data class WordListState(
    val searchQuery: String = "",
    val words: List<Word> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val filteredWords: List<Word>
        get() = if (searchQuery.isBlank()) {
            words
        } else {
            words.filter { it.word.contains(searchQuery, ignoreCase = true) }
        }
}
