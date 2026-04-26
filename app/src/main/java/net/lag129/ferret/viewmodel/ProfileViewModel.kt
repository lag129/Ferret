package net.lag129.ferret.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.lag129.ferret.repository.MastodonRepository
import net.lag129.ferret.model.Status

class ProfileViewModel(
    private val mastodonRepository: MastodonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(listOf<Status>())
    val uiState: StateFlow<List<Status>> = _uiState.asStateFlow()

    private var currentAccountId: String? = null

    fun fetchAccountStatuses(accountId: String) {
        if (currentAccountId == accountId) {
            return
        }
        currentAccountId = accountId
        clearStatuses()

        viewModelScope.launch {
            val statuses = mastodonRepository.getAccountStatuses(accountId)

            statuses.onSuccess { statuses ->
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
            val statuses = mastodonRepository.getAccountStatuses(accountId, maxId)

            statuses.onSuccess { statuses ->
                _uiState.value += statuses
            }.onFailure { error ->
                Napier.e("Failed to fetch next account statuses", error)
            }
        }
    }

    private fun clearStatuses() {
        _uiState.value = emptyList()
    }
}
