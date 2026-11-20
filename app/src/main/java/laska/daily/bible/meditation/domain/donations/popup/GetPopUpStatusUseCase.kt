package laska.daily.bible.meditation.domain.donations.popup

import javax.inject.Inject

class GetPopUpStatusUseCase @Inject constructor (private val repository: PopUpLogicRepository) {
    suspend operator fun invoke() = repository.getPopUpStatus()
}