package com.moetaz.words.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moetaz.words.domain.usecase.GetWordsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WordListViewModel(
    private val getWordsUseCase: GetWordsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WordListState())
    val state: StateFlow<WordListState> = _state.asStateFlow()

    init {
        handleIntent(WordListIntent.LoadWords)
    }

    fun handleIntent(intent: WordListIntent) {
        when (intent) {
            is WordListIntent.LoadWords -> loadWords()
            is WordListIntent.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }
            else -> {} // Navigation handled by UI callback to Navigator
        }
    }

    private fun loadWords() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getWordsUseCase()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { words ->
                    _state.update { it.copy(isLoading = false, words = words) }
                }
        }
    }
}
