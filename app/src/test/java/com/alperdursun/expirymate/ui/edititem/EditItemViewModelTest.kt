package com.alperdursun.expirymate.ui.edititem

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.alperdursun.expirymate.data.local.ItemDao
import com.alperdursun.expirymate.data.reminder.ReminderScheduler
import com.alperdursun.expirymate.data.repository.ItemRepository
import com.alperdursun.expirymate.data.repository.SettingsRepository
import com.alperdursun.expirymate.domain.model.Item
import com.alperdursun.expirymate.domain.model.ItemCategory
import com.alperdursun.expirymate.domain.model.ItemStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class EditItemViewModelTest {

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private fun createSettingsRepository(): SettingsRepository {
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("test_settings_${System.nanoTime()}.preferences_pb") }
        )
        return SettingsRepository(testDataStore)
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testPrepopulatesFormAndPreservesIdAndCreatedAtOnSave() = runTest {
        val fixedCreatedAt = LocalDateTime.of(2026, 1, 10, 10, 0)
        val originalItem = Item(
            id = 42L,
            name = "Original Milk",
            expirationDate = LocalDate.of(2026, 3, 20),
            category = ItemCategory.FOOD,
            reminderDaysBefore = 1,
            notes = "Fridge shelf 1",
            status = ItemStatus.ACTIVE,
            createdAt = fixedCreatedAt
        )

        var updatedItemResult: Item? = null
        var scheduledReminderItem: Item? = null

        val fakeDao = object : ItemDao {
            override fun observeActiveItems(): Flow<List<Item>> = flowOf(listOf(originalItem))
            override fun observeHistoryItems(): Flow<List<Item>> = flowOf(emptyList())
            override fun observeItemById(id: Long): Flow<Item?> = flowOf(originalItem)
            override suspend fun getItemById(id: Long): Item? = originalItem
            override suspend fun insertItem(item: Item): Long = 42L
            override suspend fun updateItem(item: Item) {
                updatedItemResult = item
            }
            override suspend fun updateItemStatus(id: Long, status: ItemStatus, completedAtTimestamp: Long?) {}
            override suspend fun deleteItem(id: Long) {}
        }

        val fakeScheduler = object : ReminderScheduler(null) {
            override fun scheduleReminder(item: Item) {
                scheduledReminderItem = item
            }
        }

        val repository = ItemRepository(fakeDao)
        val settingsRepository = createSettingsRepository()
        val viewModel = EditItemViewModel(42L, repository, settingsRepository, fakeScheduler)

        testScheduler.advanceUntilIdle()

        val formState = viewModel.formState.value
        assertEquals("Original Milk", formState.productName)
        assertEquals(LocalDate.of(2026, 3, 20), formState.expirationDate)

        // Edit fields
        viewModel.onNameChanged("Organic Milk 2%")
        viewModel.onDateSelected(LocalDate.of(2026, 3, 25))
        viewModel.onCategorySelected(ItemCategory.FOOD)
        viewModel.onReminderDaysSelected(3)
        viewModel.onNotesChanged("Fridge door")

        var saveEventReceived = false
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.saveSuccessEvent.collect { saveEventReceived = true }
        }

        viewModel.saveItem()
        testScheduler.advanceUntilIdle()

        assertNotNull(updatedItemResult)
        assertEquals(42L, updatedItemResult?.id)
        assertEquals("Organic Milk 2%", updatedItemResult?.name)
        assertEquals(LocalDate.of(2026, 3, 25), updatedItemResult?.expirationDate)
        assertEquals(3, updatedItemResult?.reminderDaysBefore)
        assertEquals("Fridge door", updatedItemResult?.notes)
        assertEquals(fixedCreatedAt, updatedItemResult?.createdAt)
        assertEquals(ItemStatus.ACTIVE, updatedItemResult?.status)

        assertNotNull(scheduledReminderItem)
        assertEquals(42L, scheduledReminderItem?.id)
        assertEquals("Organic Milk 2%", scheduledReminderItem?.name)
        assertEquals(true, saveEventReceived)

        collectJob.cancel()
    }
}
