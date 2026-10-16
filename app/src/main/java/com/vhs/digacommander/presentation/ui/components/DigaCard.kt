package com.vhs.digacommander.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.vhs.digacommander.presentation.constant.DigaUiDimensions
import com.vhs.digacommander.presentation.theme.DigaOutline
import com.vhs.digacommander.presentation.theme.DigaSurface
import com.vhs.digacommander.presentation.theme.DigaTextMuted
import com.vhs.digacommander.presentation.theme.DigaTextSecondary

/**
 * Standard titled card: one border, one radius, one header style across the entire app.
 *
 * @param accent Colour of the header text and icon (defaults to the muted label tone).
 */
@Composable
fun DigaCard(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accent: Color = DigaTextMuted,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DigaSurface),
        shape = RoundedCornerShape(DigaUiDimensions.RadiusCard),
        border = BorderStroke(DigaUiDimensions.BorderWidthHairline, DigaOutline),
    ) {
        Column(
            modifier = Modifier.padding(DigaUiDimensions.PaddingCardCompact),
            verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingRows),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(DigaUiDimensions.SizeSectionIcon),
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                        modifier = Modifier.semantics { heading() },
                    )
                }
                trailing?.invoke()
            }
            content()
        }
    }
}

/**
 * Body copy inside a card, using the secondary text tone with comfortable line height.
 */
@Composable
fun DigaBodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = DigaTextSecondary,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = DigaUiDimensions.TextSizeBody,
        lineHeight = DigaUiDimensions.LineHeightBody,
    )
}
