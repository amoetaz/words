package com.moetaz.words.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object WordList : Route

    @Serializable
    data object AddWord : Route

    @Serializable
    data class WordDetail(val id: Long) : Route
}
