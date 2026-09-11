package com.alperdursun.expirymate.ui.additem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.alperdursun.expirymate.data.repository.ItemRepository
import com.alperdursun.expirymate.domain.model.Item
import com.alperdursun.expirymate.domain.model.ItemCategory
import com.alperdursun.expirymate.domain.model.ItemStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AddItemFormState(
    val productName: String = "",
    val expirationDate: LocalDate? = null,
    val category: ItemCategory = ItemCategory.FOOD,
    val reminderDaysBefore: Int = 1,
    val notes: String = "",
    val nameError: String? = null,
    val dateError: String? = null,
    val isSaving: Boolean = false,
)

class AddItemViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(AddItemFormState())
    val formState: StateFlow<AddItemFormState> = _formState.asStateFlow()

    private val _saveSuccessEvent = Channel<Unit>(Channel.BUFFERED)
    val saveSuccessEvent: Flow<Unit> = _saveSuccessEvent.receiveAsFlow()

    fun onNameChanged(name: String) {
        _formState.update {
            it.copy(
                productName = name,
                nameError = if (name.isBlank()) "Product name cannot be empty" else null
            )
        }
    }

    fun onDateSelected(date: LocalDate) {
        _formState.update {
            it.copy(
                expirationDate = date,
                dateError = null
            )
        }
    }

    fun onCategorySelected(category: ItemCategory) {
        _formState.update {
            it.copy(category = category)
        }
    }

    fun onReminderDaysSelected(days: Int) {
        _formState.update {
            it.copy(reminderDaysBefore = days)
        }
    }

    fun onNotesChanged(notes: String) {
        _formState.update {
            it.copy(notes = notes)
        }
    }

    fun saveItem() {
        val currentState = _formState.value
        val trimmedName = currentState.productName.trim()
        val date = currentState.expirationDate

        var hasError = false

        if (trimmedName.isEmpty()) {
            _formState.update { it.copy(nameError = "Please enter a product name") }
            hasError = true
        }

        if (date == null) {
            _formState.update { it.copy(dateError = "Please select an expiration date") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true) }
            try {
                val newItem = Item(
                    name = trimmedName,
                    expirationDate = date!!,
                    category = currentState.category,
                    reminderDaysBefore = currentState.reminderDaysBefore,
                    notes = currentState.notes.trim().ifEmpty { null },
                    status = ItemStatus.ACTIVE
                )
                itemRepository.addItem(newItem)
                _saveSuccessEvent.send(Unit)
            } catch (_: Exception) {
                _formState.update { it.copy(nameError = "Failed to save item. Please try again.") }
            } finally {
                _formState.update { it.copy(isSaving = false) }
            }
        }
    }

    companion object {
        fun Factory(repository: ItemRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AddItemViewModel(repository)
            }
        }
    }
}
