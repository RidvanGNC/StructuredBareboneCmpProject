package org.structured_barebone.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.structured_barebone.data.client.AppConfig
import org.structured_barebone.data.client.InMemoryTokenProvider
import org.structured_barebone.data.client.SessionCleaner
import org.structured_barebone.data.domain.AuthRepository
import org.structured_barebone.data.model.UserProfile
import org.structured_barebone.shared.model.AppError
import org.structured_barebone.shared.model.AppMessages
import org.structured_barebone.shared.model.ErrorKind
import org.structured_barebone.shared.model.Result
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class SessionViewModelTest {
    private val enabled = AppConfig("https://api.test")
    private val messages = AppMessages()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private class FakeAuth(var profileResult: Result<UserProfile>) : AuthRepository {
        var profileCalls = 0
        override suspend fun login(email: String, password: String): Result<Unit> = Result.Success(Unit)
        override suspend fun profile(): Result<UserProfile> {
            profileCalls++
            return profileResult
        }
    }

    private val profile = UserProfile("u1", "user@example.test")

    private fun viewModel(
        tokens: InMemoryTokenProvider,
        auth: FakeAuth = FakeAuth(Result.Success(profile)),
        config: AppConfig = enabled,
    ) = SessionViewModel(config, tokens, auth, SessionCleaner(tokens), messages)

    @Test
    fun without_a_backend_there_is_no_sign_in() = runTest {
        val model = viewModel(InMemoryTokenProvider(), config = AppConfig())

        assertEquals(SessionState.SIGNED_IN, model.state.value)
    }

    @Test
    fun without_tokens_the_user_is_signed_out_and_signing_in_opens_the_session_without_a_check() = runTest {
        val tokens = InMemoryTokenProvider()
        val auth = FakeAuth(Result.Success(profile))
        val model = viewModel(tokens, auth)
        assertEquals(SessionState.SIGNED_OUT, model.state.value)

        tokens.save("a", "r")

        assertEquals(SessionState.SIGNED_IN, model.state.value)
        assertEquals(0, auth.profileCalls)
    }

    @Test
    fun a_stored_session_is_checked_once_and_kept_when_valid() = runTest {
        val auth = FakeAuth(Result.Success(profile))
        val model = viewModel(InMemoryTokenProvider("a", "r"), auth)
        advanceUntilIdle()

        assertEquals(SessionState.SIGNED_IN, model.state.value)
        assertEquals(1, auth.profileCalls)
    }

    @Test
    fun a_network_failure_keeps_the_user_signed_in() = runTest {
        val auth = FakeAuth(Result.Error(AppError(ErrorKind.NETWORK_UNREACHABLE)))
        val model = viewModel(InMemoryTokenProvider("a", "r"), auth)
        advanceUntilIdle()

        assertEquals(SessionState.SIGNED_IN, model.state.value)
        assertNull(messages.current.value)
    }

    @Test
    fun a_rejected_session_signs_out_and_explains_why() = runTest {
        val tokens = InMemoryTokenProvider("a", "r")
        val auth = FakeAuth(Result.Error(AppError(ErrorKind.UNAUTHORIZED)))
        val model = viewModel(tokens, auth)
        advanceUntilIdle()

        assertEquals(SessionState.SIGNED_OUT, model.state.value)
        assertEquals(ErrorKind.UNAUTHORIZED, assertNotNull(messages.current.value).error.kind)
        messages.dismiss()
    }

    @Test
    fun signing_out_by_choice_posts_no_message() = runTest {
        val tokens = InMemoryTokenProvider("a", "r")
        val model = viewModel(tokens)
        advanceUntilIdle()

        model.signOut()
        advanceUntilIdle()

        assertEquals(SessionState.SIGNED_OUT, model.state.value)
        assertNull(messages.current.value)
    }

    @Test
    fun a_session_that_ends_by_itself_posts_a_message() = runTest {
        val tokens = InMemoryTokenProvider("a", "r")
        val model = viewModel(tokens)
        advanceUntilIdle()

        tokens.clear()
        advanceUntilIdle()

        assertEquals(SessionState.SIGNED_OUT, model.state.value)
        assertNotNull(messages.current.value)
        messages.dismiss()
    }
}
