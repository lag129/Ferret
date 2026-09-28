package net.lag129.ferret.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import net.lag129.ferret.repository.PreferencesRepository

sealed class LoginState {
    object LoggedIn : LoginState()
    object LoggedOut : LoginState()
    object Loading : LoginState()
}

class PreferencesViewModel(
    private val repository: PreferencesRepository,
) : ViewModel() {
    val loginState: StateFlow<LoginState> =
        combine(repository.serverName, repository.bearerToken) { server, token ->
            if (server.isNotBlank() && token.isNotBlank()) LoginState.LoggedIn else LoginState.LoggedOut
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LoginState.Loading)
}
