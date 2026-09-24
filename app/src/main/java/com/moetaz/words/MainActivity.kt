package com.moetaz.words

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.moetaz.words.presentation.add.AddWordScreen
import com.moetaz.words.presentation.add.AddWordViewModel
import com.moetaz.words.presentation.detail.WordDetailScreen
import com.moetaz.words.presentation.detail.WordDetailViewModel
import com.moetaz.words.presentation.list.WordListScreen
import com.moetaz.words.presentation.list.WordListViewModel
import com.moetaz.words.presentation.navigation.Navigator
import com.moetaz.words.presentation.navigation.Route
import com.moetaz.words.presentation.navigation.rememberNavigationState
import com.moetaz.words.presentation.navigation.toEntries
import com.moetaz.words.presentation.settings.SettingsScreen
import com.moetaz.words.presentation.settings.SettingsViewModel
import com.moetaz.words.ui.theme.WordsTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordsTheme {
                val navigationState = rememberNavigationState<Route>(
                    startRoute = Route.WordList,
                    topLevelRoutes = setOf(Route.WordList)
                )
                val navigator = remember { Navigator(navigationState) }

                val entryProvider = entryProvider {
                    entry<Route.WordList> {
                        val viewModel: WordListViewModel = koinViewModel()
                        WordListScreen(
                            viewModel = viewModel,
                            onWordClick = { id ->
                                navigator.navigate(Route.WordDetail(id))
                            },
                            onAddWordClick = {
                                navigator.navigate(Route.AddWord())
                            },
                            onSettingsClick = {
                                navigator.navigate(Route.Settings)
                            }
                        )
                    }
                    entry<Route.AddWord> { key ->
                        val viewModel: AddWordViewModel = koinViewModel()
                        AddWordScreen(
                            wordId = key.wordId,
                            viewModel = viewModel,
                            onBack = { navigator.goBack() }
                        )
                    }
                    entry<Route.WordDetail> { key ->
                        val viewModel: WordDetailViewModel = koinViewModel()
                        WordDetailScreen(
                            wordId = key.id,
                            viewModel = viewModel,
                            onEditWord = { id ->
                                navigator.navigate(Route.AddWord(id))
                            },
                            onBack = { navigator.goBack() }
                        )
                    }
                    entry<Route.Settings> {
                        val viewModel: SettingsViewModel = koinViewModel()
                        SettingsScreen(
                            viewModel = viewModel,
                            onBack = { navigator.goBack() }
                        )
                    }
                }

                NavDisplay(
                    entries = navigationState.toEntries(entryProvider),
                    onBack = { navigator.goBack() }
                )
            }
        }
    }
}
