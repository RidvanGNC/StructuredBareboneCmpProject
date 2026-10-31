package org.structured_barebone.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.structured_barebone.data.domain.AuthRepository
import org.structured_barebone.data.model.UserProfile
import org.structured_barebone.shared.model.AppError
import org.structured_barebone.shared.model.ErrorKind
import org.structured_barebone.shared.model.Result
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private class FakeAuth(var result: Result<Unit> = Result.Success(Unit)) : AuthRepository {
        val logins = mutableListOf<Pair<String, String>>()
        override suspend fun login(email: String, password: String): Result<Unit> {
            logins += email to password
            return result
        }

        override suspend fun profile(): Result<UserProfile> = Result.Error(AppError(ErrorKind.UNEXPECTED))
    }

    @Test
    fun missing_fields_are_flagged_and_nothing_is_sent() = runTest {
        val auth = FakeAuth()
        val model = LoginViewModel(auth)

        model.submit()

        assertTrue(model.state.value.emailMissing)
        assertTrue(model.state.value.passwordMissing)
        assertTrue(auth.logins.isEmpty())

        model.onEmailChange("user@example.test")
        assertFalse(model.state.value.emailMissing)
        assertTrue(model.state.value.passwordMissing)
    }

    @Test
    fun a_successful_sign_in_clears_the_password() = runTest {
        val auth = FakeAuth()
        val model = LoginViewModel(auth)
        model.onEmailChange("user@example.test")
        model.onPasswordChange("secret")

        model.submit()

        assertEquals(listOf("user@example.test" to "secret"), auth.logins)
        assertEquals("", model.state.value.password)
        assertFalse(model.state.value.submitting)
        assertNull(model.state.value.error)
    }

    @Test
    fun a_failed_sign_in_keeps_the_form_and_exposes_the_error() = runTest {
        val auth = FakeAuth(Result.Error(AppError(ErrorKind.UNAUTHORIZED, code = "auth.invalid_credentials")))
        val model = LoginViewModel(auth)
        model.onEmailChange("user@example.test")
        model.onPasswordChange("wrong")

        model.submit()

        assertEquals("wrong", model.state.value.password)
        assertEquals(ErrorKind.UNAUTHORIZED, model.state.value.error?.kind)
        assertFalse(model.state.value.submitting)

        model.onPasswordChange("other")
        assertNull(model.state.value.error)
    }
}
