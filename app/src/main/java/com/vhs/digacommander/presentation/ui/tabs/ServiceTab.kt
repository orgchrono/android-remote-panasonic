package com.vhs.digacommander.presentation.ui.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.vhs.digacommander.data.catalog.PanasonicDigaCatalog
import com.vhs.digacommander.data.model.MacroCommand
import com.vhs.digacommander.presentation.constant.DigaUiDimensions
import com.vhs.digacommander.presentation.constant.DigaUiStrings
import com.vhs.digacommander.presentation.theme.*

/**
 * Service & Bench Diagnostics tab for Panasonic DIGA recorders.
 * Provides optical pickup hour meter reading, EEPROM error logs,
 * HDD surface tests, and mechanical calibration routines.
 */
@Composable
fun ServiceTab(
    onRunMacro: (commands: List<MacroCommand>) -> Unit,
    onSendCommand: (label: String, command: Int, isVcr: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(DigaUiDimensions.PaddingScreen),
        verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingScreenSections),
    ) {
        // --- SERVICE MODE ENTRY GUIDE CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface),
        ) {
            Column(modifier = Modifier.padding(DigaUiDimensions.PaddingCardInner)) {
                Text(
                    text = DigaUiStrings.SERVICE_GUIDE_TITLE,
                    color = DigaBlueLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = DigaUiDimensions.TextSizeCardHeader,
                )
                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingButtons))
                Text(
                    text = DigaUiStrings.SERVICE_GUIDE_BODY,
                    color = Color.LightGray,
                    fontSize = DigaUiDimensions.TextSizeButtonStandard,
                    lineHeight = DigaUiDimensions.LineHeightBody,
                )
            }
        }

        // --- SECTION 1: OPTICAL PICKUP & LASER HOUR COUNTER ---
        Text(
            text = DigaUiStrings.SERVICE_SECTION_OPTICAL,
            style = MaterialTheme.typography.labelSmall,
            color = DigaBlueLight,
            fontWeight = FontWeight.Bold,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingCards),
        ) {
            ServiceActionCard(
                title = DigaUiStrings.SERVICE_LASER_HOURS_TITLE,
                description = DigaUiStrings.SERVICE_LASER_HOURS_DESC,
                buttonText = DigaUiStrings.SERVICE_LASER_HOURS_BTN,
                containerColor = DigaSurface,
                accentColor = DigaBlue,
                modifier = Modifier.weight(1f),
                onClick = {
                    onRunMacro(PanasonicDigaCatalog.Macros.LASER_HOURS)
                },
            )

            ServiceActionCard(
                title = DigaUiStrings.SERVICE_RAM_ERROR_TITLE,
                description = DigaUiStrings.SERVICE_RAM_ERROR_DESC,
                buttonText = DigaUiStrings.SERVICE_RAM_ERROR_BTN,
                containerColor = DigaSurface,
                accentColor = DigaBlue,
                modifier = Modifier.weight(1f),
                onClick = {
                    onRunMacro(PanasonicDigaCatalog.Macros.RAM_DRIVE_LAST_ERROR)
                },
            )
        }

        // --- SECTION 2: EEPROM FAULT HISTORY & SELF-DIAGNOSTICS ---
        Text(
            text = DigaUiStrings.SERVICE_SECTION_EEPROM,
            style = MaterialTheme.typography.labelSmall,
            color = DigaAmber,
            fontWeight = FontWeight.Bold,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface),
        ) {
            Column(modifier = Modifier.padding(DigaUiDimensions.PaddingCardInner)) {
                Text(
                    text = DigaUiStrings.SERVICE_EEPROM_CARD_TITLE,
                    color = DigaAmber,
                    fontWeight = FontWeight.Bold,
                    fontSize = DigaUiDimensions.TextSizeCardHeader,
                )
                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingTight))
                Text(
                    text = DigaUiStrings.SERVICE_EEPROM_CARD_DESC,
                    color = Color.LightGray,
                    fontSize = DigaUiDimensions.TextSizeButtonStandard,
                )
                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingCards))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingRows),
                ) {
                    Button(
                        onClick = {
                            onRunMacro(PanasonicDigaCatalog.Macros.ERROR_HISTORY_LATEST)
                        },
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.SERVICE_LATEST_ERROR_BTN,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = DigaUiDimensions.TextSizeButtonStandard,
                        )
                    }

                    Button(
                        onClick = {
                            onRunMacro(PanasonicDigaCatalog.Macros.CLEAR_ERROR_F00)
                        },
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.SERVICE_CLEAR_ERROR_BTN,
                            fontSize = DigaUiDimensions.TextSizeButtonStandard,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(DigaUiDimensions.PaddingCardCompact))
                HorizontalDivider(color = DigaSurfaceVariant)
                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingCards))

                Text(
                    text = DigaUiStrings.SERVICE_SLOTS_LABEL,
                    color = Color.Gray,
                    fontSize = DigaUiDimensions.TextSizeDescription,
                )
                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingButtons))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
                ) {
                    val slots =
                        listOf(
                            "Slot 1" to PanasonicDigaCatalog.Macros.ERROR_HISTORY_SLOT_1,
                            "Slot 2" to PanasonicDigaCatalog.Macros.ERROR_HISTORY_SLOT_2,
                            "Slot 3" to PanasonicDigaCatalog.Macros.ERROR_HISTORY_SLOT_3,
                            "Slot 4" to PanasonicDigaCatalog.Macros.ERROR_HISTORY_SLOT_4,
                            "Slot 5" to PanasonicDigaCatalog.Macros.ERROR_HISTORY_SLOT_5,
                        )
                    slots.forEach { (label, macro) ->
                        Button(
                            onClick = { onRunMacro(macro) },
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .height(DigaUiDimensions.HeightActionCardButton),
                            contentPadding = PaddingValues(DigaUiDimensions.PaddingZero),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        ) {
                            Text(label, fontSize = DigaUiDimensions.TextSizeButtonCompact)
                        }
                    }
                }
            }
        }

        // --- SECTION 3: HARDWARE DIAGNOSTICS & MECHANICAL CYCLES ---
        Text(
            text = DigaUiStrings.SERVICE_SECTION_HARDWARE,
            style = MaterialTheme.typography.labelSmall,
            color = DigaGreen,
            fontWeight = FontWeight.Bold,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingCards),
        ) {
            ServiceActionCard(
                title = DigaUiStrings.SERVICE_HDD_TEST_TITLE,
                description = DigaUiStrings.SERVICE_HDD_TEST_DESC,
                buttonText = DigaUiStrings.SERVICE_HDD_TEST_BTN,
                containerColor = DigaSurface,
                accentColor = DigaGreen,
                modifier = Modifier.weight(1f),
                onClick = {
                    onRunMacro(PanasonicDigaCatalog.Macros.HDD_SURFACE_TEST)
                },
            )

            ServiceActionCard(
                title = DigaUiStrings.SERVICE_VFD_TEST_TITLE,
                description = DigaUiStrings.SERVICE_VFD_TEST_DESC,
                buttonText = DigaUiStrings.SERVICE_VFD_TEST_BTN,
                containerColor = DigaSurface,
                accentColor = DigaGreen,
                modifier = Modifier.weight(1f),
                onClick = {
                    onRunMacro(PanasonicDigaCatalog.Macros.VFD_SEGMENTS_TEST)
                },
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingCards),
        ) {
            ServiceActionCard(
                title = DigaUiStrings.SERVICE_TRAY_TEST_TITLE,
                description = DigaUiStrings.SERVICE_TRAY_TEST_DESC,
                buttonText = DigaUiStrings.SERVICE_TRAY_TEST_BTN,
                containerColor = DigaSurface,
                accentColor = DigaSurfaceVariant,
                modifier = Modifier.weight(1f),
                onClick = {
                    onRunMacro(PanasonicDigaCatalog.Macros.TRAY_CYCLE_TEST)
                },
            )

            ServiceActionCard(
                title = DigaUiStrings.SERVICE_VIDEO_IO_TITLE,
                description = DigaUiStrings.SERVICE_VIDEO_IO_DESC,
                buttonText = DigaUiStrings.SERVICE_VIDEO_IO_BTN,
                containerColor = DigaSurface,
                accentColor = DigaSurfaceVariant,
                modifier = Modifier.weight(1f),
                onClick = {
                    onRunMacro(PanasonicDigaCatalog.Macros.VIDEO_IO_TEST_81)
                },
            )
        }

        // --- SAFE SERVICE MODE EXIT ---
        Button(
            onClick = { onSendCommand(DigaUiStrings.SERVICE_EXIT_BTN, PanasonicDigaCatalog.OBC.POWER, false) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(DigaUiDimensions.HeightStandardButton),
            colors = ButtonDefaults.buttonColors(containerColor = DigaRed),
            shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
        ) {
            Text(DigaUiStrings.SERVICE_EXIT_BTN, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Reusable diagnostic action card for service bench commands.
 */
@Composable
fun ServiceActionCard(
    title: String,
    description: String,
    buttonText: String,
    containerColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(modifier = Modifier.padding(DigaUiDimensions.PaddingCardCompact)) {
            Text(
                text = title,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = DigaUiDimensions.TextSizeCardHeader,
            )
            Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingTight))
            Text(
                text = description,
                color = Color.LightGray,
                fontSize = DigaUiDimensions.TextSizeDescription,
                lineHeight = DigaUiDimensions.LineHeightCompact,
                minLines = 3,
            )
            Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingCards))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
            ) {
                Text(buttonText, fontWeight = FontWeight.Medium, fontSize = DigaUiDimensions.TextSizeButtonStandard)
            }
        }
    }
}
