package com.alperdursun.expirymate.ui.itemdetail

import com.alperdursun.expirymate.data.local.ItemDao
import com.alperdursun.expirymate.data.reminder.ReminderScheduler
import com.alperdursun.expirymate.data.repository.ItemRepository
import com.alperdursun.expirymate.domain.model.Item
import com.alperdursun.expirymate.domain.model.ItemCategory
import com.alperdursun.expirymate.domain.model.ItemStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class ItemDetailViewModelTest {

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
    fun testItemDetailLoadsSuccessfully() = runTest {
        val sampleItem = Item(
            id = 42L,
            name = "Milk",
            expirationDate = LocalDate.now().plusDays(3),
            category = ItemCategory.FOOD,
            reminderDaysBefore = 1
        )

        val fakeDao = object : ItemDao {
            override fun observeActiveItems(): Flow<List<Item>> = flowOf(listOf(sampleItem))
            override fun observeHistoryItems(): Flow<List<Item>> = flowOf(emptyList())
            override fun observeItemById(id: Long): Flow<Item?> = flowOf(if (id == 42L) sampleItem else null)
            override suspend fun getItemById(id: Long): Item? = if (id == 42L) sampleItem else null
            override suspend fun insertItem(item: Item): Long = 42L
            override suspend fun updateItem(item: Item) {}
            override suspend fun updateItemStatus(id: Long, status: ItemStatus, completedAtTimestamp: Long?) {}
            override suspend fun deleteItem(id: Long) {}
        }

        val repository = ItemRepository(fakeDao)
        val fakeScheduler = object : ReminderScheduler(null) {}

        val viewModel = ItemDetailViewModel(42L, repository, fakeScheduler)

        var latestState: ItemDetailUiState? = null
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { latestState = it }
        }

        testScheduler.advanceUntilIdle()

        assertNotNull(latestState?.item)
        assertEquals("Milk", latestState?.item?.name)
        assertEquals(false, latestState?.isNotFound)

        collectJob.cancel()
    }

    @Test
    fun testItemNotFoundHandlesGracefully() = runTest {
        val fakeDao = object : ItemDao {
            override fun observeActiveItems(): Flow<List<Item>> = flowOf(emptyList())
            override fun observeHistoryItems(): Flow<List<Item>> = flowOf(emptyList())
            override fun observeItemById(id: Long): Flow<Item?> = flowOf(null)
            override suspend fun getItemById(id: Long): Item? = null
            override suspend fun insertItem(item: Item): Long = 0L
            override suspend fun updateItem(item: Item) {}
            override suspend fun updateItemStatus(id: Long, status: ItemStatus, completedAtTimestamp: Long?) {}
            override suspend fun deleteItem(id: Long) {}
        }

        val repository = ItemRepository(fakeDao)
        val fakeScheduler = object : ReminderScheduler(null) {}

        val viewModel = ItemDetailViewModel(99L, repository, fakeScheduler)

        var latestState: ItemDetailUiState? = null
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { latestState = it }
        }

        testScheduler.advanceUntilIdle()

        assertEquals(true, latestState?.isNotFound)

        collectJob.cancel()
    }

    @Test
    fun testDeleteItemCancelsReminderAndDeletes() = runTest {
        var deletedId = -1L
        var cancelledReminderId = -1L

        val sampleItem = Item(
            id = 42L,
            name = "Milk",
            expirationDate = LocalDate.now().plusDays(3),
            category = ItemCategory.FOOD,
            reminderDaysBefore = 1
        )
        val itemState = MutableStateFlow<Item?>(sampleItem)

        val fakeDao = object : ItemDao {
            override fun observeActiveItems(): Flow<List<Item>> = flowOf(emptyList())
            override fun observeHistoryItems(): Flow<List<Item>> = flowOf(emptyList())
            override fun observeItemById(id: Long): Flow<Item?> = itemState
            override suspend fun getItemById(id: Long): Item? = sampleItem
            override suspend fun insertItem(item: Item): Long = 42L
            override suspend fun updateItem(item: Item) {}
            override suspend fun updateItemStatus(id: Long, status: ItemStatus, completedAtTimestamp: Long?) {}
            override suspend fun deleteItem(id: Long) {
                deletedId = id
                itemState.value = null
            }
        }

        val fakeScheduler = object : ReminderScheduler(null) {
            override fun cancelReminder(itemId: Long) {
                cancelledReminderId = itemId
            }
        }

        val repository = ItemRepository(fakeDao)
        val viewModel = ItemDetailViewModel(42L, repository, fakeScheduler)

        var callbackExecuted = false
        viewModel.deleteItem { callbackExecuted = true }
        testScheduler.advanceUntilIdle()

        assertEquals(42L, deletedId)
        assertEquals(42L, cancelledReminderId)
        assertTrue(callbackExecuted)
    }
}
