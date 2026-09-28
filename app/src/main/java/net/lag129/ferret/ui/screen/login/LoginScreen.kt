package net.lag129.ferret.ui.screen.login

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.lag129.ferret.R
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier
) {
    val authViewModel: LoginViewModel = koinViewModel()
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    var serverName by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        when (authState) {
            is LoginViewModel.AuthState.Redirect -> {
                val oauthUrl = (authState as LoginViewModel.AuthState.Redirect).oauthUrl
                val intent = Intent(Intent.ACTION_VIEW, oauthUrl.toUri())
                context.startActivity(intent)
            }

            is LoginViewModel.AuthState.Success -> {}

            is LoginViewModel.AuthState.Error -> {
                val message = (authState as LoginViewModel.AuthState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }

            else -> {}
        }
    }

    Scaffold { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = serverName,
                onValueChange = { serverName = it },
                label = { Text(stringResource(R.string.server_name)) },
                singleLine = true,
                enabled = authState !is LoginViewModel.AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (authState) {
                is LoginViewModel.AuthState.Loading -> {
                    CircularProgressIndicator()
                }

                is LoginViewModel.AuthState.Error -> {}
                else -> {
                    Button(
                        onClick = { authViewModel.registerClientApp(serverName) },
                        modifier = Modifier
                    ) {
                        Text(stringResource(R.string.login_button))
                    }
                }
            }
        }
    }
}
