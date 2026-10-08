package com.marvel.recruiter.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class StoryArcDto(
    val id: Long,
    val name: String,
    val deck: String? = null,
    val issues: List<NamedRef> = emptyList(),
    val image: ImageDto? = null,
    val description: String? = null,
)
