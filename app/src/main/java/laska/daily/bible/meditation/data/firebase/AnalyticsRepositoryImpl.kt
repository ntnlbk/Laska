package laska.daily.bible.meditation.data.firebase

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import laska.daily.bible.meditation.domain.analytics.AnalyticsRepository
import laska.daily.bible.meditation.domain.analytics.CounterType
import laska.daily.bible.meditation.domain.analytics.Platform
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsRepositoryImpl @Inject constructor(
    private val analytics: FirebaseAnalytics,
    @param:ApplicationContext private val context: Context
) : AnalyticsRepository {

    private val prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val db = Firebase.firestore
    private val userID = getUserId()

    override suspend fun incrementCounter(counter: CounterType) {
        when (counter) {
            CounterType.SESSION_COUNT -> TODO()
            CounterType.DAILY_REFLECTION_AUDIO_PLAY -> {
                analytics.logEvent(DAILY_REFLECTION_AUDIO_PLAY, null)
                val updates = hashMapOf<String, Any>(
                    "statistics.daily_reflection_audio_play" to FieldValue.increment(1)

                )
                updateUser(updates)
            }

            CounterType.DAILY_REFLECTION_AUDIO_COMPLETED -> {
                analytics.logEvent(DAILY_REFLECTION_AUDIO_COMPLETED, null)
                val updates = hashMapOf<String, Any>(
                    "statistics.daily_reflection_audio_completed" to FieldValue.increment(1)
                )
                updateUser(updates)
            }

            CounterType.DAILY_REFLECTION_TEXT -> {
                analytics.logEvent(DAILY_REFLECTION_TEXT, null)
                val updates = hashMapOf<String, Any>(
                    "statistics.daily_reflection_text" to FieldValue.increment(1)
                )
                updateUser(updates)
            }

            CounterType.SUPPORT_COUNT -> TODO()
        }
    }

    override suspend fun startSession() {
        checkAndCreateUser(userID)
    }

    private suspend fun updateUser(updates: HashMap<String, Any>){
        try {
            db.collection("users").document(userID).update(updates).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private suspend fun checkAndCreateUser(id: String) {
        val userRef = db.collection("users").document(id)
        val documentSnapshot = userRef.get().await()
        if (!documentSnapshot.exists()) {
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
                )
            ).await()
        } else {
            val updates = hashMapOf<String, Any>(
                "last_session" to FieldValue.serverTimestamp(),
                "session_count" to FieldValue.increment(1)
            )
            updateUser(updates)
        }
    }


    private fun getUserId(): String {
        var id = prefs.getString(
            USER_ID_PREFERENCE_NAME,
            null
        )

        if (id == null) {

            id = UUID.randomUUID().toString()

            prefs.edit {
                putString(
                    USER_ID_PREFERENCE_NAME,
                    id
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
    }
}