package mobi.laska.daily.bible.meditation.data.mappers

import android.app.Application
import mobi.laska.daily.bible.meditation.R
import mobi.laska.daily.bible.meditation.domain.Language
import javax.inject.Inject

class LanguageMapper @Inject constructor(
    private val context: Application
) {
    fun mapLanguageToString(language: Language): String {
        return when (language) {
            Language.RU -> context.getString(R.string.russian_language_name)
            Language.BY -> context.getString(R.string.belarusian_language_name)
        }
    }

    fun mapStringToLanguage(string: String): Language {
        return when (string) {
            context.getString(R.string.russian_language_name) -> Language.RU
            context.getString(R.string.belarusian_language_name) -> Language.BY
            else -> throw Exception("Incorrect language string")
        }
    }
}