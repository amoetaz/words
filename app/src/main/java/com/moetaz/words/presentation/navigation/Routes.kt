package com.moetaz.words.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object WordList : Route

    @Serializable
    data class AddWord(val wordId: Long? = null) : Route

    @Serializable
    data class WordDetail(val id: Long) : Route

    @Serializable
    data object Settings : Route
}
