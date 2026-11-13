package laska.daily.bible.meditation.data.firebase

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.core.content.edit

class CourseAnalyticsPrefs @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("course_analytics_prefs", Context.MODE_PRIVATE)

    fun setEventSent(eventId: String, isSent: Boolean) {
        prefs.edit { putBoolean(eventId, isSent) }
    }


    fun isEventSent(eventId: String): Boolean {
        return prefs.getBoolean(eventId, false)
    }

}