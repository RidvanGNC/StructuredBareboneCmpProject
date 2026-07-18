package org.structured_barebone.shared.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Template : Screen()

    @Serializable
    data object Mails : Screen()

    @Serializable
    data class MailDetail(val mailId: String) : Screen()

    @Serializable
    data object RoutingContacts : Screen()

    @Serializable
    data object PricingRates : Screen()

    @Serializable
    data object Settings : Screen()

    @Serializable
    data object User : Screen()
}
