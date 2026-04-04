package com.flynid.laska.domain.settings

class UpdateSettingsUseCase(private val repository: SettingsRepository) {
    operator fun invoke(newSetting: Settings) = repository.updateSettings(newSetting)
}