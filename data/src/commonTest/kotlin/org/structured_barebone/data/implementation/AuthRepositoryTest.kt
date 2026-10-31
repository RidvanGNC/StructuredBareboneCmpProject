package org.structured_barebone.data.implementation

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.structured_barebone.data.client.AppConfig
import org.structured_barebone.data.client.HttpClientOptions
import org.structured_barebone.data.client.InMemoryTokenProvider
import org.structured_barebone.data.client.RevokeSessionStep
import org.structured_barebone.data.client.createHttpClient
import org.structured_barebone.data.model.UserProfile
import org.structured_barebone.shared.model.ErrorKind
import org.structured_barebone.shared.model.Result
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AuthRepositoryTest {
    private val config = AppConfig("https://api.test/")
    private val json = headersOf(HttpHeaders.ContentType, "application/json")
    private val problem = headersOf(HttpHeaders.ContentType, "application/problem+json")

    /** The client works on real dispatchers; virtual test time would expire its timeouts. */
    private fun realTime(block: suspend CoroutineScope.() -> Unit) = runTest { withContext(Dispatchers.Default) { block() } }

    private fun options(tokens: InMemoryTokenProvider) = HttpClientOptions(
        tokenProvider = tokens,
        refreshUrl = config.url("/v1/auth/refresh"),
        maxRetries = 0,
    )

    @Test
    fun login_stores_the_token_pair() = realTime {
        val tokens = InMemoryTokenProvider()
        var body = ""
        val engine = MockEngine { request ->
            body = request.body.toByteArray().decodeToString()
            respond("""{"accessToken":"a1","refreshToken":"r1"}""", HttpStatusCode.OK, json)
        }
        val repository = AuthRepositoryImpl(createHttpClient(engine, options(tokens)), config, tokens)

        val result = repository.login("  user@example.test ", "secret")

        assertIs<Result.Success<Unit>>(result)
        assertEquals("a1", tokens.accessToken())
        assertEquals("r1", tokens.refreshToken())
        assertTrue(tokens.isLoggedIn.value)
        assertTrue(body.contains("\"email\":\"user@example.test\""), body)
    }

    @Test
    fun wrong_credentials_become_an_unauthorized_error_and_store_nothing() = realTime {
        val tokens = InMemoryTokenProvider()
        val engine = MockEngine {
            respond("""{"status":401,"code":"auth.invalid_credentials","traceId":"t-9"}""", HttpStatusCode.Unauthorized, problem)
        }
        val repository = AuthRepositoryImpl(createHttpClient(engine, options(tokens)), config, tokens)

        val error = assertIs<Result.Error>(repository.login("user@example.test", "wrong")).error

        assertEquals(ErrorKind.UNAUTHORIZED, error.kind)
        assertEquals("auth.invalid_credentials", error.code)
        assertEquals("t-9", error.traceId)
        assertFalse(tokens.isLoggedIn.value)
    }

    @Test
    fun profile_reads_the_signed_in_user() = realTime {
        val tokens = InMemoryTokenProvider("a1", "r1")
        val engine = MockEngine { request ->
            assertEquals("Bearer a1", request.headers[HttpHeaders.Authorization])
            respond("""{"id":"u1","email":"user@example.test","displayName":"Sample User","extra":1}""", HttpStatusCode.OK, json)
        }
        val repository = AuthRepositoryImpl(createHttpClient(engine, options(tokens)), config, tokens)

        val profile = assertIs<Result.Success<UserProfile>>(repository.profile()).data

        assertEquals(UserProfile("u1", "user@example.test", "Sample User"), profile)
    }

    @Test
    fun revoke_step_posts_the_refresh_token_and_does_nothing_without_one() = realTime {
        val seen = mutableListOf<String>()
        val engine = MockEngine { request ->
            seen += "${request.method.value} ${request.url.encodedPath} ${request.body.toByteArray().decodeToString()}"
            respond("", HttpStatusCode.NoContent)
        }
        val tokens = InMemoryTokenProvider("a1", "r1")
        RevokeSessionStep(createHttpClient(engine, options(tokens)), config, tokens).run()
        assertEquals(listOf("POST /v1/auth/logout {\"refreshToken\":\"r1\"}"), seen)

        seen.clear()
        val signedOut = InMemoryTokenProvider()
        RevokeSessionStep(createHttpClient(engine, options(signedOut)), config, signedOut).run()
        assertTrue(seen.isEmpty())
    }
}
