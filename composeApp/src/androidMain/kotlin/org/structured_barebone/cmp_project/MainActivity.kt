package org.structured_barebone.cmp_project

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.structured_barebone.cmp_project.theme.AppTheme
import org.structured_barebone.data.auth.AuthService
import org.structured_barebone.di.commonModule
import org.structured_barebone.di.platformModule
import org.structured_barebone.shared.model.Result

class AndroidPlatform {
    fun initKoin(context: Context) {
        startKoin {
            androidLogger(Level.INFO)
            androidContext(context)
            modules(commonModule(), platformModule(context))
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        AndroidPlatform().initKoin(this)
        setContent {
            AuthGate(activity = this)
        }
    }
}

@Composable
private fun AuthGate(activity: MainActivity) {
    val authService = koinInject<AuthService>()
    val isSignedIn by authService.isSignedIn.collectAsState()

    if (isSignedIn) {
        App()
        return
    }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Devam etmek için giriş yapın", style = MaterialTheme.typography.titleMedium)
                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                }
                Button(
                    modifier = Modifier.padding(top = 16.dp),
                    enabled = !isLoading,
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        scope.launch {
                            val result = authService.signIn(activity)
                            if (result is Result.Error) {
                                errorMessage = result.message
                            }
                            isLoading = false
                        }
                    },
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text("Giriş Yap")
                    }
                }
            }
        }
    }
}
