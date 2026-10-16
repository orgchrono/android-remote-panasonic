package com.vhs.digacommander.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.vhs.digacommander.presentation.constant.DigaUiDimensions
import com.vhs.digacommander.presentation.theme.DigaAmber
import com.vhs.digacommander.presentation.theme.DigaBlue
import com.vhs.digacommander.presentation.theme.DigaBlueLight
import com.vhs.digacommander.presentation.theme.DigaGreen
import com.vhs.digacommander.presentation.theme.DigaOutline
import com.vhs.digacommander.presentation.theme.DigaPurple
import com.vhs.digacommander.presentation.theme.DigaRed
import com.vhs.digacommander.presentation.theme.DigaRedDark
import com.vhs.digacommander.presentation.theme.DigaSurfaceVariant
import com.vhs.digacommander.presentation.theme.readableContentColor

/**
 * Semantic colour role of a key. The label colour is derived automatically for legibility,
 * so call sites never hand-pick black/white text.
 */
enum class KeyStyle(
    val container: Color,
) {
    Neutral(DigaSurfaceVariant),
    Primary(DigaBlue),
    Info(DigaBlueLight),
    Success(DigaGreen),
    Warning(DigaAmber),
    Danger(DigaRed),
    DangerDark(DigaRedDark),
    Accent(DigaPurple),
}

/**
 * Single reusable remote-control key: consistent size, shape, haptics and contrast.
 * Replaces the dozens of copy-pasted Button blocks (DRY).
 *
 * @param iconOnly Renders only the icon; [label] then becomes the accessibility description.
 */
@Composable
fun RemoteKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: KeyStyle = KeyStyle.Neutral,
    icon: ImageVector? = null,
    iconOnly: Boolean = false,
    height: Dp = DigaUiDimensions.HeightKey,
    fontSize: TextUnit = DigaUiDimensions.TextSizeKey,
    shape: Shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
    contentPadding: PaddingValues =
        PaddingValues(
            horizontal = DigaUiDimensions.PaddingKeyH,
            vertical = DigaUiDimensions.PaddingKeyV,
        ),
    isVerticalContent: Boolean = false,
) {
    val haptics = LocalHapticFeedback.current
    val container = style.container
    Button(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        modifier = modifier.heightIn(min = height),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = container,
                contentColor = readableContentColor(container),
            ),
        shape = shape,
        border = if (style == KeyStyle.Neutral) BorderStroke(DigaUiDimensions.BorderWidthHairline, DigaOutline) else null,
        contentPadding = contentPadding,
    ) {
        if (isVerticalContent) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingMicro, Alignment.CenterVertically),
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = if (iconOnly) label else null,
                        modifier = Modifier.size(DigaUiDimensions.SizeKeyIconCompact),
                    )
                }
                if (!iconOnly) {
                    Text(
                        text = label,
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingTight, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = if (iconOnly) label else null,
                        modifier = Modifier.size(DigaUiDimensions.SizeKeyIcon),
                    )
                }
                if (!iconOnly) {
                    Text(
                        text = label,
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = DigaUiDimensions.LineHeightButtonTwoLine,
                    )
                }
            }
        }
    }
}

/**
 * Horizontal single-choice selector (radio semantics) used for profile / channel / option pickers.
 */
@Composable
fun <T> ChoiceRow(
    options: List<T>,
    selected: T,
    labelOf: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    accentOf: (T) -> KeyStyle = { KeyStyle.Primary },
    height: Dp = DigaUiDimensions.HeightModeButton,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            RemoteKey(
                label = labelOf(option),
                onClick = { onSelect(option) },
                modifier =
                    Modifier
                        .weight(1f)
                        .semantics { this.selected = isSelected },
                style = if (isSelected) accentOf(option) else KeyStyle.Neutral,
                height = height,
                fontSize = DigaUiDimensions.TextSizeButtonCompact,
            )
        }
    }
}
