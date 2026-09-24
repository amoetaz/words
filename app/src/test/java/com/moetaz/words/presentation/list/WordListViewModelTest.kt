package com.moetaz.words.presentation.list

import com.moetaz.words.domain.model.Example
import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.usecase.GetWordsUseCase
import com.moetaz.words.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class WordListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getWordsUseCase: GetWordsUseCase = mockk()

    @Test
    fun init_loadsWordsSuccessfully() = runTest {
        val words = listOf(
            Word(1L, "Eloquent", listOf("فصيح"), listOf(Example("He is eloquent.", "إنه فصيح."))),
            Word(2L, "Luminous", listOf("مضيء"), listOf(Example("A luminous night.", "ليلة مضيئة.")))
        )
        every { getWordsUseCase() } returns flowOf(words)

        val viewModel = WordListViewModel(getWordsUseCase)

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(words, state.words)
        assertNull(state.error)
    }

    @Test
    fun init_whenErrorOccurs_updatesErrorState() = runTest {
        val errorMessage = "Database query failed"
        every { getWordsUseCase() } returns flow {
            throw RuntimeException(errorMessage)
        }

        val viewModel = WordListViewModel(getWordsUseCase)

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(emptyList<Word>(), state.words)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun handleIntent_otherIntents_doNotMutateState() = runTest {
        every { getWordsUseCase() } returns flowOf(emptyList())

        val viewModel = WordListViewModel(getWordsUseCase)
        val stateBefore = viewModel.state.value

        viewModel.handleIntent(WordListIntent.OnWordClick(1L))
        assertEquals(stateBefore, viewModel.state.value)

        viewModel.handleIntent(WordListIntent.OnAddWordClick)
        assertEquals(stateBefore, viewModel.state.value)
    }
}
