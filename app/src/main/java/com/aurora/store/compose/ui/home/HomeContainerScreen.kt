/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.AdaptStrategy
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableSupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation3.runtime.NavKey
import com.aurora.store.compose.composable.TopAppBar
import com.aurora.store.compose.navigation.Screen
import com.aurora.store.compose.preview.PreviewTemplate
import com.aurora.store.compose.ui.home.menu.HomeContainerMenu
import com.aurora.store.compose.ui.home.menu.MenuItem
import com.aurora.store.compose.ui.home.navigation.HomeScreen
import kotlinx.coroutines.launch

@Composable
fun HomeContainerScreen() {
    val screens = listOf(
        HomeScreen.APPS,
        HomeScreen.GAMES,
        HomeScreen.UPDATES
    )

    ScreenContent(
        screens = screens
    )
}

@Composable
private fun ScreenContent(
    default: HomeScreen = HomeScreen.APPS,
    screens: List<HomeScreen> = emptyList(),
    onNavigateTo: (screen: NavKey) -> Unit = {},
) {
    var currentScreen by rememberSaveable { mutableStateOf(default) }
    var shouldShowMoreDialog by rememberSaveable { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator<NavKey>(
        adaptStrategies = SupportingPaneScaffoldDefaults.adaptStrategies(
            supportingPaneAdaptStrategy = AdaptStrategy.Hide
        )
    )

    if (shouldShowMoreDialog) {
        MoreDialog(
            onDismiss = { shouldShowMoreDialog = false },
            onNavigateTo = { screen ->
                coroutineScope.launch {
                    scaffoldNavigator.navigateTo(SupportingPaneScaffoldRole.Extra, screen)
                }
            }
        )
    }

    @Composable
    fun SetupMenu() {
        HomeContainerMenu { menuItem ->
            when (menuItem) {
                MenuItem.DOWNLOADS -> onNavigateTo(Screen.Downloads)
                MenuItem.MORE -> { shouldShowMoreDialog = true }
            }
        }
    }

    @Composable
    fun MainPane() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = stringResource(currentScreen.localized),
                    actions = { SetupMenu() }
                )
            }
        ) { paddingValues ->
            NavigationSuiteScaffold(
                modifier = Modifier.padding(paddingValues),
                navigationSuiteItems = {
                    screens.forEach { screen ->
                        item(
                            icon = {
                                Icon(
                                    painter = painterResource(screen.icon),
                                    contentDescription = null
                                )
                            },
                            label = { Text(text = stringResource(screen.localized)) },
                            selected = currentScreen == screen,
                            onClick = { currentScreen = screen }
                        )
                    }
                }
            ) {
                when (currentScreen) {
                    HomeScreen.APPS -> AppsScreen()
                    HomeScreen.GAMES -> GamesScreen()
                    HomeScreen.UPDATES -> UpdatesScreen()
                }
            }
        }
    }

    @Composable
    fun ExtraPane(screen: NavKey) {
        when (screen) {
            else -> Unit
        }
    }

    NavigableSupportingPaneScaffold(
        navigator = scaffoldNavigator,
        mainPane = { AnimatedPane { MainPane() } },
        supportingPane = { AnimatedPane {  } },
        extraPane = {
            scaffoldNavigator.currentDestination?.contentKey?.let { screen ->
                AnimatedPane { ExtraPane(screen) }
            }
        }
    )
}

@PreviewScreenSizes
@Composable
private fun HomeContainerScreenPreview() {
    PreviewTemplate {
        val screens = listOf(
            HomeScreen.APPS,
            HomeScreen.GAMES,
            HomeScreen.UPDATES
        )
        ScreenContent(screens = screens)
    }
}
