package net.lag129.ferret.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.lag129.ferret.model.Search
import net.lag129.ferret.repository.MastodonRepository

class SearchViewModel(
    private val mastodonRepository: MastodonRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<Search?>(null)
    val uiState: StateFlow<Search?> = _uiState.asStateFlow()

    fun searchByKeyword(keyword: String) {
        viewModelScope.launch {
            mastodonRepository.search(keyword)
                .onSuccess { result -> _uiState.value = result }
                .onFailure { error -> Napier.e("Failed to search: $keyword", error) }
        }
    }
}
