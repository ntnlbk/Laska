package mobi.laska.daily.bible.meditation.domain.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<Settings>
    suspend fun updateSettings(newSettings: Settings)
}