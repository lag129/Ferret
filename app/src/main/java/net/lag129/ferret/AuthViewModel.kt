package net.lag129.ferret

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class AuthViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Redirect(val oauthUrl: String) : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var clientId: String = ""
    private var clientSecret: String = ""
    private var currentServerName: String = ""

    fun registerClientApp(serverName: String) {
        currentServerName = serverName
        _authState.value = AuthState.Loading

        val client = createHttpClient(serverName)
        val repository = MastodonRepositoryImpl(client)

        viewModelScope.launch {
            val result = repository.registerClientApp(
                clientName = CLIENT_NAME,
                redirectUris = REDIRECT_URI,
                scopes = SCOPES,
            )
            result.onSuccess { credentialApp ->
                clientId = credentialApp.clientId
                clientSecret = credentialApp.clientSecret

                preferencesRepository.saveTemporaryAuthData(
                    serverName = serverName,
                    clientId = clientId,
                    clientSecret = clientSecret
                )

                val oauthUrl = buildOAuthUrl(
                    serverName = serverName,
                    clientId = clientId
                )

                _authState.value = AuthState.Redirect(oauthUrl)
            }.onFailure { error ->
                Napier.e("Failed to register client app", error)
            }
        }
    }

    fun obtainAccessToken(code: String) {
        viewModelScope.launch {
            if (currentServerName.isEmpty() || clientId.isEmpty() || clientSecret.isEmpty()) {
                val tempAuthData = preferencesRepository.temporaryAuthData.first()
                currentServerName = tempAuthData?.serverName ?: ""
                clientId = tempAuthData?.clientId ?: ""
                clientSecret = tempAuthData?.clientSecret ?: ""
            }

            val client = createHttpClient(currentServerName)
            val repository = MastodonRepositoryImpl(client)

            val result = repository.obtainAccessToken(
                code = code,
                clientId = clientId,
                clientSecret = clientSecret,
                redirectUri = REDIRECT_URI
            )
            result.onSuccess { token ->
                preferencesRepository.saveServerName(currentServerName)
                preferencesRepository.saveBearerToken(token.accessToken)
                preferencesRepository.clearTemporaryAuthData()
                _authState.value = AuthState.Success
                Napier.d("Obtained access token: $token")
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "")
                Napier.e("Failed to obtain access token", error)
            }
        }
    }

    private fun createHttpClient(serverName: String): HttpClient {
        return HttpClient(CIO) {
            defaultRequest { url("https://$serverName/") }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    private fun buildOAuthUrl(
        serverName: String,
        clientId: String
    ): String {
        return "https://$serverName/oauth/authorize" +
                "?client_id=${clientId}" +
                "&redirect_uri=${REDIRECT_URI}" +
                "&response_type=code" +
                "&scope=${SCOPES}"
    }

    companion object {
        const val CLIENT_NAME = "Ferret"
        const val REDIRECT_URI = "ferret://oauth"
        const val SCOPES = "read write follow"
    }
}