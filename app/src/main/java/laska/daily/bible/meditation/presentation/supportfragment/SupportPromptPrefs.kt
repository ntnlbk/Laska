package laska.daily.bible.meditation.presentation.supportfragment

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupportPromptPrefs @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("support_prompt_prefs", Context.MODE_PRIVATE)

    var sessionNumber: Int
        get() = prefs.getInt("session_number", 0)
        set(value) = prefs.edit { putInt("session_number", value) }


    var lastDismissedTime: Long
        get() = prefs.getLong(KEY_LAST_DISMISSED_TIME, 0L)
        set(value) = prefs.edit { putLong(KEY_LAST_DISMISSED_TIME, value) }

    var lastDismissedSession: Int
        get() = prefs.getInt(KEY_LAST_DISMISSED_SESSION, -1)
        set(value) = prefs.edit { putInt(KEY_LAST_DISMISSED_SESSION, value) }

    var isInitialPromptShown: Boolean
        get() = prefs.getBoolean(KEY_INITIAL_PROMPT_SHOWN, false)
        set(value) = prefs.edit { putBoolean(KEY_INITIAL_PROMPT_SHOWN, value) }


    /**
     * Call when user clicks "Later"
     */
    fun recordPromptDismissed(currentSession: Int) {
        lastDismissedTime = System.currentTimeMillis()
        lastDismissedSession = currentSession
        isInitialPromptShown = true
    }

    companion object {
        private const val KEY_LAST_DISMISSED_TIME = "key_last_dismissed_time"
        private const val KEY_LAST_DISMISSED_SESSION = "key_last_dismissed_session"
        private const val KEY_INITIAL_PROMPT_SHOWN = "key_initial_prompt_shown"
        private const val KEY_EXISTING_USER_ON_UPDATE = "key_existing_user_on_update"
        
        const val FIVE_DAYS_IN_MS = 5L * 24 * 60 * 60 * 1000 // 432,000,000 ms
    }
}