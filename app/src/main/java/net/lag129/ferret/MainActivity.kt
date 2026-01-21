package net.lag129.ferret

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.EaseOutQuint
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.http.decodeURLQueryComponent
import io.ktor.http.encodeURLParameter
import net.lag129.ferret.compose.LoginScreen
import net.lag129.ferret.compose.MediaScreen
import net.lag129.ferret.compose.NavigationBarItems
import net.lag129.ferret.compose.TimelineScreen
import net.lag129.ferret.ui.theme.FerretTheme

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Napier.base(DebugAntilog())
        handleIntent(intent)

        val preferencesRepository = PreferencesRepositoryImpl(application)

        setContent {
            val navController = rememberNavController()
            val scrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()

            val serverName by preferencesRepository.serverName
                .collectAsStateWithLifecycle(initialValue = "")
            val bearerToken by preferencesRepository.bearerToken
                .collectAsStateWithLifecycle(initialValue = "")

            val isLoggedIn = serverName.isNotEmpty() && bearerToken.isNotEmpty()

            FerretTheme {
                NavHost(
                    navController = navController,
                    startDestination = if (isLoggedIn) "home" else "login"
                ) {
                    composable("login") {
                        Scaffold(
                            modifier = Modifier.fillMaxSize()
                        ) { innerPadding ->
                            LoginScreen(
                                authViewModel = authViewModel,
                                onLoggedIn = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            )
                        }
                    }

                    composable("home") {
                        Scaffold(
                            bottomBar = {
                                BottomAppBar(
                                    scrollBehavior = scrollBehavior,
                                    modifier = Modifier
                                        .background(Color.Transparent)
                                        .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
                                        .height(64.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                ) {
                                    NavigationBarItems()
                                }
                            },
                            floatingActionButton = {
                                FloatingActionButton(
                                    onClick = {}
                                ) {
                                    Icon(Icons.Filled.Edit, "Edit")
                                }
                            },
                            modifier = Modifier
                                .nestedScroll(scrollBehavior.nestedScrollConnection)
                                .fillMaxSize(),
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
                        ),
                        enterTransition = {
                            fadeIn(
                                animationSpec = tween(durationMillis = 300, easing = EaseOutQuint)
                            )
                        },
                        popExitTransition = {
                            fadeOut(
                                animationSpec = tween(durationMillis = 300, easing = EaseOutQuint)
                            )
                        }
                    ) { backStackEntry ->
                        val encodedUrl = backStackEntry.arguments?.getString("mediaUrl") ?: ""
                        val mediaUrl = encodedUrl.decodeURLQueryComponent()

                        val description = backStackEntry.arguments?.getString("description")

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
