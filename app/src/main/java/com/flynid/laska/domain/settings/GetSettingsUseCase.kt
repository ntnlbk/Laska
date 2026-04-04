package com.flynid.laska.domain.settings

import javax.inject.Inject

class GetSettingsUseCase @Inject constructor (private val repository: SettingsRepository){
    operator fun invoke() = repository.getSettings()
}