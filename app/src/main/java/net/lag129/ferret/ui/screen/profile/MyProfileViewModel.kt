package net.lag129.ferret.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.lag129.ferret.model.Account
import net.lag129.ferret.model.Status
import net.lag129.ferret.repository.MastodonRepository

class MyProfileViewModel(
    private val mastodonRepository: MastodonRepository
) : ViewModel() {

    private val _statuses = MutableStateFlow(listOf<Status>())
    val statuses: StateFlow<List<Status>> = _statuses.asStateFlow()

    private val _account = MutableStateFlow<Account?>(null)
    val account: StateFlow<Account?> = _account.asStateFlow()

    fun fetchMyStatuses() {
        viewModelScope.launch {
            val account = mastodonRepository.getMyCredential()
                .onFailure { Napier.e("Failed to fetch my credential", it) }
                .getOrNull() ?: return@launch
            _account.value = account

            mastodonRepository.getAccountStatuses(account.id)
                .onSuccess { statuses -> _statuses.value = statuses }
                .onFailure { error -> Napier.e("Failed to fetch account statuses", error) }
        }
    }

    fun fetchNextStatusesById(maxId: String) {
        val accountId = account.value?.id ?: return
        viewModelScope.launch {
            mastodonRepository.getAccountStatuses(accountId, maxId)
                .onSuccess { statuses -> _statuses.value += statuses }
                .onFailure { error -> Napier.e("Failed to fetch next account statuses", error) }
        }
    }
}
