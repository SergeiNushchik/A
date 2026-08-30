package com.autojournal

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AutoJournalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // ✅ Инициализация Yandex Maps SDK ДО создания любых Activity
        MapKitFactory.setApiKey("e7a954e5-da1a-4c6c-88e2-bf80ce524f08")
        MapKitFactory.initialize(this)
    }
}