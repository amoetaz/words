package com.moetaz.words.presentation.settings

import com.moetaz.words.domain.repository.WordRepository
import com.moetaz.words.util.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: WordRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        viewModel = SettingsViewModel(repository)
    }

    @Test
    fun importJson_validJson_insertsWordsAndUpdatesSuccessMessage() = runTest {
        val sampleJson = """
            {
              "words": [
                {
                  "word": "exuberance",
                  "phonetics": "exuberance",
                  "definition": "exuberance",
                  "translation": [
                    "امتلاء بالحيوية"
                  ],
                  "examples": [
                    {
                      "english": "Her exuberance was contagious.",
                      "arabic": "كان امتلائها بالحيوية معدياً."
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        viewModel.handleIntent(SettingsIntent.ImportJsonContent(sampleJson))

        assertFalse(viewModel.state.value.isImporting)
        assertEquals("Successfully imported 1 word(s)!", viewModel.state.value.importMessage)
        assertNull(viewModel.state.value.importError)

        coVerify(exactly = 1) { repository.insertWord(any()) }
    }

    @Test
    fun importJson_invalidJson_setsImportError() = runTest {
        val invalidJson = "invalid json content"

        viewModel.handleIntent(SettingsIntent.ImportJsonContent(invalidJson))

        assertFalse(viewModel.state.value.isImporting)
        assertNull(viewModel.state.value.importMessage)
        assertEquals(true, viewModel.state.value.importError?.contains("Failed to parse JSON file"))
    }

    @Test
    fun clearImportStatus_resetsMessages() = runTest {
        val invalidJson = "invalid json"
        viewModel.handleIntent(SettingsIntent.ImportJsonContent(invalidJson))

        viewModel.handleIntent(SettingsIntent.ClearImportStatus)

        assertNull(viewModel.state.value.importMessage)
        assertNull(viewModel.state.value.importError)
    }
}
