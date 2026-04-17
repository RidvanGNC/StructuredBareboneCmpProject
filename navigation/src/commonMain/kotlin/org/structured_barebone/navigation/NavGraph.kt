package org.structured_barebone.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.structured_barebone.shared.navigation.Screen
import org.structured_barebone.template.TemplateScreen

@Composable
fun SetupNavGraph(startDestination: Screen = Screen.Template) {
    val navController = rememberNavController()
    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        composable<Screen.Template> {
            TemplateScreen(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}