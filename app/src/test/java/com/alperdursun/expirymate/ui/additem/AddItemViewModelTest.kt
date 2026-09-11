package com.alperdursun.expirymate.ui.additem

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.alperdursun.expirymate.data.local.ItemDao
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AddItemViewModelTest {

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
    fun testValidationFailsWhenNameOrDateMissing() {
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

        val repository = ItemRepository(fakeDao)
        val settingsRepository = createSettingsRepository()
        val viewModel = AddItemViewModel(repository, settingsRepository)

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
            override fun observeItemById(id: Long): Flow<Item?> = flowOf(null)
            override suspend fun getItemById(id: Long): Item? = null
            override suspend fun insertItem(item: Item): Long = 42L
            override suspend fun updateItem(item: Item) {}
            override suspend fun updateItemStatus(id: Long, status: ItemStatus, completedAtTimestamp: Long?) {}
            override suspend fun deleteItem(id: Long) {}
        }

        val repository = ItemRepository(fakeDao)
        val settingsRepository = createSettingsRepository()
        val viewModel = AddItemViewModel(repository, settingsRepository)

        var savedItemResult: Item? = null
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.saveSuccessEvent.collect { item ->
                savedItemResult = item
            }
        }

        viewModel.onNameChanged("Fresh Milk")
        viewModel.onDateSelected(LocalDate.now().plusDays(5))

        viewModel.saveItem()
        testScheduler.advanceUntilIdle()

        val state = viewModel.formState.value
        assertNull(state.nameError)
        assertNull(state.dateError)
        assertNotNull(savedItemResult)
        assertEquals(42L, savedItemResult?.id)
        assertEquals("Fresh Milk", savedItemResult?.name)

        collectJob.cancel()
    }
}
