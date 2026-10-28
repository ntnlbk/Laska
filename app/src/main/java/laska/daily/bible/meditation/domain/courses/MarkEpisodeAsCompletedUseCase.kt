package laska.daily.bible.meditation.domain.courses

import javax.inject.Inject

class MarkEpisodeAsCompletedUseCase @Inject constructor(
    private val repository: CoursesRepository
) {
    suspend operator fun invoke(episodeId: Int) = repository.markEpisodeAsCompleted(episodeId)
}