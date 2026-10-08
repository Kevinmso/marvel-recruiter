package com.marvel.recruiter.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterDto(
    val id: Long,
    val name: String,
    @SerialName("real_name") val realName: String? = null,
    val aliases: String? = null,
    val deck: String? = null,
    @SerialName("count_of_issue_appearances") val issueAppearances: Int = 0,
    val image: ImageDto? = null,
    val powers: List<NamedRef> = emptyList(),
    val teams: List<NamedRef> = emptyList(),
    @SerialName("character_friends") val friends: List<NamedRef> = emptyList(),
)

@Serializable
data class NamedRef(val id: Long, val name: String? = null)

@Serializable
data class ImageDto(
    @SerialName("medium_url") val mediumUrl: String? = null,
    @SerialName("screen_url") val screenUrl: String? = null,
    @SerialName("original_url") val originalUrl: String? = null,
)
