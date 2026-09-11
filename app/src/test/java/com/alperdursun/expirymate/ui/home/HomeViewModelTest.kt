package com.alperdursun.expirymate.ui.home

import com.alperdursun.expirymate.data.local.ItemDao
import com.alperdursun.expirymate.data.repository.ItemRepository
import com.alperdursun.expirymate.domain.model.Item
import com.alperdursun.expirymate.domain.model.ItemCategory
import com.alperdursun.expirymate.domain.model.ItemStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

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
    fun testExpiringSoonItemsFiltersToThisWeekOnlyAndMax3() = runTest {
        val today = LocalDate.now()

        val expiredItem = Item(id = 1, name = "Expired Item", expirationDate = today.minusDays(2), category = ItemCategory.FOOD, reminderDaysBefore = 1)
        val todayItem = Item(id = 2, name = "Today Item", expirationDate = today, category = ItemCategory.FOOD, reminderDaysBefore = 1)
        val in3DaysItem = Item(id = 3, name = "In 3 Days", expirationDate = today.plusDays(3), category = ItemCategory.FOOD, reminderDaysBefore = 1)
        val in5DaysItem = Item(id = 4, name = "In 5 Days", expirationDate = today.plusDays(5), category = ItemCategory.FOOD, reminderDaysBefore = 1)
        val in7DaysItem = Item(id = 5, name = "In 7 Days", expirationDate = today.plusDays(7), category = ItemCategory.FOOD, reminderDaysBefore = 1)
        val in10DaysItem = Item(id = 6, name = "In 10 Days", expirationDate = today.plusDays(10), category = ItemCategory.FOOD, reminderDaysBefore = 1)

        val items = listOf(expiredItem, todayItem, in3DaysItem, in5DaysItem, in7DaysItem, in10DaysItem)

        val fakeDao = object : ItemDao {
            override fun observeActiveItems(): Flow<List<Item>> = flowOf(items)
            override fun observeHistoryItems(): Flow<List<Item>> = flowOf(emptyList())
            override suspend fun getItemById(id: Long): Item? = null
            override suspend fun insertItem(item: Item): Long = 0L
            override suspend fun updateItem(item: Item) {}
            override suspend fun updateItemStatus(id: Long, status: ItemStatus, completedAtTimestamp: Long?) {}
            override suspend fun deleteItem(id: Long) {}
        }

        val repository = ItemRepository(fakeDao)
        val viewModel = HomeViewModel(repository)

        var latestState: HomeUiState? = null

        backgroundScope.launch {
            viewModel.uiState.collect { state ->
                latestState = state
            }
        }

        testScheduler.advanceUntilIdle()

        val state = latestState
        assertNotNull(state)
        assertEquals(false, state?.isLoading)

        assertEquals(1, state?.expiredCount)
        assertEquals(4, state?.thisWeekCount)
        assertEquals(1, state?.laterCount)

        assertNotNull(state?.nextToExpire)
        assertEquals(expiredItem.id, state?.nextToExpire?.id)

        // Expiring soon should only contain active items expiring from today through next 7 days inclusive, max 3
        val expiringSoon = state?.expiringSoonItems ?: emptyList()
        assertEquals(3, expiringSoon.size)
        assertEquals(todayItem.id, expiringSoon[0].id)
        assertEquals(in3DaysItem.id, expiringSoon[1].id)
        assertEquals(in5DaysItem.id, expiringSoon[2].id)
    }
}
