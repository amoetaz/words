package com.moetaz.words.presentation.settings

import android.content.Context
import com.moetaz.words.domain.repository.WordRepository
import com.moetaz.words.ui.theme.AppThemeMode
import com.moetaz.words.ui.theme.ThemePreferences
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

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
}
