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
    val PaddingScreen: Dp = 14.dp
    val PaddingCardInner: Dp = 14.dp
    val PaddingCardCompact: Dp = 12.dp
    val PaddingHorizontalDefault: Dp = 14.dp
    val PaddingVerticalDefault: Dp = 8.dp
    val PaddingCompactH: Dp = 8.dp
    val PaddingCompactV: Dp = 4.dp
    val PaddingBadgeH: Dp = 10.dp
    val PaddingBadgeV: Dp = 4.dp
    val PaddingProfileButtonH: Dp = 4.dp
    val PaddingModeButtonH: Dp = 6.dp
    val PaddingAbortButtonH: Dp = 14.dp
    val PaddingKeyH: Dp = 6.dp
    val PaddingKeyV: Dp = 4.dp
    val PaddingZero: Dp = 0.dp

    // Spacing between elements
    val SpacingScreenSections: Dp = 12.dp
    val SpacingSections: Dp = 12.dp
    val SpacingCards: Dp = 12.dp
    val SpacingRows: Dp = 8.dp
    val SpacingButtons: Dp = 8.dp
    val SpacingTight: Dp = 6.dp
    val SpacingMicro: Dp = 2.dp

    // Heights & Sizes
    // Note: HeightDpadCluster = 3 * HeightKey + 2 * SpacingRows (3 * 50 + 2 * 8 = 166.dp)
    // and HeightFlankDpadButton = (HeightDpadCluster - SpacingRows) / 2 (158 / 2 = 79.dp)
    // so the 3-card Left Column (3 + 5 + 1 = 9 rows) matches the 3-card Right Column (3 + 2 + 4 = 9 rows) to 0.0 dp.
    val HeightStandardButton: Dp = 50.dp
    val HeightKey: Dp = 50.dp
    val HeightKeypadButton: Dp = 50.dp
    val HeightActionCardButton: Dp = 48.dp
    val HeightProfileButton: Dp = 48.dp
    val HeightModeButton: Dp = 50.dp
    val HeightMiniButton: Dp = 44.dp
    val HeightProgressBar: Dp = 8.dp
    val SizeTargetDpad: Dp = 166.dp
    val SizeTargetDpadCompact: Dp = 152.dp
    val SizeTargetOk: Dp = 62.dp
    val SizeTargetOkCompact: Dp = 56.dp
    val HeightFlankDpadButton: Dp = 79.dp
    val HeightFlankDpadButtonCompact: Dp = 72.dp
    val RadiusTargetFlankOuter: Dp = 16.dp
    val RadiusTargetFlankInner: Dp = 8.dp
    val SpacingDpadGap: Dp = 4.dp
    val SpacingDpadFlankGap: Dp = 8.dp
    val SizeKeyIcon: Dp = 20.dp
    val SizeKeyIconCompact: Dp = 18.dp
    val SizeDpadArrowIcon: Dp = 26.dp
    val SizeStatusDot: Dp = 8.dp
    val SizeSectionIcon: Dp = 18.dp
    val HeightVfdMin: Dp = 44.dp
    val HeightBannerCompact: Dp = 38.dp

    // Adaptive layout
    val BreakpointWide: Dp = 800.dp
    val BreakpointPhoneCompact: Dp = 380.dp
    val BreakpointTabletCards: Dp = 560.dp
    val MaxContentWidth: Dp = 1360.dp
    val WidthNavigationRail: Dp = 84.dp

    // Border Widths
    val BorderWidthStandard: Dp = 2.dp
    val BorderWidthHairline: Dp = 1.dp

    // Corner Radiuses
    val RadiusCard: Dp = 14.dp
    val RadiusButton: Dp = 10.dp
    val RadiusButtonCompact: Dp = 10.dp
    val RadiusBadge: Dp = 10.dp

    // Font Sizes
    val TextSizeTitle: TextUnit = 20.sp
    val TextSizeKeypadDigit: TextUnit = 20.sp
    val TextSizeDpadIcon: TextUnit = 22.sp
    val TextSizeDpadFlank: TextUnit = 13.sp
    val TextSizeNavIcon: TextUnit = 24.sp
    val TextSizeHeading: TextUnit = 16.sp
    val TextSizeDpadOk: TextUnit = 17.sp
    val TextSizeVfdDisplayMain: TextUnit = 22.sp
    val TextSizeVfdMain: TextUnit = 15.sp
    val TextSizeBody: TextUnit = 14.sp
    val TextSizeCardHeader: TextUnit = 14.sp
    val TextSizeTransportSymbol: TextUnit = 15.sp
    val TextSizeVfdDisplaySub: TextUnit = 13.sp
    val TextSizeButtonStandard: TextUnit = 13.sp
    val TextSizeVfdSub: TextUnit = 12.sp
    val TextSizeButtonCompact: TextUnit = 13.sp
    val TextSizeSectionLabel: TextUnit = 12.sp
    val TextSizeDescription: TextUnit = 13.sp
    val TextSizeVfdStatus: TextUnit = 12.sp
    val TextSizeButtonMini: TextUnit = 12.sp
    val TextSizeProfileTab: TextUnit = 12.sp
    val TextSizeKey: TextUnit = 13.sp

    // Letter & Line Spacing
    val LetterSpacingVfd: TextUnit = 1.sp
    val LineHeightBody: TextUnit = 20.sp
    val LineHeightCardText: TextUnit = 19.sp
    val LineHeightCompact: TextUnit = 18.sp
    val LineHeightButtonTwoLine: TextUnit = 15.sp
}
