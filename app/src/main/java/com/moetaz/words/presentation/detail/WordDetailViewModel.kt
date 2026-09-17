package com.moetaz.words.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moetaz.words.domain.usecase.GetWordByIdUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WordDetailViewModel(
    private val getWordByIdUseCase: GetWordByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WordDetailState())
    val state: StateFlow<WordDetailState> = _state.asStateFlow()

    fun handleIntent(intent: WordDetailIntent) {
        when (intent) {
            is WordDetailIntent.LoadWord -> loadWord(intent.id)
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
