package net.lag129.ferret

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.lag129.ferret.api.entity.Status

enum class Timeline {
    HOME, LOCAL, FEDERATED
}

class TimelineViewModel(
    private val mastodonRepository: MastodonRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(persistentListOf<Status>())
    val uiState: StateFlow<ImmutableList<Status>> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _currentTimeline = MutableStateFlow(Timeline.HOME)
    val currentTimeline: StateFlow<Timeline> = _currentTimeline.asStateFlow()

    init {
        fetchTimeline()
    }

    private fun fetchTimeline() {
        viewModelScope.launch {
            val statuses = when (_currentTimeline.value) {
                Timeline.HOME -> mastodonRepository.getHomeTimeline()
                Timeline.LOCAL -> mastodonRepository.getLocalTimeline()
                Timeline.FEDERATED -> mastodonRepository.getFederatedTimeline()
            }

            statuses.onSuccess { statuses ->
                _uiState.value = statuses.toPersistentList()
            }.onFailure { error ->
                Napier.e("Failed to fetch timeline", error)
            }
        }
    }

    fun fetchNextHomeTimeline(
        maxId: String
    ) {
        viewModelScope.launch {
            val statuses = when (_currentTimeline.value) {
                Timeline.HOME -> mastodonRepository.getHomeTimeline(maxId)
                Timeline.LOCAL -> mastodonRepository.getLocalTimeline(maxId)
                Timeline.FEDERATED -> mastodonRepository.getFederatedTimeline(maxId)
            }

            statuses.onSuccess { statuses ->
                _uiState.value = _uiState.value.addAll(statuses)
            }.onFailure { error ->
                Napier.e("Failed to fetch next home timeline", error)
            }
        }
    }

    fun refreshHomeTimeline() {
        if (_isRefreshing.value) return

        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val statuses = when (_currentTimeline.value) {
                    Timeline.HOME -> mastodonRepository.getHomeTimeline()
                    Timeline.LOCAL -> mastodonRepository.getLocalTimeline()
                    Timeline.FEDERATED -> mastodonRepository.getFederatedTimeline()
                }

                statuses.onSuccess { statuses ->
                    val currentFirst = _uiState.value.firstOrNull()?.id
                    val newStatuses = statuses.takeWhile { it.id != currentFirst }
                    _uiState.value = newStatuses.toPersistentList().addAll(_uiState.value)
                }.onFailure { error ->
                    Napier.e("Failed to refresh home timeline", error)
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun switchTimeline(timeline: Timeline) {
        if (_currentTimeline.value == timeline) return

        _currentTimeline.value = timeline
        _uiState.value = persistentListOf()
        fetchTimeline()
    }
}
