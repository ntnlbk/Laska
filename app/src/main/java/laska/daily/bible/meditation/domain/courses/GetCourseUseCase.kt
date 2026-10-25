package laska.daily.bible.meditation.domain.courses

import javax.inject.Inject

class GetCourseUseCase @Inject constructor(
    private val coursesRepository: CoursesRepository
) {
    suspend operator fun invoke(courseId: Int): Course {
        return coursesRepository.getCourse(courseId)
    }

}