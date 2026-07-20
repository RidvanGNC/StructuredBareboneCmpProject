package org.structured_barebone.cmp_project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.structured_barebone.navigation.SetupNavGraph
import org.structured_barebone.shared.navigation.Screen

@Composable
fun App() {
    MaterialTheme {
        SetupNavGraph(startDestination = Screen.Template)
    }
}