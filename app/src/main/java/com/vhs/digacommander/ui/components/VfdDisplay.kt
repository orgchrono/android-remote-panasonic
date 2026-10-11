package com.vhs.digacommander.ui.components

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vhs.digacommander.ui.theme.VfdBackground
import com.vhs.digacommander.ui.theme.VfdDim
import com.vhs.digacommander.ui.theme.VfdGlow
import com.vhs.digacommander.ui.theme.VfdText

@Composable
fun VfdDisplay(
    mainText: String,
    subText: String,
    irStatusText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(VfdBackground, RoundedCornerShape(8.dp))
            .border(2.dp, VfdDim, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "PANASONIC DIGA DMR-EH55",
                    color = VfdDim,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = irStatusText,
                    color = if (irStatusText.contains("ONLINE")) VfdGlow else Color(0xFFFFB74D),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = mainText,
                color = VfdGlow,
                fontSize = 22.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subText,
                color = VfdText,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
