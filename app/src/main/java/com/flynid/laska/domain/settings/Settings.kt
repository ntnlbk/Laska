package com.flynid.laska.domain.settings

import com.flynid.laska.domain.Language

data class Settings(
    val language: Language = Language.BY,
    val fontSize: FontSize = FontSize.NORMAL
)
