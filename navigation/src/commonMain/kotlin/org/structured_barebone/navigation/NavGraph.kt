package org.structured_barebone.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.structured_barebone.mailgroups.MailGroupsScreen
import org.structured_barebone.mails.MailsScreen
import org.structured_barebone.maildetail.MailDetailScreen
import org.structured_barebone.pricingrates.PricingRatesScreen
import org.structured_barebone.routingcontacts.RoutingContactsScreen
import org.structured_barebone.settings.SettingsScreen
import org.structured_barebone.shared.navigation.Screen
import org.structured_barebone.user.UserScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: Screen = Screen.Mails,
) {
    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        composable<Screen.Mails> {
            MailsScreen(
                onMailClick = { mailId -> navController.navigate(Screen.MailDetail(mailId)) },
                onMailGroupsClick = { navController.navigate(Screen.MailGroups) },
            )
        }
        composable<Screen.MailDetail> { backStackEntry ->
            val destination: Screen.MailDetail = backStackEntry.toRoute()
            MailDetailScreen(
                mailId = destination.mailId,
                onBack = { navController.popBackStack() },
            )
        }
        composable<Screen.RoutingContacts> {
            RoutingContactsScreen()
        }
        composable<Screen.MailGroups> {
            MailGroupsScreen(onBack = { navController.popBackStack() })
        }
        composable<Screen.PricingRates> {
            PricingRatesScreen()
        }
        composable<Screen.Settings> {
            SettingsScreen()
        }
        composable<Screen.User> {
            UserScreen()
        }
    }
}
