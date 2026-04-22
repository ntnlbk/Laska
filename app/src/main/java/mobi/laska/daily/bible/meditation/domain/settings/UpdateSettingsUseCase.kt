package mobi.laska.daily.bible.meditation.domain.settings

import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(private val repository: SettingsRepository) {
    operator fun invoke(newSetting: Settings) = repository.updateSettings(newSetting)
}