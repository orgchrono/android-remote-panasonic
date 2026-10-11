package com.vhs.digacommander.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DigaBlueLight,
    onPrimary = Color.Black,
    secondary = DigaGreen,
    onSecondary = Color.White,
    tertiary = DigaAmber,
    background = DigaDarkBackground,
    surface = DigaSurface,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = DigaSurfaceVariant,
    onSurfaceVariant = Color(0xFFCFD8DC)
)

@Composable
fun PanasonicDigaCommanderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // For DIGA Commander we default to dark studio deck theme
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
