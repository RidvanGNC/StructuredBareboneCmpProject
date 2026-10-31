package org.structured_barebone.cmp_project

import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.structured_barebone.data.client.AppInfo
import org.structured_barebone.data.client.AppLog
import org.structured_barebone.di.initKoin

class StructuredBareboneApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppInfo.version = packageManager.getPackageInfo(packageName, 0).versionName.orEmpty()
        AppLog.debug = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        initKoin {
            androidLogger(Level.INFO)
            androidContext(this@StructuredBareboneApplication)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}
