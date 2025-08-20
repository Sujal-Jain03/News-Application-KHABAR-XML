package com.example.khabar.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.khabar.utils.ThemeManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val themeManager = ThemeManager.getInstance(application)

    val currentTheme: StateFlow<ThemeManager.ThemeMode> = themeManager.currentTheme

    fun setTheme(themeMode: ThemeManager.ThemeMode) {
        viewModelScope.launch {
            themeManager.setTheme(themeMode)
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            themeManager.toggleTheme()
        }
    }

    fun isDarkTheme(): Boolean = themeManager.isDarkTheme()
}
