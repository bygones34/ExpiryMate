package com.alperdursun.expirymate.ui.settings

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.alperdursun.expirymate.data.local.ItemDao
import com.alperdursun.expirymate.data.reminder.ReminderScheduler
import com.alperdursun.expirymate.data.repository.ItemRepository
import com.alperdursun.expirymate.data.repository.SettingsRepository
import com.alperdursun.expirymate.domain.model.Item
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

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
    fun testTogglingRemindersAndDefaultTiming() = runTest {
        val fakeDao = object : ItemDao {
            override fun observeActiveItems(): Flow<List<Item>> = flowOf(emptyList())
            override fun observeHistoryItems(): Flow<List<Item>> = flowOf(emptyList())
            override fun observeItemById(id: Long): Flow<Item?> = flowOf(null)
            override suspend fun getItemById(id: Long): Item? = null
            override suspend fun insertItem(item: Item): Long = 1L
            override suspend fun updateItem(item: Item) {}
            override suspend fun updateItemStatus(id: Long, status: ItemStatus, completedAtTimestamp: Long?) {}
            override suspend fun deleteItem(id: Long) {}
        }

        var cancelAllCalled = false
        var rescheduleCalled = false

        val fakeScheduler = object : ReminderScheduler(null) {
            override fun scheduleReminder(item: Item) {}
            override fun cancelReminder(itemId: Long) {}
            override fun cancelAllReminders() { cancelAllCalled = true }
            override fun rescheduleAllActiveReminders(activeItems: List<Item>) { rescheduleCalled = true }
        }

        val repository = ItemRepository(fakeDao)
        val settingsRepository = createSettingsRepository()
        val viewModel = SettingsViewModel(settingsRepository, repository, fakeScheduler)

        var latestState: SettingsUiState? = null
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {
                latestState = it
            }
        }

        assertEquals(true, latestState?.isRemindersEnabled)
        assertEquals(1, latestState?.defaultReminderDays)

        viewModel.onRemindersEnabledToggled(false)
        testScheduler.advanceUntilIdle()

        assertEquals(false, latestState?.isRemindersEnabled)
        assertEquals(true, cancelAllCalled)

        viewModel.onRemindersEnabledToggled(true)
        testScheduler.advanceUntilIdle()

        assertEquals(true, latestState?.isRemindersEnabled)
        assertEquals(true, rescheduleCalled)

        viewModel.onDefaultReminderDaysSelected(3)
        testScheduler.advanceUntilIdle()

        assertEquals(3, latestState?.defaultReminderDays)

        collectJob.cancel()
    }
}
