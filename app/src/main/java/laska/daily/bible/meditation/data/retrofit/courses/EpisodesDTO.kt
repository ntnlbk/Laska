package laska.daily.bible.meditation.data.retrofit.courses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpisodesDTO(
    @SerialName("schema_version")
    val schemaVersion: Int,
    val items: List<EpisodeShortDTO>,
    val pagination: PaginationDTO
)
