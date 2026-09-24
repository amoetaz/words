package com.moetaz.words.domain.usecase

import app.cash.turbine.test
import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.repository.WordRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GetWordByIdUseCaseTest {

    private lateinit var repository: WordRepository
    private lateinit var useCase: GetWordByIdUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetWordByIdUseCase(repository)
    }

    @Test
    fun invoke_delegatesToRepositoryWithCorrectId() = runTest {
        val word = Word(42L, "Serendipity", listOf("مصادفة سعيدة"), emptyList())
        every { repository.getWordById(42L) } returns flowOf(word)

        useCase(42L).test {
            val result = awaitItem()
            assertEquals(word, result)
            awaitComplete()
        }

        verify(exactly = 1) { repository.getWordById(42L) }
    }

    @Test
    fun invoke_whenWordNotFound_emitsNull() = runTest {
        every { repository.getWordById(100L) } returns flowOf(null)

        useCase(100L).test {
            val result = awaitItem()
            assertNull(result)
            awaitComplete()
        }

        verify(exactly = 1) { repository.getWordById(100L) }
    }
}
