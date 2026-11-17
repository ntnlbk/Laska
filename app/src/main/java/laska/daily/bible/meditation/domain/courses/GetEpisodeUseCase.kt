package laska.daily.bible.meditation.domain.courses

import laska.daily.bible.meditation.domain.Language
import javax.inject.Inject

class GetEpisodeUseCase @Inject constructor(
    private val repository: CoursesRepository
) {
    suspend operator fun invoke(courseId: Int, episodeId: Int, language: Language) =
        repository.getEpisode(courseId, episodeId, language)
}