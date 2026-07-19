package laska.daily.bible.meditation.domain.analytics

interface AnalyticsRepository {

    fun incrementCounter(counter: CounterType)

    suspend fun startSession()

}