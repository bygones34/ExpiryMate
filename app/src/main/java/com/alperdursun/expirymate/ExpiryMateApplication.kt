package com.alperdursun.expirymate

import android.app.Application
import com.alperdursun.expirymate.data.local.AppDatabase
import com.alperdursun.expirymate.data.repository.ItemRepository

class ExpiryMateApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

class AppContainer(context: Application) {
    private val database = AppDatabase.getInstance(context)
    val itemRepository = ItemRepository(database.itemDao())
}
