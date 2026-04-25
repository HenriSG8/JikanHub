package com.jikanhub.app.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = OnSurface,
    secondary = PriorityLow,
    tertiary = PriorityMedium,
    background = Surface,
    surface = Surface,
    surfaceVariant = SurfaceVariant,
    surfaceContainer = SurfaceContainer,
    onBackground = OnSurface,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    error = PriorityHigh
)

private val LightColorScheme = lightColorScheme(
    primary = Accent,
    onPrimary = LightSurface,
    secondary = PriorityLow,
    tertiary = PriorityMedium,
    background = LightSurface,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onBackground = LightOnSurface,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    error = PriorityHigh
)

@Composable
fun JikanHubTheme(
    darkTheme: Boolean = true, // Dark is default — Japanese aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surfaceContainer.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = JikanTypography,
        content = content
    )
}
