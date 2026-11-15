package laska.daily.bible.meditation.domain.analytics

import javax.inject.Inject

class IncrementCourseCounterUseCase @Inject constructor(private val repository: AnalyticsRepository) {
    operator fun invoke(type: CourseCounterType, title: String, number: Int) =
        repository.incrementCourseCounterWithCourseTitle(type, title, number)
}