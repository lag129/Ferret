package net.lag129.ferret.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.lag129.ferret.model.Post
import net.lag129.ferret.repository.FediverseRepository

class SearchViewModel(
    private val fediverseRepository: FediverseRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<List<Post>>(emptyList())
    val uiState: StateFlow<List<Post>> = _uiState.asStateFlow()

    fun searchByKeyword(keyword: String) {
        viewModelScope.launch {
            fediverseRepository.search(keyword)
                .onSuccess { result -> _uiState.value = result }
                .onFailure { error -> Napier.e("Failed to search: $keyword", error) }
        }
    }
}
