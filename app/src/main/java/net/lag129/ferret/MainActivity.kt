package net.lag129.ferret

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.serialization.NavBackStackSerializer
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import net.lag129.ferret.ui.compose.BottomAppBarItem
import net.lag129.ferret.ui.navigation.*
import net.lag129.ferret.ui.screen.DetailScreen
import net.lag129.ferret.ui.screen.MediaScreen
import net.lag129.ferret.ui.screen.SettingScreen
import net.lag129.ferret.ui.screen.SplashScreen
import net.lag129.ferret.ui.screen.login.LoginScreen
import net.lag129.ferret.ui.screen.login.LoginViewModel
import net.lag129.ferret.ui.screen.profile.ProfileScreen
import net.lag129.ferret.ui.screen.profile.ProfileViewModel
import net.lag129.ferret.ui.screen.timeline.TimelineScreen
import net.lag129.ferret.ui.theme.FerretTheme
import net.lag129.ferret.viewmodel.LoginState
import net.lag129.ferret.viewmodel.PreferencesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.viewmodel.koinViewModel


class MainActivity : ComponentActivity() {

    private val authViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)

        setContent {
            val preferencesViewModel: PreferencesViewModel = koinViewModel()
            val loginState by preferencesViewModel.loginState.collectAsStateWithLifecycle()

            FerretTheme {
                when (loginState) {
                    LoginState.Loading -> SplashScreen()
                    else -> FerretNavDisplay(loginState)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val uri = intent?.data ?: return

        if (uri.scheme == "ferret" && uri.host == "oauth") {
            val code = uri.getQueryParameter("code")
            if (code != null) authViewModel.obtainAccessToken(code)
        }
    }
}

@Composable
private fun FerretNavDisplay(
    loginState: LoginState,
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val accountId by profileViewModel.accountId.collectAsStateWithLifecycle(initialValue = null)

    SharedTransitionLayout {
        val backStack = rememberFerretBackStack(
            if (loginState == LoginState.LoggedIn) Home else Login
        )

        LaunchedEffect(loginState) {
            if (loginState == LoginState.LoggedIn) {
                if (backStack.lastOrNull() == Login) {
                    backStack.clear()
                    backStack.add(Home)
                }
                profileViewModel.fetchMyCredential()
            }
        }

        fun selectTab(item: BottomAppBarItem) {
            when (item) {
                BottomAppBarItem.HOME -> while (backStack.lastOrNull() != Home) {
                    backStack.removeLastOrNull()
                }

                BottomAppBarItem.PROFILE -> if (backStack.lastOrNull() != Profile) {
                    backStack.add(Profile)
                }
            }
        }

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
                            onClickBottomAppBar = ::selectTab,
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

                    is Profile -> NavEntry(key) {
                        ProfileScreen(
                            id = accountId ?: "",
                            onClickDetail = { data ->
                                backStack.add(Detail(data))
                            },
                            onClickMedia = { mediaUrl, description ->
                                backStack.add(Media(mediaUrl, description))
                            },
                            onClickProfile = { account ->
                                backStack.add(TimelineProfile(account.id, account))
                            },
                            onClickBottomAppBar = ::selectTab,
                            selectedItem = BottomAppBarItem.PROFILE,
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
                            onClickBottomAppBar = ::selectTab,
                            selectedItem = BottomAppBarItem.HOME,
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
            }
        )
    }
}

@Composable
private fun rememberFerretBackStack(
    vararg elements: FerretNavKey
): NavBackStack<FerretNavKey> =
    rememberSerializable(serializer = NavBackStackSerializer<FerretNavKey>()) {
        NavBackStack(*elements)
    }
