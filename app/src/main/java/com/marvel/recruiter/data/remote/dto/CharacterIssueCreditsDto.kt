package com.marvel.recruiter.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterIssueCreditsDto(
    val id: Long,
    @SerialName("issue_credits") val issueCredits: List<NamedRef> = emptyList(),
)
