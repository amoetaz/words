package com.moetaz.words.domain.usecase

import com.moetaz.words.domain.model.Example
import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.repository.WordRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AddWordUseCaseTest {

    private lateinit var repository: WordRepository
    private lateinit var useCase: AddWordUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = AddWordUseCase(repository)
    }

    @Test
    fun invoke_delegatesWordInsertionToRepository() = runTest {
        val word = Word(
            id = 1L,
            word = "Resilience",
            translations = listOf("مرونة", "قدرة على التحمل"),
            examples = listOf(Example("Her resilience helped her overcome adversity.", "ساعدتها مرونتها."))
        )

        useCase(word)

        coVerify(exactly = 1) { repository.insertWord(word) }
    }
}
