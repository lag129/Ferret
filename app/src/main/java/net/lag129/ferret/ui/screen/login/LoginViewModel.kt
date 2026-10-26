package net.lag129.ferret.ui.screen.login

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
import net.lag129.ferret.repository.MastodonRepositoryImpl
import net.lag129.ferret.repository.MisskeyRepositoryImpl
import net.lag129.ferret.repository.NodeInfoRepositoryImpl
import net.lag129.ferret.repository.Platform
import net.lag129.ferret.repository.PreferencesRepository
import kotlin.uuid.Uuid

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Redirect(val oauthUrl: String) : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class LoginViewModel(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(serverName: String) {
        _authState.value = AuthState.Loading
        val nodeInfoRepository = NodeInfoRepositoryImpl(createHttpClient(serverName))

        viewModelScope.launch {
            nodeInfoRepository.getSoftwareName()
                .onSuccess {
                    when (it) {
                        "mastodon" -> registerClientApp(serverName)
                        "misskey" -> registerMisskeyApp(serverName)
                        else -> _authState.value = AuthState.Error("Unsupported server")
                    }
                }.onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "")
                    Napier.e("Failed to fetch server name", error)
                }
        }
    }

    fun registerClientApp(serverName: String) {
        val client = createHttpClient(serverName)
        val mastodonRepository = MastodonRepositoryImpl(client)

        viewModelScope.launch {
            mastodonRepository.registerClientApp(
                clientName = CLIENT_NAME,
                redirectUris = REDIRECT_URI,
                scopes = SCOPES,
            ).onSuccess { credentialApp ->
                preferencesRepository.saveTemporaryAuthData(
                    serverName = serverName,
                    clientId = credentialApp.clientId,
                    clientSecret = credentialApp.clientSecret,
                )
                _authState.value =
                    AuthState.Redirect(buildOAuthUrl(serverName, credentialApp.clientId))
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "")
                Napier.e("Failed to register client app", error)
            }
        }
    }

    private suspend fun registerMisskeyApp(serverName: String) {
        val session = Uuid.random().toString()
        preferencesRepository.saveMiAuthSession(serverName, session)
        _authState.value = AuthState.Redirect(buildMiAuthUrl(serverName, session))
    }

    fun obtainAccessToken(code: String) {
        viewModelScope.launch {
            val tempAuthData = preferencesRepository.temporaryAuthData.first() ?: return@launch
            val client = createHttpClient(tempAuthData.serverName)
            val mastodonRepository = MastodonRepositoryImpl(client)

            mastodonRepository.obtainAccessToken(
                code = code,
                clientId = tempAuthData.clientId,
                clientSecret = tempAuthData.clientSecret,
                redirectUri = REDIRECT_URI
            ).onSuccess { token ->
                preferencesRepository.saveBearerToken(token.accessToken)
                preferencesRepository.savePlatform(Platform.MASTODON)
                preferencesRepository.clearTemporaryAuthData()
                _authState.value = AuthState.Success
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "")
                Napier.e("Failed to obtain access token", error)
            }
        }
    }

    fun obtainMiAuthToken() {
        viewModelScope.launch {
            val serverName = preferencesRepository.serverName.first()
            val session = preferencesRepository.miAuthSession.first() ?: return@launch
            val client = createHttpClient(serverName)
            val misskeyRepository = MisskeyRepositoryImpl(client)

            misskeyRepository.checkMiAuth(session)
                .onSuccess { miAuth ->
                    val token = miAuth.token
                    if (!miAuth.ok || token == null) {
                        _authState.value = AuthState.Error("Failed to obtain access token")
                        return@onSuccess
                    }
                    preferencesRepository.saveBearerToken(token)
                    preferencesRepository.savePlatform(Platform.MISSKEY)
                    preferencesRepository.clearTemporaryAuthData()
                    _authState.value = AuthState.Success
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

    private fun buildMiAuthUrl(
        serverName: String,
        session: String
    ): String {
        return "https://$serverName/miauth/$session" +
            "?name=${CLIENT_NAME}" +
            "&callback=${MI_AUTH_CALLBACK}" +
            "&permission=${MI_AUTH_PERMISSION}"
    }

    companion object {
        const val CLIENT_NAME = "Ferret"
        const val REDIRECT_URI = "ferret://oauth"
        const val SCOPES = "read write follow"
        const val MI_AUTH_CALLBACK = "ferret://miauth"
        const val MI_AUTH_PERMISSION = "read:account"
    }
}
