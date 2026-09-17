package com.moetaz.words.domain.model

data class Word(
    val id: Long = 0,
    val word: String,
    val translations: List<String>,
    val examples: List<String>
)
