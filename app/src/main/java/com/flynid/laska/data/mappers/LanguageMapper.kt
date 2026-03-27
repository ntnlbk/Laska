package com.flynid.laska.data.mappers

import android.app.Application
import com.flynid.laska.R
import com.flynid.laska.domain.Language
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