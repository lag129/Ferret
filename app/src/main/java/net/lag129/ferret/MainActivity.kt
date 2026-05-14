package net.lag129.ferret

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import net.lag129.ferret.model.Account
import net.lag129.ferret.repository.PreferencesRepository
import net.lag129.ferret.ui.compose.BottomAppBarItem
import net.lag129.ferret.ui.compose.DetailScreen
import net.lag129.ferret.ui.compose.FerretBottomAppBar
import net.lag129.ferret.ui.compose.FerretTopAppBar
import net.lag129.ferret.ui.compose.LoginScreen
import net.lag129.ferret.ui.compose.MediaScreen
import net.lag129.ferret.ui.compose.ProfileScreen
import net.lag129.ferret.ui.compose.SettingScreen
import net.lag129.ferret.ui.compose.StatusCardData
import net.lag129.ferret.ui.compose.TimelineScreen
import net.lag129.ferret.ui.theme.FerretTheme
import net.lag129.ferret.viewmodel.AuthViewModel
import net.lag129.ferret.viewmodel.ProfileViewModel
import net.lag129.ferret.viewmodel.TimelineViewModel
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel


@Serializable
private data class Detail(val data: StatusCardData) : NavKey

@Serializable
private data object Home : NavKey

@Serializable
private data object Login : NavKey

@Serializable
private data class Media(val url: String, val description: String?) : NavKey

@Serializable
private data class Profile(val id: String, val account: Account?) : NavKey

@Serializable
private data object Setting : NavKey

@Serializable
private data object Splash : NavKey

@Serializable
private data class TimelineProfile(val id: String, val account: Account?) : NavKey


class MainActivity : ComponentActivity() {

    private val preferencesRepository = get<PreferencesRepository>()
    private val authViewModel: AuthViewModel by viewModel()
    private val profileViewModel: ProfileViewModel by viewModel()
    private val timelineViewModel: TimelineViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)

        setContent {
            val serverName by preferencesRepository.serverName.collectAsStateWithLifecycle(
                initialValue = null
            )
            val bearerToken by preferencesRepository.bearerToken.collectAsStateWithLifecycle(
                initialValue = null
            )

            val customBackStack = remember { CustomBackStack() }
            val currentKey = customBackStack.backStack.lastOrNull()

            val accountId by profileViewModel.accountId.collectAsStateWithLifecycle(initialValue = null)

            LaunchedEffect(serverName, bearerToken) {
                if (serverName != null && bearerToken != null) {
                    val hasValidCredentials =
                        serverName!!.isNotBlank() && bearerToken!!.isNotBlank()
                    customBackStack.restoreLoginState(hasValidCredentials)
                }

                profileViewModel.fetchMyCredential()
            }

            val currentTimeline by timelineViewModel.currentTimeline.collectAsStateWithLifecycle()

            FerretTheme {
                SharedTransitionLayout {
                    Scaffold(
                        topBar = {
                            when (currentKey) {
                                is Home -> {
                                    FerretTopAppBar(
                                        currentTimeline = currentTimeline,
                                        onSwitch = { timelineViewModel.switchTimeline(it) },
                                        onSettingClick = {
                                            customBackStack.backStack.add(
                                                Setting
                                            )
                                        }
                                    )
                                }

                                else -> {}
                            }
                        },
                        bottomBar = {
                            when (currentKey) {
                                is Home, is Profile, is TimelineProfile -> {
                                    val selectedItem = when (currentKey) {
                                        is Home -> BottomAppBarItem.HOME
                                        is Profile -> BottomAppBarItem.PROFILE
                                        else -> BottomAppBarItem.HOME
                                    }

                                    FerretBottomAppBar(
                                        selected = selectedItem,
                                        onClick = {
                                            when (it) {
                                                BottomAppBarItem.HOME -> {
                                                    customBackStack.backStack.add(
                                                        Home
                                                    )
                                                }

                                                BottomAppBarItem.PROFILE -> {
                                                    customBackStack.backStack.add(
                                                        Profile(
                                                            id = accountId ?: "",
                                                            account = null,
                                                        )
                                                    )
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                else -> {}
                            }
                        }
                    ) { innerPadding ->
                        NavDisplay(
                            backStack = customBackStack.backStack,
                            onBack = { customBackStack.removeLast() },
                            entryProvider = entryProvider {
                                entry<Detail> { key ->
                                    DetailScreen(
                                        data = key.data,
                                        onClickMedia = { mediaUrl, description ->
                                            customBackStack.backStack.add(
                                                Media(mediaUrl, description)
                                            )
                                        },
                                        onClickProfile = { account ->
                                            customBackStack.backStack.add(
                                                Profile(account.id, account)
                                            )
                                        },
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    )
                                }
                                entry<Home> {
                                    TimelineScreen(
                                        viewModel = timelineViewModel,
                                        onClickDetail = { data ->
                                            customBackStack.backStack.add(Detail(data))
                                        },
                                        onClickMedia = { mediaUrl, description ->
                                            customBackStack.backStack.add(
                                                Media(mediaUrl, description)
                                            )
                                        },
                                        onClickProfile = { account ->
                                            customBackStack.backStack.add(
                                                TimelineProfile(account.id, account)
                                            )
                                        },
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    )
                                }
                                entry<Login> {
                                    LoginScreen(
                                        authViewModel = authViewModel,
                                        onLoggedIn = {
                                            customBackStack.onLoginSuccess()
                                            timelineViewModel.fetchTimeline()
                                        },
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    )
                                }
                                entry<Media> { key ->
                                    MediaScreen(
                                        mediaUrl = key.url,
                                        description = key.description,
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    )
                                }
                                entry<Profile> { key ->
                                    ProfileScreen(
                                        id = key.id,
                                        account = key.account,
                                        viewModel = profileViewModel,
                                        onClickDetail = { data ->
                                            customBackStack.backStack.add(Detail(data))
                                        },
                                        onClickMedia = { mediaUrl, description ->
                                            customBackStack.backStack.add(
                                                Media(mediaUrl, description)
                                            )
                                        },
                                        onClickProfile = { account ->
                                            customBackStack.backStack.add(
                                                Profile(account.id, account)
                                            )
                                        },
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    )
                                }
                                entry<Setting> {
                                    SettingScreen(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    )
                                }
                                entry<Splash> {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                                entry<TimelineProfile> { key ->
                                    ProfileScreen(
                                        id = key.id,
                                        account = key.account,
                                        viewModel = profileViewModel,
                                        onClickDetail = { data ->
                                            customBackStack.backStack.add(Detail(data))
                                        },
                                        onClickMedia = { mediaUrl, description ->
                                            customBackStack.backStack.add(
                                                Media(mediaUrl, description)
                                            )
                                        },
                                        onClickProfile = { account ->
                                            customBackStack.backStack.add(
                                                Profile(account.id, account)
                                            )
                                        },
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    )
                                }
                            }
                        )
                    }
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
            if (code != null) {
                authViewModel.obtainAccessToken(code)
            }
        }
    }
}

private class CustomBackStack {
    private var isRestored = false
    val backStack = mutableStateListOf<NavKey>(Splash)

    fun restoreLoginState(hasValidCredentials: Boolean) {
        if (isRestored) return
        isRestored = true
        backStack.clear()
        backStack.add(if (hasValidCredentials) Home else Login)
    }

    fun onLoginSuccess() {
        backStack.clear()
        backStack.add(Home)
    }

    fun removeLast() {
        backStack.removeLastOrNull()
    }
}
