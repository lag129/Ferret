package net.lag129.ferret

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.lag129.ferret.ui.navigation.FerretNavDisplay
import net.lag129.ferret.ui.screen.SplashScreen
import net.lag129.ferret.ui.screen.login.LoginViewModel
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
