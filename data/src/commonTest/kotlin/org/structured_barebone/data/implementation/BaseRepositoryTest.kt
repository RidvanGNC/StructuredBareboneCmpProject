package org.structured_barebone.data.implementation

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.CancellationException
import org.structured_barebone.data.model.ApiResponse
import org.structured_barebone.shared.model.Result

class BaseRepositoryTest {
    @Test
    fun successful_response_returns_its_data() = runTest {
        val result = TestRepository().request(ApiResponse(success = true, data = "template"))

        assertEquals(Result.Success("template"), result)
    }

    @Test
    fun unsuccessful_response_returns_its_message() = runTest {
        val result = TestRepository().request(ApiResponse(success = false, message = "Unavailable"))

        assertEquals(Result.Error("Unavailable"), result)
    }

    @Test
    fun cancellation_is_rethrown_instead_of_becoming_an_error() = runTest {
        assertFailsWith<CancellationException> {
            TestRepository().requestWith { throw CancellationException("screen closed") }
        }
    }

    @Test
    fun unexpected_exception_returns_a_generic_error_without_its_message() = runTest {
        val result = TestRepository().requestWith { throw IllegalStateException("internal detail") }

        assertEquals(true, result is Result.Error)
        assertEquals(false, (result as Result.Error).message.contains("internal detail"))
    }

    private class TestRepository : BaseRepository() {
        suspend fun request(response: ApiResponse<String>): Result<String> = safeRequest { response }

        suspend fun requestWith(block: suspend () -> ApiResponse<String>): Result<String> = safeRequest { block() }
    }
}
