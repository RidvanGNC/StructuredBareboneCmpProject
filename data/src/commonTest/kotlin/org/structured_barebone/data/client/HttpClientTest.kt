package org.structured_barebone.data.client

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.structured_barebone.data.model.ServerException
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HttpClientTest {
    private val baseUrl = "https://api.test"
    private val refreshUrl = "$baseUrl/v1/auth/refresh"
    private val problemHeaders = headersOf(HttpHeaders.ContentType, "application/problem+json")
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")

    /** The client works on real dispatchers; virtual test time would expire its timeouts while it waits for them. */
    private fun realTime(block: suspend CoroutineScope.() -> Unit) = runTest { withContext(Dispatchers.Default) { block() } }

    @AfterTest
    fun resetVersion() {
        AppInfo.version = ""
    }

    private fun MockRequestHandleScope.json(content: String, status: HttpStatusCode = HttpStatusCode.OK) =
        respond(content, status, jsonHeaders)

    private fun options(
        provider: TokenProvider? = null,
        gate: UpdateGate? = null,
        maxRetries: Int = 2,
    ) = HttpClientOptions(
        tokenProvider = provider,
        refreshUrl = if (provider != null) refreshUrl else null,
        updateGate = gate,
        maxRetries = maxRetries,
        retryBaseDelayMillis = 1,
        retryJitterMillis = 0,
    )

    @Test
    fun problem_document_becomes_a_server_exception_with_its_fields() = realTime {
        val engine = MockEngine {
            respond(
                """{"title":"Conflict","status":409,"code":"stock.changed","traceId":"t-1","errors":{"qty":["too low"]}}""",
                HttpStatusCode.Conflict,
                problemHeaders,
            )
        }
        val error = assertFailsWith<ServerException> { createHttpClient(engine, options()).get("$baseUrl/items") }

        assertEquals(409, error.status)
        assertEquals("stock.changed", error.problem?.code)
        assertEquals("t-1", error.problem?.traceId)
        assertEquals(listOf("too low"), error.problem?.errors?.get("qty"))
    }

    @Test
    fun body_that_is_not_a_problem_document_gives_no_problem() = realTime {
        val engine = MockEngine { respond("<html>Bad gateway</html>", HttpStatusCode.NotFound) }
        val error = assertFailsWith<ServerException> { createHttpClient(engine, options()).get("$baseUrl/items") }

        assertEquals(404, error.status)
        assertNull(error.problem)
    }

    @Test
    fun every_request_carries_the_numeric_client_version() = realTime {
        AppInfo.version = "1.4.0-rc.1"
        var sent: String? = null
        val engine = MockEngine { request ->
            sent = request.headers[HttpDefaults.VERSION_HEADER]
            json("{}")
        }
        createHttpClient(engine, options()).get("$baseUrl/items")

        assertEquals("1.4.0", sent)
    }

    @Test
    fun builds_without_a_version_report_the_development_version() = realTime {
        var sent: String? = null
        val engine = MockEngine { request ->
            sent = request.headers[HttpDefaults.VERSION_HEADER]
            json("{}")
        }
        createHttpClient(engine, options()).get("$baseUrl/items")

        assertEquals(AppInfo.DEV_VERSION, sent)
    }

    @Test
    fun upgrade_required_response_opens_the_update_gate() = realTime {
        val gate = UpdateGate()
        val engine = MockEngine { respond("", HttpStatusCode.UpgradeRequired) }
        assertFailsWith<ServerException> { createHttpClient(engine, options(gate = gate)).get("$baseUrl/items") }

        assertTrue(gate.required.value)
    }

    @Test
    fun expired_access_token_is_refreshed_and_the_request_repeated() = realTime {
        val provider = InMemoryTokenProvider("old-access", "refresh-1")
        val seen = mutableListOf<String?>()
        val engine = MockEngine { request ->
            if (request.url.toString() == refreshUrl) {
                json("""{"accessToken":"new-access","refreshToken":"refresh-2"}""")
            } else {
                seen += request.headers[HttpHeaders.Authorization]
                if (request.headers[HttpHeaders.Authorization] == "Bearer new-access") json("""{"ok":true}""")
                else respond("", HttpStatusCode.Unauthorized, headersOf(HttpHeaders.WWWAuthenticate, "Bearer"))
            }
        }
        val response = createHttpClient(engine, options(provider)).get("$baseUrl/items")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(listOf<String?>("Bearer old-access", "Bearer new-access"), seen)
        assertEquals("refresh-2", provider.refreshToken())
    }

    @Test
    fun concurrent_unauthorized_requests_share_one_refresh() = realTime {
        val provider = InMemoryTokenProvider("old-access", "refresh-1")
        var refreshCalls = 0
        val engine = MockEngine { request ->
            if (request.url.toString() == refreshUrl) {
                refreshCalls++
                json("""{"accessToken":"new-access","refreshToken":"refresh-2"}""")
            } else if (request.headers[HttpHeaders.Authorization] == "Bearer new-access") {
                json("""{"ok":true}""")
            } else {
                respond("", HttpStatusCode.Unauthorized, headersOf(HttpHeaders.WWWAuthenticate, "Bearer"))
            }
        }
        val client = createHttpClient(engine, options(provider))
        val statuses = (1..3).map { async { client.get("$baseUrl/items/$it").status } }.awaitAll()

        assertTrue(statuses.all { it == HttpStatusCode.OK })
        assertEquals(1, refreshCalls)
    }

    @Test
    fun rejected_refresh_token_ends_the_session() = realTime {
        val provider = InMemoryTokenProvider("old-access", "refresh-1")
        val engine = MockEngine { request ->
            if (request.url.toString() == refreshUrl) respond("", HttpStatusCode.Unauthorized)
            else respond("", HttpStatusCode.Unauthorized, headersOf(HttpHeaders.WWWAuthenticate, "Bearer"))
        }
        val error = assertFailsWith<ServerException> { createHttpClient(engine, options(provider)).get("$baseUrl/items") }

        assertEquals(401, error.status)
        assertFalse(provider.isLoggedIn.value)
        assertNull(provider.refreshToken())
    }

    @Test
    fun network_failure_during_refresh_keeps_the_session() = realTime {
        val provider = InMemoryTokenProvider("old-access", "refresh-1")
        val engine = MockEngine { request ->
            if (request.url.toString() == refreshUrl) throw kotlinx.io.IOException("offline")
            respond("", HttpStatusCode.Unauthorized, headersOf(HttpHeaders.WWWAuthenticate, "Bearer"))
        }
        assertFailsWith<ServerException> { createHttpClient(engine, options(provider)).get("$baseUrl/items") }

        assertTrue(provider.isLoggedIn.value)
        assertNotNull(provider.refreshToken())
    }

    @Test
    fun token_is_not_sent_unprompted_to_other_hosts() = realTime {
        val provider = InMemoryTokenProvider("access", "refresh")
        var header: String? = "unset"
        val engine = MockEngine { request ->
            header = request.headers[HttpHeaders.Authorization]
            json("{}")
        }
        createHttpClient(engine, options(provider)).get("https://third-party.test/data")

        assertNull(header)
    }

    @Test
    fun token_is_sent_without_a_challenge_to_the_api_host() = realTime {
        val provider = InMemoryTokenProvider("access", "refresh")
        var header: String? = null
        val engine = MockEngine { request ->
            header = request.headers[HttpHeaders.Authorization]
            json("{}")
        }
        createHttpClient(engine, options(provider)).get("$baseUrl/items")

        assertEquals("Bearer access", header)
    }

    @Test
    fun idempotent_requests_are_retried_after_a_gateway_error() = realTime {
        var calls = 0
        val engine = MockEngine {
            calls++
            if (calls < 3) respond("", HttpStatusCode.ServiceUnavailable) else json("""{"ok":true}""")
        }
        val response = createHttpClient(engine, options()).get("$baseUrl/items")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(3, calls)
    }

    @Test
    fun post_requests_are_never_retried() = realTime {
        var calls = 0
        val engine = MockEngine {
            calls++
            respond("", HttpStatusCode.ServiceUnavailable)
        }
        assertFailsWith<ServerException> { createHttpClient(engine, options()).post("$baseUrl/items") }

        assertEquals(1, calls)
    }

    @Test
    fun response_body_is_readable_after_a_successful_call() = realTime {
        val engine = MockEngine { json("""{"ok":true}""") }
        val response = createHttpClient(engine, options()).get("$baseUrl/items")

        assertEquals("""{"ok":true}""", response.bodyAsText())
    }

    @Test
    fun refresh_request_carries_the_refresh_token_in_its_body() = realTime {
        val provider = InMemoryTokenProvider("old-access", "refresh-1")
        var refreshBody = ""
        val engine = MockEngine { request ->
            if (request.url.toString() == refreshUrl) {
                refreshBody = request.body.toByteArray().decodeToString()
                json("""{"accessToken":"new-access","refreshToken":"refresh-2"}""")
            } else if (request.headers[HttpHeaders.Authorization] == "Bearer new-access") {
                json("{}")
            } else {
                respond("", HttpStatusCode.Unauthorized, headersOf(HttpHeaders.WWWAuthenticate, "Bearer"))
            }
        }
        createHttpClient(engine, options(provider)).get("$baseUrl/items")

        assertEquals("""{"refreshToken":"refresh-1"}""", refreshBody)
    }
}
