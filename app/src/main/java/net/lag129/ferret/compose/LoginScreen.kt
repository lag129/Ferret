package net.lag129.ferret.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.lag129.ferret.PreferencesViewModel

@Composable
fun LoginScreen(
    viewModel: PreferencesViewModel,
    onServerNameChanged: (String) -> Unit,
    onBearerTokenChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val serverName by viewModel.serverName.collectAsStateWithLifecycle()
    val bearerToken by viewModel.bearerToken.collectAsStateWithLifecycle()

    var inputServerName by remember { mutableStateOf(serverName ?: "") }
    var inputBearerToken by remember { mutableStateOf(bearerToken ?: "") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = inputServerName,
            onValueChange = {
                inputServerName = it
            },
            label = { Text("サーバー名") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = inputBearerToken,
            onValueChange = {
                inputBearerToken = it
            },
            label = { Text("トークン") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onServerNameChanged(inputServerName)
                onBearerTokenChanged(inputBearerToken)
            },
            content = { Text("ログイン") },
            modifier = Modifier
        )
    }
}
