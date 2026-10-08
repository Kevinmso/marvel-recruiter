package com.marvel.recruiter.data.remote

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StoryArcApiTest {

    private fun apiReturning(resourceName: String): ComicVineApi {
        val json = requireNotNull(javaClass.getResourceAsStream("/comicvine/$resourceName"))
            .bufferedReader().use { it.readText() }
        val engine = MockEngine {
            respond(json, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        return ComicVineApi(comicVineHttpClient(apiKey = "x", engine = engine))
    }

    @Test
    fun `desserializa o arco e a lista de issues`() = runTest {
        val dto = apiReturning("story_arc_civil_war.json").storyArc(40615)

        assertEquals(40615L, dto.id)
        assertEquals("Civil War", dto.name)
        assertTrue(dto.deck!!.contains("Registration Act"))
        assertEquals(4, dto.issues.size)
        assertEquals(106921L, dto.issues.first().id)
    }

    @Test
    fun `arco sem deck nem issues cai no default`() = runTest {
        val engine = MockEngine {
            respond(
                """{"error":"OK","status_code":1,"results":{"id":1,"name":"Vazio"}}""",
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dto = ComicVineApi(comicVineHttpClient("x", engine)).storyArc(1)

        assertNull(dto.deck)
        assertTrue(dto.issues.isEmpty())
    }
}
