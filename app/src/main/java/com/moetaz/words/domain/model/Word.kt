package com.moetaz.words.domain.model

enum class WordMasteryStatus {
    LEARNING,
    REVIEWING,
    MASTERED
}

data class Word(
    val id: Long = 0,
    val word: String,
    val translations: List<String> = emptyList(),
    val examples: List<Example> = emptyList(),
    val phonetic: String? = null,
    val definition: String? = null,
    val definitionTranslation: String? = null,
    val masteryStatus: WordMasteryStatus = WordMasteryStatus.LEARNING,
    val isFavorite: Boolean = false
)
