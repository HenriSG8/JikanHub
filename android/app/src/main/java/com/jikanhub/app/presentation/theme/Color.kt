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

val DarkPriorityHigh = Color(0xFFE03131)       
val DarkPriorityMedium = Color(0xFFFAB005)     
val DarkPriorityLow = Color(0xFF2F9E44)        

val DarkAccent = Color(0xFF4C6EF5)             
val DarkAccentVariant = Color(0xFF5C7CFA)

// Raw Light colors (Modern Minimalist / Snow)
val LightSurface = Color(0xFFF8F9FA)       
val LightSurfaceVariant = Color(0xFFF1F3F5) 
val LightSurfaceContainer = Color(0xFFE9ECEF) 
val LightSurfaceBright = Color(0xFFFFFFFF)  
val LightOnSurface = Color(0xFF1A1A1A)     
val LightOnSurfaceVariant = Color(0xFF5F6368) 
val LightOutline = Color(0xFFDEE2E6)

val LightPriorityHigh = Color(0xFFE03131) 
val LightPriorityMedium = Color(0xFFFAB005) 
val LightPriorityLow = Color(0xFF2F9E44) 
val LightAccent = Color(0xFF4C6EF5)
val LightAccentVariant = Color(0xFF5C7CFA)

// Status colors (same for both)
val Completed = Color(0xFF4A9E7A)          
val Postponed = Color(0xFF9E7A4A)          

// Raw Vitoria colors
val VitoriaSurface = Color(0xFF121212)
val VitoriaSurfaceVariant = Color(0xFF1E1E1E)
val VitoriaSurfaceContainer = Color(0xFF000000)
val VitoriaSurfaceBright = Color(0xFF2C2C2C)
val VitoriaOnSurface = Color(0xFFFFFFFF)
val VitoriaOnSurfaceVariant = Color(0xFFB3B3B3)
val VitoriaAccent = Color(0xFFD32F2F)
val VitoriaAccentVariant = Color(0xFFB71C1C)

// Raw Bahia colors
val BahiaSurface = Color(0xFF003366)
val BahiaSurfaceVariant = Color(0xFF004080)
val BahiaSurfaceContainer = Color(0xFF002244)
val BahiaSurfaceBright = Color(0xFF0059B3)
val BahiaOnSurface = Color(0xFFFFFFFF)
val BahiaOnSurfaceVariant = Color(0xFFB3D9FF)
val BahiaAccent = Color(0xFFD32F2F) // Red accent
val BahiaAccentVariant = Color(0xFFFFFFFF) // White accent variant

// Dynamic Color Proxies (Resolve to active theme)
val JikanSurface: Color
    @Composable get() = MaterialTheme.colorScheme.surface

val JikanSurfaceVariant: Color
    @Composable get() = MaterialTheme.colorScheme.surfaceVariant

val JikanSurfaceContainer: Color
    @Composable get() = MaterialTheme.colorScheme.surfaceContainer

val JikanSurfaceBright: Color
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.LIGHT -> LightSurfaceBright
        AppTheme.DARK -> DarkSurfaceBright
        AppTheme.VITORIA -> VitoriaSurfaceBright
        AppTheme.BAHIA -> BahiaSurfaceBright
    }

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
    @Composable get() = when (LocalAppTheme.current) {
        AppTheme.LIGHT -> LightAccentVariant
        AppTheme.DARK -> DarkAccentVariant
        AppTheme.VITORIA -> VitoriaAccentVariant
        AppTheme.BAHIA -> BahiaAccentVariant
    }
