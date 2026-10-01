package net.lag129.ferret.ui.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.serialization.NavBackStackSerializer
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import net.lag129.ferret.ui.compose.BottomAppBarItem
import net.lag129.ferret.ui.compose.FerretBottomAppBar
import net.lag129.ferret.ui.screen.DetailScreen
import net.lag129.ferret.ui.screen.MediaScreen
import net.lag129.ferret.ui.screen.SettingScreen
import net.lag129.ferret.ui.screen.login.LoginScreen
import net.lag129.ferret.ui.screen.profile.MyProfileScreen
import net.lag129.ferret.ui.screen.profile.ProfileScreen
import net.lag129.ferret.ui.screen.timeline.TimelineScreen
import net.lag129.ferret.viewmodel.LoginState

@Composable
fun FerretNavDisplay(
    loginState: LoginState,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberFerretBackStack(if (loginState == LoginState.LoggedIn) Home else Login)

    LaunchedEffect(loginState) {
        if (loginState == LoginState.LoggedIn) {
            if (backStack.lastOrNull() == Login) {
                backStack.clear()
                backStack.add(Home)
            }
        }
    }

    fun selectTab(item: BottomAppBarItem) {
        when (item) {
            BottomAppBarItem.HOME -> while (backStack.lastOrNull() != Home) {
                backStack.removeLastOrNull()
            }

            BottomAppBarItem.PROFILE -> if (backStack.lastOrNull() != MyProfile) {
                backStack.add(MyProfile)
            }
        }
    }

    SharedTransitionLayout {
        Scaffold(
            bottomBar = {
                when (backStack.lastOrNull()) {
                    Home, MyProfile -> {
                        FerretBottomAppBar(
                            selected = when (backStack.lastOrNull()) {
                                is MyProfile -> BottomAppBarItem.PROFILE
                                else -> BottomAppBarItem.HOME
                            },
                            onClick = ::selectTab,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    else -> {}
                }
            },
            contentWindowInsets = WindowInsets(0),
            modifier = modifier,
        ) { innerPadding ->
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = { key ->
                    when (key) {
                        is Home -> NavEntry(key) {
                            TimelineScreen(
                                onClickDetail = { data ->
                                    backStack.add(Detail(data))
                                },
                                onClickMedia = { mediaUrl, description ->
                                    backStack.add(Media(mediaUrl, description))
                                },
                                onClickProfile = { account ->
                                    backStack.add(TimelineProfile(account.id, account))
                                },
                                onClickSetting = {
                                    backStack.add(Setting)
                                },
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                            )
                        }

                        is Detail -> NavEntry(key) {
                            DetailScreen(
                                data = key.data,
                                onClickMedia = { mediaUrl, description ->
                                    backStack.add(Media(mediaUrl, description))
                                },
                                onClickProfile = { account ->
                                    backStack.add(TimelineProfile(account.id, account))
                                },
                                onBack = { backStack.removeLastOrNull() },
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                            )
                        }

                        is MyProfile -> NavEntry(key, metadata = transition) {
                            MyProfileScreen(
                                onClickDetail = { data ->
                                    backStack.add(Detail(data))
                                },
                                onClickMedia = { mediaUrl, description ->
                                    backStack.add(Media(mediaUrl, description))
                                },
                                onClickProfile = { account ->
                                    backStack.add(TimelineProfile(account.id, account))
                                },
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                            )
                        }

                        is TimelineProfile -> NavEntry(key) {
                            ProfileScreen(
                                id = key.id,
                                account = key.account,
                                onClickDetail = { data ->
                                    backStack.add(Detail(data))
                                },
                                onClickMedia = { mediaUrl, description ->
                                    backStack.add(Media(mediaUrl, description))
                                },
                                onClickProfile = { account ->
                                    backStack.add(TimelineProfile(account.id, account))
                                },
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                            )
                        }

                        is Media -> NavEntry(key) {
                            MediaScreen(
                                mediaUrl = key.url,
                                description = key.description,
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                            )
                        }

                        is Setting -> NavEntry(key) {
                            SettingScreen()
                        }

                        is Login -> NavEntry(key) {
                            LoginScreen()
                        }
                    }
                },
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun rememberFerretBackStack(
    vararg elements: FerretNavKey
): NavBackStack<FerretNavKey> =
    rememberSerializable(serializer = NavBackStackSerializer<FerretNavKey>()) {
        NavBackStack(*elements)
    }

private val transition: Map<String, Any> =
    NavDisplay.transitionSpec {
        slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) togetherWith slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(300)
        )
    } + NavDisplay.popTransitionSpec {
        slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) togetherWith slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(300)
        )
    } + NavDisplay.predictivePopTransitionSpec {
        slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) togetherWith slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(300)
        )
    }
