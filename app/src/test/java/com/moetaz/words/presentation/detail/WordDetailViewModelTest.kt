package com.moetaz.words.presentation.detail

import com.moetaz.words.domain.model.Example
import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.usecase.GetWordByIdUseCase
import com.moetaz.words.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WordDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getWordByIdUseCase: GetWordByIdUseCase
    private lateinit var viewModel: WordDetailViewModel

    @Before
    fun setUp() {
        getWordByIdUseCase = mockk()
        viewModel = WordDetailViewModel(getWordByIdUseCase)
    }

    @Test
    fun initialState_isDefault() {
        val state = viewModel.state.value
        assertNull(state.word)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun loadWord_success_updatesWordState() = runTest {
        val word = Word(
            id = 42L,
            word = "Sonder",
            translations = listOf("الشعور بأن لكل إنسان حياة معقدة مثلك"),
            examples = listOf(Example("Looking out the bus window, he felt sonder.", "نظر من نافذة الحافلة وشعر بـ sonder."))
        )
        every { getWordByIdUseCase(42L) } returns flowOf(word)

        viewModel.handleIntent(WordDetailIntent.LoadWord(42L))

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(word, state.word)
        assertNull(state.error)
    }

    @Test
    fun loadWord_error_updatesErrorState() = runTest {
        val errorMessage = "Failed to fetch word details"
        every { getWordByIdUseCase(42L) } returns flow {
            throw RuntimeException(errorMessage)
        }

        viewModel.handleIntent(WordDetailIntent.LoadWord(42L))

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNull(state.word)
        assertEquals(errorMessage, state.error)
    }
}
