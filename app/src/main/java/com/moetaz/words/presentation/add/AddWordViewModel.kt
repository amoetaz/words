package com.moetaz.words.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.usecase.AddWordUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddWordViewModel(
    private val addWordUseCase: AddWordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddWordState())
    val state: StateFlow<AddWordState> = _state.asStateFlow()

    fun handleIntent(intent: AddWordIntent) {
        when (intent) {
            is AddWordIntent.OnWordChanged -> {
                _state.update { it.copy(word = intent.word) }
            }
            is AddWordIntent.OnTranslationChanged -> {
                val newList = _state.value.translations.toMutableList()
                newList[intent.index] = intent.translation
                _state.update { it.copy(translations = newList) }
            }
            is AddWordIntent.OnExampleChanged -> {
                val newList = _state.value.examples.toMutableList()
                newList[intent.index] = intent.example
                _state.update { it.copy(examples = newList) }
            }
            is AddWordIntent.AddTranslationField -> {
                _state.update { it.copy(translations = it.translations + "") }
            }
            is AddWordIntent.AddExampleField -> {
                _state.update { it.copy(examples = it.examples + "") }
            }
            is AddWordIntent.SaveWord -> saveWord()
        }
    }

    private fun saveWord() {
        val currentState = _state.value
        if (currentState.word.isBlank()) {
            _state.update { it.copy(error = "Word cannot be empty") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            try {
                val word = Word(
                    word = currentState.word,
                    translations = currentState.translations.filter { it.isNotBlank() },
                    examples = currentState.examples.filter { it.isNotBlank() }
                )
                addWordUseCase(word)
                _state.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}
