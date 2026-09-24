package com.moetaz.words.data.repository

import app.cash.turbine.test
import com.moetaz.words.data.local.dao.WordDao
import com.moetaz.words.data.local.entity.WordEntity
import com.moetaz.words.domain.model.Example
import com.moetaz.words.domain.model.Word
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class WordRepositoryImplTest {

    private lateinit var dao: WordDao
    private lateinit var repository: WordRepositoryImpl

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        repository = WordRepositoryImpl(dao)
    }

    @Test
    fun getWords_mapsEntitiesToDomainWords() = runTest {
        val entities = listOf(
            WordEntity(1L, "Word1", listOf("ترجمة1"), listOf(Example("Ex1", "مثال1"))),
            WordEntity(2L, "Word2", listOf("ترجمة2"), listOf(Example("Ex2", "مثال2")))
        )
        every { dao.getWords() } returns flowOf(entities)

        repository.getWords().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("Word1", result[0].word)
            assertEquals(1L, result[0].id)
            assertEquals("Word2", result[1].word)
            assertEquals(2L, result[1].id)
            awaitComplete()
        }
    }

    @Test
    fun getWordById_whenEntityExists_mapsToDomainWord() = runTest {
        val entity = WordEntity(5L, "Test", listOf("Translation"), listOf(Example("Example", "مثال")))
        every { dao.getWordById(5L) } returns flowOf(entity)

        repository.getWordById(5L).test {
            val result = awaitItem()
            assertEquals(5L, result?.id)
            assertEquals("Test", result?.word)
            assertEquals(listOf("Translation"), result?.translations)
            assertEquals(listOf(Example("Example", "مثال")), result?.examples)
            awaitComplete()
        }
    }

    @Test
    fun getWordById_whenEntityDoesNotExist_emitsNull() = runTest {
        every { dao.getWordById(999L) } returns flowOf(null)

        repository.getWordById(999L).test {
            val result = awaitItem()
            assertNull(result)
            awaitComplete()
        }
    }

    @Test
    fun insertWord_convertsToEntityAndCallsDao() = runTest {
        val word = Word(
            id = 10L,
            word = "Eloquent",
            translations = listOf("فصيح", "بليغ"),
            examples = listOf(Example("An eloquent speaker.", "متحدث فصيح."))
        )
        val expectedEntity = WordEntity.fromWord(word)

        repository.insertWord(word)

        coVerify(exactly = 1) { dao.insertWord(expectedEntity) }
    }

    @Test
    fun deleteWord_convertsToEntityAndCallsDao() = runTest {
        val word = Word(
            id = 10L,
            word = "Eloquent",
            translations = listOf("فصيح"),
            examples = emptyList()
        )
        val expectedEntity = WordEntity.fromWord(word)

        repository.deleteWord(word)

        coVerify(exactly = 1) { dao.deleteWord(expectedEntity) }
    }
}
