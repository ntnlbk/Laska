package laska.daily.bible.meditation.data.retrofit.courses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpisodeBlockDTO(
    @SerialName("block_id")
    val blockId: String,
    @SerialName("block_type")
    val blockType: String,
    @SerialName("sort_order")
    val sortOrder: Int,
    val data: EpisodeBlockDataDTO
)
