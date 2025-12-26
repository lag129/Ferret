package net.lag129.ferret

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.lag129.ferret.ui.theme.FerretTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Napier.base(DebugAntilog())

        enableEdgeToEdge()
        setContent {
            FerretTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@SuppressLint("LocalContextResourcesRead")
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val resources = context.resources

    val client = HttpClient(CIO) {
        defaultRequest {
            url(resources.getString(R.string.base_url))
        }

        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(
                        resources.getString(R.string.access_token),
                        resources.getString(R.string.access_token)
                    )
                }
            }
        }

        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }

        install(HttpCache)
    }

    val repository: MastodonRepository = MastodonRepositoryImpl(client)

    LaunchedEffect(Unit) {
        repository.getHomeTimeline(resources.getString(R.string.id))
            .onSuccess { statuses ->
                Napier.d("Fetched ${statuses.size} statuses")
            }
            .onFailure { exception ->
                Napier.e("Error fetching timeline", exception)
            }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FerretTheme {
        Greeting("Android")
    }
}