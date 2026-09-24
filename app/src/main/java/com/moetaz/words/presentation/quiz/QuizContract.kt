package com.moetaz.words.presentation.quiz

import com.moetaz.words.domain.model.Word

data class QuizQuestion(
    val word: Word,
    val options: List<String>,
    val correctAnswer: String
)

data class QuizState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOption: String? = null,
    val isAnswered: Boolean = false,
    val score: Int = 0,
    val isQuizFinished: Boolean = false,
    val isLoading: Boolean = true
)

sealed interface QuizIntent {
    data class SelectOption(val option: String) : QuizIntent
    data object NextQuestion : QuizIntent
    data object RestartQuiz : QuizIntent
}
