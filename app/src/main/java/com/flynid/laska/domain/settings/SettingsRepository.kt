package com.flynid.laska.domain.settings

interface SettingsRepository {
    fun getSettings(): Settings
    fun updateSettings(newSettings: Settings)
}