package com.flynid.laska.domain.settings

class GetSettingsUseCase (private val repository: SettingsRepository){
    operator fun invoke() = repository.getSettings()
}