package net.lag129.ferret.ui.screen.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import net.lag129.ferret.db.CachedStatus
import net.lag129.ferret.db.CachedStatusDao
import net.lag129.ferret.repository.TimelineRepository
import net.lag129.ferret.ui.model.Post

enum class Timeline {
    HOME, LOCAL, FEDERATED
}

class TimelineViewModel(
    private val timelineRepository: TimelineRepository,
    private val cachedStatusDao: CachedStatusDao
) : ViewModel() {

    private val json = Json { ignoreUnknownKeys = true }

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
                saveToCache(_currentTimeline.value, statuses)
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
                saveToCache(_currentTimeline.value, _uiState.value)
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
                saveToCache(_currentTimeline.value, _uiState.value)
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
            val cached = cachedStatusDao.getCachedStatus(timeline.name)
            if (cached.isNotEmpty()) {
                _uiState.value = restoreFromCache(cached)
            } else {
                fetchTimeline()
            }
        }
    }

    private suspend fun fetchTimelineByType(
        maxId: String? = null
    ): Result<List<Post>> {
        return when (_currentTimeline.value) {
            Timeline.HOME -> timelineRepository.getHomeTimeline(maxId)
            Timeline.LOCAL -> timelineRepository.getLocalTimeline(maxId)
            Timeline.FEDERATED -> timelineRepository.getFederatedTimeline(maxId)
        }
    }

    private suspend fun saveToCache(timeline: Timeline, statuses: List<Post>) {
        val cachedStatuses = withContext(Dispatchers.Default) {
            statuses.mapIndexed { index, status ->
                CachedStatus(
                    statusId = status.id,
                    timelineType = timeline.name,
                    statusJson = json.encodeToString(status),
                    orderIndex = index
                )
            }
        }

        cachedStatusDao.clearTimeline(timeline.name)
        cachedStatusDao.insertAll(cachedStatuses)
    }

    private suspend fun restoreFromCache(cached: List<CachedStatus>): List<Post> {
        return withContext(Dispatchers.Default) { cached.map { json.decodeFromString<Post>(it.statusJson) } }
    }
}
