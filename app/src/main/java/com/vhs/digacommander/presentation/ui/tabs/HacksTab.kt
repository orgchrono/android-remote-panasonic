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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vhs.digacommander.data.catalog.PanasonicDigaCatalog
import com.vhs.digacommander.data.model.MacroCommand
import com.vhs.digacommander.data.model.RemoteMode
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // =========================================================================
        // 🔄 1. AUTOMATED OSD RECOVERY: RESTORE SET 2 / SET 3 BACK TO SET 1
        // =========================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🔄 AUTOMATED OSD RECOVERY (SET 2 / SET 3 ➔ SET 1)",
                        color = DigaBlueLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "If your recorder's front VFD flashes 'SET 2' or 'SET 3' and ignores regular remote buttons, " +
                           "the NVRAM was locked to an alternate remote code.\n\n" +
                           "These 1-touch automated macros transmit the entire OSD setup navigation using the matching " +
                           "subdevice frequency (Mode 2 or Mode 3):\n" +
                           "FUNCTIONS ➔ DOWN ➔ ENTER (Setup) ➔ DOWN ➔ ENTER (Others) ➔ DOWN ➔ ENTER (Remote Code) ➔ UP ➔ ENTER (Set 1) ➔ RETURN.",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onSetAutoRestoreSet2ConfirmVisible(true) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "AUTO-RESTORE\nSET 2 ➔ SET 1",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }

                    Button(
                        onClick = { onSetAutoRestoreSet3ConfirmVisible(true) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "AUTO-RESTORE\nSET 3 ➔ SET 1",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = DigaSurfaceVariant)
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Manual Remote Channel Switching (Sync Commander Transmitter):",
                    color = Color.Gray,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RemoteMode.entries.forEach { mode ->
                        val isSelected = activeMode == mode
                        Button(
                            onClick = { onModeSelected(mode) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) DigaBlue else DigaSurfaceVariant,
                                contentColor = if (isSelected) Color.White else Color.LightGray
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(mode.label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
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
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "★ MULTI-REGION HACK (DVD REGION FREE / REGION 0)",
                    color = DigaPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Permanently unlocks the DVD drive for worldwide playback across all regions (R1, R2, R3, R4, R5, R6).\n\n" +
                           "Mandatory Prerequisites:\n" +
                           "• DIGA recorder powered ON;\n" +
                           "• Disc loading tray OPEN and EMPTY (no media inserted);\n" +
                           "• Sequential 12-pulse injection with 500 ms inter-frame timing (anti-brick protected).",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onSetMultiRegionConfirmVisible(true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DigaPurple),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("UNLOCK MULTI-REGION (12-STEP MACRO)", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        onRunMacro(PanasonicDigaCatalog.Macros.REGION_READOUT)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DigaPurple),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Read Current Region on VFD [0][2] (00 = Region Free)", fontSize = 12.sp)
                }
            }
        }

        // =========================================================================
        // 🔒 3. EMERGENCY FRONT PANEL LOCK OVERRIDE (CHILD LOCK / X-HOLD)
        // =========================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🔒 FRONT PANEL CHILD LOCK OVERRIDE (X-HOLD / LOCKED)",
                    color = DigaAmber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "If physical front-panel buttons are unresponsive and the VFD flashes 'LOCKED' or 'X-HOLD', " +
                           "transmit this sustained 5-second pulse sequence to clear the microcontroller lockout.",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        onRunMacro(PanasonicDigaCatalog.Macros.CHILD_LOCK_UNLOCK)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        "Transmit Sustained Unlock [ENTER 5s]",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // =========================================================================
        // ⏱ 4. TVGOS EPG NIGHTLY SPIN-UP BYPASS (HDD LIFESPAN PROTECTION)
        // =========================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "⏱ TVGOS NIGHTLY SPIN-UP BYPASS (HDD WEAR REDUCTION)",
                    color = DigaGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Legacy TV Guide On Screen firmware attempts nightly 3:00 AM spins searching for extinct analog broadcasts, " +
                           "causing noise and spindle bearing wear.\n" +
                           "Navigate to Setup ➔ TV Guide ➔ Postal / Zip Code and inject '00000' to permanently disable nighttime scanning.",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        onRunMacro(PanasonicDigaCatalog.Macros.TVGOS_DISABLE_ZIP)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DigaGreen),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Inject '00000' + ENTER in Zip Field", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // =========================================================================
        // ⚠ 5. FACTORY MASTER RESET / NVRAM RE-INITIALIZATION (99)
        // =========================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "⚠ FACTORY MASTER RESET / NVRAM CLEAR (COMMAND 99)",
                    color = DigaRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Service bench command only. Clears all EEPROM/NVRAM settings, resets error logs, " +
                           "and restores factory defaults (displays 'CLEAR' on VFD). Recorder must be in Service Mode.",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { onSetResetConfirmVisible(true) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DigaRed),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Execute Factory Reset (Command 99)", fontWeight = FontWeight.Bold)
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
            title = { Text("Confirm SET 2 ➔ SET 1 Recovery") },
            text = {
                Text(
                    "This macro transmits 10 sequential navigation steps on Mode 2 (Subdevice 0x10) to automatically " +
                    "open the Functions menu, enter Setup -> Others -> Remote Control Code, switch from Set 2 to Set 1, " +
                    "and commit to NVRAM.\n\n" +
                    "Prerequisites:\n" +
                    "• DMR recorder is powered ON;\n" +
                    "• Connected TV screen is ON and displaying the recorder's video output;\n" +
                    "• Recorder currently shows 'SET 2' when pressing remote buttons.\n\n" +
                    "Transmit recovery sequence now?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSetAutoRestoreSet2ConfirmVisible(false)
                        onRunMacro(PanasonicDigaCatalog.Macros.AUTO_RESTORE_SET2_TO_SET1)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DigaGreen)
                ) {
                    Text("Start Recovery")
                }
            },
            dismissButton = {
                TextButton(onClick = { onSetAutoRestoreSet2ConfirmVisible(false) }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Confirmation Modal: Auto-Restore SET 3 -> SET 1
    if (showAutoRestoreSet3Confirm) {
        AlertDialog(
            onDismissRequest = { onSetAutoRestoreSet3ConfirmVisible(false) },
            title = { Text("Confirm SET 3 ➔ SET 1 Recovery") },
            text = {
                Text(
                    "This macro transmits 11 sequential navigation steps on Mode 3 (Subdevice 0x20) to automatically " +
                    "open the Functions menu, navigate to Remote Control Code, switch from Set 3 to Set 1, " +
                    "and commit to NVRAM.\n\n" +
                    "Prerequisites:\n" +
                    "• DMR recorder is powered ON;\n" +
                    "• Connected TV screen is ON and displaying the recorder's video output;\n" +
                    "• Recorder currently shows 'SET 3' when pressing remote buttons.\n\n" +
                    "Transmit recovery sequence now?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSetAutoRestoreSet3ConfirmVisible(false)
                        onRunMacro(PanasonicDigaCatalog.Macros.AUTO_RESTORE_SET3_TO_SET1)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DigaAmber)
                ) {
                    Text("Start Recovery", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { onSetAutoRestoreSet3ConfirmVisible(false) }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Confirmation Modal: Multi-Region Hack
    if (showMultiRegionConfirm) {
        AlertDialog(
            onDismissRequest = { onSetMultiRegionConfirmVisible(false) },
            title = { Text("Confirm Multi-Region Unlock") },
            text = {
                Text(
                    "Please verify that the DIGA recorder is powered ON, and the DVD tray is OPEN and EMPTY.\n\n" +
                    "The app will transmit a precise 12-pulse sequence with 500 ms intervals.\n\n" +
                    "Proceed with multi-region unlock?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSetMultiRegionConfirmVisible(false)
                        onRunMacro(PanasonicDigaCatalog.Macros.MULTI_REGION_HACK)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DigaPurple)
                ) {
                    Text("Confirm & Transmit")
                }
            },
            dismissButton = {
                TextButton(onClick = { onSetMultiRegionConfirmVisible(false) }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Confirmation Modal: Factory Reset
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { onSetResetConfirmVisible(false) },
            title = { Text("Warning: Factory Master Reset (99)", color = DigaRed) },
            text = {
                Text(
                    "This operation re-initializes all NVRAM configurations and resets error history to factory status.\n\n" +
                    "The recorder must already be in Service Mode to accept this command.\n\n" +
                    "Confirm transmission of reset code 99?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSetResetConfirmVisible(false)
                        onRunMacro(PanasonicDigaCatalog.Macros.FACTORY_RESET)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DigaRed)
                ) {
                    Text("Execute Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { onSetResetConfirmVisible(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
}
