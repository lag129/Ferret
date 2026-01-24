package net.lag129.ferret

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.lag129.ferret.api.entity.Status

class TimelineViewModel(
    private val mastodonRepository: MastodonRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(listOf<Status>())
    val uiState: StateFlow<List<Status>> = _uiState.asStateFlow()

    init {
        fetchHomeTimeline()
    }

    private fun fetchHomeTimeline() {
        viewModelScope.launch {
            val statuses = mastodonRepository.getHomeTimeline()

            statuses.onSuccess { statuses ->
                _uiState.value = statuses
            }.onFailure { error ->
                Napier.e("Failed to fetch home timeline", error)
            }
        }
    }

    fun fetchNextHomeTimeline(
        maxId: String
    ) {
        viewModelScope.launch {
            val statuses = mastodonRepository.getHomeTimeline(maxId)

            statuses.onSuccess { statuses ->
                _uiState.value += statuses
            }.onFailure { error ->
                Napier.e("Failed to fetch next home timeline", error)
            }
        }
    }
}
