package com.moetaz.words.presentation.flashcards

import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.model.WordMasteryStatus

data class FlashcardsState(
    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isLoading: Boolean = true
)

sealed interface FlashcardsIntent {
    data object FlipCard : FlashcardsIntent
    data object NextCard : FlashcardsIntent
    data object PreviousCard : FlashcardsIntent
    data class MarkMastery(val wordId: Long, val status: WordMasteryStatus) : FlashcardsIntent
    data class ToggleFavorite(val wordId: Long, val isFavorite: Boolean) : FlashcardsIntent
}
