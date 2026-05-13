package net.lag129.ferret.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.lag129.ferret.model.Status
import net.lag129.ferret.repository.MastodonRepository

class ProfileViewModel(
    private val mastodonRepository: MastodonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(listOf<Status>())
    val uiState: StateFlow<List<Status>> = _uiState.asStateFlow()

    private val _accountId = MutableSharedFlow<String>()
    val accountId: SharedFlow<String> = _accountId.asSharedFlow()

    private var currentAccountId: String? = null

    fun fetchAccountStatuses(accountId: String) {
        if (currentAccountId == accountId) {
            return
        }
        currentAccountId = accountId
        clearStatuses()

        viewModelScope.launch {
            mastodonRepository.getAccountStatuses(accountId)
                .onSuccess { statuses ->
                    _uiState.value = statuses
                }.onFailure { error ->
                    Napier.e("Failed to fetch account statuses", error)
                }
        }
    }

    fun fetchNextAccountStatuses(
        accountId: String,
        maxId: String
    ) {
        viewModelScope.launch {
            mastodonRepository.getAccountStatuses(accountId, maxId)
                .onSuccess { statuses ->
                    _uiState.value += statuses
                }.onFailure { error ->
                    Napier.e("Failed to fetch next account statuses", error)
                }
        }
    }

    fun fetchMyCredential() {
        viewModelScope.launch {
            mastodonRepository.getMyCredential()
                .onSuccess { account -> _accountId.emit(account.id) }
                .onFailure { Napier.e("Failed to fetch my credential", it) }
        }
    }

    private fun clearStatuses() {
        _uiState.value = emptyList()
    }
}
