package org.structured_barebone.navigation.route

import kotlinx.serialization.Serializable

/** CMP navigation is a presentation concern, intentionally outside shared logic. */
@Serializable
sealed class Screen {
    @Serializable
    data object Template : Screen()

    // Example definitions:
    // @Serializable data object Login : Screen()
    // @Serializable data class Detail(val id: String) : Screen()
}
