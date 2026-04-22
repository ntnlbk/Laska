package mobi.laska.daily.bible.meditation.domain.settings

interface SettingsRepository {
    fun getSettings(): Settings
    fun updateSettings(newSettings: Settings)
}