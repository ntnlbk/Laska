package laska.daily.bible.meditation.data.retrofit.courses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpisodeDTO(
    @SerialName("schema_version")
    val schemaVersion: Int,
    val id: Int,
    val slug: String,
    @SerialName("course_id")
    val courseId: Int,
    @SerialName("course_slug")
    val courseSlag: String,
    val number: Int,
    val title: String,
    val subtitle: String = "",
    @SerialName("scripture_reference")
    val scriptureReference: String = "",
    val cover: ImageDTO?,
    val authors: List<AuthorShortDTO>,
    val audio: AudioDTO?,
    val text: String?,
    val blocks: List<EpisodeBlockDTO>,
    val availability: EpisodeAvailabilityDTO,
    @SerialName("updated_at")
    val updateAt: String
) {
}