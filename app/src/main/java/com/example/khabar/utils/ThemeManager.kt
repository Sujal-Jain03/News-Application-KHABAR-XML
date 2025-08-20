package com.example.khabar.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeManager private constructor(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "theme_preferences"
        private const val KEY_THEME = "selected_theme"

        @Volatile
        private var INSTANCE: ThemeManager? = null

        fun getInstance(context: Context): ThemeManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThemeManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    enum class ThemeMode(val value: Int) {
        LIGHT(0),
        DARK(1),
        SYSTEM(2);

        companion object {
            fun fromValue(value: Int): ThemeMode {
                return values().find { it.value == value } ?: SYSTEM
            }
        }
    }

    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _currentTheme = MutableStateFlow(getSavedTheme())
    val currentTheme: StateFlow<ThemeMode> = _currentTheme.asStateFlow()

    /**
     * Set and apply the theme
     */
    fun setTheme(themeMode: ThemeMode) {
        preferences.edit().putInt(KEY_THEME, themeMode.value).apply()
        _currentTheme.value = themeMode
        applyTheme(themeMode)
        if (context is Activity) {
            (context as Activity).recreate()
        }
    }

    /**
     * Get the currently saved theme
     */
    fun getSavedTheme(): ThemeMode {
        val savedValue = preferences.getInt(KEY_THEME, ThemeMode.SYSTEM.value)
        return ThemeMode.fromValue(savedValue)
    }

    /**
     * Apply theme to the app
     */
    fun applyTheme(themeMode: ThemeMode) {
        when (themeMode) {
            ThemeMode.LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            ThemeMode.DARK -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            ThemeMode.SYSTEM -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    /**
     * Apply saved theme - call this at app startup
     */
    fun applySavedTheme() {
        applyTheme(getSavedTheme())
    }

    /**
     * Toggle between light and dark theme
     */
    fun toggleTheme() {
        val newTheme = when (_currentTheme.value) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.SYSTEM -> ThemeMode.DARK // Default to dark when toggling from system
        }
        setTheme(newTheme)
    }

    /**
     * Check if current theme is dark
     */
    fun isDarkTheme(): Boolean {
        return when (_currentTheme.value) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
            ThemeMode.SYSTEM -> {
                val nightMode = context.resources.configuration.uiMode and
                        android.content.res.Configuration.UI_MODE_NIGHT_MASK
                nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
        }
    }

    /**
     * Get theme display name for UI
     */
    fun getThemeDisplayName(themeMode: ThemeMode): String {
        return when (themeMode) {
            ThemeMode.LIGHT -> "Light"
            ThemeMode.DARK -> "Dark"
            ThemeMode.SYSTEM -> "System Default"
        }
    }
}
