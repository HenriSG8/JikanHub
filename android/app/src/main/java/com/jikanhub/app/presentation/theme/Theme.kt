package com.jikanhub.app.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppTheme {
    LIGHT, DARK, VITORIA, BAHIA
}

val LocalAppTheme = staticCompositionLocalOf { AppTheme.DARK }

private val DarkColorScheme = darkColorScheme(
    primary = DarkAccent,
    onPrimary = DarkOnSurface,
    secondary = DarkPriorityLow,
    tertiary = DarkPriorityMedium,
    background = DarkSurface,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    surfaceContainer = DarkSurfaceContainer,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = DarkPriorityHigh
)

private val LightColorScheme = lightColorScheme(
    primary = LightAccent,
    onPrimary = LightSurface,
    secondary = LightPriorityLow,
    tertiary = LightPriorityMedium,
    background = LightSurface,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    surfaceContainer = LightSurfaceContainer,
    onBackground = LightOnSurface,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    error = LightPriorityHigh
)

private val VitoriaColorScheme = darkColorScheme(
    primary = VitoriaAccent,
    onPrimary = VitoriaOnSurface,
    secondary = DarkPriorityLow,
    tertiary = DarkPriorityMedium,
    background = VitoriaSurface,
    surface = VitoriaSurface,
    surfaceVariant = VitoriaSurfaceVariant,
    surfaceContainer = VitoriaSurfaceContainer,
    onBackground = VitoriaOnSurface,
    onSurface = VitoriaOnSurface,
    onSurfaceVariant = VitoriaOnSurfaceVariant,
    error = DarkPriorityHigh
)

private val BahiaColorScheme = darkColorScheme(
    primary = BahiaAccent,
    onPrimary = BahiaOnSurface,
    secondary = DarkPriorityLow,
    tertiary = DarkPriorityMedium,
    background = BahiaSurface,
    surface = BahiaSurface,
    surfaceVariant = BahiaSurfaceVariant,
    surfaceContainer = BahiaSurfaceContainer,
    onBackground = BahiaOnSurface,
    onSurface = BahiaOnSurface,
    onSurfaceVariant = BahiaOnSurfaceVariant,
    error = DarkPriorityHigh
)

@Composable
fun JikanHubTheme(
    theme: AppTheme = AppTheme.DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        AppTheme.LIGHT -> LightColorScheme
        AppTheme.DARK -> DarkColorScheme
        AppTheme.VITORIA -> VitoriaColorScheme
        AppTheme.BAHIA -> BahiaColorScheme
    }

    val isLightStatusBar = theme == AppTheme.LIGHT

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surfaceContainer.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = isLightStatusBar
                isAppearanceLightNavigationBars = isLightStatusBar
            }
        }
    }

    CompositionLocalProvider(LocalAppTheme provides theme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = JikanTypography,
            content = content
        )
    }
}
