package com.alperdursun.expirymate.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.alperdursun.expirymate.data.repository.ItemRepository
import com.alperdursun.expirymate.domain.model.Item
import com.alperdursun.expirymate.domain.model.ItemStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class HistoryFilter {
    ALL,
    USED,
    DISCARDED
}

data class HistoryUiState(
    val selectedFilter: HistoryFilter = HistoryFilter.ALL,
    val items: List<Item> = emptyList()
)

class HistoryViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val selectedFilter = MutableStateFlow(HistoryFilter.ALL)

    val uiState: StateFlow<HistoryUiState> = combine(
        itemRepository.observeHistoryItems(),
        selectedFilter
    ) { historyItems, filter ->
        val filtered = when (filter) {
            HistoryFilter.ALL -> historyItems
            HistoryFilter.USED -> historyItems.filter { it.status == ItemStatus.USED }
            HistoryFilter.DISCARDED -> historyItems.filter { it.status == ItemStatus.DISCARDED }
        }

        HistoryUiState(
            selectedFilter = filter,
            items = filtered
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState()
    )

    fun onFilterSelected(filter: HistoryFilter) {
        selectedFilter.value = filter
    }

    companion object {
        fun Factory(repository: ItemRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HistoryViewModel(repository)
            }
        }
    }
}
