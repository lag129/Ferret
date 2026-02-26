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
import kotlinx.serialization.json.Json
import net.lag129.ferret.api.entity.Status
import net.lag129.ferret.db.CachedStatus
import net.lag129.ferret.db.CachedStatusDao

enum class Timeline {
    HOME, LOCAL, FEDERATED
}

class TimelineViewModel(
    private val mastodonRepository: MastodonRepository,
    private val cachedStatusDao: CachedStatusDao
) : ViewModel() {

    private val json = Json { ignoreUnknownKeys = true }

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
            val statuses = fetchTimelineByType()

            statuses.onSuccess { statuses ->
                _uiState.value = statuses.toPersistentList()
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
                _uiState.value = _uiState.value.addAll(statuses)
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
                _uiState.value = newStatuses.toPersistentList().addAll(_uiState.value)
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
        _uiState.value = persistentListOf()

        viewModelScope.launch {
            val cached = cachedStatusDao.getCachedStatus(timeline.name)
            if (cached.isNotEmpty()) {
                _uiState.value = restoreFromCache(cached).toPersistentList()
            } else {
                fetchTimeline()
            }
        }
    }

    private suspend fun fetchTimelineByType(
        maxId: String? = null
    ): Result<List<Status>> {
        return when (_currentTimeline.value) {
            Timeline.HOME -> mastodonRepository.getHomeTimeline(maxId)
            Timeline.LOCAL -> mastodonRepository.getLocalTimeline(maxId)
            Timeline.FEDERATED -> mastodonRepository.getFederatedTimeline(maxId)
        }
    }

    private suspend fun saveToCache(timeline: Timeline, statuses: List<Status>) {
        val cachedStatuses = statuses.mapIndexed { index, status ->
            CachedStatus(
                statusId = status.id,
                timelineType = timeline.name,
                statusJson = json.encodeToString(status),
                orderIndex = index
            )
        }

        cachedStatusDao.clearTimeline(timeline.name)
        cachedStatusDao.insertAll(cachedStatuses)
    }

    private fun restoreFromCache(cached: List<CachedStatus>): List<Status> {
        return cached.map { json.decodeFromString<Status>(it.statusJson) }
    }
}
