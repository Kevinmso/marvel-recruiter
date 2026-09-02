package com.marvel.recruiter.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val USER_AGENT = "MarvelRecruiter/0.1 (trabalho de curso)"

/**
 * Sem User-Agent próprio a Comic Vine responde 403. `engine` é injetável para
 * testar com MockEngine.
 */
fun comicVineHttpClient(
    apiKey: String,
    engine: HttpClientEngine = CIO.create(),
): HttpClient = HttpClient(engine) {
    expectSuccess = true

    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            },
        )
    }

    install(Logging) {
        level = LogLevel.INFO
    }

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = "comicvine.gamespot.com"
            path("api/")
            parameters.append("api_key", apiKey)
            parameters.append("format", "json")
        }
        header(HttpHeaders.UserAgent, USER_AGENT)
    }
}
