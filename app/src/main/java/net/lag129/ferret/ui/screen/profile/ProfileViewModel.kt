package net.lag129.ferret.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.lag129.ferret.model.Post
import net.lag129.ferret.repository.FediverseRepository

class ProfileViewModel(
    private val fediverseRepository: FediverseRepository
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
            fediverseRepository.getAccountPosts(accountId)
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
            fediverseRepository.getAccountPosts(accountId, maxId)
                .onSuccess { statuses ->
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
