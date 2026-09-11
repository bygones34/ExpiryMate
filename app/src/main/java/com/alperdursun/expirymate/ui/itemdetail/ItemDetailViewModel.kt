package com.alperdursun.expirymate.ui.itemdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.alperdursun.expirymate.data.reminder.ReminderScheduler
import com.alperdursun.expirymate.data.repository.ItemRepository
import com.alperdursun.expirymate.domain.model.Item
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ItemDetailUiState(
    val item: Item? = null,
    val isLoading: Boolean = true,
    val isNotFound: Boolean = false,
)

class ItemDetailViewModel(
    private val itemId: Long,
    private val itemRepository: ItemRepository,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    val uiState: StateFlow<ItemDetailUiState> = itemRepository.observeItemById(itemId)
        .map { item ->
            if (item == null) {
                ItemDetailUiState(isLoading = false, isNotFound = true)
            } else {
                ItemDetailUiState(item = item, isLoading = false, isNotFound = false)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ItemDetailUiState(),
        )

    fun markAsUsed() {
        viewModelScope.launch {
            itemRepository.markAsUsed(itemId)
            reminderScheduler.cancelReminder(itemId)
        }
    }

    fun markAsDiscarded() {
        viewModelScope.launch {
            itemRepository.markAsDiscarded(itemId)
            reminderScheduler.cancelReminder(itemId)
        }
    }

    fun deleteItem(onDeleted: () -> Unit) {
        viewModelScope.launch {
            reminderScheduler.cancelReminder(itemId)
            itemRepository.deleteItem(itemId)
            onDeleted()
        }
    }

    companion object {
        fun Factory(
            itemId: Long,
            repository: ItemRepository,
            reminderScheduler: ReminderScheduler,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ItemDetailViewModel(itemId, repository, reminderScheduler)
            }
        }
    }
}
