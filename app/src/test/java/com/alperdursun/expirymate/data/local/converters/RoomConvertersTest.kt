package com.alperdursun.expirymate.data.local.converters

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class RoomConvertersTest {

    private val converters = RoomConverters()

    @Test
    fun testLocalDateTimeUtcRoundTrip() {
        val originalDateTime = LocalDateTime.of(2026, 3, 15, 14, 30, 45)
        val timestamp = converters.toTimestamp(originalDateTime)

        val restoredDateTime = converters.fromTimestamp(timestamp)

        assertEquals(originalDateTime, restoredDateTime)
    }

    @Test
    fun testNullLocalDateTimeHandling() {
        assertNull(converters.toTimestamp(null))
        assertNull(converters.fromTimestamp(null))
    }

    @Test
    fun testLocalDateRoundTrip() {
        val originalDate = LocalDate.of(2026, 5, 20)
        val epochDay = converters.toEpochDay(originalDate)

        val restoredDate = converters.fromEpochDay(epochDay)

        assertEquals(originalDate, restoredDate)
    }
}
