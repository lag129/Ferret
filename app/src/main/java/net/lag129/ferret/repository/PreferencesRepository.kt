package net.lag129.ferret.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.cybozu.datastore.crypto.preferences.encryptedPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by encryptedPreferencesDataStore(
    name = "preferences",
    masterKeyAlias = "preferences_master_key"
)

enum class Platform {
    MASTODON,
    MISSKEY,
}

data class TemporaryAuthData(
    val serverName: String,
    val clientId: String,
    val clientSecret: String
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
        clientSecret: String
    ): Preferences

    suspend fun clearTemporaryAuthData(): Preferences
}

class PreferencesRepositoryImpl(
    private val context: Context
) : PreferencesRepository {

    override val serverName: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[SERVER_NAME] ?: "" }

    override val bearerToken: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[BEARER_TOKEN] ?: "" }

    override val temporaryAuthData = context.dataStore.data.map { preferences ->
        val serverName = preferences[SERVER_NAME]
        val clientId = preferences[CLIENT_ID]
        val clientSecret = preferences[CLIENT_SECRET]

        if (serverName != null && clientId != null && clientSecret != null) {
            TemporaryAuthData(
                serverName = serverName,
                clientId = clientId,
                clientSecret = clientSecret
            )
        } else {
            null
        }
    }

    override val platform: Flow<Platform> = context.dataStore.data
        .map { preferences ->
            preferences[PLATFORM]?.let { Platform.valueOf(it) } ?: Platform.MASTODON
        }

    override val miAuthSession: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[MI_AUTH_SESSION] }

    override suspend fun saveServerName(serverName: String) =
        context.dataStore.edit { preferences ->
            preferences[SERVER_NAME] = serverName
        }

    override suspend fun saveBearerToken(bearerToken: String) =
        context.dataStore.edit { preferences ->
            preferences[BEARER_TOKEN] = bearerToken
        }

    override suspend fun savePlatform(platform: Platform) =
        context.dataStore.edit { preferences ->
            preferences[PLATFORM] = platform.name
        }

    override suspend fun saveMiAuthSession(serverName: String, session: String) =
        context.dataStore.edit { preferences ->
            preferences[SERVER_NAME] = serverName
            preferences[MI_AUTH_SESSION] = session
        }

    override suspend fun saveTemporaryAuthData(
        serverName: String,
        clientId: String,
        clientSecret: String
    ) = context.dataStore.edit { preferences ->
        preferences[SERVER_NAME] = serverName
        preferences[CLIENT_ID] = clientId
        preferences[CLIENT_SECRET] = clientSecret
    }

    override suspend fun clearTemporaryAuthData() = context.dataStore.edit { preferences ->
        preferences.remove(CLIENT_ID)
        preferences.remove(CLIENT_SECRET)
        preferences.remove(MI_AUTH_SESSION)
    }

    companion object {
        val SERVER_NAME = stringPreferencesKey("server_name")
        val BEARER_TOKEN = stringPreferencesKey("bearer_token")
        val CLIENT_ID = stringPreferencesKey("client_id")
        val CLIENT_SECRET = stringPreferencesKey("client_secret")
        val PLATFORM = stringPreferencesKey("platform")
        val MI_AUTH_SESSION = stringPreferencesKey("mi_auth_session")
    }
}
