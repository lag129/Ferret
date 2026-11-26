package net.lag129.ferret.ui.screen.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.lag129.ferret.model.Post
import net.lag129.ferret.repository.CacheRepository
import net.lag129.ferret.repository.FediverseRepository

enum class Timeline {
    HOME, LOCAL, FEDERATED
}

class TimelineViewModel(
    private val fediverseRepository: FediverseRepository,
    private val cacheRepository: CacheRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(listOf<Post>())
    val uiState: StateFlow<List<Post>> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _currentTimeline = MutableStateFlow(Timeline.HOME)
    val currentTimeline: StateFlow<Timeline> = _currentTimeline.asStateFlow()

    init {
        fetchTimeline()
    }

    fun fetchTimeline() {
        viewModelScope.launch {
            val statuses = fetchTimelineByType()

            statuses.onSuccess { statuses ->
                _uiState.value = statuses
                cacheRepository.savePosts(_currentTimeline.value.name, statuses)
            }.onFailure { error ->
                Napier.e("Failed to fetch timeline", error)
            }
        }
    }

    fun fetchNextTimeline(maxId: String) {
        viewModelScope.launch {
            val statuses = fetchTimelineByType(maxId)

            statuses.onSuccess { statuses ->
                _uiState.value += statuses
                cacheRepository.savePosts(_currentTimeline.value.name, _uiState.value)
            }.onFailure { error ->
                Napier.e("Failed to fetch next home timeline", error)
            }
        }
    }

    fun refreshTimeline() {
        if (_isRefreshing.value) return

        viewModelScope.launch {
            _isRefreshing.value = true
            val statuses = fetchTimelineByType()

            statuses.onSuccess { statuses ->
                val currentFirst = _uiState.value.firstOrNull()?.id
                val newStatuses = statuses.takeWhile { it.id != currentFirst }
                _uiState.value = newStatuses + _uiState.value
                cacheRepository.savePosts(_currentTimeline.value.name, _uiState.value)
            }.onFailure { error ->
                Napier.e("Failed to refresh home timeline", error)
            }
            _isRefreshing.value = false
        }
    }

    fun switchTimeline(timeline: Timeline) {
        if (_currentTimeline.value == timeline) return

        _currentTimeline.value = timeline
        _uiState.value = emptyList()

        viewModelScope.launch {
            val cached = cacheRepository.getPosts(timeline.name)
            if (cached.isNotEmpty()) {
                _uiState.value = cached
            } else {
                fetchTimeline()
            }
        }
    }

    private suspend fun fetchTimelineByType(
        maxId: String? = null
    ): Result<List<Post>> {
        return when (_currentTimeline.value) {
            Timeline.HOME -> fediverseRepository.getHomeTimeline(maxId)
            Timeline.LOCAL -> fediverseRepository.getLocalTimeline(maxId)
            Timeline.FEDERATED -> fediverseRepository.getFederatedTimeline(maxId)
        }
    }
}
