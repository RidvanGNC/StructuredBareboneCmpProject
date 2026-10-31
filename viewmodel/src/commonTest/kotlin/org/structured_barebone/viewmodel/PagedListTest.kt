package org.structured_barebone.viewmodel

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.structured_barebone.shared.model.AppError
import org.structured_barebone.shared.model.ErrorKind
import org.structured_barebone.shared.model.Result
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PagedListTest {
    private val pages = mapOf(
        null to Page(listOf("a", "b"), "p2"),
        "p2" to Page(listOf("c"), null),
    )

    /**
     * A scope on the test scheduler. `backgroundScope` is not used: its coroutines did not run on
     * `advanceUntilIdle` with the coroutines-test version of this project.
     */
    private fun pagedTest(block: suspend TestScope.(CoroutineScope) -> Unit) = runTest {
        val scope = CoroutineScope(StandardTestDispatcher(testScheduler) + SupervisorJob())
        try {
            block(scope)
        } finally {
            scope.cancel()
        }
    }

    @Test
    fun first_page_then_more_until_the_cursor_ends() = pagedTest { scope ->
        val list = PagedList<String>(scope) { _, cursor -> Result.Success(pages.getValue(cursor)) }

        list.refresh()
        advanceUntilIdle()
        assertEquals(listOf("a", "b"), list.state.value.items)
        assertFalse(list.state.value.endReached)

        list.loadMore()
        advanceUntilIdle()
        assertEquals(listOf("a", "b", "c"), list.state.value.items)
        assertTrue(list.state.value.endReached)

        list.loadMore()
        advanceUntilIdle()
        assertEquals(3, list.state.value.items.size)
    }

    @Test
    fun rapid_typing_loads_once_with_the_last_query() = pagedTest { scope ->
        val queries = mutableListOf<String>()
        val list = PagedList<String>(scope, debounceMillis = 300) { query, _ ->
            queries += query
            Result.Success(Page(listOf(query), null))
        }

        list.setQuery("a")
        advanceTimeBy(100)
        list.setQuery("ab")
        advanceTimeBy(100)
        list.setQuery("abc")
        advanceTimeBy(299)
        assertTrue(queries.isEmpty())
        advanceUntilIdle()

        assertEquals(listOf("abc"), queries)
        assertEquals(listOf("abc"), list.state.value.items)
        assertEquals("abc", list.state.value.query)
    }

    @Test
    fun a_slow_answer_for_an_old_query_is_discarded() = pagedTest { scope ->
        val slow = CompletableDeferred<Result<Page<String>>>()
        val list = PagedList<String>(scope, debounceMillis = 10) { query, _ ->
            if (query == "old") slow.await() else Result.Success(Page(listOf(query), null))
        }

        list.setQuery("old")
        advanceTimeBy(11)
        list.setQuery("new")
        advanceUntilIdle()
        slow.complete(Result.Success(Page(listOf("stale"), null)))
        advanceUntilIdle()

        assertEquals(listOf("new"), list.state.value.items)
    }

    @Test
    fun a_failed_page_keeps_the_items_already_shown() = pagedTest { scope ->
        var failNext = false
        val list = PagedList<String>(scope) { _, cursor ->
            if (failNext) Result.Error(AppError(ErrorKind.NETWORK_UNREACHABLE)) else Result.Success(pages.getValue(cursor))
        }
        list.refresh()
        advanceUntilIdle()

        failNext = true
        list.loadMore()
        advanceUntilIdle()

        assertEquals(listOf("a", "b"), list.state.value.items)
        assertEquals(ErrorKind.NETWORK_UNREACHABLE, list.state.value.error?.kind)
        assertFalse(list.state.value.loadingMore)

        failNext = false
        list.loadMore()
        advanceUntilIdle()
        assertEquals(listOf("a", "b", "c"), list.state.value.items)
        assertNull(list.state.value.error)
    }

    @Test
    fun a_failed_first_page_shows_an_error_and_no_items() = pagedTest { scope ->
        val list = PagedList<String>(scope) { _, _ -> Result.Error(AppError(ErrorKind.SERVER)) }

        list.refresh()
        advanceUntilIdle()

        assertTrue(list.state.value.items.isEmpty())
        assertEquals(ErrorKind.SERVER, list.state.value.error?.kind)
        assertFalse(list.state.value.loading)
    }
}
