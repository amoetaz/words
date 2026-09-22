package com.moetaz.words.data.local.dto

import com.moetaz.words.data.local.entity.WordEntity
import com.moetaz.words.domain.model.Example
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WordsResponseDto(
    val words: List<WordDto> = emptyList()
)

@Serializable
data class WordDto(
    val word: String,
    @SerialName("translation") val translations: List<String> = emptyList(),
    val examples: List<Example> = emptyList()
) {
    fun toEntity(): WordEntity = WordEntity(
        word = word,
        translations = translations,
        examples = examples
    )
}
