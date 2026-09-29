package com.moetaz.words.presentation.favorites

import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.repository.WordRepository
import com.moetaz.words.domain.usecase.GetWordsUseCase
import com.moetaz.words.util.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getWordsUseCase: GetWordsUseCase
    private lateinit var repository: WordRepository
    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setUp() {
        getWordsUseCase = mockk(relaxed = true)
        repository = mockk(relaxed = true)
    }

    @Test
    fun loadFavorites_filtersOnlyFavoriteWords() = runTest {
        val allWords = listOf(
            Word(id = 1L, word = "Eloquent", isFavorite = true),
            Word(id = 2L, word = "Meticulous", isFavorite = false),
            Word(id = 3L, word = "Resilient", isFavorite = true)
        )
        every { getWordsUseCase() } returns flowOf(allWords)

        viewModel = FavoritesViewModel(getWordsUseCase, repository)

        val state = viewModel.state.value
        assertEquals(2, state.favoriteWords.size)
        assertEquals("Eloquent", state.favoriteWords[0].word)
        assertEquals("Resilient", state.favoriteWords[1].word)
        assertFalse(state.isLoading)
    }

    @Test
    fun toggleFavorite_callsRepositoryToggleFavorite() = runTest {
        every { getWordsUseCase() } returns flowOf(emptyList())
        viewModel = FavoritesViewModel(getWordsUseCase, repository)

        viewModel.handleIntent(FavoritesIntent.ToggleFavorite(1L, false))

        coVerify(exactly = 1) { repository.toggleFavorite(1L, false) }
    }
}
