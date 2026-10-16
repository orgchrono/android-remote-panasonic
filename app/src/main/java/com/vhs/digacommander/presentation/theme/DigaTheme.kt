package com.vhs.digacommander.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// --- Surfaces (dark, three clearly separated elevation levels) ---------------------------------
val DigaDarkBackground = Color(0xFF0E1116)
val DigaSurface = Color(0xFF1A1F27)
val DigaSurfaceVariant = Color(0xFF2C3440)
val DigaOutline = Color(0xFF47536A)

// --- Text (all pairs verified >= 4.5:1 against DigaSurface) -----------------------------------
val DigaTextPrimary = Color(0xFFF2F5F8)
val DigaTextSecondary = Color(0xFFC3CEDA)
val DigaTextMuted = Color(0xFF9FB0C3)

// --- Accents (each is >= 4.5:1 against white or black label text, see contentColorFor) --------
val DigaBlue = Color(0xFF0277BD)
val DigaBlueLight = Color(0xFF4FC3F7)
val DigaRed = Color(0xFFC62828)
val DigaRedDark = Color(0xFFA11B1B)
val DigaGreen = Color(0xFF2E7D32)
val DigaAmber = Color(0xFFFFB300)
val DigaPurple = Color(0xFF7B1FA2)

// --- Link status -------------------------------------------------------------------------------
val DigaStatusOk = Color(0xFF69F0AE)
val DigaStatusWarn = Color(0xFFFFD54F)
val DigaStatusError = Color(0xFFFF8A80)

// --- Fluorescent VFD display -------------------------------------------------------------------
val VfdBackground = Color(0xFF041014)
val VfdGlow = Color(0xFF00E5FF)
val VfdDim = Color(0xFF2E7D74)
val VfdText = Color(0xFF9BE7F2)

val DigaTypography =
    Typography(
        bodyLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.25.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.2.sp,
            ),
        titleLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp,
            ),
        titleSmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.4.sp,
            ),
        labelLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                letterSpacing = 0.3.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.8.sp,
            ),
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = DigaBlueLight,
        onPrimary = Color.Black,
        secondary = DigaGreen,
        onSecondary = Color.White,
        tertiary = DigaAmber,
        onTertiary = Color.Black,
        error = DigaStatusError,
        onError = Color.Black,
        background = DigaDarkBackground,
        onBackground = DigaTextPrimary,
        surface = DigaSurface,
        onSurface = DigaTextPrimary,
        surfaceVariant = DigaSurfaceVariant,
        onSurfaceVariant = DigaTextSecondary,
        outline = DigaOutline,
        outlineVariant = DigaSurfaceVariant,
        surfaceContainer = DigaSurface,
        secondaryContainer = DigaSurfaceVariant,
        onSecondaryContainer = DigaTextPrimary,
    )

/**
 * Relative-luminance based pick of a readable label colour for a filled button.
 */
fun readableContentColor(background: Color): Color = if (background.luminance() > LUMINANCE_THRESHOLD) Color.Black else Color.White

private const val LUMINANCE_THRESHOLD = 0.36f

@Composable
fun PanasonicDigaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = DigaTypography,
        content = content,
    )
}

/**
 * The app is intentionally dark-only (it mimics a hardware panel used next to a CRT/TV), so the
 * system theme is not consulted.
 */
@Composable
fun PanasonicDigaCommanderTheme(content: @Composable () -> Unit) {
    PanasonicDigaTheme(content = content)
}
