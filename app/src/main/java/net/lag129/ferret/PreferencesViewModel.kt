package net.lag129.ferret

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {

    private companion object {
        val SERVER_NAME_KEY = stringPreferencesKey("server_name")
        val BEARER_TOKEN_KEY = stringPreferencesKey("bearer_token")
    }

    suspend fun saveServerName(serverName: String) {
        dataStore.edit { preferences ->
            preferences[SERVER_NAME_KEY] = serverName
        }
    }

    suspend fun saveBearerToken(bearerToken: String) {
        dataStore.edit { preferences ->
            preferences[BEARER_TOKEN_KEY] = bearerToken
        }
    }

    suspend fun readServerName(): String {
        return dataStore.data.map { preferences ->
            preferences[SERVER_NAME_KEY] ?: ""
        }.first()
    }

    suspend fun readBearerToken(): String {
        return dataStore.data.map { preferences ->
            preferences[BEARER_TOKEN_KEY] ?: ""
        }.first()
    }
}

class PreferencesViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    private val _serverName = MutableStateFlow<String?>(null)
    val serverName = _serverName.asStateFlow()

    private val _bearerToken = MutableStateFlow<String?>(null)
    val bearerToken = _bearerToken.asStateFlow()

    init {
        getServerName()
        getBearerToken()
    }

    fun setServerName(serverName: String) {
        viewModelScope.launch {
            preferencesRepository.saveServerName(serverName)
        }
    }

    fun setBearerToken(bearerToken: String) {
        viewModelScope.launch {
            preferencesRepository.saveBearerToken(bearerToken)
        }
    }

    fun getServerName() {
        viewModelScope.launch {
            val serverName = preferencesRepository.readServerName()
            _serverName.emit(serverName)
        }
    }

    fun getBearerToken() {
        viewModelScope.launch {
            val bearerToken = preferencesRepository.readBearerToken()
            _bearerToken.emit(bearerToken)
        }
    }
}