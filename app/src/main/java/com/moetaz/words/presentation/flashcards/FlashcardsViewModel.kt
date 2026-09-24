package com.moetaz.words.presentation.flashcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moetaz.words.domain.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlashcardsViewModel(
    private val repository: WordRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FlashcardsState())
    val state: StateFlow<FlashcardsState> = _state.asStateFlow()

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch {
            repository.getWords().collect { words ->
                _state.update {
                    it.copy(
                        words = words,
                        isLoading = false,
                        currentIndex = if (it.currentIndex >= words.size) 0 else it.currentIndex
                    )
                }
            }
        }
    }

    fun handleIntent(intent: FlashcardsIntent) {
        when (intent) {
            FlashcardsIntent.FlipCard -> {
                _state.update { it.copy(isFlipped = !it.isFlipped) }
            }
            FlashcardsIntent.NextCard -> {
                _state.update {
                    val nextIndex = if (it.words.isNotEmpty()) (it.currentIndex + 1) % it.words.size else 0
                    it.copy(currentIndex = nextIndex, isFlipped = false)
                }
            }
            FlashcardsIntent.PreviousCard -> {
                _state.update {
                    val prevIndex = if (it.words.isNotEmpty()) (it.currentIndex - 1 + it.words.size) % it.words.size else 0
                    it.copy(currentIndex = prevIndex, isFlipped = false)
                }
            }
            is FlashcardsIntent.MarkMastery -> {
                viewModelScope.launch {
                    repository.updateMasteryStatus(intent.wordId, intent.status)
                    val nextIndex = if (_state.value.words.isNotEmpty()) {
                        (_state.value.currentIndex + 1) % _state.value.words.size
                    } else 0
                    _state.update { it.copy(currentIndex = nextIndex, isFlipped = false) }
                }
            }
            is FlashcardsIntent.ToggleFavorite -> {
                viewModelScope.launch {
                    repository.toggleFavorite(intent.wordId, intent.isFavorite)
                }
            }
        }
    }
}
