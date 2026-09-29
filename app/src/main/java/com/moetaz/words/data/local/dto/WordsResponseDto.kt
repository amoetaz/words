package com.moetaz.words.data.local.dto

import com.moetaz.words.data.local.entity.WordEntity
import com.moetaz.words.domain.model.Example
import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.model.WordMasteryStatus
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class WordsResponseDto(
    val words: List<WordDto> = emptyList()
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class WordDto(
    val id: Long = 0L,
    val word: String,
    @JsonNames("translation")
    val translations: List<String> = emptyList(),
    val examples: List<Example> = emptyList(),
    @JsonNames("phonetics")
    val phonetic: String? = null,
    val definition: String? = null,
    @JsonNames("definitionTranslation")
    val definitionTranslation: String? = null
) {
    fun toEntity(): WordEntity = WordEntity(
        id = id,
        word = word,
        translations = translations,
        examples = examples,
        phonetic = phonetic,
        definition = definition,
        definitionTranslation = definitionTranslation,
    )

    companion object {
        fun fromWord(word: Word): WordDto = WordDto(
            id = word.id,
            word = word.word,
            translations = word.translations,
            examples = word.examples,
            phonetic = word.phonetic,
            definition = word.definition,
            definitionTranslation = word.definitionTranslation,

        )
    }
}
