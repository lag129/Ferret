package net.lag129.ferret

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.serialization.Serializable
import net.lag129.ferret.api.entity.Account
import net.lag129.ferret.compose.FerretTopAppBar
import net.lag129.ferret.compose.LoginScreen
import net.lag129.ferret.compose.MediaScreen
import net.lag129.ferret.compose.ProfileScreen
import net.lag129.ferret.compose.TimelineScreen
import net.lag129.ferret.ui.theme.FerretTheme
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

@Serializable
private data object Splash : NavKey

@Serializable
private data object Home : NavKey

@Serializable
private data object Login : NavKey

@Serializable
private data class Media(val url: String, val description: String?) : NavKey

@Serializable
private data class Profile(val account: Account) : NavKey

class MainActivity : ComponentActivity() {

    private val preferencesRepository = get<PreferencesRepository>()
    private val authViewModel: AuthViewModel by viewModel()
    private val profileViewModel: ProfileViewModel by viewModel()
    private val timelineViewModel: TimelineViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        handleIntent(intent)
        Napier.base(DebugAntilog())

        setContent {
            val serverName by preferencesRepository.serverName.collectAsStateWithLifecycle(
                initialValue = null
            )
            val bearerToken by preferencesRepository.bearerToken.collectAsStateWithLifecycle(
                initialValue = null
            )

            val customBackStack = remember { CustomBackStack() }

            LaunchedEffect(serverName, bearerToken) {
                if (serverName != null && bearerToken != null) {
                    val hasValidCredentials =
                        serverName!!.isNotBlank() && bearerToken!!.isNotBlank()
                    customBackStack.restoreLoginState(hasValidCredentials)
                }
            }

            val currentTimeline by timelineViewModel.currentTimeline.collectAsStateWithLifecycle()

            FerretTheme {
                SharedTransitionLayout {
                    NavDisplay(
                        backStack = customBackStack.backStack,
                        onBack = { customBackStack.removeLast() },
                        entryProvider = entryProvider {
                            entry<Splash> {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            entry<Home> {
                                Scaffold(
                                    topBar = {
                                        FerretTopAppBar(
                                            currentTimeline = currentTimeline,
                                            onSwitch = { timelineViewModel.switchTimeline(it) }
                                        )
                                    }
                                ) { innerPadding ->
                                    TimelineScreen(
                                        viewModel = timelineViewModel,
                                        navigateToMediaScreen = { mediaUrl, description ->
                                            customBackStack.backStack.add(
                                                Media(mediaUrl, description)
                                            )
                                        },
                                        navigateToProfileScreen = { account ->
                                            customBackStack.backStack.add(
                                                Profile(account)
                                            )
                                        },
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    )
                                }
                            }
                            entry<Login> {
                                Scaffold { innerPadding ->
                                    LoginScreen(
                                        authViewModel = authViewModel,
                                        onLoggedIn = { customBackStack.onLoginSuccess() },
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    )
                                }
                            }
                            entry<Media> { key ->
                                Scaffold { innerPadding ->
                                    MediaScreen(
                                        mediaUrl = key.url,
                                        description = key.description,
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    )
                                }
                            }
                            entry<Profile> { key ->
                                Scaffold { innerPadding ->
                                    ProfileScreen(
                                        data = key.account,
                                        viewModel = profileViewModel,
                                        navigateToProfileScreen = { account ->
                                            customBackStack.backStack.add(
                                                Profile(account)
                                            )
                                        },
                                        navigateToMediaScreen = { mediaUrl, description ->
                                            customBackStack.backStack.add(
                                                Media(mediaUrl, description)
                                            )
                                        },
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    )
                                }
                            }
                        }
                    )
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
