package com.moetaz.words.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moetaz.words.domain.model.Example
import com.moetaz.words.domain.model.Word

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val translations: List<String>,
    val examples: List<Example>
) {
    fun toWord(): Word = Word(
        id = id,
        word = word,
        translations = translations,
        examples = examples
    )

    companion object {
        fun fromWord(word: Word): WordEntity = WordEntity(
            id = word.id,
            word = word.word,
            translations = word.translations,
            examples = word.examples
        )
    }
}
