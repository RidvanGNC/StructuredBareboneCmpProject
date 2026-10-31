package org.structured_barebone.cmp_project

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.DesktopComposeUiTest
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.structured_barebone.data.client.AppConfig
import org.structured_barebone.data.client.InMemoryTokenProvider
import org.structured_barebone.data.client.TokenProvider
import org.structured_barebone.data.client.UpdateGate
import org.structured_barebone.data.domain.AuthRepository
import org.structured_barebone.data.domain.SecureKeyStore
import org.structured_barebone.data.implementation.InMemorySecureKeyStore
import org.structured_barebone.data.model.UserProfile
import org.structured_barebone.di.commonModule
import org.structured_barebone.shared.model.AppError
import org.structured_barebone.shared.model.ErrorKind
import org.structured_barebone.shared.model.Result
import java.io.File
import java.util.Locale
import javax.imageio.ImageIO
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Drives the real app shell with a fake backend: sign-in gate, navigation, sign-out, session expiry, language
 * change and the update gate. Screenshots of the main states are written to `build/ui-shots` for review.
 */
@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class)
class AppFlowTest {
    private val originalLocale = Locale.getDefault()
    private val tokens = InMemoryTokenProvider()
    private val updateGate = UpdateGate()
    private val owner = object : ViewModelStoreOwner {
        override val viewModelStore = ViewModelStore()
    }

    private inner class FakeAuth : AuthRepository {
        var accepted = "secret"
        override suspend fun login(email: String, password: String): Result<Unit> {
            if (password != accepted) return Result.Error(AppError(ErrorKind.UNAUTHORIZED, code = "auth.invalid_credentials"))
            tokens.save("access", "refresh")
            return Result.Success(Unit)
        }

        override suspend fun profile(): Result<UserProfile> =
            Result.Success(UserProfile("u1", "user@example.test", "Sample User"))
    }

    private fun start(config: AppConfig) {
        startKoin {
            modules(commonModule(config))
            modules(
                module {
                    single<Settings> { MapSettings() }
                    single<SecureKeyStore> { InMemorySecureKeyStore() }
                    single<TokenProvider> { tokens }
                    single<AuthRepository> { FakeAuth() }
                    single { updateGate }
                },
            )
        }
    }

    @BeforeTest
    fun setUp() {
        Locale.setDefault(Locale.ENGLISH)
        // Lifecycle code insists on the main thread; an unconfined Main lets the test thread qualify.
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @AfterTest
    fun tearDown() {
        // Clear the view models while Main still exists: cancelling their scopes dispatches to it.
        owner.viewModelStore.clear()
        stopKoin()
        Dispatchers.resetMain()
        Locale.setDefault(originalLocale)
    }

    private fun DesktopComposeUiTest.show() = setContent {
        CompositionLocalProvider(LocalViewModelStoreOwner provides owner) { App() }
    }

    private fun DesktopComposeUiTest.shot(name: String) {
        val file = File("build/ui-shots/$name.png").also { it.parentFile.mkdirs() }
        ImageIO.write(onRoot().captureToImage().toAwtImage(), "png", file)
    }

    private fun DesktopComposeUiTest.signIn(password: String = "secret") {
        onNodeWithText("Email").performTextInput("user@example.test")
        onNodeWithText("Password").performTextInput(password)
        onNode(hasText("Sign in") and hasClickAction()).performClick()
    }

    private fun DesktopComposeUiTest.waitForText(text: String) =
        waitUntil(timeoutMillis = 5_000) { onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() }

    @Test
    fun without_a_backend_the_app_opens_on_the_home_screen() = runDesktopComposeUiTest(width = 420, height = 800) {
        start(AppConfig())
        show()

        waitForText("Operations Workspace")
        onAllNodesWithText("Email").assertCountEquals(0)
        shot("home-compact")
    }

    @Test
    fun sign_in_then_navigate_then_sign_out() = runDesktopComposeUiTest(width = 420, height = 800) {
        start(AppConfig("https://api.test"))
        show()

        waitForText("Email")
        shot("login-compact")
        signIn()
        waitForText("Operations Workspace")

        onNodeWithText("Profile").performClick()
        waitForText("Appearance")
        waitForText("Sample User")
        shot("profile-compact")

        onNode(hasText("Sign out") and hasClickAction()).performClick()
        waitForText("Sign out?")
        val signOutNodes = onAllNodesWithText("Sign out")
        signOutNodes[signOutNodes.fetchSemanticsNodes().size - 1].performClick()

        waitForText("Email")
        onAllNodesWithText("Your session has expired. Please sign in again.").assertCountEquals(0)
    }

    @Test
    fun wrong_credentials_show_a_clear_message_and_keep_the_form() = runDesktopComposeUiTest(width = 420, height = 800) {
        start(AppConfig("https://api.test"))
        show()

        waitForText("Email")
        signIn(password = "wrong")

        waitForText("The email or password is incorrect.")
        assertTrue(onAllNodesWithText("Email").fetchSemanticsNodes().isNotEmpty())
    }

    @Test
    fun a_session_that_ends_by_itself_returns_to_sign_in_with_a_message() = runDesktopComposeUiTest(width = 420, height = 800) {
        start(AppConfig("https://api.test"))
        show()
        waitForText("Email")
        signIn()
        waitForText("Operations Workspace")

        runOnIdle { kotlinx.coroutines.runBlocking { tokens.clear() } }

        waitForText("Your session has expired. Please sign in again.")
        assertTrue(onAllNodesWithText("Email").fetchSemanticsNodes().isNotEmpty())
        shot("session-expired")
    }

    @Test
    fun changing_the_language_keeps_the_user_on_the_same_screen() = runDesktopComposeUiTest(width = 1100, height = 800) {
        start(AppConfig())
        show()
        waitForText("Operations Workspace")

        onNodeWithText("Profile").performClick()
        waitForText("Appearance")
        shot("profile-wide")
        onNode(hasText("Türkçe") and hasClickAction()).performClick()

        waitForText("Görünüm")
        onAllNodesWithText("Operations Workspace").assertCountEquals(0)
        shot("profile-turkish")
    }

    @Test
    fun an_unsupported_version_blocks_the_app() = runDesktopComposeUiTest(width = 420, height = 800) {
        start(AppConfig())
        show()
        waitForText("Operations Workspace")

        runOnIdle { updateGate.markRequired() }

        waitForText("Update required")
    }
}
