package net.lag129.ferret.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class Platform {
    MASTODON,
    MISSKEY,
}

data class TemporaryAuthData(
    val serverName: String,
    val clientId: String,
    val clientSecret: String,
    val state: String,
)

interface PreferencesRepository {
    val serverName: Flow<String>
    val bearerToken: Flow<String>
    val temporaryAuthData: Flow<TemporaryAuthData?>
    val platform: Flow<Platform>
    val miAuthSession: Flow<String?>
    suspend fun saveServerName(serverName: String): Preferences
    suspend fun saveBearerToken(bearerToken: String): Preferences
    suspend fun savePlatform(platform: Platform): Preferences
    suspend fun saveMiAuthSession(serverName: String, session: String): Preferences

    suspend fun saveTemporaryAuthData(
        serverName: String,
        clientId: String,
        clientSecret: String,
        state: String,
    ): Preferences

    suspend fun clearTemporaryAuthData(): Preferences
}

class PreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : PreferencesRepository {

    override val serverName: Flow<String> = dataStore.data
        .map { preferences -> preferences[SERVER_NAME] ?: "" }

    override val bearerToken: Flow<String> = dataStore.data
        .map { preferences -> preferences[BEARER_TOKEN] ?: "" }

    override val temporaryAuthData = dataStore.data.map { preferences ->
        val serverName = preferences[SERVER_NAME]
        val clientId = preferences[CLIENT_ID]
        val clientSecret = preferences[CLIENT_SECRET]
        val state = preferences[OAUTH_STATE]

        if (serverName != null && clientId != null && clientSecret != null && state != null) {
            TemporaryAuthData(
                serverName = serverName,
                clientId = clientId,
                clientSecret = clientSecret,
                state = state,
            )
        } else {
            null
        }
    }

    override val platform: Flow<Platform> = dataStore.data
        .map { preferences ->
            preferences[PLATFORM]?.let { Platform.valueOf(it) } ?: Platform.MASTODON
        }

    override val miAuthSession: Flow<String?> = dataStore.data
        .map { preferences -> preferences[MI_AUTH_SESSION] }

    override suspend fun saveServerName(serverName: String) =
        dataStore.edit { preferences ->
            preferences[SERVER_NAME] = serverName
        }

    override suspend fun saveBearerToken(bearerToken: String) =
        dataStore.edit { preferences ->
            preferences[BEARER_TOKEN] = bearerToken
        }

    override suspend fun savePlatform(platform: Platform) =
        dataStore.edit { preferences ->
            preferences[PLATFORM] = platform.name
        }

    override suspend fun saveMiAuthSession(serverName: String, session: String) =
        dataStore.edit { preferences ->
            preferences[SERVER_NAME] = serverName
            preferences[MI_AUTH_SESSION] = session
        }

    override suspend fun saveTemporaryAuthData(
        serverName: String,
        clientId: String,
        clientSecret: String,
        state: String,
    ) = dataStore.edit { preferences ->
        preferences[SERVER_NAME] = serverName
        preferences[CLIENT_ID] = clientId
        preferences[CLIENT_SECRET] = clientSecret
        preferences[OAUTH_STATE] = state
    }

    override suspend fun clearTemporaryAuthData() = dataStore.edit { preferences ->
        preferences.remove(CLIENT_ID)
        preferences.remove(CLIENT_SECRET)
        preferences.remove(OAUTH_STATE)
        preferences.remove(MI_AUTH_SESSION)
    }

    companion object {
        val SERVER_NAME = stringPreferencesKey("server_name")
        val BEARER_TOKEN = stringPreferencesKey("bearer_token")
        val CLIENT_ID = stringPreferencesKey("client_id")
        val CLIENT_SECRET = stringPreferencesKey("client_secret")
        val OAUTH_STATE = stringPreferencesKey("oauth_state")
        val PLATFORM = stringPreferencesKey("platform")
        val MI_AUTH_SESSION = stringPreferencesKey("mi_auth_session")
    }
}
