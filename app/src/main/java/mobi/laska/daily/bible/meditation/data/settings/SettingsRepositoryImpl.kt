package mobi.laska.daily.bible.meditation.data.settings

import mobi.laska.daily.bible.meditation.domain.settings.Settings
import mobi.laska.daily.bible.meditation.domain.settings.SettingsRepository
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl: SettingsRepository {

    override fun getSettings(): Settings {
        TODO("Not yet implemented")
    }

    override fun updateSettings(newSettings: Settings) {
        TODO("Not yet implemented")
    }

}