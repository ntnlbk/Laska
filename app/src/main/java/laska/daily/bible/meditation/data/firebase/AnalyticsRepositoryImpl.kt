package laska.daily.bible.meditation.data.firebase

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import laska.daily.bible.meditation.domain.analytics.AnalyticsRepository
import laska.daily.bible.meditation.domain.analytics.CounterType
import laska.daily.bible.meditation.domain.analytics.Platform
import laska.daily.bible.meditation.presentation.supportfragment.PopUpPrefs
import laska.daily.bible.meditation.presentation.supportfragment.SupportPaymentPrefs
import laska.daily.bible.meditation.presentation.supportfragment.SupportPromptPrefs
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class AnalyticsRepositoryImpl @Inject constructor(
    private val analytics: FirebaseAnalytics,
    @param:ApplicationContext private val context: Context,
    private val paymentPrefs: SupportPaymentPrefs,
    private val promptPrefs: SupportPromptPrefs,
    private val popUpPrefs: PopUpPrefs
) : AnalyticsRepository {

    private val prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val db = Firebase.firestore
    private val userID = getUserId()

    override fun incrementCounter(counter: CounterType) {
        when (counter) {
            CounterType.SESSION_COUNT -> TODO()
            CounterType.DAILY_REFLECTION_AUDIO_PLAY -> {
                analytics.logEvent(DAILY_REFLECTION_AUDIO_PLAY, null)
                val updates = hashMapOf<String, Any>(
                    "statistics" to hashMapOf(
                        "daily_reflection_audio_play" to FieldValue.increment(1)
                    )
                )
                updateUser(updates)
            }

            CounterType.DAILY_REFLECTION_AUDIO_COMPLETED -> {
                analytics.logEvent(DAILY_REFLECTION_AUDIO_COMPLETED, null)
                val updates = hashMapOf<String, Any>(
                    "statistics" to hashMapOf(
                        "daily_reflection_audio_completed" to FieldValue.increment(1)
                    )
                )
                updateUser(updates)
            }

            CounterType.DAILY_REFLECTION_TEXT -> {
                analytics.logEvent(DAILY_REFLECTION_TEXT, null)
                val updates = hashMapOf<String, Any>(
                    "statistics" to hashMapOf(
                        "daily_reflection_text" to FieldValue.increment(1)
                    )
                )
                updateUser(updates)
            }

            CounterType.SUPPORT_COUNT -> TODO()
            CounterType.DONATE_MAIN_SCREEN -> analytics.logEvent(DONATE_MAIN_SCREEN, null)
            CounterType.DONATE_MENU -> analytics.logEvent(DONATE_MENU, null)
            CounterType.DONATE_ERIP -> {
                analytics.logEvent(DONATE_ERIP, null)
                val updates = hashMapOf<String, Any>(
                    "donations" to hashMapOf<String, Any>(
                        paymentPrefs.lastDonationId to hashMapOf(
                            "created_at" to FieldValue.serverTimestamp(),
                            "sum" to paymentPrefs.lastDonationAmount
                        )
                    )
                )
                updateUser(updates)

            }

            CounterType.DONATE_BELARUS_NOT -> analytics.logEvent(DONATE_BELARUS_NOT, null)
            CounterType.DONATE_CLOSE -> analytics.logEvent(DONATE_CLOSE, null)
            CounterType.DONATE_CONFIRMED -> {
                val updates = hashMapOf<String, Any>(
                    "donations" to hashMapOf<String, Any>(
                        paymentPrefs.lastDonationId to hashMapOf(
                            "confirmed" to true
                        )
                    )
                )
                updateUser(updates)
                analytics.logEvent(DONATE_CONFIRMED, null)
            }

            CounterType.DONATE_UNCONFIRMED -> {
                val updates = hashMapOf<String, Any>(
                    "donations" to hashMapOf<String, Any>(
                        paymentPrefs.lastDonationId to hashMapOf(
                            "confirmed" to false
                        )
                    )
                )
                updateUser(updates)
            }

            CounterType.FROM_RUSSIA -> logUserCountry("RUSSIA")
            CounterType.FROM_EU -> logUserCountry("EU")
            CounterType.FROM_OTHER -> logUserCountry("OTHER")
            CounterType.POP_UP_SHOWN -> {
                analytics.logEvent(POP_UP_SHOWN, null)
                val updates = hashMapOf<String, Any>(
                    "pop_ups" to hashMapOf<String, Any>(
                        popUpPrefs.lastPopupId to hashMapOf(
                            "created_at" to FieldValue.serverTimestamp()
                        )
                    )
                )
                updateUser(updates)
            }
            CounterType.POP_UP_LATER_CLICKED -> {
                analytics.logEvent(POP_UP_LATER_CLICKED, null)
                logPopUpOutcome("CLICKED_LATER")
            }
            CounterType.POP_UP_DISMISSED -> logPopUpOutcome("DISMISS")
            CounterType.POP_UP_ERIP_CLICKED -> logPopUpOutcome("ERIP_CLICKED")
        }
    }

    private fun logPopUpOutcome(outcome: String) {
        val updates = hashMapOf<String, Any>(
            "pop_ups" to hashMapOf<String, Any>(
                popUpPrefs.lastPopupId to hashMapOf(
                    "outcome" to outcome
                )
            )
        )
        updateUser(updates)
    }

    private fun logUserCountry(country: String) {
        val updates = hashMapOf<String, Any>(
            "not_from_belarus" to hashMapOf<String, Any>(
                UUID.randomUUID().toString() to hashMapOf(
                    "created_at" to FieldValue.serverTimestamp(),
                    "choice" to country
                )
            )
        )
        updateUser(updates)
    }

    override suspend fun startSession() {
        checkAndCreateUser(userID)
    }

    private fun updateUser(updates: HashMap<String, Any>) {
        db.collection("users_dev").document(userID).set(updates, SetOptions.merge())
    }

    private suspend fun checkAndCreateUser(id: String) {
        val userRef = db.collection("users_dev").document(id)
        val documentSnapshot = try {
            userRef.get().await()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            null
        }
        if (documentSnapshot == null || !documentSnapshot.exists() || !documentSnapshot.contains("created_at")) {
            if (promptPrefs.sessionNumber == 0) {
                promptPrefs.sessionNumber = 1
            }
            userRef.set(
                hashMapOf(
                    "session_count" to 1,
                    "platform" to Platform.ANDROID,
                    "created_at" to FieldValue.serverTimestamp(),
                    "first_session" to FieldValue.serverTimestamp(),
                    "last_session" to FieldValue.serverTimestamp(),
                    "statistics" to hashMapOf(
                        "daily_reflection_audio_play" to 0,
                        "daily_reflection_audio_completed" to 0,
                        "daily_reflection_text" to 0,
                        "support_count" to 0,
                    )
                ), SetOptions.merge()
            )
        } else {
            if (promptPrefs.sessionNumber == 0) {
                promptPrefs.sessionNumber = documentSnapshot.getLong("session_count")?.toInt() ?: 1
            } else{
                promptPrefs.sessionNumber += 1
            }

            val updates = hashMapOf<String, Any>(
                "last_session" to FieldValue.serverTimestamp(),
                "session_count" to FieldValue.increment(1)
            )
            updateUser(updates)
        }
    }


    private fun getUserId(): String {
        var id = prefs.getString(
            USER_ID_PREFERENCE_NAME, null
        )

        if (id == null) {

            id = UUID.randomUUID().toString()

            prefs.edit {
                putString(
                    USER_ID_PREFERENCE_NAME, id
                )
            }
        }
        return id
    }

    companion object {
        private const val DAILY_REFLECTION_AUDIO_PLAY = "daily_reflection_audio_play"
        private const val DAILY_REFLECTION_AUDIO_COMPLETED = "daily_reflection_audio_completed"
        private const val DAILY_REFLECTION_TEXT = "daily_reflection_text"
        private const val SHARED_PREFERENCES_NAME = "app_preferences"
        private const val USER_ID_PREFERENCE_NAME = "user_id"

        private const val DONATE_MAIN_SCREEN = "donate_main_screen"

        private const val DONATE_MENU = "donate_menu"

        private const val DONATE_ERIP = "donate_erip"

        private const val DONATE_BELARUS_NOT = "donate_belarus_not"

        private const val DONATE_CLOSE = "donate_close"

        private const val DONATE_CONFIRMED = "donate_confirmed"
        private const val POP_UP_SHOWN = "donate_request_first"
        private const val POP_UP_LATER_CLICKED = "donate_request_later"
    }
}