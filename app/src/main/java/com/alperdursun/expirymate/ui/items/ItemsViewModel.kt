package com.alperdursun.expirymate.ui.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.alperdursun.expirymate.data.repository.ItemRepository
import com.alperdursun.expirymate.domain.model.Item
import com.alperdursun.expirymate.domain.model.ItemCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ItemsUiState(
    val selectedCategory: ItemCategory? = null,
    val searchQuery: String = "",
    val items: List<Item> = emptyList(),
    val totalActiveCount: Int = 0
)

class ItemsViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val selectedCategory = MutableStateFlow<ItemCategory?>(null)
    private val searchQuery = MutableStateFlow("")

    val uiState: StateFlow<ItemsUiState> = combine(
        itemRepository.observeActiveItems(),
        selectedCategory,
        searchQuery
    ) { activeItems, category, query ->
        val filtered = activeItems.filter { item ->
            val matchesCategory = (category == null) || (item.category == category)
            val matchesSearch = query.isBlank() ||
                item.name.contains(query, ignoreCase = true) ||
                item.category.displayName.contains(query, ignoreCase = true) ||
                (item.notes?.contains(query, ignoreCase = true) == true)
            matchesCategory && matchesSearch
        }

        ItemsUiState(
            selectedCategory = category,
            searchQuery = query,
            items = filtered,
            totalActiveCount = activeItems.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ItemsUiState()
    )

    fun onCategorySelected(category: ItemCategory?) {
        selectedCategory.value = category
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun markAsUsed(itemId: Long) {
        viewModelScope.launch {
            itemRepository.markAsUsed(itemId)
        }
    }

    fun markAsDiscarded(itemId: Long) {
        viewModelScope.launch {
            itemRepository.markAsDiscarded(itemId)
        }
    }

    companion object {
        fun Factory(repository: ItemRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ItemsViewModel(repository)
            }
        }
    }
}
