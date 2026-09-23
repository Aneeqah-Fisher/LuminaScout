package com.example.luminascout.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AmberGold,
    onPrimary = MidnightNavy,
    secondary = SoftGold,
    onSecondary = MidnightNavy,
    background = MidnightNavy,
    onBackground = TextPrimary,
    surface = DarkNavySurface,
    onSurface = TextPrimary,
    error = ErrorRed,
    onError = TextPrimary
)

@Composable
fun LuminaScoutTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}