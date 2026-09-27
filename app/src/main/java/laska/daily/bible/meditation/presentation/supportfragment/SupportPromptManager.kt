package laska.daily.bible.meditation.domain.usecase

import laska.daily.bible.meditation.domain.analytics.AnalyticsRepository
import laska.daily.bible.meditation.presentation.supportfragment.SupportPromptPrefs
import javax.inject.Inject

class SupportPromptManager @Inject constructor(
    private val analyticsRepository: AnalyticsRepository,
    private val promptPrefs: SupportPromptPrefs
) {

    fun shouldShowPrompt(): Boolean {
        val currentSession = promptPrefs.sessionNumber

        // 1. Check if user dismissed prompt previously ("Later" clicked)
        if (promptPrefs.isInitialPromptShown) {
            val sessionsPassed = currentSession - promptPrefs.lastDismissedSession
            val timePassedMs = System.currentTimeMillis() - promptPrefs.lastDismissedTime

            // Must satisfy BOTH conditions: at least 2 sessions AND 5 days passed
            return sessionsPassed >= 2 && timePassedMs >= SupportPromptPrefs.FIVE_DAYS_IN_MS
        }

        // 2. First-time display logic
        return currentSession >= 6

    }

    fun onPromptDismissedLater() {
        val currentSession = promptPrefs.sessionNumber
        promptPrefs.recordPromptDismissed(currentSession)
    }

    fun onPromptCompleted() {
        // If user completes support or permanently closes, mark shown so it won't reappear
        promptPrefs.isInitialPromptShown = true
        promptPrefs.lastDismissedSession = Int.MAX_VALUE
    }
}