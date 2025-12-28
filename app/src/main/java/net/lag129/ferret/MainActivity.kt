package net.lag129.ferret

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import net.lag129.ferret.compose.LoginScreen
import net.lag129.ferret.compose.StatusCard
import net.lag129.ferret.ui.theme.FerretTheme

sealed class Screen(val route: String) {
    data object Login : Screen("Login")
    data object Home : Screen("Home")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Napier.base(DebugAntilog())

        val repository = PreferencesRepository(
            dataStore = applicationContext.getSharedPreferences(
                "preferences",
                MODE_PRIVATE
            ) as DataStore<Preferences>
        )
        val viewModel = PreferencesViewModel(repository)

        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()
            val coroutineScope = rememberCoroutineScope()

            val serverName by viewModel.serverName.collectAsState()
            val bearerToken by viewModel.bearerToken.collectAsState()

            val isLoggedIn = !serverName.isNullOrEmpty() && !bearerToken.isNullOrEmpty()

            FerretTheme {
                NavHost(
                    navController = navController,
                    startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
                ) {
                    composable(Screen.Login.route) {
                        Scaffold { innerPadding ->
                            LoginScreen(
                                viewModel = viewModel,
                                onServerNameChanged = { serverName ->
                                    coroutineScope.launch {
                                        viewModel.setServerName(serverName)
                                    }
                                },
                                onBearerTokenChanged = { token ->
                                    coroutineScope.launch {
                                        viewModel.setBearerToken(token)
                                        if (serverName.isNullOrEmpty()) {
                                            navController.navigate(Screen.Home.route) {
                                                popUpTo(Screen.Login.route) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            )
                        }
                    }

                    composable(Screen.Home.route) {
                        Scaffold(
                            floatingActionButton = {
                                FloatingActionButton(
                                    onClick = {}
                                ) {
                                    Icon(Icons.Filled.Edit, "Edit")
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        ) { innerPadding ->

                            val timelineViewModel: TimelineViewModel by viewModels()

                            TimelineScreen(
                                viewModel = timelineViewModel,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel,
    modifier: Modifier = Modifier
) {
    val statuses by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(
            items = statuses,
            key = { status -> status.id }
        ) { status ->
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                StatusCard(
                    displayName = status.account.displayName,
                    userName = status.account.acct,
                    avatarUrl = status.account.avatar,
                    content = status.content
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    thickness = 0.2.dp
                )
            }
        }

        val isLast = statuses.isEmpty()

        if (isLast.not()) {
            item {
                LoadingIndicator(viewModel, statuses.last().id)
            }
        }
    }
}

@Composable
fun LoadingIndicator(viewModel: TimelineViewModel, maxId: String) {
    LinearProgressIndicator(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
    )

    LaunchedEffect(Unit) {
        viewModel.fetchNextHomeTimeline(maxId)
    }
}
