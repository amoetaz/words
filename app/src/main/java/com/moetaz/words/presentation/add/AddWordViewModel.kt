package com.moetaz.words.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moetaz.words.domain.model.Example
import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.usecase.AddWordUseCase
import com.moetaz.words.domain.usecase.GetWordByIdUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddWordViewModel(
    private val addWordUseCase: AddWordUseCase,
    private val getWordByIdUseCase: GetWordByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddWordState())
    val state: StateFlow<AddWordState> = _state.asStateFlow()

    fun handleIntent(intent: AddWordIntent) {
        when (intent) {
            is AddWordIntent.LoadWord -> loadWord(intent.id)
            is AddWordIntent.OnWordChanged -> {
                _state.update { it.copy(word = intent.word) }
            }
            is AddWordIntent.OnTranslationChanged -> {
                val newList = _state.value.translations.toMutableList()
                newList[intent.index] = intent.translation
                _state.update { it.copy(translations = newList) }
            }
            is AddWordIntent.OnExampleEnglishChanged -> {
                val newList = _state.value.examples.toMutableList()
                newList[intent.index] = newList[intent.index].copy(english = intent.english)
                _state.update { it.copy(examples = newList) }
            }
            is AddWordIntent.OnExampleArabicChanged -> {
                val newList = _state.value.examples.toMutableList()
                newList[intent.index] = newList[intent.index].copy(arabic = intent.arabic)
                _state.update { it.copy(examples = newList) }
            }
            is AddWordIntent.AddTranslationField -> {
                _state.update { it.copy(translations = it.translations + "") }
            }
            is AddWordIntent.AddExampleField -> {
                _state.update { it.copy(examples = it.examples + Example("", "")) }
            }
            is AddWordIntent.SaveWord -> saveWord()
        }
    }

    private fun loadWord(id: Long) {
        if (id <= 0 || _state.value.wordId == id) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                getWordByIdUseCase(id).firstOrNull()?.let { loadedWord ->
                    _state.update {
                        it.copy(
                            wordId = loadedWord.id,
                            word = loadedWord.word,
                            translations = loadedWord.translations.ifEmpty { listOf("") },
                            examples = loadedWord.examples.ifEmpty { listOf(Example("", "")) },
                            isLoading = false
                        )
                    }
                } ?: _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
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
                    id = currentState.wordId ?: 0,
                    word = currentState.word,
                    translations = currentState.translations.filter { it.isNotBlank() },
                    examples = currentState.examples.filter { it.english.isNotBlank() || it.arabic.isNotBlank() }
                )
                addWordUseCase(word)
                _state.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}
