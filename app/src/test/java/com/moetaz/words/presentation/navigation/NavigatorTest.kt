package com.moetaz.words.presentation.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import io.mockk.mockk
import io.mockk.verify
import io.mockk.every
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigatorTest {

    @Test
    fun navigate_whenRouteIsTopLevel_updatesTopLevelRoute() {
        val startRoute: Route = Route.WordList
        val otherTopLevelRoute: Route = Route.AddWord
        val topLevelState = mutableStateOf(startRoute)
        val backStacks = mapOf<Route, NavBackStack<Route>>(
            Route.WordList to mockk(relaxed = true),
            Route.AddWord to mockk(relaxed = true)
        )
        val navState = NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelState,
            backStacks = backStacks
        )
        val navigator = Navigator(navState)

        navigator.navigate(otherTopLevelRoute)

        assertEquals(otherTopLevelRoute, navState.topLevelRoute)
    }

    @Test
    fun navigate_whenRouteIsNotTopLevel_addsToCurrentStack() {
        val startRoute: Route = Route.WordList
        val topLevelState = mutableStateOf(startRoute)
        val wordListStack = mockk<NavBackStack<Route>>(relaxed = true)
        val backStacks = mapOf<Route, NavBackStack<Route>>(
            Route.WordList to wordListStack
        )
        val navState = NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelState,
            backStacks = backStacks
        )
        val navigator = Navigator(navState)
        val detailRoute = Route.WordDetail(42L)

        navigator.navigate(detailRoute)

        verify(exactly = 1) { wordListStack.add(detailRoute) }
    }

    @Test
    fun goBack_whenCurrentRouteIsTopLevelAndNotStartRoute_resetsToStartRoute() {
        val startRoute: Route = Route.WordList
        val currentTopLevel: Route = Route.AddWord
        val topLevelState = mutableStateOf(currentTopLevel)
        val addWordStack = mockk<NavBackStack<Route>>(relaxed = true) {
            every { last() } returns Route.AddWord
        }
        val backStacks = mapOf<Route, NavBackStack<Route>>(
            Route.WordList to mockk(relaxed = true),
            Route.AddWord to addWordStack
        )
        val navState = NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelState,
            backStacks = backStacks
        )
        val navigator = Navigator(navState)

        navigator.goBack()

        assertEquals(startRoute, navState.topLevelRoute)
    }

    @Test
    fun goBack_whenCurrentRouteIsNotTopLevel_removesLastFromCurrentStack() {
        val startRoute: Route = Route.WordList
        val topLevelState = mutableStateOf(startRoute)
        val wordListStack = mockk<NavBackStack<Route>>(relaxed = true) {
            every { last() } returns Route.WordDetail(1L)
        }
        val backStacks = mapOf<Route, NavBackStack<Route>>(
            Route.WordList to wordListStack
        )
        val navState = NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelState,
            backStacks = backStacks
        )
        val navigator = Navigator(navState)

        navigator.goBack()

        verify(exactly = 1) { wordListStack.removeAt(any()) }
        assertEquals(startRoute, navState.topLevelRoute)
    }
}
