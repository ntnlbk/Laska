package mobi.laska.daily.bible.meditation.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mobi.laska.daily.bible.meditation.domain.Language
import mobi.laska.daily.bible.meditation.domain.settings.DEFAULT_TEXT_SIZE
import mobi.laska.daily.bible.meditation.domain.settings.Settings
import mobi.laska.daily.bible.meditation.domain.settings.SettingsRepository
import mobi.laska.daily.bible.meditation.domain.settings.TextFragmentTheme
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val application: Context
) : SettingsRepository {

    private val languageKey = stringPreferencesKey("LANGUAGE_KEY")
    private val fontSizeKey = floatPreferencesKey("FONT_SIZE_KEY")
    private val themeKey = stringPreferencesKey("THEME_KEY")


    override fun getSettings(): Flow<Settings> {
        return application.dataStore.data.map { preferences ->
            val language =
                preferences[languageKey]?.let { runCatching { Language.valueOf(it) }.getOrNull() }
                    ?: Language.BY
            val fontSize =
                preferences[fontSizeKey] ?: DEFAULT_TEXT_SIZE
            val theme =
                preferences[themeKey]?.let { runCatching { TextFragmentTheme.valueOf(it) }.getOrNull() }
                    ?: TextFragmentTheme.LIGHT

            Settings(language, fontSize, theme)
        }
    }

    override suspend fun updateSettings(newSettings: Settings) {
        application.dataStore.edit { preferences ->
            preferences[languageKey] = newSettings.language.name
            preferences[fontSizeKey] = newSettings.fontSize
            preferences[themeKey] = newSettings.textFragmentTheme.name
        }
    }

}