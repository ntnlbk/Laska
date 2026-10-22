package laska.daily.bible.meditation.domain.courses

import javax.inject.Inject

class GetEpisodesByCourseIdUseCase @Inject constructor(private val repository: CoursesRepository) {
    suspend operator fun invoke(courseId: Int) = repository.getEpisodesByCourseId(courseId)
}