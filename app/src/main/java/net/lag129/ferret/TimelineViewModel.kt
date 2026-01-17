package net.lag129.ferret

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.lag129.ferret.api.entity.Status

class TimelineViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = PreferencesRepositoryImpl(application)

    private var client: HttpClient? = null
    private var mastodonRepository: MastodonRepository? = null

    private val _uiState = MutableStateFlow(listOf<Status>())
    val uiState: StateFlow<List<Status>> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val serverName = preferencesRepository.serverName.first()
            val bearerToken = preferencesRepository.bearerToken.first()

            client = createHttpClient(serverName, bearerToken)
            mastodonRepository = MastodonRepositoryImpl(client ?: return@launch)
            fetchHomeTimeline()
        }
    }

    private fun createHttpClient(
        serverName: String,
        bearerToken: String
    ): HttpClient {
        return HttpClient(CIO) {
            defaultRequest { url("https://$serverName/") }
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(bearerToken, bearerToken)
                    }
                }
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(HttpCache)
        }
    }

    private fun fetchHomeTimeline() {
        viewModelScope.launch {
            val statuses = mastodonRepository?.getHomeTimeline() ?: return@launch

            statuses.onSuccess { statuses ->
                _uiState.value = statuses
            }.onFailure { error ->
                Napier.e("Failed to fetch home timeline", error)
            }
        }
    }

    fun fetchNextHomeTimeline(
        maxId: String
    ) {
        viewModelScope.launch {
            val statuses = mastodonRepository?.getHomeTimeline(
                maxId = maxId
            ) ?: return@launch

            statuses.onSuccess { statuses ->
                _uiState.value += statuses
            }.onFailure { error ->
                Napier.e("Failed to fetch next home timeline", error)
            }
        }
    }
}