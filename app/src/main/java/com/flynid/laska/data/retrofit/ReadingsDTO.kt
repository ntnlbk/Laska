package com.flynid.laska.data.retrofit

import kotlinx.serialization.Serializable

@Serializable
data class ReadingsDTO(
    val success: Boolean,
    val count: Int,
    val data: List<ReadingDTO>
)
