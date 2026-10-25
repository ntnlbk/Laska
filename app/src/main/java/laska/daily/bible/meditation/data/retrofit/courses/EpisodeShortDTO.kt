package laska.daily.bible.meditation.data.retrofit.courses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpisodeShortDTO(
    val id: Int,
    val slug: String,
    @SerialName("course_id")
    val courseId: Int,
    @SerialName("course_slug")
    val courseSlag: String,
    val number: Int,
    val title: String,
    val subtitle: String,
    val duration: Int,
    val availability: EpisodeAvailabilityDTO
) {
}