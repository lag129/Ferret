package net.lag129.ferret

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PreferencesViewModel(
    private val preferencesRepository: PreferencesRepositoryImpl
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