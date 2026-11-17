package laska.daily.bible.meditation.data.donations.courses

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import laska.daily.bible.meditation.data.firebase.AnalyticsRepositoryImpl
import laska.daily.bible.meditation.domain.donations.courses.CoursesDonationRepository
import javax.inject.Inject

class CoursesDonationRepositoryImpl @Inject constructor(
    private val analyticsRepositoryImpl: AnalyticsRepositoryImpl,
    private val coursesDonationPrefs: CoursesDonationPrefs
) : CoursesDonationRepository {

    override suspend fun shouldShowPopUp(): Boolean {

        val userHasNotFromBelarusField = analyticsRepositoryImpl.hasNotFromBelarusField()
        val userHasConfirmedDonationWithin30Days = analyticsRepositoryImpl.hasConfirmedDonationWithin30Days()
        val userHasCompleted20 = coursesDonationPrefs.is20Completed
        val userHasCompleted75 = coursesDonationPrefs.is75Completed

        Log.d("COURSES", "userHasNotFromBelarusField: $userHasNotFromBelarusField")
        Log.d("COURSES", "userHasConfirmedDonationWithin30Days: $userHasConfirmedDonationWithin30Days")
        Log.d("COURSES", "userHasCompleted20: $userHasCompleted20")
        Log.d("COURSES", "userHasCompleted75: $userHasCompleted75")

        val result = !userHasNotFromBelarusField && !userHasConfirmedDonationWithin30Days &&
                (userHasCompleted20 || userHasCompleted75)

        if (result) {
            coursesDonationPrefs.is20Completed = false
            coursesDonationPrefs.is75Completed = false
        }

        Log.d("COURSES", "result: $result")

        return result
    }
}