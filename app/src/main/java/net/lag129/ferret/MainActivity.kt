package net.lag129.ferret

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.navigation3.ui.NavDisplay
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.serialization.Serializable
import net.lag129.ferret.compose.LoginScreen
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

class MainActivity : ComponentActivity() {

    private val preferencesRepository = get<PreferencesRepository>()
    private val authViewModel: AuthViewModel by viewModel()
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

            val authBackStack = remember { AuthBackStack() }

            LaunchedEffect(serverName, bearerToken) {
                if (serverName != null && bearerToken != null) {
                    val hasValidCredentials =
                        serverName!!.isNotBlank() && bearerToken!!.isNotBlank()
                    authBackStack.restoreLoginState(hasValidCredentials)
                }
            }

            FerretTheme {
                NavDisplay(
                    backStack = authBackStack.backStack,
                    onBack = { authBackStack.removeLast() },
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
                            Scaffold { innerPadding ->
                                TimelineScreen(
                                    viewModel = timelineViewModel,
                                    onNavigate = { _, _ -> },
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
                                    onLoggedIn = { authBackStack.onLoginSuccess() },
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

private class AuthBackStack {
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
