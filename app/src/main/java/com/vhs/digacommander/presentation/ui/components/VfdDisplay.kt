package com.vhs.digacommander.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.vhs.digacommander.presentation.constant.DigaUiDimensions
import com.vhs.digacommander.presentation.constant.DigaUiStrings
import com.vhs.digacommander.presentation.theme.VfdBackground
import com.vhs.digacommander.presentation.theme.VfdDim
import com.vhs.digacommander.presentation.theme.VfdGlow
import com.vhs.digacommander.presentation.theme.VfdText

/**
 * Retro Vacuum Fluorescent Display (VFD) simulation component.
 */
@Composable
fun VfdDisplay(
    mainText: String,
    subText: String,
    irStatusText: String,
    headerTitle: String = DigaUiStrings.VFD_HEADER_TITLE,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(VfdBackground, RoundedCornerShape(DigaUiDimensions.RadiusButton))
                .border(DigaUiDimensions.BorderWidthStandard, VfdDim, RoundedCornerShape(DigaUiDimensions.RadiusButton))
                .padding(DigaUiDimensions.PaddingCardCompact),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = headerTitle,
                    color = VfdDim,
                    fontSize = DigaUiDimensions.TextSizeVfdStatus,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = irStatusText,
                    color = if (irStatusText.contains(DigaUiStrings.VFD_ONLINE_KEYWORD)) VfdGlow else Color(0xFFFFB74D),
                    fontSize = DigaUiDimensions.TextSizeVfdStatus,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingButtons))

            Text(
                text = mainText,
                color = VfdGlow,
                fontSize = DigaUiDimensions.TextSizeVfdDisplayMain,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = DigaUiDimensions.LetterSpacingVfd,
            )

            Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingTight))

            Text(
                text = subText,
                color = VfdText,
                fontSize = DigaUiDimensions.TextSizeVfdDisplaySub,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}
