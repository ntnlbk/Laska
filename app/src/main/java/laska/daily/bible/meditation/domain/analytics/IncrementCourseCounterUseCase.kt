package laska.daily.bible.meditation.domain.analytics

import javax.inject.Inject

class IncrementCourseCounterUseCase @Inject constructor(private val repository: AnalyticsRepository) {
    operator fun invoke(type: CourseCounterType, title: String) =
        repository.incrementCourseCounterWithCourseTitle(type, title)
}