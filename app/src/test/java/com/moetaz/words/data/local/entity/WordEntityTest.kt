package com.moetaz.words.data.local.entity

import com.moetaz.words.domain.model.Example
import com.moetaz.words.domain.model.Word
import org.junit.Assert.assertEquals
import org.junit.Test

class WordEntityTest {

    @Test
    fun toWord_mapsAllFieldsCorrectly() {
        val entity = WordEntity(
            id = 42L,
            word = "Serendipity",
            translations = listOf("حسن الحظ", "مصادفة سعيدة"),
            examples = listOf(Example("Finding that book was pure serendipity.", "العثور على ذلك الكتاب كان مصادفة نادرة."))
        )

        val domainWord = entity.toWord()

        assertEquals(42L, domainWord.id)
        assertEquals("Serendipity", domainWord.word)
        assertEquals(listOf("حسن الحظ", "مصادفة سعيدة"), domainWord.translations)
        assertEquals(listOf(Example("Finding that book was pure serendipity.", "العثور على ذلك الكتاب كان مصادفة نادرة.")), domainWord.examples)
    }

    @Test
    fun fromWord_mapsAllFieldsCorrectly() {
        val word = Word(
            id = 100L,
            word = "Ephemeral",
            translations = listOf("سريع الزوال", "مؤقت"),
            examples = listOf(Example("Fame in the digital age is often ephemeral.", "الشهرة في العصر الرقمي مؤقتة."))
        )

        val entity = WordEntity.fromWord(word)

        assertEquals(100L, entity.id)
        assertEquals("Ephemeral", entity.word)
        assertEquals(listOf("سريع الزوال", "مؤقت"), entity.translations)
        assertEquals(listOf(Example("Fame in the digital age is often ephemeral.", "الشهرة في العصر الرقمي مؤقتة.")), entity.examples)
    }

    @Test
    fun toWord_withEmptyLists_preservesEmptyLists() {
        val entity = WordEntity(
            id = 1L,
            word = "Test",
            translations = emptyList(),
            examples = emptyList()
        )

        val domainWord = entity.toWord()

        assertEquals(1L, domainWord.id)
        assertEquals("Test", domainWord.word)
        assertEquals(emptyList<String>(), domainWord.translations)
        assertEquals(emptyList<Example>(), domainWord.examples)
    }
}
