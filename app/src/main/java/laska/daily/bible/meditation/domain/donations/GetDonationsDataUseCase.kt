package laska.daily.bible.meditation.domain.donations

import javax.inject.Inject

class GetDonationsDataUseCase @Inject constructor(
    private val repository: DonationsRepository
) {
    suspend operator fun invoke() = repository.getDonationsData()
}