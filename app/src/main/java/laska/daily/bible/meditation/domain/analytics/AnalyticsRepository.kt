package laska.daily.bible.meditation.domain.analytics

interface AnalyticsRepository {

    suspend fun incrementCounter(counter: CounterType)

    suspend fun startSession()

}