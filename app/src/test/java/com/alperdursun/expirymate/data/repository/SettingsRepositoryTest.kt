package com.alperdursun.expirymate.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsRepositoryTest {

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private fun createRepository(): SettingsRepository {
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("test_settings.preferences_pb") }
        )
        return SettingsRepository(testDataStore)
    }

    @Test
    fun testDefaultValues() = runTest(testDispatcher) {
        val repository = createRepository()

        assertTrue(repository.isRemindersEnabled.first())
        assertEquals(1, repository.defaultReminderDays.first())
    }

    @Test
    fun testUpdateRemindersEnabled() = runTest(testDispatcher) {
        val repository = createRepository()

        repository.setRemindersEnabled(false)
        assertEquals(false, repository.isRemindersEnabled.first())

        repository.setRemindersEnabled(true)
        assertEquals(true, repository.isRemindersEnabled.first())
    }

    @Test
    fun testUpdateDefaultReminderDays() = runTest(testDispatcher) {
        val repository = createRepository()

        repository.setDefaultReminderDays(3)
        assertEquals(3, repository.defaultReminderDays.first())
    }
}
