package laska.daily.bible.meditation.data.retrofit.courses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseDTO(
    @SerialName("schema_version")
    val schemaVersion: Int,
    val id: Int,
    val title: String,
    val slug: String,
    val language: String,
    @SerialName("home_placement")
    val homePlacement: String,
    @SerialName("short_description")
    val shortDescription: String,
    val description: String,
    val cover: ImageDTO,
    val authors: List<AuthorDTO>,
    @SerialName("episodes_count")
    val episodesCount: Int,
    @SerialName("episodes_available_count")
    val episodesAvailableCount: Int,
    @SerialName("updated_at")
    val updatedAt: String
)
