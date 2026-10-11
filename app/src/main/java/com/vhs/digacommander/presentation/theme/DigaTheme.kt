package com.vhs.digacommander.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Panasonic DIGA Theme Colors
val DigaDarkBackground = Color(0xFF121417)
val DigaSurface = Color(0xFF1E2228)
val DigaSurfaceVariant = Color(0xFF282D36)

val DigaBlue = Color(0xFF0288D1)
val DigaBlueLight = Color(0xFF4FC3F7)
val DigaRed = Color(0xFFD32F2F)
val DigaRedDark = Color(0xFFB71C1C)
val DigaGreen = Color(0xFF388E3C)
val DigaAmber = Color(0xFFF57F17)
val DigaPurple = Color(0xFF7B1FA2)

// Fluorescent VFD Display Colors
val VfdBackground = Color(0xFF051114)
val VfdGlow = Color(0xFF00E5FF)
val VfdDim = Color(0xFF004D40)
val VfdText = Color(0xFF80DEEA)

val DigaTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

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
fun PanasonicDigaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = DigaTypography,
        content = content
    )
}

@Composable
fun PanasonicDigaCommanderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    PanasonicDigaTheme(
        darkTheme = darkTheme,
        content = content
    )
}
