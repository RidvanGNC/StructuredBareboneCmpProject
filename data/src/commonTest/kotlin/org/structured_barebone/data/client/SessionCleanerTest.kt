package org.structured_barebone.data.client

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class SessionCleanerTest {
    @Test
    fun steps_run_in_order_while_the_tokens_still_exist_and_the_tokens_are_cleared_last() = runTest {
        val tokens = InMemoryTokenProvider("access", "refresh")
        val seen = mutableListOf<String>()
        val cleaner = SessionCleaner(
            tokens,
            listOf(
                SignOutStep { seen += "first:${tokens.accessToken()}" },
                SignOutStep { seen += "second:${tokens.accessToken()}" },
            ),
        )

        cleaner.signOut()

        assertEquals(listOf("first:access", "second:access"), seen)
        assertNull(tokens.accessToken())
        assertFalse(tokens.isLoggedIn.value)
    }

    @Test
    fun a_failing_step_does_not_stop_the_others() = runTest {
        val tokens = InMemoryTokenProvider("access", "refresh")
        var secondRan = false
        val cleaner = SessionCleaner(
            tokens,
            listOf(SignOutStep { error("offline") }, SignOutStep { secondRan = true }),
        )

        cleaner.signOut()

        assertEquals(true, secondRan)
        assertFalse(tokens.isLoggedIn.value)
    }

    @Test
    fun a_hanging_step_is_skipped_after_its_timeout() = runTest {
        val tokens = InMemoryTokenProvider("access", "refresh")
        var afterHang = false
        val cleaner = SessionCleaner(
            tokens,
            listOf(SignOutStep { delay(60_000) }, SignOutStep { afterHang = true }),
            stepTimeoutMillis = 1_000,
        )

        cleaner.signOut()

        assertEquals(true, afterHang)
        assertFalse(tokens.isLoggedIn.value)
        assertEquals(1_000L, testScheduler.currentTime)
    }

    @Test
    fun cancelling_the_sign_out_still_clears_the_tokens() = runTest {
        val inner = InMemoryTokenProvider("access", "refresh")
        // A real store suspends while it clears; a cancelled caller must not interrupt that.
        val tokens = object : TokenProvider by inner {
            override suspend fun clear() {
                delay(10)
                inner.clear()
            }
        }
        val cleaner = SessionCleaner(tokens, listOf(SignOutStep { delay(60_000) }), stepTimeoutMillis = 120_000)

        val job = launch { cleaner.signOut() }
        advanceTimeBy(1_000)
        job.cancel(CancellationException("screen closed"))
        job.join()

        assertFalse(tokens.isLoggedIn.value)
        assertNull(tokens.refreshToken())
    }

    @Test
    fun without_steps_it_only_clears_the_tokens() = runTest {
        val tokens = InMemoryTokenProvider("access", "refresh")

        SessionCleaner(tokens).signOut()

        assertFalse(tokens.isLoggedIn.value)
    }
}
