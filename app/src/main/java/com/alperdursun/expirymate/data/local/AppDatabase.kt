package com.alperdursun.expirymate.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alperdursun.expirymate.data.local.converters.RoomConverters
import com.alperdursun.expirymate.domain.model.Item

@Database(entities = [Item::class], version = 1, exportSchema = true)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expirymate.db",
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
