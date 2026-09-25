package com.moetaz.words.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moetaz.words.domain.model.Example
import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.model.WordMasteryStatus

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val translations: List<String> = emptyList(),
    val examples: List<Example> = emptyList(),
    val phonetic: String? = null,
    val definition: String? = null,
    val definitionTranslation: String? = null,
    val masteryStatus: WordMasteryStatus = WordMasteryStatus.LEARNING,
    val isFavorite: Boolean = false
) {
    fun toWord(): Word = Word(
        id = id,
        word = word,
        translations = translations,
        examples = examples,
        phonetic = phonetic,
        definition = definition,
        definitionTranslation = definitionTranslation,
        masteryStatus = masteryStatus,
        isFavorite = isFavorite
    )

    companion object {
        fun fromWord(word: Word): WordEntity = WordEntity(
            id = word.id,
            word = word.word,
            translations = word.translations,
            examples = word.examples,
            phonetic = word.phonetic,
            definition = word.definition,
            definitionTranslation = word.definitionTranslation,
            masteryStatus = word.masteryStatus,
            isFavorite = word.isFavorite
        )
    }
}
