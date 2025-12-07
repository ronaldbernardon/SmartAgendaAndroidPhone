package com.smartagenda.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Couleurs du thème clair
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF667EEA),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8EAFF),
    onPrimaryContainer = Color(0xFF001C3B),
    
    secondary = Color(0xFF764BA2),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E5FF),
    onSecondaryContainer = Color(0xFF2D0057),
    
    tertiary = Color(0xFF27AE60),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD5F5E3),
    onTertiaryContainer = Color(0xFF003A0A),
    
    error = Color(0xFFE74C3C),
    onError = Color.White,
    errorContainer = Color(0xFFFFEDEA),
    onErrorContainer = Color(0xFF410002),
    
    background = Color(0xFFFBFBFE),
    onBackground = Color(0xFF1B1B1F),
    
    surface = Color(0xFFFBFBFE),
    onSurface = Color(0xFF1B1B1F),
    surfaceVariant = Color(0xFFE1E2EC),
    onSurfaceVariant = Color(0xFF44464F),
    
    outline = Color(0xFF74777F),
    outlineVariant = Color(0xFFC4C6D0)
)

// Couleurs du thème sombre
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF667EEA),
    onPrimary = Color(0xFF002F65),
    primaryContainer = Color(0xFF00468F),
    onPrimaryContainer = Color(0xFFD6E3FF),
    
    secondary = Color(0xFF764BA2),
    onSecondary = Color(0xFF48008D),
    secondaryContainer = Color(0xFF6600C6),
    onSecondaryContainer = Color(0xFFF3E5FF),
    
    tertiary = Color(0xFF27AE60),
    onTertiary = Color(0xFF003A0A),
    tertiaryContainer = Color(0xFF006D1A),
    onTertiaryContainer = Color(0xFFD5F5E3),
    
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    
    background = Color(0xFF1B1B1F),
    onBackground = Color(0xFFE3E2E6),
    
    surface = Color(0xFF1B1B1F),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF44464F),
    onSurfaceVariant = Color(0xFFC4C6D0),
    
    outline = Color(0xFF8E9099),
    outlineVariant = Color(0xFF44464F)
)

@Composable
fun SmartAgendaAndroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
