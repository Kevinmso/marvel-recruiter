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

class ComicVineApiTest {

    private fun apiReturning(resourceName: String): ComicVineApi {
        val json = requireNotNull(javaClass.getResourceAsStream("/comicvine/$resourceName"))
            .bufferedReader().use { it.readText() }
        val engine = MockEngine {
            respond(json, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        return ComicVineApi(comicVineHttpClient(apiKey = "x", engine = engine))
    }

    @Test
    fun `desserializa os campos escalares do personagem`() = runTest {
        val dto = apiReturning("character_wolverine.json").character(1440)

        assertEquals(1440L, dto.id)
        assertEquals("Wolverine", dto.name)
        assertEquals("James Howlett", dto.realName)
        assertEquals(16929, dto.issueAppearances)
        assertTrue(dto.aliases!!.contains("Weapon X"))
    }

    @Test
    fun `desserializa os arrays de poderes, times e amigos`() = runTest {
        val dto = apiReturning("character_wolverine.json").character(1440)

        assertEquals(3, dto.powers.size)
        assertEquals(listOf(3173L, 50418L), dto.teams.map { it.id })
        assertEquals(listOf(1460L, 21561L), dto.friends.map { it.id })
    }

    @Test
    fun `mapeia a imagem`() = runTest {
        val dto = apiReturning("character_wolverine.json").character(1440)

        assertTrue(dto.image!!.mediumUrl!!.endsWith("wolverine.jpg"))
        assertTrue(dto.image!!.screenUrl!!.contains("screen_kubrick"))
    }

    @Test
    fun `campo ausente cai no default`() = runTest {
        val engine = MockEngine {
            respond(
                """{"error":"OK","status_code":1,"results":{"id":99,"name":"Sem Dados"}}""",
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val dto = ComicVineApi(comicVineHttpClient("x", engine)).character(99)

        assertNull(dto.realName)
        assertEquals(0, dto.issueAppearances)
        assertTrue(dto.powers.isEmpty())
        assertNull(dto.image)
    }
}
