package com.jikanhub.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Raw Dark colors
val DarkSurface = Color(0xFF0F1729)
val DarkSurfaceVariant = Color(0xFF1A2340)
val DarkSurfaceContainer = Color(0xFF141D33)
val DarkSurfaceBright = Color(0xFF212B47)

val DarkOnSurface = Color(0xFFE8EAF0)
val DarkOnSurfaceVariant = Color(0xFF8B92A8)

val DarkPriorityHigh = Color(0xFFC75B39)       
val DarkPriorityMedium = Color(0xFFD4A843)     
val DarkPriorityLow = Color(0xFF5B8FA8)        

val DarkAccent = Color(0xFFC75B39)             
val DarkAccentVariant = Color(0xFFE07050)

// Raw Light colors (Modern Minimalist / Snow)
val LightSurface = Color(0xFFF8F9FA)       
val LightSurfaceVariant = Color(0xFFF1F3F5) 
val LightSurfaceContainer = Color(0xFFE9ECEF) 
val LightSurfaceBright = Color(0xFFFFFFFF)  
val LightOnSurface = Color(0xFF1A1A1A)     
val LightOnSurfaceVariant = Color(0xFF5F6368) 
val LightOutline = Color(0xFFDEE2E6)

val LightPriorityHigh = Color(0xFFD94111) 
val LightPriorityMedium = Color(0xFFF59F00) 
val LightPriorityLow = Color(0xFF1098AD) 
val LightAccent = Color(0xFFD94111)
val LightAccentVariant = Color(0xFFF76707)

// Status colors (same for both)
val Completed = Color(0xFF4A9E7A)          
val Postponed = Color(0xFF9E7A4A)          

// Dynamic Color Proxies (Resolve to active theme)
val JikanSurface: Color
    @Composable get() = MaterialTheme.colorScheme.surface

val JikanSurfaceVariant: Color
    @Composable get() = MaterialTheme.colorScheme.surfaceVariant

val JikanSurfaceContainer: Color
    @Composable get() = MaterialTheme.colorScheme.surfaceContainer

val JikanSurfaceBright: Color
    @Composable get() = if (LocalIsDarkTheme.current) DarkSurfaceBright else LightSurfaceBright

val JikanOnSurface: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface

val JikanOnSurfaceVariant: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

val JikanPriorityHigh: Color
    @Composable get() = MaterialTheme.colorScheme.error

val JikanPriorityMedium: Color
    @Composable get() = MaterialTheme.colorScheme.tertiary

val JikanPriorityLow: Color
    @Composable get() = MaterialTheme.colorScheme.secondary

val JikanAccent: Color
    @Composable get() = MaterialTheme.colorScheme.primary

val JikanAccentVariant: Color
    @Composable get() = if (LocalIsDarkTheme.current) DarkAccentVariant else LightAccentVariant
