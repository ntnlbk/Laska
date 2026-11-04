package laska.daily.bible.meditation.domain.analytics

interface AnalyticsRepository {

    fun incrementCounter(counter: CounterType)

    suspend fun startSession()

    fun incrementCourseCounterWithCourseTitle(counter: CourseCounterType, title: String)

}