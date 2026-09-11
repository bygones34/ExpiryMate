package com.alperdursun.expirymate.data.local.converters

import androidx.room.TypeConverter
import com.alperdursun.expirymate.domain.model.ItemCategory
import com.alperdursun.expirymate.domain.model.ItemStatus
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class RoomConverters {

    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun toEpochDay(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
        }
    }

    @TypeConverter
    fun toTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }

    @TypeConverter
    fun fromCategory(value: String?): ItemCategory? {
        return value?.let { enumValueOf<ItemCategory>(it) }
    }

    @TypeConverter
    fun toCategory(category: ItemCategory?): String? {
        return category?.name
    }

    @TypeConverter
    fun fromStatus(value: String?): ItemStatus? {
        return value?.let { enumValueOf<ItemStatus>(it) }
    }

    @TypeConverter
    fun toStatus(status: ItemStatus?): String? {
        return status?.name
    }
}
