package com.marvel.recruiter.data.remote

import com.marvel.recruiter.data.remote.dto.ComicVineResponse
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.junit.Assert.assertEquals
import org.junit.Test

class ComicVineHttpClientTest {

    @Serializable
    private data class Probe(val id: Long, val name: String)

    private fun mockEngine(assertRequest: (io.ktor.client.request.HttpRequestData) -> Unit) =
        MockEngine { request ->
            assertRequest(request)
            respond(
                content = """{"error":"OK","status_code":1,"results":{"id":1440,"name":"Wolverine"}}""",
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

    @Test
    fun `anexa api_key e format em toda requisicao`() = runTest {
        val engine = mockEngine { request ->
            assertEquals("chave-fake", request.url.parameters["api_key"])
            assertEquals("json", request.url.parameters["format"])
        }
        comicVineHttpClient(apiKey = "chave-fake", engine = engine)
            .get("character/4005-1440/")
    }

    @Test
    fun `manda User-Agent proprio`() = runTest {
        val engine = mockEngine { request ->
            assertEquals("MarvelRecruiter/0.1 (trabalho de curso)", request.headers[HttpHeaders.UserAgent])
        }
        comicVineHttpClient(apiKey = "x", engine = engine).get("character/4005-1440/")
    }

    @Test
    fun `desserializa o envelope e ignora campos desconhecidos`() = runTest {
        val engine = MockEngine {
            respond(
                content = """
                    {"error":"OK","status_code":1,"limit":1,"offset":0,
                     "results":{"id":1440,"name":"Wolverine","aliases":"Logan"}}
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val response: ComicVineResponse<Probe> =
            comicVineHttpClient(apiKey = "x", engine = engine).get("character/4005-1440/").body()

        assertEquals("OK", response.error)
        assertEquals(1, response.statusCode)
        assertEquals(Probe(id = 1440, name = "Wolverine"), response.results)
    }
}
