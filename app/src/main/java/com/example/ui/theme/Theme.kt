package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ConvictionPrimaryRed,
    onPrimary = Color.White,
    secondary = ConvictionGold,
    onSecondary = Color.Black,
    tertiary = ConvictionSecondaryRed,
    background = ConvictionBlack,
    onBackground = ConvictionTextPrimary,
    surface = ConvictionCharcoal,
    onSurface = ConvictionTextPrimary,
    surfaceVariant = ConvictionCardBg,
    onSurfaceVariant = ConvictionTextSecondary
)

@Composable
fun ConvictionTheme(
    darkTheme: Boolean = true, // Force dark mode to align with the premium intense vibe
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
