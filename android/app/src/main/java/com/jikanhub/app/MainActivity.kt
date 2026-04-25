package com.jikanhub.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.jikanhub.app.presentation.navigation.JikanNavHost
import com.jikanhub.app.presentation.theme.JikanHubTheme
import com.jikanhub.app.domain.repository.AuthRepository
import dagger.hilt.android.AndroidEntryPoint
import com.jikanhub.app.data.local.TokenManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()

        var startDestination by mutableStateOf<String?>(null)
        var isDarkMode by mutableStateOf(true)

        lifecycleScope.launch {
            launch {
                startDestination = if (authRepository.isLoggedIn()) "dashboard" else "login"
            }
            launch {
                tokenManager.isDarkMode.collectLatest { dark ->
                    isDarkMode = dark
                }
            }
        }

        splashScreen.setKeepOnScreenCondition { startDestination == null }

        setContent {
            JikanHubTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    startDestination?.let { destination ->
                        JikanNavHost(startDestination = destination)
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(permission), 1)
            }
        }
    }
}
