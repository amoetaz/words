package com.moetaz.words.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moetaz.words.domain.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizViewModel(
    private val repository: WordRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state.asStateFlow()

    init {
        generateQuiz()
    }

    fun generateQuiz() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getWords().collect { words ->
                if (words.size >= 2) {
                    val shuffledWords = words.shuffled().take(10)
                    val allTranslations = words.flatMap { it.translations }.distinct()

                    val questions = shuffledWords.map { targetWord ->
                        val correctAnswer = targetWord.translations.firstOrNull() ?: ""
                        val distractors = allTranslations
                            .filter { it != correctAnswer }
                            .shuffled()
                            .take(3)
                        val options = (distractors + correctAnswer).shuffled()
                        QuizQuestion(
                            word = targetWord,
                            options = options,
                            correctAnswer = correctAnswer
                        )
                    }

                    _state.update {
                        QuizState(
                            questions = questions,
                            currentQuestionIndex = 0,
                            selectedOption = null,
                            isAnswered = false,
                            score = 0,
                            isQuizFinished = false,
                            isLoading = false
                        )
                    }
                } else {
                    _state.update { QuizState(isLoading = false) }
                }
            }
        }
    }

    fun handleIntent(intent: QuizIntent) {
        when (intent) {
            is QuizIntent.SelectOption -> {
                if (_state.value.isAnswered) return
                val currentQ = _state.value.questions.getOrNull(_state.value.currentQuestionIndex) ?: return
                val isCorrect = intent.option == currentQ.correctAnswer
                val newScore = if (isCorrect) _state.value.score + 1 else _state.value.score

                _state.update {
                    it.copy(
                        selectedOption = intent.option,
                        isAnswered = true,
                        score = newScore
                    )
                }
            }
            QuizIntent.NextQuestion -> {
                val nextIdx = _state.value.currentQuestionIndex + 1
                if (nextIdx < _state.value.questions.size) {
                    _state.update {
                        it.copy(
                            currentQuestionIndex = nextIdx,
                            selectedOption = null,
                            isAnswered = false
                        )
                    }
                } else {
                    _state.update { it.copy(isQuizFinished = true) }
                }
            }
            QuizIntent.RestartQuiz -> {
                generateQuiz()
            }
        }
    }
}
