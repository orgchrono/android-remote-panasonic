package com.vhs.digacommander.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vhs.digacommander.presentation.constant.DigaUiDimensions
import com.vhs.digacommander.presentation.constant.DigaUiStrings
import com.vhs.digacommander.presentation.theme.DigaBlueLight
import com.vhs.digacommander.presentation.theme.DigaStatusError
import com.vhs.digacommander.presentation.theme.DigaStatusOk
import com.vhs.digacommander.presentation.theme.DigaStatusWarn
import com.vhs.digacommander.presentation.theme.DigaSurfaceVariant
import com.vhs.digacommander.presentation.theme.VfdBackground
import com.vhs.digacommander.presentation.theme.VfdDim
import com.vhs.digacommander.presentation.theme.VfdGlow
import com.vhs.digacommander.presentation.theme.VfdText
import com.vhs.digacommander.presentation.viewmodel.IrLinkStatus

/**
 * Colour that communicates the IR link health at a glance.
 */
fun IrLinkStatus.indicatorColor(): Color =
    when (this) {
        IrLinkStatus.ONLINE -> DigaStatusOk
        IrLinkStatus.ADAPTED -> DigaStatusWarn
        IrLinkStatus.NO_HARDWARE -> DigaStatusError
    }

/**
 * Single-line Vacuum Fluorescent Display (VFD) status bar.
 * Combines the IR emitter status indicator, fluorescent command telemetry, and active deck/channel
 * badge into one compact horizontal bar, adapting cleanly on narrow smartphone screens.
 */
@Composable
fun VfdDisplay(
    mainText: String,
    subText: String,
    irStatusText: String,
    irLinkStatus: IrLinkStatus,
    badgeText: String = "",
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact)
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val showSubText = screenWidth >= DigaUiDimensions.BreakpointTabletCards
    val showFullBadge = screenWidth >= DigaUiDimensions.BreakpointPhoneCompact

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = DigaUiDimensions.HeightVfdMin)
                .background(VfdBackground, shape)
                .border(DigaUiDimensions.BorderWidthHairline, VfdDim, shape)
                .padding(
                    horizontal = DigaUiDimensions.PaddingCardCompact,
                    vertical = DigaUiDimensions.PaddingCompactV,
                ),
        horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingRows),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left: IR Link Status Dot + Carrier Label
        Row(
            horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingTight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(DigaUiDimensions.SizeStatusDot)
                        .background(irLinkStatus.indicatorColor(), CircleShape),
            )
            Text(
                text = irStatusText,
                color = irLinkStatus.indicatorColor(),
                fontSize = DigaUiDimensions.TextSizeVfdStatus,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // Center: Single-line VFD Fluorescent Telemetry Readout
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .semantics { liveRegion = LiveRegionMode.Polite },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = mainText,
                color = VfdGlow,
                fontSize = DigaUiDimensions.TextSizeVfdMain,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = DigaUiDimensions.LetterSpacingVfd,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (showSubText && subText.isNotBlank()) {
                Text(
                    text = "${DigaUiStrings.VFD_SEPARATOR}$subText",
                    color = VfdText,
                    fontSize = DigaUiDimensions.TextSizeVfdSub,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // Right: Active Deck & Channel Badge
        if (showFullBadge && badgeText.isNotBlank()) {
            Surface(
                shape = RoundedCornerShape(DigaUiDimensions.RadiusBadge),
                color = DigaSurfaceVariant,
            ) {
                Text(
                    text = badgeText,
                    modifier =
                        Modifier.padding(
                            horizontal = DigaUiDimensions.PaddingBadgeH,
                            vertical = DigaUiDimensions.SpacingMicro,
                        ),
                    fontSize = DigaUiDimensions.TextSizeVfdStatus,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = DigaBlueLight,
                    maxLines = 1,
                )
            }
        }
    }
}
