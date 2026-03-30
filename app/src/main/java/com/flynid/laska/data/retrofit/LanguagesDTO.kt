package com.flynid.laska.data.retrofit

import kotlinx.serialization.Serializable

@Serializable
data class LanguagesDTO(
    val slug: String,
    val name: String
)
