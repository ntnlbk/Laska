package laska.daily.bible.meditation.domain.donations

import javax.inject.Inject

class CheckPopupEnabledUseCase @Inject constructor(
    private val repository: DonationsRepository
) {
    suspend operator fun invoke() = repository.checkPopupEnabled()
}