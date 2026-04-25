package com.jikanhub.app

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
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var startDestination by mutableStateOf<String?>(null)

        lifecycleScope.launch {
            startDestination = if (authRepository.isLoggedIn()) "dashboard" else "login"
        }

        splashScreen.setKeepOnScreenCondition { startDestination == null }

        setContent {
            JikanHubTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    startDestination?.let { destination ->
                        JikanNavHost(startDestination = destination)
                    }
                }
            }
        }
    }
}
