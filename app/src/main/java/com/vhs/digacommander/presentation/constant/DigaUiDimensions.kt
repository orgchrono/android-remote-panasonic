package com.vhs.digacommander.presentation.constant

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Single Source of Truth (SSOT) for Jetpack Compose UI dimensions, paddings,
 * corner radiuses, button heights, and font sizing.
 */
object DigaUiDimensions {
    // Padding Values
    val PaddingScreen: Dp = 16.dp
    val PaddingCardInner: Dp = 14.dp
    val PaddingCardCompact: Dp = 12.dp
    val PaddingHorizontalDefault: Dp = 16.dp
    val PaddingVerticalDefault: Dp = 8.dp
    val PaddingCompactH: Dp = 8.dp
    val PaddingCompactV: Dp = 4.dp
    val PaddingBadgeH: Dp = 8.dp
    val PaddingBadgeV: Dp = 4.dp
    val PaddingProfileButtonH: Dp = 2.dp
    val PaddingModeButtonH: Dp = 4.dp
    val PaddingAbortButtonH: Dp = 10.dp
    val PaddingZero: Dp = 0.dp

    // Spacing between elements
    val SpacingScreenSections: Dp = 16.dp
    val SpacingSections: Dp = 14.dp
    val SpacingCards: Dp = 10.dp
    val SpacingRows: Dp = 8.dp
    val SpacingButtons: Dp = 6.dp
    val SpacingTight: Dp = 4.dp
    val SpacingMicro: Dp = 2.dp

    // Heights & Sizes
    val HeightStandardButton: Dp = 48.dp
    val HeightKeypadButton: Dp = 46.dp
    val HeightActionCardButton: Dp = 36.dp
    val HeightProfileButton: Dp = 34.dp
    val HeightModeButton: Dp = 32.dp
    val HeightMiniButton: Dp = 28.dp
    val HeightProgressBar: Dp = 6.dp
    val SizeDpadOkButton: Dp = 72.dp
    val WidthDpadUpDown: Dp = 90.dp
    val HeightDpadUpDown: Dp = 48.dp
    val WidthDpadLeftRight: Dp = 72.dp
    val HeightDpadLeftRight: Dp = 56.dp

    // Border Widths
    val BorderWidthStandard: Dp = 2.dp

    // Corner Radiuses
    val RadiusCard: Dp = 12.dp
    val RadiusButton: Dp = 8.dp
    val RadiusButtonCompact: Dp = 6.dp
    val RadiusBadge: Dp = 12.dp

    // Font Sizes
    val TextSizeTitle: TextUnit = 18.sp
    val TextSizeKeypadDigit: TextUnit = 18.sp
    val TextSizeDpadIcon: TextUnit = 18.sp
    val TextSizeNavIcon: TextUnit = 18.sp
    val TextSizeHeading: TextUnit = 16.sp
    val TextSizeDpadOk: TextUnit = 16.sp
    val TextSizeVfdDisplayMain: TextUnit = 22.sp
    val TextSizeVfdMain: TextUnit = 15.sp
    val TextSizeBody: TextUnit = 13.sp
    val TextSizeCardHeader: TextUnit = 13.sp
    val TextSizeTransportSymbol: TextUnit = 13.sp
    val TextSizeVfdDisplaySub: TextUnit = 12.sp
    val TextSizeButtonStandard: TextUnit = 12.sp
    val TextSizeVfdSub: TextUnit = 11.sp
    val TextSizeButtonCompact: TextUnit = 11.sp
    val TextSizeSectionLabel: TextUnit = 11.sp
    val TextSizeDescription: TextUnit = 11.sp
    val TextSizeVfdStatus: TextUnit = 10.sp
    val TextSizeButtonMini: TextUnit = 10.sp
    val TextSizeProfileTab: TextUnit = 9.sp

    // Letter & Line Spacing
    val LetterSpacingVfd: TextUnit = 2.sp
    val LineHeightBody: TextUnit = 17.sp
    val LineHeightCardText: TextUnit = 16.sp
    val LineHeightCompact: TextUnit = 15.sp
    val LineHeightButtonTwoLine: TextUnit = 14.sp
}
