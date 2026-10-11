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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- SERVICE MODE ENTRY GUIDE CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "⚙ SERVICE MODE BENCH ENTRY PROCEDURE",
                    color = DigaBlueLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "1. Turn ON the DIGA recorder, remove any disc, and verify STOP status.\n" +
                           "2. On the physical front panel, hold [STOP] + [PLAY] simultaneously for 5 seconds.\n" +
                           "   (Alternative for select models: hold [STOP] + [CH UP] in Standby mode).\n" +
                           "3. The VFD fluorescent display will indicate 'SERVICE' mode.\n" +
                           "4. Trigger diagnostic macros below to read parameters directly on the display.",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        // --- SECTION 1: OPTICAL PICKUP & LASER HOUR COUNTER ---
        Text(
            text = "🩺 OPTICAL HEALTH & LASER HOUR COUNTER",
            style = MaterialTheme.typography.labelSmall,
            color = DigaBlueLight,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ServiceActionCard(
                title = "LASER HOURS (41)",
                description = "Total cumulative laser diode emission time (e.g., L0185 = 185 hours).",
                buttonText = "Execute [4][1]",
                containerColor = DigaSurface,
                accentColor = DigaBlue,
                modifier = Modifier.weight(1f),
                onClick = {
                    onRunMacro(PanasonicDigaCatalog.Macros.LASER_HOURS)
                }
            )

            ServiceActionCard(
                title = "RAM DRIVE ERROR (42)",
                description = "Displays the most recent optical drive RAM mechanism error code.",
                buttonText = "Execute [4][2]",
                containerColor = DigaSurface,
                accentColor = DigaBlue,
                modifier = Modifier.weight(1f),
                onClick = {
                    onRunMacro(PanasonicDigaCatalog.Macros.RAM_DRIVE_LAST_ERROR)
                }
            )
        }

        // --- SECTION 2: EEPROM FAULT HISTORY & SELF-DIAGNOSTICS ---
        Text(
            text = "📋 EEPROM FAULT LOG & SELF-DIAGNOSTICS",
            style = MaterialTheme.typography.labelSmall,
            color = DigaAmber,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "FAULT RECALL & ERROR BUFFER MANAGEMENT",
                    color = DigaAmber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Reads self-diagnostic error codes (U**, H**, F**) stored in NVRAM/EEPROM memory.",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onRunMacro(PanasonicDigaCatalog.Macros.ERROR_HISTORY_LATEST)
                        },
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Latest Error [0][1]", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            onRunMacro(PanasonicDigaCatalog.Macros.CLEAR_ERROR_F00)
                        },
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Clear [9][8] (F00)", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = DigaSurfaceVariant)
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Historical Error Log Slots (Mode 65 Buffer):",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { onRunMacro(PanasonicDigaCatalog.Macros.ERROR_HISTORY_SLOT_1) },
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant)
                    ) { Text("Slot 1", fontSize = 11.sp) }

                    Button(
                        onClick = { onRunMacro(PanasonicDigaCatalog.Macros.ERROR_HISTORY_SLOT_2) },
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant)
                    ) { Text("Slot 2", fontSize = 11.sp) }

                    Button(
                        onClick = { onRunMacro(PanasonicDigaCatalog.Macros.ERROR_HISTORY_SLOT_3) },
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant)
                    ) { Text("Slot 3", fontSize = 11.sp) }

                    Button(
                        onClick = { onRunMacro(PanasonicDigaCatalog.Macros.ERROR_HISTORY_SLOT_4) },
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant)
                    ) { Text("Slot 4", fontSize = 11.sp) }

                    Button(
                        onClick = { onRunMacro(PanasonicDigaCatalog.Macros.ERROR_HISTORY_SLOT_5) },
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant)
                    ) { Text("Slot 5", fontSize = 11.sp) }
                }
            }
        }

        // --- SECTION 3: HARDWARE DIAGNOSTICS & MECHANICAL CYCLES ---
        Text(
            text = "🔬 HARDWARE DIAGNOSTICS, HDD & DISPLAY",
            style = MaterialTheme.typography.labelSmall,
            color = DigaGreen,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ServiceActionCard(
                title = "HDD TEST (31)",
                description = "SMART surface scan and bad sector reallocation in MEIHDFS filesystem.",
                buttonText = "Execute [3][1]",
                containerColor = DigaSurface,
                accentColor = DigaGreen,
                modifier = Modifier.weight(1f),
                onClick = {
                    onRunMacro(PanasonicDigaCatalog.Macros.HDD_SURFACE_TEST)
                }
            )

            ServiceActionCard(
                title = "VFD DISPLAY (00)",
                description = "Illuminates 100% of fluorescent segments and annunciator icons.",
                buttonText = "Execute [0][0]",
                containerColor = DigaSurface,
                accentColor = DigaGreen,
                modifier = Modifier.weight(1f),
                onClick = {
                    onRunMacro(PanasonicDigaCatalog.Macros.VFD_SEGMENTS_TEST)
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ServiceActionCard(
                title = "TRAY CYCLE (91)",
                description = "Continuous optical drive load/eject mechanical stress test.",
                buttonText = "Execute [9][1]",
                containerColor = DigaSurface,
                accentColor = DigaSurfaceVariant,
                modifier = Modifier.weight(1f),
                onClick = {
                    onRunMacro(PanasonicDigaCatalog.Macros.TRAY_CYCLE_TEST)
                }
            )

            ServiceActionCard(
                title = "VIDEO I/O (80 / 81)",
                description = "Hardware video encoder test patterns for RGB/Component/S-Video.",
                buttonText = "Pattern [8][1]",
                containerColor = DigaSurface,
                accentColor = DigaSurfaceVariant,
                modifier = Modifier.weight(1f),
                onClick = {
                    onRunMacro(PanasonicDigaCatalog.Macros.VIDEO_IO_TEST_81)
                }
            )
        }

        // --- SAFE SERVICE MODE EXIT ---
        Button(
            onClick = { onSendCommand("EXIT SERVICE MODE (POWER)", PanasonicDigaCatalog.OBC.POWER, false) },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DigaRed),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("EXIT SERVICE MODE (POWER) ⏻", fontWeight = FontWeight.Bold)
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
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = Color.LightGray,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                minLines = 3
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(buttonText, fontWeight = FontWeight.Medium, fontSize = 12.sp)
            }
        }
    }
}
