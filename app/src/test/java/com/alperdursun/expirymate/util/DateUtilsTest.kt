package com.alperdursun.expirymate.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.util.Locale

class DateUtilsTest {

    private val today = LocalDate.of(2026, 1, 15)

    @Test
    fun testSupportedLocaleFallback() {
        val turkishLocale = Locale.forLanguageTag("tr-TR")
        val frenchLocale = Locale.FRENCH

        assertEquals(Locale.forLanguageTag("tr-TR"), DateUtils.getSupportedLocale(turkishLocale))
        assertEquals(Locale.US, DateUtils.getSupportedLocale(Locale.ENGLISH))
        assertEquals(Locale.US, DateUtils.getSupportedLocale(frenchLocale))
    }

    @Test
    fun testFormatDateLocaleAware() {
        val testDate = LocalDate.of(2026, 3, 20)

        val turkishFormatted = DateUtils.formatDate(testDate, Locale.forLanguageTag("tr-TR"))
        val englishFormatted = DateUtils.formatDate(testDate, Locale.US)
        val frenchFormatted = DateUtils.formatDate(testDate, Locale.FRENCH)

        assertEquals("20 Mar 2026", turkishFormatted)
        assertEquals("Mar 20, 2026", englishFormatted)
        assertEquals("Mar 20, 2026", frenchFormatted) // Falls back to English
    }

    @Test
    fun testExpiredDate() {
        val expiredDate = today.minusDays(1)
        assertTrue(DateUtils.isExpired(expiredDate, today))
        assertFalse(DateUtils.isThisWeek(expiredDate, today))
        assertFalse(DateUtils.isLater(expiredDate, today))
        assertEquals("Expired yesterday", DateUtils.getRelativeExpiryText(expiredDate, today))
    }

    @Test
    fun testExpiringToday() {
        val todayDate = today
        assertFalse(DateUtils.isExpired(todayDate, today))
        assertTrue(DateUtils.isThisWeek(todayDate, today))
        assertFalse(DateUtils.isLater(todayDate, today))
        assertEquals("Expires today", DateUtils.getRelativeExpiryText(todayDate, today))
    }

    @Test
    fun testExpiringWithinNext7Days() {
        val in3Days = today.plusDays(3)
        val in7Days = today.plusDays(7)

        assertFalse(DateUtils.isExpired(in3Days, today))
        assertTrue(DateUtils.isThisWeek(in3Days, today))
        assertFalse(DateUtils.isLater(in3Days, today))
        assertEquals("Expires in 3 days", DateUtils.getRelativeExpiryText(in3Days, today))

        assertTrue(DateUtils.isThisWeek(in7Days, today))
        assertFalse(DateUtils.isLater(in7Days, today))
    }

    @Test
    fun testExpiringLaterThan7Days() {
        val in8Days = today.plusDays(8)

        assertFalse(DateUtils.isExpired(in8Days, today))
        assertFalse(DateUtils.isThisWeek(in8Days, today))
        assertTrue(DateUtils.isLater(in8Days, today))
        assertEquals("Expires in 8 days", DateUtils.getRelativeExpiryText(in8Days, today))
    }
}
