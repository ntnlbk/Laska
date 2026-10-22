package laska.daily.bible.meditation.data.retrofit.courses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationDTO(
    val page: Int,
    @SerialName("per_page")
    val perPage: Int,
    val total: Int,
    @SerialName("total_pages")
    val totalPages: Int
)
