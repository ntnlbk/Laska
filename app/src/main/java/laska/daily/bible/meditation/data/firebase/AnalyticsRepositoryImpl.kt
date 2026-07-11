package laska.daily.bible.meditation.data.firebase

import com.google.firebase.analytics.FirebaseAnalytics
import laska.daily.bible.meditation.domain.analytics.AnalyticsRepository
import laska.daily.bible.meditation.domain.analytics.CounterType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsRepositoryImpl @Inject constructor(
    private var analytics: FirebaseAnalytics
): AnalyticsRepository {

    override suspend fun incrementCounter(counter: CounterType) {
        when (counter){
            CounterType.SESSION_COUNT -> TODO()
            CounterType.DAILY_REFLECTION_AUDIO_PLAY -> {
                analytics.logEvent(DAILY_REFLECTION_AUDIO_PLAY, null)
            }
            CounterType.DAILY_REFLECTION_AUDIO_COMPLETED -> {
                analytics.logEvent(DAILY_REFLECTION_AUDIO_COMPLETED, null)
            }
            CounterType.DAILY_REFLECTION_TEXT -> {
                analytics.logEvent(DAILY_REFLECTION_TEXT, null)
            }
            CounterType.SUPPORT_COUNT -> TODO()
        }
    }

    override suspend fun startSession() {

    }

    companion object{
        private const val DAILY_REFLECTION_AUDIO_PLAY = "daily_reflection_audio_play"
        private const val DAILY_REFLECTION_AUDIO_COMPLETED = "daily_reflection_audio_completed"
        private const val DAILY_REFLECTION_TEXT = "daily_reflection_text"
    }
}