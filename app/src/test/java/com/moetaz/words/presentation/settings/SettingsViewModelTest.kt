package com.moetaz.words.presentation.settings

import android.content.Context
import android.net.Uri
import com.moetaz.words.domain.repository.WordRepository
import com.moetaz.words.ui.theme.AppThemeMode
import com.moetaz.words.ui.theme.ThemePreferences
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: WordRepository
    private lateinit var context: Context
    private lateinit var themePreferences: ThemePreferences
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        context = mockk(relaxed = true)
        themePreferences = ThemePreferences(context)
        viewModel = SettingsViewModel(repository, themePreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun defaultState_hasSystemThemeMode() {
        assertEquals(AppThemeMode.SYSTEM, viewModel.state.value.selectedThemeMode)
    }

    @Test
    fun changeThemeMode_updatesStateAndPreferences() = runTest {
        viewModel.handleIntent(SettingsIntent.ChangeThemeMode(AppThemeMode.DARK))

        assertEquals(AppThemeMode.DARK, viewModel.state.value.selectedThemeMode)
        assertEquals(AppThemeMode.DARK, themePreferences.themeMode.value)
    }

    @Test
    fun changeThemeMode_toLight_updatesStateAndPreferences() = runTest {
        viewModel.handleIntent(SettingsIntent.ChangeThemeMode(AppThemeMode.LIGHT))

        assertEquals(AppThemeMode.LIGHT, viewModel.state.value.selectedThemeMode)
        assertEquals(AppThemeMode.LIGHT, themePreferences.themeMode.value)
    }

    @Test
    fun importJson_validDatabaseStructure_insertsWordsAndUpdatesState() = runTest {
        val dbJson = """
            {
              "words": [
                {
                  "id": 1,
                  "word": "Eloquent",
                  "translations": ["فصيح"],
                  "masteryStatus": "LEARNING",
                  "isFavorite": true
                }
              ]
            }
        """.trimIndent()

        viewModel.handleIntent(SettingsIntent.ImportJsonContent(dbJson))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Successfully imported 1 word(s)!", state.importMessage)
        coVerify { repository.insertWord(any()) }
    }

    @Test
    fun importJson_jsonArray_insertsWordsAndUpdatesState() = runTest {
        val arrayJson = """
            [
              {
                "id": 2,
                "word": "Meticulous",
                "translations": ["دقيق"],
                "masteryStatus": "MASTERED"
              }
            ]
        """.trimIndent()

        viewModel.handleIntent(SettingsIntent.ImportJsonContent(arrayJson))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Successfully imported 1 word(s)!", state.importMessage)
        coVerify { repository.insertWord(any()) }
    }

    @Test
    fun exportJson_emptyDatabase_setsExportError() = runTest {
        coEvery { repository.getWords() } returns flowOf(emptyList())

        viewModel.handleIntent(SettingsIntent.ExportJson(mockk(relaxed = true), context))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("No words in database to export.", state.exportError)
    }

    @Test
    fun downloadExampleJson_success_updatesImportMessage() = runTest {
        val mockUri = mockk<Uri>(relaxed = true)
        val mockOutputStream = ByteArrayOutputStream()
        coEvery { context.contentResolver.openOutputStream(mockUri) } returns mockOutputStream

        viewModel.handleIntent(SettingsIntent.DownloadExampleJson(mockUri, context))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Example JSON file saved successfully!", state.importMessage)
        Assert.assertTrue(mockOutputStream.toString().contains("Eloquent"))
        Assert.assertTrue(mockOutputStream.toString().contains("Meticulous"))
    }
}
