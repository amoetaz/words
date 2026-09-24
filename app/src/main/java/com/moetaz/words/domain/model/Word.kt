package com.moetaz.words.domain.model

data class Word(
    val id: Long = 0,
    val word: String,
    val translations: List<String> = emptyList(),
    val examples: List<Example> = emptyList(),
    val phonetic: String? = null,
    val definition: String? = null
)
