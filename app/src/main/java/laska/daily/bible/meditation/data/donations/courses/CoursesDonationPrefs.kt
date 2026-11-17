package laska.daily.bible.meditation.data.donations.courses

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CoursesDonationPrefs @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("courses_donation_prefs", Context.MODE_PRIVATE)

    var is20Completed: Boolean
        get() = prefs.getBoolean(KEY_20, false)
        set(value) = prefs.edit { putBoolean(KEY_20, value) }

    var is75Completed: Boolean
        get() = prefs.getBoolean(KEY_75, false)
        set(value) = prefs.edit { putBoolean(KEY_75, value) }

    fun markCourseAsCompletedFor20(courseId: Int) {
        prefs.edit {
            putBoolean("${courseId}_$KEY_20", true)
        }
    }

    fun markCourseAsCompletedFor75(courseId: Int) {
        prefs.edit {
            putBoolean("${courseId}_$KEY_75", true)
        }
    }

    fun checkIfCourseWasMarkedAsCompletedFor20(courseId: Int): Boolean {
        return prefs.getBoolean("${courseId}_$KEY_20", false)
    }

    fun checkIfCourseWasMarkedAsCompletedFor75(courseId: Int): Boolean {
        return prefs.getBoolean("${courseId}_$KEY_75", false)
    }

    companion object {
        private const val KEY_20 = "is_20_completed"
        private const val KEY_75 = "is_75_completed"
    }
}