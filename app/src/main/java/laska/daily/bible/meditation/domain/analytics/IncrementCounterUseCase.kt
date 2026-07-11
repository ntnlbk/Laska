package laska.daily.bible.meditation.domain.analytics

import javax.inject.Inject

class IncrementCounterUseCase @Inject constructor(private val repository: AnalyticsRepository) {
    suspend operator fun invoke(type: CounterType) = repository.incrementCounter(type)
}