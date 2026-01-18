package net.lag129.ferret

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("preferences")

data class TemporaryAuthData(
    val serverName: String,
    val clientId: String,
    val clientSecret: String
)

interface PreferencesRepository {
    val serverName: Flow<String>
    val bearerToken: Flow<String>
    val temporaryAuthData: Flow<TemporaryAuthData?>
    suspend fun saveServerName(serverName: String): Preferences
    suspend fun saveBearerToken(bearerToken: String): Preferences

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

    override val serverName = context.dataStore.data.map { preferences ->
        preferences[SERVER_NAME] ?: ""
    }

    override val bearerToken = context.dataStore.data.map { preferences ->
        preferences[BEARER_TOKEN] ?: ""
    }

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

    override suspend fun saveServerName(serverName: String) =
        context.dataStore.edit { preferences ->
            preferences[SERVER_NAME] = serverName
        }

    override suspend fun saveBearerToken(bearerToken: String) =
        context.dataStore.edit { preferences ->
            preferences[BEARER_TOKEN] = bearerToken
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
    }

    companion object {
        val SERVER_NAME = stringPreferencesKey("server_name")
        val BEARER_TOKEN = stringPreferencesKey("bearer_token")
        val CLIENT_ID = stringPreferencesKey("client_id")
        val CLIENT_SECRET = stringPreferencesKey("client_secret")
    }
}
