package org.structured_barebone.data.implementation

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import org.structured_barebone.data.model.ProblemDetails
import org.structured_barebone.data.model.ServerException
import org.structured_barebone.shared.model.ErrorKind
import org.structured_barebone.shared.model.Result
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class BaseRepositoryTest {
    private suspend fun <T> call(block: suspend () -> T): Result<T> = TestRepository().call(block)

    @Test
    fun successful_call_returns_its_data() = runTest {
        assertEquals(Result.Success("template"), call { "template" })
    }

    @Test
    fun server_problem_becomes_an_error_with_its_code_and_trace_id() = runTest {
        val problem = ProblemDetails(code = "item.missing", traceId = "t-7", detail = "internal detail")
        val result = call<String> { throw ServerException(404, problem) }

        val error = assertIs<Result.Error>(result).error
        assertEquals(ErrorKind.NOT_FOUND, error.kind)
        assertEquals("item.missing", error.code)
        assertEquals("t-7", error.traceId)
        assertEquals(404, error.status)
    }

    @Test
    fun unreachable_server_is_marked_offline() = runTest {
        val connect = assertIs<Result.Error>(call<String> { throw ConnectTimeoutException("host") }).error
        val io = assertIs<Result.Error>(call<String> { throw IOException("down") }).error

        assertEquals(ErrorKind.NETWORK_UNREACHABLE, connect.kind)
        assertEquals(true, connect.isOffline)
        assertEquals(ErrorKind.NETWORK_UNREACHABLE, io.kind)
    }

    @Test
    fun request_timeout_is_a_timeout_not_offline() = runTest {
        val error = assertIs<Result.Error>(call<String> { throw HttpRequestTimeoutException("url", 1) }).error

        assertEquals(ErrorKind.TIMEOUT, error.kind)
        assertEquals(false, error.isOffline)
    }

    @Test
    fun cancellation_is_rethrown_instead_of_becoming_an_error() = runTest {
        assertFailsWith<CancellationException> {
            call<String> { throw CancellationException("screen closed") }
        }
    }

    @Test
    fun unexpected_exception_exposes_only_its_class_name() = runTest {
        val error = assertIs<Result.Error>(call<String> { throw IllegalStateException("secret internal text") }).error

        assertEquals(ErrorKind.UNEXPECTED, error.kind)
        assertEquals("IllegalStateException", error.detail)
    }

    private class TestRepository : BaseRepository() {
        suspend fun <T> call(block: suspend () -> T): Result<T> = safeCall { block() }
    }
}
