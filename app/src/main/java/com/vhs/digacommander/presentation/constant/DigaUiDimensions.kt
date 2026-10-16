package com.vhs.digacommander.presentation.constant

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Single Source of Truth (SSOT) for Jetpack Compose UI dimensions, paddings,
 * corner radiuses, button heights, and font sizing.
 *
 * Rules: no body text below 12 sp, no touch target below 44 dp (48 dp for primary keys).
 */
object DigaUiDimensions {
    // Padding Values
    val PaddingScreen: Dp = 16.dp
    val PaddingCardInner: Dp = 16.dp
    val PaddingCardCompact: Dp = 14.dp
    val PaddingHorizontalDefault: Dp = 16.dp
    val PaddingVerticalDefault: Dp = 8.dp
    val PaddingCompactH: Dp = 8.dp
    val PaddingCompactV: Dp = 4.dp
    val PaddingBadgeH: Dp = 10.dp
    val PaddingBadgeV: Dp = 4.dp
    val PaddingProfileButtonH: Dp = 4.dp
    val PaddingModeButtonH: Dp = 6.dp
    val PaddingAbortButtonH: Dp = 14.dp
    val PaddingKeyH: Dp = 8.dp
    val PaddingKeyV: Dp = 6.dp
    val PaddingZero: Dp = 0.dp

    // Spacing between elements
    val SpacingScreenSections: Dp = 16.dp
    val SpacingSections: Dp = 16.dp
    val SpacingCards: Dp = 12.dp
    val SpacingRows: Dp = 10.dp
    val SpacingButtons: Dp = 8.dp
    val SpacingTight: Dp = 6.dp
    val SpacingMicro: Dp = 2.dp

    // Heights & Sizes
    val HeightStandardButton: Dp = 52.dp
    val HeightKey: Dp = 52.dp
    val HeightKeypadButton: Dp = 56.dp
    val HeightActionCardButton: Dp = 48.dp
    val HeightProfileButton: Dp = 48.dp
    val HeightModeButton: Dp = 48.dp
    val HeightMiniButton: Dp = 44.dp
    val HeightProgressBar: Dp = 8.dp
    val SizeDpadOkButton: Dp = 88.dp
    val WidthDpadUpDown: Dp = 112.dp
    val HeightDpadUpDown: Dp = 56.dp
    val WidthDpadLeftRight: Dp = 88.dp
    val HeightDpadLeftRight: Dp = 64.dp
    val SizeDpadOkWheel: Dp = 68.dp
    val WidthDpadUpDownWheel: Dp = 96.dp
    val HeightDpadUpDownWheel: Dp = 44.dp
    val WidthDpadLeftRightWheel: Dp = 48.dp
    val HeightDpadLeftRightWheel: Dp = 68.dp
    val WidthDpadFlankButton: Dp = 60.dp
    val HeightDpadFlankButton: Dp = 76.dp
    val SpacingDpadGap: Dp = 6.dp
    val SpacingDpadFlankGap: Dp = 8.dp
    val SizeKeyIcon: Dp = 24.dp
    val SizeKeyIconCompact: Dp = 20.dp
    val SizeStatusDot: Dp = 8.dp
    val SizeSectionIcon: Dp = 20.dp
    val HeightVfdMin: Dp = 44.dp
    val HeightBannerCompact: Dp = 38.dp

    // Adaptive layout
    val BreakpointWide: Dp = 840.dp
    val MaxContentWidth: Dp = 1280.dp
    val WidthNavigationRail: Dp = 88.dp

    // Border Widths
    val BorderWidthStandard: Dp = 2.dp
    val BorderWidthHairline: Dp = 1.dp

    // Corner Radiuses
    val RadiusCard: Dp = 16.dp
    val RadiusButton: Dp = 12.dp
    val RadiusButtonCompact: Dp = 10.dp
    val RadiusBadge: Dp = 12.dp
    val RadiusDpadOuterArc: Dp = 28.dp
    val RadiusDpadInnerArc: Dp = 8.dp
    val RadiusDpadFlankOuter: Dp = 18.dp
    val RadiusDpadFlankInner: Dp = 8.dp

    // Font Sizes
    val TextSizeTitle: TextUnit = 20.sp
    val TextSizeKeypadDigit: TextUnit = 22.sp
    val TextSizeDpadIcon: TextUnit = 22.sp
    val TextSizeDpadFlank: TextUnit = 11.sp
    val TextSizeNavIcon: TextUnit = 24.sp
    val TextSizeHeading: TextUnit = 17.sp
    val TextSizeDpadOk: TextUnit = 18.sp
    val TextSizeVfdDisplayMain: TextUnit = 24.sp
    val TextSizeVfdMain: TextUnit = 16.sp
    val TextSizeBody: TextUnit = 14.sp
    val TextSizeCardHeader: TextUnit = 15.sp
    val TextSizeTransportSymbol: TextUnit = 15.sp
    val TextSizeVfdDisplaySub: TextUnit = 14.sp
    val TextSizeButtonStandard: TextUnit = 14.sp
    val TextSizeVfdSub: TextUnit = 13.sp
    val TextSizeButtonCompact: TextUnit = 14.sp
    val TextSizeSectionLabel: TextUnit = 12.sp
    val TextSizeDescription: TextUnit = 13.sp
    val TextSizeVfdStatus: TextUnit = 12.sp
    val TextSizeButtonMini: TextUnit = 13.sp
    val TextSizeProfileTab: TextUnit = 12.sp
    val TextSizeKey: TextUnit = 14.sp

    // Letter & Line Spacing
    val LetterSpacingVfd: TextUnit = 1.sp
    val LineHeightBody: TextUnit = 20.sp
    val LineHeightCardText: TextUnit = 19.sp
    val LineHeightCompact: TextUnit = 18.sp
    val LineHeightButtonTwoLine: TextUnit = 17.sp
}
