package com.vhs.digacommander.presentation.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.vhs.digacommander.data.catalog.PanasonicDigaCatalog
import com.vhs.digacommander.data.model.MacroCommand
import com.vhs.digacommander.presentation.constant.DigaUiDimensions
import com.vhs.digacommander.presentation.constant.DigaUiStrings
import com.vhs.digacommander.presentation.theme.DigaAmber
import com.vhs.digacommander.presentation.theme.DigaBlue
import com.vhs.digacommander.presentation.theme.DigaBlueLight
import com.vhs.digacommander.presentation.theme.DigaGreen
import com.vhs.digacommander.presentation.theme.DigaOutline
import com.vhs.digacommander.presentation.theme.DigaStatusError
import com.vhs.digacommander.presentation.theme.DigaStatusOk
import com.vhs.digacommander.presentation.theme.DigaSurface
import com.vhs.digacommander.presentation.theme.DigaSurfaceVariant
import com.vhs.digacommander.presentation.theme.DigaTextPrimary
import com.vhs.digacommander.presentation.theme.DigaTextSecondary
import com.vhs.digacommander.presentation.ui.components.DigaBodyText
import com.vhs.digacommander.presentation.ui.components.DigaCard
import com.vhs.digacommander.presentation.ui.components.KeyStyle
import com.vhs.digacommander.presentation.ui.components.RemoteKey

/**
 * Legible text/icon tone for headers and labels that belong to a [KeyStyle] family. The raw key
 * containers are tuned for filled buttons, so on the dark card surface a lighter tone is used.
 */
internal fun KeyStyle.headerAccent(): Color =
    when (this) {
        KeyStyle.Primary, KeyStyle.Info -> DigaBlueLight
        KeyStyle.Success -> DigaStatusOk
        KeyStyle.Warning -> DigaAmber
        KeyStyle.Danger, KeyStyle.DangerDark -> DigaStatusError
        KeyStyle.Accent -> DigaTextPrimary
        KeyStyle.Neutral -> DigaTextSecondary
    }

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
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier =
                Modifier
                    .widthIn(max = DigaUiDimensions.MaxContentWidth)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(DigaUiDimensions.PaddingScreen),
            verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingScreenSections),
        ) {
            // --- SERVICE MODE ENTRY GUIDE CARD ---
            DigaCard(
                title = DigaUiStrings.SERVICE_GUIDE_TITLE,
                icon = Icons.Filled.Info,
                accent = DigaBlueLight,
            ) {
                DigaBodyText(text = DigaUiStrings.SERVICE_GUIDE_BODY)
            }

            // --- SECTION 1: OPTICAL PICKUP & LASER HOUR COUNTER ---
            ServiceSectionLabel(text = DigaUiStrings.SERVICE_SECTION_OPTICAL, color = KeyStyle.Primary.headerAccent())

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
            ServiceSectionLabel(text = DigaUiStrings.SERVICE_SECTION_EEPROM, color = KeyStyle.Warning.headerAccent())

            DigaCard(
                title = DigaUiStrings.SERVICE_EEPROM_CARD_TITLE,
                icon = Icons.Filled.MonitorHeart,
                accent = KeyStyle.Warning.headerAccent(),
            ) {
                DigaBodyText(text = DigaUiStrings.SERVICE_EEPROM_CARD_DESC)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingRows),
                ) {
                    RemoteKey(
                        label = DigaUiStrings.SERVICE_LATEST_ERROR_BTN,
                        onClick = {
                            onRunMacro(PanasonicDigaCatalog.Macros.ERROR_HISTORY_LATEST)
                        },
                        modifier = Modifier.weight(1f),
                        style = KeyStyle.Warning,
                    )

                    RemoteKey(
                        label = DigaUiStrings.SERVICE_CLEAR_ERROR_BTN,
                        onClick = {
                            onRunMacro(PanasonicDigaCatalog.Macros.CLEAR_ERROR_F00)
                        },
                        modifier = Modifier.weight(1f),
                        style = KeyStyle.Neutral,
                    )
                }

                HorizontalDivider(color = DigaOutline)

                DigaBodyText(text = DigaUiStrings.SERVICE_SLOTS_LABEL)

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
                        RemoteKey(
                            label = label,
                            onClick = { onRunMacro(macro) },
                            modifier = Modifier.weight(1f),
                            style = KeyStyle.Neutral,
                            height = DigaUiDimensions.HeightActionCardButton,
                        )
                    }
                }
            }

            // --- SECTION 3: HARDWARE DIAGNOSTICS & MECHANICAL CYCLES ---
            ServiceSectionLabel(text = DigaUiStrings.SERVICE_SECTION_HARDWARE, color = KeyStyle.Success.headerAccent())

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
            RemoteKey(
                label = DigaUiStrings.SERVICE_EXIT_BTN,
                onClick = { onSendCommand(DigaUiStrings.SERVICE_EXIT_BTN, PanasonicDigaCatalog.OBC.POWER, false) },
                modifier = Modifier.fillMaxWidth(),
                style = KeyStyle.Danger,
                height = DigaUiDimensions.HeightStandardButton,
            )
        }
    }
}

/**
 * Section heading shown above a group of cards (exposed to accessibility as a heading).
 */
@Composable
private fun ServiceSectionLabel(
    text: String,
    color: Color,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.semantics { heading() },
    )
}

/**
 * Reusable diagnostic action card for service bench commands.
 *
 * [accentColor] must be one of the [KeyStyle] container colours; it selects the button style and
 * the legible header tone. [containerColor] is kept for call-site compatibility: [DigaCard]
 * always uses the shared surface colour.
 */
@Suppress("UNUSED_PARAMETER")
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
    val style = KeyStyle.entries.firstOrNull { it.container == accentColor } ?: KeyStyle.Neutral
    DigaCard(
        title = title,
        modifier = modifier,
        icon = Icons.Filled.Build,
        accent = style.headerAccent(),
    ) {
        Text(
            text = description,
            color = DigaTextSecondary,
            fontSize = DigaUiDimensions.TextSizeDescription,
            lineHeight = DigaUiDimensions.LineHeightCompact,
            minLines = SERVICE_DESCRIPTION_MIN_LINES,
        )
        RemoteKey(
            label = buttonText,
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            style = style,
        )
    }
}

/** Minimum description lines so that side-by-side action cards keep aligned buttons. */
private const val SERVICE_DESCRIPTION_MIN_LINES = 3
