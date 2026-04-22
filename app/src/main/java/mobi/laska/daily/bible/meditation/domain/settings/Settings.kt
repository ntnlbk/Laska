package mobi.laska.daily.bible.meditation.domain.settings

import mobi.laska.daily.bible.meditation.domain.Language

data class Settings(
    val language: Language = Language.BY,
    val fontSize: FontSize = FontSize.NORMAL
)
