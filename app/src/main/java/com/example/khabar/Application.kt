package com.example.khabar

import android.app.Application
import com.example.khabar.utils.ThemeManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application:Application(){
    override fun onCreate() {
        super.onCreate()

        // Apply saved theme on app startup
        ThemeManager.getInstance(this).applySavedTheme()
    }
}