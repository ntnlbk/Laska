package mobi.laska.daily.bible.meditation.domain.settings

import mobi.laska.daily.bible.meditation.domain.Language

const val DEFAULT_TEXT_SIZE = 16f

data class Settings(
    val language: Language = Language.BY,
    val fontSize: Float = DEFAULT_TEXT_SIZE,
    val textFragmentTheme: TextFragmentTheme = TextFragmentTheme.LIGHT
)
