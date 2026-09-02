package com.marvel.recruiter.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComicVineResponse<T>(
    val error: String,
    @SerialName("status_code") val statusCode: Int,
    val results: T,
)
