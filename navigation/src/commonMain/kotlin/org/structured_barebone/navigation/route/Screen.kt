package org.structured_barebone.navigation.route

import kotlinx.serialization.Serializable

/**
 * Routes of the signed-in app. CMP navigation is a presentation concern, intentionally outside shared logic.
 * The sign-in screen is not a route: the app shell shows it instead of the navigation while signed out, so no
 * back stack survives a sign-out.
 */
@Serializable
sealed class Screen {
    @Serializable
    data object Template : Screen()

    @Serializable
    data object Profile : Screen()

    // Example definition with an argument:
    // @Serializable data class Detail(val id: String) : Screen()
}
