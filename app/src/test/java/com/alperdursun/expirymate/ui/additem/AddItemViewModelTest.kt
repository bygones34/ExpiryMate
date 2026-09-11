package com.alperdursun.expirymate.ui.additem

import com.alperdursun.expirymate.data.local.ItemDao
import com.alperdursun.expirymate.data.repository.ItemRepository
import com.alperdursun.expirymate.domain.model.Item
import com.alperdursun.expirymate.domain.model.ItemStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AddItemViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testValidationFailsWhenNameOrDateMissing() {
        val fakeDao = object : ItemDao {
            override fun observeActiveItems(): Flow<List<Item>> = flowOf(emptyList())
            override fun observeHistoryItems(): Flow<List<Item>> = flowOf(emptyList())
            override suspend fun getItemById(id: Long): Item? = null
            override suspend fun insertItem(item: Item): Long = 1L
            override suspend fun updateItem(item: Item) {}
            override suspend fun updateItemStatus(id: Long, status: ItemStatus, completedAtTimestamp: Long?) {}
            override suspend fun deleteItem(id: Long) {}
        }

        val repository = ItemRepository(fakeDao)
        val viewModel = AddItemViewModel(repository)

        viewModel.saveItem()

        val state = viewModel.formState.value
        assertNotNull(state.nameError)
        assertNotNull(state.dateError)
    }

    @Test
    fun testSaveSuccessEmitsEventOnce() = runTest {
        val fakeDao = object : ItemDao {
            override fun observeActiveItems(): Flow<List<Item>> = flowOf(emptyList())
            override fun observeHistoryItems(): Flow<List<Item>> = flowOf(emptyList())
            override suspend fun getItemById(id: Long): Item? = null
            override suspend fun insertItem(item: Item): Long = 1L
            override suspend fun updateItem(item: Item) {}
            override suspend fun updateItemStatus(id: Long, status: ItemStatus, completedAtTimestamp: Long?) {}
            override suspend fun deleteItem(id: Long) {}
        }

        val repository = ItemRepository(fakeDao)
        val viewModel = AddItemViewModel(repository)

        var eventCount = 0
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.saveSuccessEvent.collect {
                eventCount++
            }
        }

        viewModel.onNameChanged("Fresh Milk")
        viewModel.onDateSelected(LocalDate.now().plusDays(5))

        viewModel.saveItem()
        testScheduler.advanceUntilIdle()

        val state = viewModel.formState.value
        assertNull(state.nameError)
        assertNull(state.dateError)
        assertEquals(1, eventCount)

        collectJob.cancel()
    }
}
