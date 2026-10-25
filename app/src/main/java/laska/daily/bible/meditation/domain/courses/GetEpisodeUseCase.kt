package laska.daily.bible.meditation.domain.courses

import javax.inject.Inject

class GetEpisodeUseCase @Inject constructor(
    private val repository: CoursesRepository
) {
    suspend operator fun invoke(courseId: Int, episodeId: Int) =
        repository.getEpisode(courseId, episodeId)
}