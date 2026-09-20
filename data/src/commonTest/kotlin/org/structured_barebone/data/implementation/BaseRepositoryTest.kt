package org.structured_barebone.data.implementation

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
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

    private class TestRepository : BaseRepository() {
        suspend fun request(response: ApiResponse<String>): Result<String> = safeRequest { response }
    }
}
