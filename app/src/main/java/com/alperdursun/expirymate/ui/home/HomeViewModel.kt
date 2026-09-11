package com.alperdursun.expirymate.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.alperdursun.expirymate.data.repository.ItemRepository
import com.alperdursun.expirymate.domain.model.Item
import com.alperdursun.expirymate.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

data class HomeUiState(
    val activeItems: List<Item> = emptyList(),
    val expiredCount: Int = 0,
    val thisWeekCount: Int = 0,
    val laterCount: Int = 0,
    val nextToExpire: Item? = null,
    val expiringSoonItems: List<Item> = emptyList(),
    val isLoading: Boolean = true,
)

class HomeViewModel(
    itemRepository: ItemRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = itemRepository.observeActiveItems()
        .map { items ->
            val today = LocalDate.now()
            val expired = items.count { DateUtils.isExpired(it.expirationDate, today) }
            val thisWeek = items.count { DateUtils.isThisWeek(it.expirationDate, today) }
            val later = items.count { DateUtils.isLater(it.expirationDate, today) }

            val sortedItems = items.sortedBy { it.expirationDate }
            val nextItem = sortedItems.firstOrNull()
            val expiringSoon = sortedItems
                .filter { DateUtils.isThisWeek(it.expirationDate, today) }
                .take(3)

            HomeUiState(
                activeItems = items,
                expiredCount = expired,
                thisWeekCount = thisWeek,
                laterCount = later,
                nextToExpire = nextItem,
                expiringSoonItems = expiringSoon,
                isLoading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState()
        )

    companion object {
        fun Factory(repository: ItemRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel(repository)
            }
        }
    }
}
