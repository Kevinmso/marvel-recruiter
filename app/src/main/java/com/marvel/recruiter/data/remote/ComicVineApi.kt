package com.marvel.recruiter.data.remote

import com.marvel.recruiter.data.remote.dto.CharacterDto
import com.marvel.recruiter.data.remote.dto.CharacterIssueCreditsDto
import com.marvel.recruiter.data.remote.dto.ComicVineResponse
import com.marvel.recruiter.data.remote.dto.StoryArcDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/** Só usado na rotina de seed (constitution.md C-02). */
class ComicVineApi(private val client: HttpClient) {

    suspend fun character(cvId: Long): CharacterDto =
        client.get("character/4005-$cvId/")
            .body<ComicVineResponse<CharacterDto>>()
            .results

    /** Ids das issues em que o personagem aparece (RF-19, rota inversa). */
    suspend fun characterIssueIds(cvId: Long): Set<Long> =
        client.get("character/4005-$cvId/") {
            parameter("field_list", "id,issue_credits")
        }
            .body<ComicVineResponse<CharacterIssueCreditsDto>>()
            .results
            .issueCredits
            .mapTo(HashSet()) { it.id }

    suspend fun storyArc(cvId: Long): StoryArcDto =
        client.get("story_arc/$cvId/")
            .body<ComicVineResponse<StoryArcDto>>()
            .results
}
