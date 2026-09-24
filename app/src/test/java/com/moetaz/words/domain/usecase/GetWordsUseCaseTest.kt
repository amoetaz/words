package com.moetaz.words.domain.usecase

import app.cash.turbine.test
import com.moetaz.words.domain.model.Example
import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.repository.WordRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetWordsUseCaseTest {

    private lateinit var repository: WordRepository
    private lateinit var useCase: GetWordsUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetWordsUseCase(repository)
    }

    @Test
    fun invoke_delegatesToRepositoryAndReturnsWordsFlow() = runTest {
        val words = listOf(
            Word(1L, "Gratitude", listOf("امتنان"), listOf(Example("Express gratitude daily.", "عبر عن الامتنان يوميا."))),
            Word(2L, "Courage", listOf("شجاعة"), listOf(Example("Courage under pressure.", "الشجاعة تحت الضغط.")))
        )
        every { repository.getWords() } returns flowOf(words)

        useCase().test {
            val result = awaitItem()
            assertEquals(words, result)
            awaitComplete()
        }

        verify(exactly = 1) { repository.getWords() }
    }
}
