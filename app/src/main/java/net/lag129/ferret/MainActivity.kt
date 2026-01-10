package net.lag129.ferret

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
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
import net.lag129.ferret.compose.TimelineScreen
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
