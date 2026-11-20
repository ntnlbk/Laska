package laska.daily.bible.meditation.data.donations.popup

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import laska.daily.bible.meditation.data.firebase.AnalyticsRepositoryImpl.Companion.FIELD_DONATIONS
import laska.daily.bible.meditation.data.firebase.AnalyticsRepositoryImpl.Companion.FIELD_DONATION_OFFER
import laska.daily.bible.meditation.data.firebase.AnalyticsRepositoryImpl.Companion.FIRESTORE_BASE_COLLECTION_NAME
import laska.daily.bible.meditation.data.firebase.AnalyticsRepositoryImpl.Companion.NOT_FROM_BELARUS
import laska.daily.bible.meditation.data.firebase.AnalyticsRepositoryImpl.Companion.SHARED_PREFERENCES_NAME
import laska.daily.bible.meditation.data.firebase.AnalyticsRepositoryImpl.Companion.USER_ID_PREFERENCE_NAME
import laska.daily.bible.meditation.domain.donations.popup.PopUpLogicRepository
import laska.daily.bible.meditation.domain.donations.popup.PopUpMode
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class PopUpLogicRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : PopUpLogicRepository {

    private val prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val db = Firebase.firestore
    private val userId: String
        get() = prefs.getString(USER_ID_PREFERENCE_NAME, "").orEmpty()

    override suspend fun getPopUpStatus(): PopUpMode {
        if (userId.isBlank()) return PopUpMode.DO_NOT_SHOW

        return try {
            val document =
                db.collection(FIRESTORE_BASE_COLLECTION_NAME).document(userId).get().await()

            if (!document.exists()) return PopUpMode.DO_NOT_SHOW

            if (document.contains(NOT_FROM_BELARUS)) {
                return PopUpMode.DO_NOT_SHOW
            }

            val nowMillis = System.currentTimeMillis()
            val thirtyDaysMillis = TimeUnit.DAYS.toMillis(30)

            val lastOfferMillis = getLatestDonationOfferMillis(document.get(FIELD_DONATION_OFFER))
            // Если попап уже показывался — в любом случае ждём >= 30 дней с последнего показа
            if (lastOfferMillis != null && (nowMillis - lastOfferMillis) < thirtyDaysMillis) {
                return PopUpMode.DO_NOT_SHOW
            }

            val lastConfirmedDonationMillis =
                getLatestConfirmedDonationMillis(document.get(FIELD_DONATIONS))

            if (lastConfirmedDonationMillis != null) {
                val isDonationOlderThan30Days = (nowMillis - lastConfirmedDonationMillis) >= thirtyDaysMillis
                if (isDonationOlderThan30Days) {
                    PopUpMode.SECOND_TIME
                } else {
                    PopUpMode.DO_NOT_SHOW
                }
            } else {
                // Попап уже показывался >= 30 дней назад (иначе мы бы уже вышли выше с DO_NOT_SHOW)
                if (lastOfferMillis != null) {
                    PopUpMode.FIRST_TIME
                } else {
                    PopUpMode.DO_NOT_SHOW
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            PopUpMode.DO_NOT_SHOW
        }
    }

    private fun getLatestConfirmedDonationMillis(donationsField: Any?): Long? {
        val donationsMap = donationsField as? Map<*, *> ?: return null

        return donationsMap.values.mapNotNull { entry ->
            val data = entry as? Map<*, *> ?: return@mapNotNull null
            val isConfirmed = data["confirmed"] as? Boolean == true
            if (!isConfirmed) return@mapNotNull null

            val timestamp = data["created_at"] as? Timestamp ?: return@mapNotNull null
            timestamp.toDate().time
        }.maxOrNull()
    }

    private fun getLatestDonationOfferMillis(offerField: Any?): Long? {
        val offerMap = offerField as? Map<*, *> ?: return null

        return offerMap.values.mapNotNull { entry ->
            val data = entry as? Map<*, *> ?: return@mapNotNull null
            val timestamp = data["created_at"] as? Timestamp ?: return@mapNotNull null
            timestamp.toDate().time
        }.maxOrNull()
    }
}