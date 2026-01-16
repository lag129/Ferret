package net.lag129.ferret

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface PreferencesRepository {
    val serverName: Flow<String>
    val bearerToken: Flow<String>
    suspend fun saveServerName(serverName: String): Preferences
    suspend fun saveBearerToken(bearerToken: String): Preferences
}

class PreferencesRepositoryImpl(
    private val context: Context
) : PreferencesRepository {

    val Context.dataStore by preferencesDataStore("preferences")

    override val serverName = context.dataStore.data.map { preferences ->
        preferences[SERVER_NAME] ?: ""
    }

    override val bearerToken = context.dataStore.data.map { preferences ->
        preferences[BEARER_TOKEN] ?: ""
    }

    override suspend fun saveServerName(serverName: String) =
        context.dataStore.edit { preferences ->
            preferences[SERVER_NAME] = serverName
        }

    override suspend fun saveBearerToken(bearerToken: String) =
        context.dataStore.edit { preferences ->
            preferences[BEARER_TOKEN] = bearerToken
        }

    companion object {
        val SERVER_NAME = stringPreferencesKey("server_name")
        val BEARER_TOKEN = stringPreferencesKey("bearer_token")
    }
}
