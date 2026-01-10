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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.http.decodeURLQueryComponent
import io.ktor.http.encodeURLParameter
import net.lag129.ferret.compose.MediaScreen
import net.lag129.ferret.compose.StatusCard
import net.lag129.ferret.compose.StatusCardData
import net.lag129.ferret.ui.theme.FerretTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Napier.base(DebugAntilog())

        setContent {
            FerretTheme {

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
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
                                onNavigate = { mediaUrl, description ->
                                    val encodedUrl = mediaUrl.encodeURLParameter()
                                    val encodedDesc = description?.encodeURLParameter() ?: ""
                                    navController.navigate("media/$encodedUrl?description=$encodedDesc")
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            )
                        }
                    }

                    composable(
                        route = "media/{mediaUrl}?description={description}",
                        arguments = listOf(
                            navArgument("mediaUrl") { type = NavType.StringType },
                            navArgument("description") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            }
                        )
                    ) { backStackEntry ->
                        val encodedUrl = backStackEntry.arguments?.getString("mediaUrl") ?: ""
                        val encodedDescription = backStackEntry.arguments?.getString("description")

                        val mediaUrl = encodedUrl.decodeURLQueryComponent()
                        val description = encodedDescription?.decodeURLQueryComponent()

                        MediaScreen(
                            mediaUrl = mediaUrl,
                            description = description,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel,
    onNavigate: (mediaUrl: String, description: String?) -> Unit,
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
                    data = StatusCardData(
                        displayName = status.account.displayName,
                        userName = status.account.acct,
                        createdAt = status.createdAt,
                        avatarUrl = status.account.avatar,
                        content = status.content,
                        card = status.card,
                        displayNameEmojis = status.account.emojis,
                        emojis = status.emojis,
                        mediaAttachments = status.mediaAttachments
                    ),
                    onMediaClick = onNavigate
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
fun LoadingIndicator(
    viewModel: TimelineViewModel,
    maxId: String,
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.fillMaxWidth()
    )

    LaunchedEffect(Unit) {
        viewModel.fetchNextHomeTimeline(maxId)
    }
}
