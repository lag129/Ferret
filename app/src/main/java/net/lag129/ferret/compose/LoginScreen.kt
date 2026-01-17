package net.lag129.ferret.compose

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.lag129.ferret.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoggedIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    var serverName by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthViewModel.AuthState.Redirect -> {
                val oauthUrl = (authState as AuthViewModel.AuthState.Redirect).oauthUrl
                val intent = Intent(Intent.ACTION_VIEW, oauthUrl.toUri())
                context.startActivity(intent)
            }

            is AuthViewModel.AuthState.Success -> {
                onLoggedIn()
            }

            else -> {}
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = serverName,
            onValueChange = { serverName = it },
            label = { Text("サーバー名") },
            singleLine = true,
            enabled = authState !is AuthViewModel.AuthState.Loading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (authState) {
            is AuthViewModel.AuthState.Loading -> {
                CircularProgressIndicator()
            }

            is AuthViewModel.AuthState.Error -> {}
            else -> {
                Button(
                    onClick = { authViewModel.registerClientApp(serverName) },
                    modifier = Modifier
                ) {
                    Text("ログイン")
                }
            }
        }
    }
}
