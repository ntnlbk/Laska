package laska.daily.bible.meditation.presentation.uils

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.core.content.edit

class ReviewHelper @Inject constructor(
    @param:ApplicationContext private val context:
    Context
) {

    private val prefs =
        context.getSharedPreferences("review_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val APP_LAUNCH_COUNT = "app_launch_count"
        private const val REVIEW_SHOWN = "review_shown"
    }

    fun onAppLaunched(activity: Activity) {
        val reviewShown = prefs.getBoolean(REVIEW_SHOWN, false)

        if (reviewShown) return

        val launchCount = prefs.getInt(APP_LAUNCH_COUNT, 0) + 1

        prefs.edit {
            putInt(APP_LAUNCH_COUNT, launchCount)
        }

        if (launchCount >= 6) {
            showReviewDialog(activity)
        }
    }

    private fun showReviewDialog(activity: Activity) {
        val manager = ReviewManagerFactory.create(context)
        val request = manager.requestReviewFlow()

        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                prefs.edit {
                    putBoolean(REVIEW_SHOWN, true)
                }

                val reviewInfo = task.result

                manager.launchReviewFlow(activity, reviewInfo)
            }
        }
    }
}