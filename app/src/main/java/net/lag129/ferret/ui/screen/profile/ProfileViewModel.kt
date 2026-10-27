package net.lag129.ferret.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.lag129.ferret.repository.MastodonRepository
import net.lag129.ferret.ui.model.Post

class ProfileViewModel(
    private val mastodonRepository: MastodonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(listOf<Post>())
    val uiState: StateFlow<List<Post>> = _uiState.asStateFlow()

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
                    _uiState.value = statuses.map { it.toPost() }
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
                    _uiState.value += statuses.map { it.toPost() }
                }.onFailure { error ->
                    Napier.e("Failed to fetch next account statuses", error)
                }
        }
    }

    private fun clearStatuses() {
        _uiState.value = emptyList()
    }
}
