package laska.daily.bible.meditation.domain.analytics

import javax.inject.Inject

class StartSessionUseCase @Inject constructor(private val repository: AnalyticsRepository) {

    suspend operator fun invoke() = repository.startSession()

}