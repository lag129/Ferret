package net.lag129.ferret.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.lag129.ferret.model.Post
import net.lag129.ferret.model.Profile
import net.lag129.ferret.repository.FediverseRepository

class MyProfileViewModel(
    private val fediverseRepository: FediverseRepository
) : ViewModel() {

    private val _statuses = MutableStateFlow(listOf<Post>())
    val statuses: StateFlow<List<Post>> = _statuses.asStateFlow()

    private val _account = MutableStateFlow<Profile?>(null)
    val account: StateFlow<Profile?> = _account.asStateFlow()

    fun fetchMyStatuses() {
        viewModelScope.launch {
            val account = fediverseRepository.getMyCredential()
                .onFailure { Napier.e("Failed to fetch my credential", it) }
                .getOrNull() ?: return@launch
            _account.value = account

            fediverseRepository.getAccountPosts(account.id)
                .onSuccess { statuses -> _statuses.value = statuses }
                .onFailure { error -> Napier.e("Failed to fetch account statuses", error) }
        }
    }

    fun fetchNextStatusesById(maxId: String) {
        val accountId = account.value?.id ?: return
        viewModelScope.launch {
            fediverseRepository.getAccountPosts(accountId, maxId)
                .onSuccess { statuses -> _statuses.value += statuses }
                .onFailure { error -> Napier.e("Failed to fetch next account statuses", error) }
        }
    }
}
