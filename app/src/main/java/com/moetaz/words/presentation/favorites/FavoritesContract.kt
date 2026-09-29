package com.moetaz.words.presentation.favorites

import com.moetaz.words.domain.model.Word

sealed interface FavoritesIntent {
    data object LoadFavorites : FavoritesIntent
    data class OnSearchQueryChanged(val query: String) : FavoritesIntent
    data class ToggleFavorite(val id: Long, val isFavorite: Boolean) : FavoritesIntent
}

data class FavoritesState(
    val searchQuery: String = "",
    val favoriteWords: List<Word> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val filteredFavorites: List<Word>
        get() = if (searchQuery.isBlank()) {
            favoriteWords
        } else {
            favoriteWords.filter {
                it.word.contains(searchQuery, ignoreCase = true) ||
                        it.translations.any { t -> t.contains(searchQuery, ignoreCase = true) }
            }
        }
}
