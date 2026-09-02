package com.marvel.recruiter.data.remote

import com.marvel.recruiter.data.remote.dto.CharacterDto
import com.marvel.recruiter.data.remote.dto.ComicVineResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/** Só usado na rotina de seed (constitution.md C-02). */
class ComicVineApi(private val client: HttpClient) {

    suspend fun character(cvId: Long): CharacterDto =
        client.get("character/4005-$cvId/")
            .body<ComicVineResponse<CharacterDto>>()
            .results
}
