package laska.daily.bible.meditation.domain.donations.courses

import javax.inject.Inject

class ShouldShowCoursesDonationPopUpUseCase @Inject constructor(private val repository: CoursesDonationRepository) {
    suspend operator fun invoke() = repository.shouldShowPopUp()
}