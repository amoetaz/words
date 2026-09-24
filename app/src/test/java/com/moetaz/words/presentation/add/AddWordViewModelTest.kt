package com.moetaz.words.presentation.add

import com.moetaz.words.domain.model.Example
import com.moetaz.words.domain.model.Word
import com.moetaz.words.domain.usecase.AddWordUseCase
import com.moetaz.words.domain.usecase.GetWordByIdUseCase
import com.moetaz.words.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddWordViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var addWordUseCase: AddWordUseCase
    private lateinit var getWordByIdUseCase: GetWordByIdUseCase
    private lateinit var viewModel: AddWordViewModel

    @Before
    fun setUp() {
        addWordUseCase = mockk(relaxed = true)
        getWordByIdUseCase = mockk(relaxed = true)
        viewModel = AddWordViewModel(addWordUseCase, getWordByIdUseCase)
    }

    @Test
    fun initialState_isCorrect() {
        val state = viewModel.state.value
        assertEquals("", state.word)
        assertEquals(listOf(""), state.translations)
        assertEquals(listOf(Example("", "")), state.examples)
        assertFalse(state.isSaving)
        assertFalse(state.isSaved)
        assertNull(state.error)
    }

    @Test
    fun onWordChanged_updatesWord() {
        viewModel.handleIntent(AddWordIntent.OnWordChanged("Ephemeral"))
        assertEquals("Ephemeral", viewModel.state.value.word)
    }

    @Test
    fun onPhoneticChanged_updatesPhonetic() {
        viewModel.handleIntent(AddWordIntent.OnPhoneticChanged("/ɪˈfɛm.ər.əl/"))
        assertEquals("/ɪˈfɛm.ər.əl/", viewModel.state.value.phonetic)
    }

    @Test
    fun onDefinitionChanged_updatesDefinition() {
        viewModel.handleIntent(AddWordIntent.OnDefinitionChanged("Lasting for a very short time."))
        assertEquals("Lasting for a very short time.", viewModel.state.value.definition)
    }

    @Test
    fun onTranslationChanged_updatesTranslationAtIndex() {
        viewModel.handleIntent(AddWordIntent.OnTranslationChanged(0, "سريع الزوال"))
        assertEquals(listOf("سريع الزوال"), viewModel.state.value.translations)
    }

    @Test
    fun onExampleEnglishChanged_updatesEnglishAtIndex() {
        viewModel.handleIntent(AddWordIntent.OnExampleEnglishChanged(0, "Fame is ephemeral."))
        assertEquals(listOf(Example("Fame is ephemeral.", "")), viewModel.state.value.examples)
    }

    @Test
    fun onExampleArabicChanged_updatesArabicAtIndex() {
        viewModel.handleIntent(AddWordIntent.OnExampleArabicChanged(0, "الشهرة قائمة على زوال."))
        assertEquals(listOf(Example("", "الشهرة قائمة على زوال.")), viewModel.state.value.examples)
    }

    @Test
    fun addTranslationField_appendsEmptyTranslation() {
        viewModel.handleIntent(AddWordIntent.AddTranslationField)
        assertEquals(listOf("", ""), viewModel.state.value.translations)
    }

    @Test
    fun addExampleField_appendsEmptyExample() {
        viewModel.handleIntent(AddWordIntent.AddExampleField)
        assertEquals(listOf(Example("", ""), Example("", "")), viewModel.state.value.examples)
    }

    @Test
    fun saveWord_withBlankWord_setsErrorAndDoesNotCallUseCase() = runTest {
        viewModel.handleIntent(AddWordIntent.OnWordChanged("   "))
        viewModel.handleIntent(AddWordIntent.SaveWord)

        val state = viewModel.state.value
        assertEquals("Word cannot be empty", state.error)
        assertFalse(state.isSaving)
        assertFalse(state.isSaved)
        coVerify(exactly = 0) { addWordUseCase(any()) }
    }

    @Test
    fun saveWord_withValidData_filtersBlankTranslationsAndCallsUseCase() = runTest {
        viewModel.handleIntent(AddWordIntent.OnWordChanged("Serendipity"))
        viewModel.handleIntent(AddWordIntent.OnTranslationChanged(0, "مصادفة حسنة"))
        viewModel.handleIntent(AddWordIntent.AddTranslationField) // empty translation
        viewModel.handleIntent(AddWordIntent.OnExampleEnglishChanged(0, "Pure serendipity."))
        viewModel.handleIntent(AddWordIntent.OnExampleArabicChanged(0, "مصادفة بحتة."))
        viewModel.handleIntent(AddWordIntent.AddExampleField) // empty example

        viewModel.handleIntent(AddWordIntent.SaveWord)

        val expectedWord = Word(
            word = "Serendipity",
            translations = listOf("مصادفة حسنة"),
            examples = listOf(Example("Pure serendipity.", "مصادفة بحتة."))
        )

        coVerify(exactly = 1) { addWordUseCase(expectedWord) }

        val state = viewModel.state.value
        assertFalse(state.isSaving)
        assertTrue(state.isSaved)
        assertNull(state.error)
    }

    @Test
    fun loadWord_populatesStateFromLoadedWord() = runTest {
        val existingWord = Word(
            id = 5L,
            word = "Eloquent",
            translations = listOf("فصيح"),
            examples = listOf(Example("An eloquent speaker.", "متحدث فصيح."))
        )
        every { getWordByIdUseCase(5L) } returns flowOf(existingWord)

        viewModel.handleIntent(AddWordIntent.LoadWord(5L))

        val state = viewModel.state.value
        assertEquals(5L, state.wordId)
        assertEquals("Eloquent", state.word)
        assertEquals(listOf("فصيح"), state.translations)
        assertEquals(listOf(Example("An eloquent speaker.", "متحدث فصيح.")), state.examples)
        assertFalse(state.isLoading)
    }

    @Test
    fun saveWord_whenEditingExistingWord_preservesWordIdInSavedWord() = runTest {
        val existingWord = Word(
            id = 5L,
            word = "Eloquent",
            translations = listOf("فصيح"),
            examples = listOf(Example("An eloquent speaker.", "متحدث فصيح."))
        )
        every { getWordByIdUseCase(5L) } returns flowOf(existingWord)

        viewModel.handleIntent(AddWordIntent.LoadWord(5L))
        viewModel.handleIntent(AddWordIntent.OnWordChanged("Eloquent Speaker"))
        viewModel.handleIntent(AddWordIntent.SaveWord)

        val expectedUpdatedWord = Word(
            id = 5L,
            word = "Eloquent Speaker",
            translations = listOf("فصيح"),
            examples = listOf(Example("An eloquent speaker.", "متحدث فصيح."))
        )

        coVerify(exactly = 1) { addWordUseCase(expectedUpdatedWord) }
        assertTrue(viewModel.state.value.isSaved)
    }

    @Test
    fun saveWord_whenUseCaseFails_setsErrorState() = runTest {
        val errorMessage = "Database constraint violation"
        coEvery { addWordUseCase(any()) } throws RuntimeException(errorMessage)

        viewModel.handleIntent(AddWordIntent.OnWordChanged("Serendipity"))
        viewModel.handleIntent(AddWordIntent.SaveWord)

        val state = viewModel.state.value
        assertFalse(state.isSaving)
        assertFalse(state.isSaved)
        assertEquals(errorMessage, state.error)
    }
}
