package org.structured_barebone.data.client

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import org.structured_barebone.data.model.ProblemDetails
import org.structured_barebone.data.model.ServerException
import org.structured_barebone.shared.model.ErrorKind
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ErrorMappingAndInfoTest {
    @AfterTest
    fun resetVersion() {
        AppInfo.version = ""
    }

    @Test
    fun statuses_map_to_error_kinds() {
        assertEquals(ErrorKind.VALIDATION, errorKindForStatus(400))
        assertEquals(ErrorKind.VALIDATION, errorKindForStatus(422))
        assertEquals(ErrorKind.UNAUTHORIZED, errorKindForStatus(401))
        assertEquals(ErrorKind.FORBIDDEN, errorKindForStatus(403))
        assertEquals(ErrorKind.NOT_FOUND, errorKindForStatus(404))
        assertEquals(ErrorKind.TIMEOUT, errorKindForStatus(408))
        assertEquals(ErrorKind.TIMEOUT, errorKindForStatus(504))
        assertEquals(ErrorKind.CONFLICT, errorKindForStatus(409))
        assertEquals(ErrorKind.UPDATE_REQUIRED, errorKindForStatus(426))
        assertEquals(ErrorKind.RATE_LIMITED, errorKindForStatus(429))
        assertEquals(ErrorKind.SERVER, errorKindForStatus(500))
        assertEquals(ErrorKind.SERVER, errorKindForStatus(503))
        assertEquals(ErrorKind.UNEXPECTED, errorKindForStatus(418))
    }

    @Test
    fun server_exception_keeps_problem_fields_as_data() {
        val problem = ProblemDetails(
            title = "Unprocessable",
            detail = "Quantity is below the minimum",
            code = "order.quantity",
            traceId = "trace-9",
            errors = mapOf("quantity" to listOf("min 1")),
        )
        val error = ServerException(422, problem).toAppError()

        assertEquals(ErrorKind.VALIDATION, error.kind)
        assertEquals("order.quantity", error.code)
        assertEquals(422, error.status)
        assertEquals("trace-9", error.traceId)
        assertEquals("Quantity is below the minimum", error.detail)
        assertEquals(listOf("min 1"), error.fieldErrors["quantity"])
    }

    @Test
    fun header_version_uses_the_numeric_part_only() {
        AppInfo.version = "2.0.1-debug"
        assertEquals("2.0.1", AppInfo.headerVersion())
        AppInfo.version = "3.1"
        assertEquals("3.1", AppInfo.headerVersion())
    }

    @Test
    fun header_version_falls_back_to_the_development_version() {
        AppInfo.version = ""
        assertEquals(AppInfo.DEV_VERSION, AppInfo.headerVersion())
        AppInfo.version = "snapshot"
        assertEquals(AppInfo.DEV_VERSION, AppInfo.headerVersion())
    }

    @Test
    fun monitor_treats_gateway_errors_as_offline_and_other_answers_as_reachable() = runTest {
        var status = HttpStatusCode.OK
        val engine = MockEngine { respond("", status) }
        val monitor = ConnectivityMonitor(createHttpClient(engine, HttpClientOptions(maxRetries = 0)), "https://api.test/health")

        assertFalse(monitor.online.value)
        assertTrue(monitor.check())
        assertTrue(monitor.online.value)

        status = HttpStatusCode.NotFound
        assertTrue(monitor.check())

        status = HttpStatusCode.BadGateway
        assertFalse(monitor.check())
        assertFalse(monitor.online.value)
    }
}
