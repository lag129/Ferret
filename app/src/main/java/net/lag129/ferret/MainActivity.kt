package net.lag129.ferret

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
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
private data object Home : NavKey

@Serializable
private data object Login : NavKey

class MainActivity : ComponentActivity() {

    private val preferencesRepository = get<PreferencesRepository>()
    private val authViewModel: AuthViewModel by viewModel()
    private val timelineViewModel: TimelineViewModel by viewModel()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        handleIntent(intent)
        Napier.base(DebugAntilog())

        setContent {
            val serverName by preferencesRepository.serverName
                .collectAsStateWithLifecycle(initialValue = "")
            val bearerToken by preferencesRepository.bearerToken
                .collectAsStateWithLifecycle(initialValue = "")

            val isLoggedIn = serverName.isNotEmpty() && bearerToken.isNotEmpty()

            val backStack = rememberNavBackStack(Home)

            FerretTheme {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = entryProvider {
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
                                    onLoggedIn = {},
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
