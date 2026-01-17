package net.lag129.ferret

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PreferencesViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val preferencesRepository = PreferencesRepositoryImpl(application)
    private val _serverName = MutableStateFlow<String?>(null)
    val serverName = _serverName.asStateFlow()

    private val _bearerToken = MutableStateFlow<String?>(null)
    val bearerToken = _bearerToken.asStateFlow()

    private val _isLoggedIn = MutableStateFlow<Boolean>(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
//        getServerName()
//        getBearerToken()
        checkLoggedIn()
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

    fun checkLoggedIn() {
        viewModelScope.launch {
            val serverName = preferencesRepository.serverName.first()
            val bearerToken = preferencesRepository.bearerToken.first()
            _isLoggedIn.emit(serverName.isNotEmpty() && bearerToken.isNotEmpty())
        }
    }

//    fun getServerName() {
//        viewModelScope.launch {
//            val serverName = preferencesRepository.serverName
//            _serverName.emit(serverName)
//        }
//    }

//    fun getBearerToken() {
//        viewModelScope.launch {
//            val bearerToken = preferencesRepository.readBearerToken()
//            _bearerToken.emit(bearerToken)
//        }
//    }
}