package org.structured_barebone.shared.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Template : Screen()

    //Example Definitions
//    @Serializable
//    data object Login : Screen()
//
//    @Serializable
//    data class Detail(val id: String): Screen()
}