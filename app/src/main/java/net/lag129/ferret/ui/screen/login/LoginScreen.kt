package net.lag129.ferret.ui.screen.login

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    modifier: Modifier = Modifier,
    authViewModel: LoginViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    var serverName by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Redirect -> {
                val oauthUrl = (authState as AuthState.Redirect).oauthUrl
                val intent = Intent(Intent.ACTION_VIEW, oauthUrl.toUri())
                context.startActivity(intent)
            }

            is AuthState.Success -> {}

            is AuthState.Error -> {
                val message = (authState as AuthState.Error).message
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
                enabled = authState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (authState) {
                is AuthState.Loading -> {
                    CircularProgressIndicator()
                }

                is AuthState.Error -> {}
                else -> {
                    Button(
                        onClick = { authViewModel.login(serverName) },
                        modifier = Modifier
                    ) {
                        Text(stringResource(R.string.login_button))
                    }
                }
            }
        }
    }
}
