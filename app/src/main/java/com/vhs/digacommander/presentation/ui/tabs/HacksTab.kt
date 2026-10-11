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
import com.vhs.digacommander.data.model.RemoteMode
import com.vhs.digacommander.presentation.constant.DigaUiDimensions
import com.vhs.digacommander.presentation.constant.DigaUiStrings
import com.vhs.digacommander.presentation.theme.*

/**
 * Advanced Hacks, Multi-Region Unlock, and Automated Recovery tab.
 * Includes automated OSD NVRAM channel restoration (SET 2 / SET 3 -> SET 1),
 * DVD Region Free multi-region patch, Child Lock override, and TVGOS bypass.
 */
@Composable
fun HacksTab(
    activeMode: RemoteMode,
    showMultiRegionConfirm: Boolean,
    showResetConfirm: Boolean,
    showAutoRestoreSet2Confirm: Boolean,
    showAutoRestoreSet3Confirm: Boolean,
    onSetMultiRegionConfirmVisible: (Boolean) -> Unit,
    onSetResetConfirmVisible: (Boolean) -> Unit,
    onSetAutoRestoreSet2ConfirmVisible: (Boolean) -> Unit,
    onSetAutoRestoreSet3ConfirmVisible: (Boolean) -> Unit,
    onModeSelected: (RemoteMode) -> Unit,
    onRunMacro: (commands: List<MacroCommand>) -> Unit,
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
        // =========================================================================
        // 🔄 1. AUTOMATED OSD RECOVERY: RESTORE SET 2 / SET 3 BACK TO SET 1
        // =========================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface),
        ) {
            Column(modifier = Modifier.padding(DigaUiDimensions.PaddingCardInner)) {
                Text(
                    text = DigaUiStrings.HACKS_AUTO_RECOVERY_TITLE,
                    color = DigaBlueLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = DigaUiDimensions.TextSizeCardHeader,
                )

                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingButtons))

                Text(
                    text = DigaUiStrings.HACKS_AUTO_RECOVERY_DESC,
                    color = Color.LightGray,
                    fontSize = DigaUiDimensions.TextSizeButtonStandard,
                    lineHeight = DigaUiDimensions.LineHeightBody,
                )

                Spacer(modifier = Modifier.height(DigaUiDimensions.PaddingCardCompact))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingRows),
                ) {
                    Button(
                        onClick = { onSetAutoRestoreSet2ConfirmVisible(true) },
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(DigaUiDimensions.HeightStandardButton),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaGreen),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                    ) {
                        Text(
                            text = DigaUiStrings.HACKS_BTN_RESTORE_SET2,
                            fontWeight = FontWeight.Bold,
                            fontSize = DigaUiDimensions.TextSizeButtonCompact,
                            lineHeight = DigaUiDimensions.LineHeightButtonTwoLine,
                        )
                    }

                    Button(
                        onClick = { onSetAutoRestoreSet3ConfirmVisible(true) },
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(DigaUiDimensions.HeightStandardButton),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                    ) {
                        Text(
                            text = DigaUiStrings.HACKS_BTN_RESTORE_SET3,
                            fontWeight = FontWeight.Bold,
                            fontSize = DigaUiDimensions.TextSizeButtonCompact,
                            lineHeight = DigaUiDimensions.LineHeightButtonTwoLine,
                            color = Color.Black,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingCards))
                HorizontalDivider(color = DigaSurfaceVariant)
                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingCards))

                Text(
                    text = DigaUiStrings.HACKS_MANUAL_SYNC_LABEL,
                    color = Color.Gray,
                    fontSize = DigaUiDimensions.TextSizeDescription,
                )

                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingButtons))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
                ) {
                    RemoteMode.entries.forEach { mode ->
                        val isSelected = activeMode == mode
                        Button(
                            onClick = { onModeSelected(mode) },
                            modifier = Modifier.weight(1f),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) DigaBlue else DigaSurfaceVariant,
                                    contentColor = if (isSelected) Color.White else Color.LightGray,
                                ),
                            shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                        ) {
                            Text(
                                mode.label,
                                fontSize = DigaUiDimensions.TextSizeButtonCompact,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            )
                        }
                    }
                }
            }
        }

        // =========================================================================
        // ★ 2. MASTER HACK: MULTI-REGION (DVD REGION FREE - REGION 0) ★
        // =========================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface),
        ) {
            Column(modifier = Modifier.padding(DigaUiDimensions.PaddingCardInner)) {
                Text(
                    text = DigaUiStrings.HACKS_MULTI_REGION_TITLE,
                    color = DigaPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = DigaUiDimensions.TextSizeCardHeader,
                )

                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingButtons))

                Text(
                    text = DigaUiStrings.HACKS_MULTI_REGION_DESC,
                    color = Color.LightGray,
                    fontSize = DigaUiDimensions.TextSizeButtonStandard,
                    lineHeight = DigaUiDimensions.LineHeightBody,
                )

                Spacer(modifier = Modifier.height(DigaUiDimensions.PaddingCardCompact))

                Button(
                    onClick = { onSetMultiRegionConfirmVisible(true) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(DigaUiDimensions.HeightStandardButton),
                    colors = ButtonDefaults.buttonColors(containerColor = DigaPurple),
                    shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                ) {
                    Text(DigaUiStrings.HACKS_MULTI_REGION_BTN, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))

                OutlinedButton(
                    onClick = {
                        onRunMacro(PanasonicDigaCatalog.Macros.REGION_READOUT)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DigaPurple),
                    shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                ) {
                    Text(DigaUiStrings.HACKS_CHECK_REGION_BTN, fontSize = DigaUiDimensions.TextSizeButtonStandard)
                }
            }
        }

        // =========================================================================
        // 🔒 3. EMERGENCY FRONT PANEL LOCK OVERRIDE (CHILD LOCK / X-HOLD)
        // =========================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface),
        ) {
            Column(modifier = Modifier.padding(DigaUiDimensions.PaddingCardInner)) {
                Text(
                    text = DigaUiStrings.HACKS_CHILD_LOCK_TITLE,
                    color = DigaAmber,
                    fontWeight = FontWeight.Bold,
                    fontSize = DigaUiDimensions.TextSizeCardHeader,
                )
                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingTight))
                Text(
                    text = DigaUiStrings.HACKS_CHILD_LOCK_DESC,
                    color = Color.LightGray,
                    fontSize = DigaUiDimensions.TextSizeButtonStandard,
                    lineHeight = DigaUiDimensions.LineHeightCardText,
                )
                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingCards))
                Button(
                    onClick = {
                        onRunMacro(PanasonicDigaCatalog.Macros.CHILD_LOCK_UNLOCK)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                    shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                ) {
                    Text(
                        DigaUiStrings.HACKS_CHILD_LOCK_BTN,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = DigaUiDimensions.TextSizeButtonStandard,
                    )
                }
            }
        }

        // =========================================================================
        // ⏱ 4. TVGOS EPG NIGHTLY SPIN-UP BYPASS (HDD LIFESPAN PROTECTION)
        // =========================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface),
        ) {
            Column(modifier = Modifier.padding(DigaUiDimensions.PaddingCardInner)) {
                Text(
                    text = DigaUiStrings.HACKS_TVGOS_TITLE,
                    color = DigaGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = DigaUiDimensions.TextSizeCardHeader,
                )
                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingTight))
                Text(
                    text = DigaUiStrings.HACKS_TVGOS_DESC,
                    color = Color.LightGray,
                    fontSize = DigaUiDimensions.TextSizeButtonStandard,
                    lineHeight = DigaUiDimensions.LineHeightCardText,
                )
                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingCards))
                Button(
                    onClick = {
                        onRunMacro(PanasonicDigaCatalog.Macros.TVGOS_DISABLE_ZIP)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DigaGreen),
                    shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                ) {
                    Text(DigaUiStrings.HACKS_TVGOS_BTN, fontWeight = FontWeight.Bold, fontSize = DigaUiDimensions.TextSizeButtonStandard)
                }
            }
        }

        // =========================================================================
        // ⚠ 5. FACTORY MASTER RESET / NVRAM RE-INITIALIZATION (99)
        // =========================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface),
        ) {
            Column(modifier = Modifier.padding(DigaUiDimensions.PaddingCardInner)) {
                Text(
                    text = DigaUiStrings.HACKS_RESET_TITLE,
                    color = DigaRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = DigaUiDimensions.TextSizeCardHeader,
                )
                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingTight))
                Text(
                    text = DigaUiStrings.HACKS_RESET_DESC,
                    color = Color.LightGray,
                    fontSize = DigaUiDimensions.TextSizeButtonStandard,
                    lineHeight = DigaUiDimensions.LineHeightCardText,
                )
                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingCards))
                OutlinedButton(
                    onClick = { onSetResetConfirmVisible(true) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DigaRed),
                    shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                ) {
                    Text(DigaUiStrings.HACKS_RESET_BTN, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // =========================================================================
    // MODAL DIALOGS
    // =========================================================================

    // Confirmation Modal: Auto-Restore SET 2 -> SET 1
    if (showAutoRestoreSet2Confirm) {
        AlertDialog(
            onDismissRequest = { onSetAutoRestoreSet2ConfirmVisible(false) },
            title = { Text(DigaUiStrings.DIALOG_SET2_TITLE) },
            text = { Text(DigaUiStrings.DIALOG_SET2_BODY) },
            confirmButton = {
                Button(
                    onClick = {
                        onSetAutoRestoreSet2ConfirmVisible(false)
                        onRunMacro(PanasonicDigaCatalog.Macros.AUTO_RESTORE_SET2_TO_SET1)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DigaGreen),
                ) {
                    Text(DigaUiStrings.DIALOG_START_RECOVERY)
                }
            },
            dismissButton = {
                TextButton(onClick = { onSetAutoRestoreSet2ConfirmVisible(false) }) {
                    Text(DigaUiStrings.DIALOG_CANCEL)
                }
            },
        )
    }

    // Confirmation Modal: Auto-Restore SET 3 -> SET 1
    if (showAutoRestoreSet3Confirm) {
        AlertDialog(
            onDismissRequest = { onSetAutoRestoreSet3ConfirmVisible(false) },
            title = { Text(DigaUiStrings.DIALOG_SET3_TITLE) },
            text = { Text(DigaUiStrings.DIALOG_SET3_BODY) },
            confirmButton = {
                Button(
                    onClick = {
                        onSetAutoRestoreSet3ConfirmVisible(false)
                        onRunMacro(PanasonicDigaCatalog.Macros.AUTO_RESTORE_SET3_TO_SET1)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                ) {
                    Text(DigaUiStrings.DIALOG_START_RECOVERY, color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { onSetAutoRestoreSet3ConfirmVisible(false) }) {
                    Text(DigaUiStrings.DIALOG_CANCEL)
                }
            },
        )
    }

    // Confirmation Modal: Multi-Region Hack
    if (showMultiRegionConfirm) {
        AlertDialog(
            onDismissRequest = { onSetMultiRegionConfirmVisible(false) },
            title = { Text(DigaUiStrings.DIALOG_MULTI_REGION_TITLE) },
            text = { Text(DigaUiStrings.DIALOG_MULTI_REGION_BODY) },
            confirmButton = {
                Button(
                    onClick = {
                        onSetMultiRegionConfirmVisible(false)
                        onRunMacro(PanasonicDigaCatalog.Macros.MULTI_REGION_HACK)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DigaPurple),
                ) {
                    Text(DigaUiStrings.DIALOG_CONFIRM)
                }
            },
            dismissButton = {
                TextButton(onClick = { onSetMultiRegionConfirmVisible(false) }) {
                    Text(DigaUiStrings.DIALOG_CANCEL)
                }
            },
        )
    }

    // Confirmation Modal: Factory Reset
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { onSetResetConfirmVisible(false) },
            title = { Text(DigaUiStrings.DIALOG_RESET_TITLE, color = DigaRed) },
            text = { Text(DigaUiStrings.DIALOG_RESET_BODY) },
            confirmButton = {
                Button(
                    onClick = {
                        onSetResetConfirmVisible(false)
                        onRunMacro(PanasonicDigaCatalog.Macros.FACTORY_RESET)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DigaRed),
                ) {
                    Text(DigaUiStrings.DIALOG_EXECUTE_RESET)
                }
            },
            dismissButton = {
                TextButton(onClick = { onSetResetConfirmVisible(false) }) {
                    Text(DigaUiStrings.DIALOG_CANCEL)
                }
            },
        )
    }
}
