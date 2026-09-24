package com.moetaz.words.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moetaz.words.domain.repository.WordRepository
import com.moetaz.words.domain.usecase.GetWordByIdUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WordDetailViewModel(
    private val getWordByIdUseCase: GetWordByIdUseCase,
    private val repository: WordRepository? = null
) : ViewModel() {

    private val _state = MutableStateFlow(WordDetailState())
    val state: StateFlow<WordDetailState> = _state.asStateFlow()

    fun handleIntent(intent: WordDetailIntent) {
        when (intent) {
            is WordDetailIntent.LoadWord -> loadWord(intent.id)
            is WordDetailIntent.ToggleFavorite -> {
                val currentWord = _state.value.word ?: return
                viewModelScope.launch {
                    repository?.toggleFavorite(currentWord.id, intent.isFavorite)
                }
            }
            is WordDetailIntent.UpdateMasteryStatus -> {
                val currentWord = _state.value.word ?: return
                viewModelScope.launch {
                    repository?.updateMasteryStatus(currentWord.id, intent.status)
                }
            }
        }
    }

    private fun loadWord(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getWordByIdUseCase(id)
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { word ->
                    _state.update { it.copy(isLoading = false, word = word) }
                }
        }
    }
}
