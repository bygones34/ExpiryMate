package com.alperdursun.expirymate.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.alperdursun.expirymate.data.repository.ItemRepository
import com.alperdursun.expirymate.data.repository.SettingsRepository
import com.alperdursun.expirymate.domain.model.Item
import com.alperdursun.expirymate.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeUiState(
    val activeItems: List<Item> = emptyList(),
    val expiredCount: Int = 0,
    val thisWeekCount: Int = 0,
    val laterCount: Int = 0,
    val nextToExpire: Item? = null,
    val expiringSoonItems: List<Item> = emptyList(),
    val hasSeenWelcome: Boolean = true,
    val isLoading: Boolean = true,
)

class HomeViewModel(
    itemRepository: ItemRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        itemRepository.observeActiveItems(),
        settingsRepository.hasSeenWelcome,
    ) { items, hasSeenWelcome ->
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
            hasSeenWelcome = hasSeenWelcome,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(),
    )

    fun onWelcomeDismissed() {
        viewModelScope.launch {
            settingsRepository.setHasSeenWelcome(true)
        }
    }

    companion object {
        fun Factory(
            itemRepository: ItemRepository,
            settingsRepository: SettingsRepository,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel(itemRepository, settingsRepository)
            }
        }
    }
}
