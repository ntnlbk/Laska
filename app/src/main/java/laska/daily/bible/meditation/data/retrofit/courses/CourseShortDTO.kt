package laska.daily.bible.meditation.data.retrofit.courses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseShortDTO(
    val id: Int,
    val title: String,
    val slug: String,
    val language: String,
    @SerialName("home_placement")
    val homePlacement: String,
    @SerialName("short_description")
    val shortDescription: String,
    val cover: ImageDTO?,
    val authors: List<AuthorShortDTO>,
    @SerialName("episodes_count")
    val episodesCount: Int,
    @SerialName("episodes_available_count")
    val episodesAvailableCount: Int,
)
