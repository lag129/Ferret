package net.lag129.ferret

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface PreferencesRepository {
    suspend fun saveServerName(serverName: String)
    suspend fun saveBearerToken(bearerToken: String)
    suspend fun readServerName(): String
    suspend fun readBearerToken(): String
}

class PreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {

    private object PreferencesKeys {
        val SERVER_NAME = stringPreferencesKey("server_name")
        val BEARER_TOKEN = stringPreferencesKey("bearer_token")
    }

    override suspend fun saveServerName(serverName: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SERVER_NAME] = serverName
        }
    }

    override suspend fun saveBearerToken(bearerToken: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BEARER_TOKEN] = bearerToken
        }
    }

    override suspend fun readServerName(): String {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.SERVER_NAME] ?: ""
        }.first()
    }

    override suspend fun readBearerToken(): String {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.BEARER_TOKEN] ?: ""
        }.first()
    }
}
