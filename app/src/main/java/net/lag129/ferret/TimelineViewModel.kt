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
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.lag129.ferret.api.entity.Status

class TimelineViewModel(application: Application) : AndroidViewModel(application) {

    private val resources = application.resources

    private val client = HttpClient(CIO) {
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

    private val _uiState = MutableStateFlow(listOf<Status>())
    val uiState: StateFlow<List<Status>> = _uiState.asStateFlow()

    init {
        fetchHomeTimeline()
    }

    private fun fetchHomeTimeline() {
        viewModelScope.launch {
            val statuses = repository.getHomeTimeline()

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
            val statuses = repository.getHomeTimeline(maxId)

            statuses.onSuccess { statuses ->
                _uiState.value += statuses
            }.onFailure { error ->
                Napier.e("Failed to fetch next home timeline", error)
            }
        }
    }
}