package com.vhs.digacommander.presentation.ui.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vhs.digacommander.data.catalog.PanasonicDigaCatalog
import com.vhs.digacommander.data.model.DigaModelProfile
import com.vhs.digacommander.data.model.RemoteMode
import com.vhs.digacommander.presentation.theme.*

/**
 * Main Remote Control tab mirroring the physical Panasonic EUR7729KG0 controller.
 */
@Composable
fun RemoteTab(
    activeProfile: DigaModelProfile,
    activeMode: RemoteMode,
    onProfileSelected: (DigaModelProfile) -> Unit,
    onModeSelected: (RemoteMode) -> Unit,
    onSendCommand: (label: String, command: Int, isVcr: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- SECTION 0: RECORDER PROFILE & IR CHANNEL MODE SELECTOR ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECORDER PROFILE & IR CHANNEL",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "ACTIVE: ${activeMode.label}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = when (activeMode) {
                            RemoteMode.MODE_1 -> DigaBlueLight
                            RemoteMode.MODE_2 -> DigaGreen
                            RemoteMode.MODE_3 -> DigaAmber
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Profile Selector Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    DigaModelProfile.entries.forEach { profile ->
                        val isSelected = activeProfile == profile
                        Button(
                            onClick = { onProfileSelected(profile) },
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) DigaBlue else DigaSurfaceVariant,
                                contentColor = if (isSelected) Color.White else Color.LightGray
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp)
                        ) {
                            Text(
                                profile.id,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Channel Selector Row (SET 1, SET 2, SET 3)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RemoteMode.entries.forEach { mode ->
                        val isSelected = activeMode == mode
                        val accentColor = when (mode) {
                            RemoteMode.MODE_1 -> DigaBlue
                            RemoteMode.MODE_2 -> DigaGreen
                            RemoteMode.MODE_3 -> DigaAmber
                        }
                        Button(
                            onClick = { onModeSelected(mode) },
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) accentColor else DigaSurfaceVariant,
                                contentColor = if (isSelected) Color.White else Color.LightGray
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Text(
                                mode.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // --- SECTION 1: SYSTEM & DRIVE SELECTION ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "SYSTEM & DRIVE SELECTION",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { onSendCommand("POWER", PanasonicDigaCatalog.OBC.POWER, false) },
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("POWER ⏻", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { onSendCommand("EJECT", PanasonicDigaCatalog.OBC.EJECT, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("EJECT ⏏", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { onSendCommand("STATUS", PanasonicDigaCatalog.OBC.STATUS, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("STATUS", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { onSendCommand("DISPLAY", PanasonicDigaCatalog.OBC.DISPLAY, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("DISP", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onSendCommand("DRIVE SELECT (HDD/DVD/SD)", PanasonicDigaCatalog.OBC.DRIVE_SELECT, false) },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("DRIVE SELECT ⟳ (HDD / DVD / SD)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = { onSendCommand("INPUT SELECT", PanasonicDigaCatalog.OBC.INPUT_SELECT, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("INPUT ⮂ (AV/DV)", fontSize = 11.sp)
                    }
                }
            }
        }

        // --- SECTION 2: NAVIGATION & MENUS (D-PAD) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NAVIGATION & MENUS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { onSendCommand("DIRECT NAVIGATOR", PanasonicDigaCatalog.OBC.DIRECT_NAVIGATOR, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaBlueLight),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("DIRECT NAV", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    Button(
                        onClick = { onSendCommand("FUNCTIONS", PanasonicDigaCatalog.OBC.FUNCTIONS, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("FUNCTIONS", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { onSendCommand("TOP MENU", PanasonicDigaCatalog.OBC.TOP_MENU, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("TOP MENU", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { onSendCommand("OPTION", PanasonicDigaCatalog.OBC.OPTION_SUB_MENU, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("OPTION", fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // D-Pad Directional Controls
                Button(
                    onClick = { onSendCommand("UP ▲", PanasonicDigaCatalog.OBC.UP, false) },
                    modifier = Modifier.size(width = 90.dp, height = 48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("▲", fontSize = 18.sp)
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onSendCommand("LEFT ◀", PanasonicDigaCatalog.OBC.LEFT, false) },
                        modifier = Modifier.size(width = 72.dp, height = 56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("◀", fontSize = 18.sp)
                    }

                    Button(
                        onClick = { onSendCommand("OK / ENTER", PanasonicDigaCatalog.OBC.ENTER_OK, false) },
                        modifier = Modifier.size(72.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaBlue),
                        shape = CircleShape
                    ) {
                        Text("OK", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Button(
                        onClick = { onSendCommand("RIGHT ▶", PanasonicDigaCatalog.OBC.RIGHT, false) },
                        modifier = Modifier.size(width = 72.dp, height = 56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("▶", fontSize = 18.sp)
                    }
                }

                Button(
                    onClick = { onSendCommand("DOWN ▼", PanasonicDigaCatalog.OBC.DOWN, false) },
                    modifier = Modifier.size(width = 90.dp, height = 48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("▼", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { onSendCommand("RETURN", PanasonicDigaCatalog.OBC.RETURN, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("RETURN", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { onSendCommand("EXIT", PanasonicDigaCatalog.OBC.EXIT, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("EXIT", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { onSendCommand("GUIDE", PanasonicDigaCatalog.OBC.GUIDE, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("GUIDE", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { onSendCommand("PROG / CHECK", PanasonicDigaCatalog.OBC.PROG_CHECK, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("PROG", fontSize = 10.sp)
                    }
                }
            }
        }

        // --- SECTION 3: TRANSPORT DECK ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "TRANSPORT DECK",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onSendCommand("PLAY ▶", PanasonicDigaCatalog.OBC.PLAY, false) },
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("PLAY ▶", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onSendCommand("PAUSE ❚❚", PanasonicDigaCatalog.OBC.PAUSE, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("PAUSE ❚❚", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onSendCommand("STOP ■", PanasonicDigaCatalog.OBC.STOP, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaRedDark),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("STOP ■", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { onSendCommand("SKIP PREV |◀", PanasonicDigaCatalog.OBC.SKIP_PREV, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("|◀", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { onSendCommand("REWIND ◀◀", PanasonicDigaCatalog.OBC.REWIND, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("◀◀", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { onSendCommand("FAST FORWARD ▶▶", PanasonicDigaCatalog.OBC.FAST_FORWARD, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("▶▶", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { onSendCommand("SKIP NEXT ▶|", PanasonicDigaCatalog.OBC.SKIP_NEXT, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("▶|", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // --- SECTION EXTRA: VCR DECK (COMBO VHS / S-VHS - DEVICE 0x90) ---
        if (activeProfile == DigaModelProfile.COMBO_VHS_DVD || activeProfile == DigaModelProfile.UNIVERSAL) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DigaSurface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📼 VCR DECK (COMBO VHS / S-VHS - DEVICE 0x90)",
                            style = MaterialTheme.typography.labelSmall,
                            color = DigaAmber,
                            fontWeight = FontWeight.Bold
                        )
                        Text(activeMode.label, fontSize = 10.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { onSendCommand("DRIVE SELECT (VHS)", PanasonicDigaCatalog.OBC.DRIVE_SELECT, true) },
                            modifier = Modifier.weight(1.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("DRIVE: VHS 📼", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                        }

                        Button(
                            onClick = { onSendCommand("VHS EJECT ⏏", PanasonicDigaCatalog.OBC.EJECT, true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("EJECT VHS ⏏", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { onSendCommand("VHS PLAY ▶", PanasonicDigaCatalog.OBC.PLAY, true) },
                            modifier = Modifier.weight(1.2f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("VHS PLAY ▶", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onSendCommand("VHS PAUSE ❚❚", PanasonicDigaCatalog.OBC.PAUSE, true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("PAUSE ❚❚", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onSendCommand("VHS STOP ■", PanasonicDigaCatalog.OBC.STOP, true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaRedDark),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("STOP ■", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { onSendCommand("VHS REWIND ◀◀", PanasonicDigaCatalog.OBC.REWIND, true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("VHS ◀◀", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { onSendCommand("VHS FF ▶▶", PanasonicDigaCatalog.OBC.FAST_FORWARD, true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("VHS ▶▶", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { onSendCommand("VHS REC ●", PanasonicDigaCatalog.OBC.REC, true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaRed),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("REC ●", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { onSendCommand("TRACKING ▲", PanasonicDigaCatalog.OBC.VCR_TRACKING_PLUS, true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaBlue),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("TRACKING ▲", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { onSendCommand("TRACKING ▼", PanasonicDigaCatalog.OBC.VCR_TRACKING_MINUS, true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaBlue),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("TRACKING ▼", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { onSendCommand("DUBBING: VHS ➔ DVD", PanasonicDigaCatalog.OBC.VCR_DUB_VHS_TO_DVD, false) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("DUB: VHS ➔ DVD 📀", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }

                        Button(
                            onClick = { onSendCommand("DUBBING: DVD ➔ VHS", PanasonicDigaCatalog.OBC.VCR_DUB_DVD_TO_VHS, false) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("DUB: DVD ➔ VHS 📼", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // --- SECTION 4: DIGA EDITING & ADVANCED PLAYBACK ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "DIGA EDITING & ADVANCED FEATURES",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { onSendCommand("TIME SLIP ⌛", PanasonicDigaCatalog.OBC.TIME_SLIP, false) },
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("TIME SLIP ⌛", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Black)
                    }

                    Button(
                        onClick = { onSendCommand("MANUAL REPLAY (-10s)", PanasonicDigaCatalog.OBC.MANUAL_REPLAY_10S, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("↶ -10s", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onSendCommand("MANUAL SKIP (+30s)", PanasonicDigaCatalog.OBC.MANUAL_SKIP_30S, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("+30s ↷", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { onSendCommand("CREATE CHAPTER", PanasonicDigaCatalog.OBC.CREATE_CHAPTER, false) },
                        modifier = Modifier.weight(1.1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("CREATE CH.", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { onSendCommand("DELETE TITLE", PanasonicDigaCatalog.OBC.DELETE_TITLE, false) },
                        modifier = Modifier.weight(1.1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaRedDark),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("DELETE 🗑", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onSendCommand("AUDIO", PanasonicDigaCatalog.OBC.AUDIO, false) },
                        modifier = Modifier.weight(0.9f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("AUDIO ♫", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { onSendCommand("SUBTITLE", PanasonicDigaCatalog.OBC.SUBTITLE, false) },
                        modifier = Modifier.weight(0.9f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("SUBTITLE", fontSize = 10.sp)
                    }
                }
            }
        }

        // --- SECTION 5: RECORDING & BROADCAST ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "RECORDING & BROADCAST",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { onSendCommand("REC ●", PanasonicDigaCatalog.OBC.REC, false) },
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaRed),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("REC ●", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { onSendCommand("REC MODE (XP/SP/LP/EP)", PanasonicDigaCatalog.OBC.REC_MODE, false) },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("REC MODE (XP/SP)", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { onSendCommand("EXT LINK", PanasonicDigaCatalog.OBC.EXT_LINK, false) },
                        modifier = Modifier.weight(1.1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("EXT LINK ⚡", fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onSendCommand("CH UP ▲", PanasonicDigaCatalog.OBC.CH_UP, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("CH ▲", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { onSendCommand("CH DOWN ▼", PanasonicDigaCatalog.OBC.CH_DOWN, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("CH ▼", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // --- SECTION 6: NUMERIC KEYPAD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NUMERIC KEYPAD",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                val digits = listOf(
                    listOf("1" to PanasonicDigaCatalog.OBC.DIGIT_1, "2" to PanasonicDigaCatalog.OBC.DIGIT_2, "3" to PanasonicDigaCatalog.OBC.DIGIT_3),
                    listOf("4" to PanasonicDigaCatalog.OBC.DIGIT_4, "5" to PanasonicDigaCatalog.OBC.DIGIT_5, "6" to PanasonicDigaCatalog.OBC.DIGIT_6),
                    listOf("7" to PanasonicDigaCatalog.OBC.DIGIT_7, "8" to PanasonicDigaCatalog.OBC.DIGIT_8, "9" to PanasonicDigaCatalog.OBC.DIGIT_9)
                )

                for (row in digits) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for ((label, cmd) in row) {
                            Button(
                                onClick = { onSendCommand(label, cmd, false) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(label, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onSendCommand("MEDIA SERVER", PanasonicDigaCatalog.OBC.MEDIA_SERVER, false) },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("MEDIA", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { onSendCommand("0", PanasonicDigaCatalog.OBC.DIGIT_0, false) },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("0", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onSendCommand("CANCEL", PanasonicDigaCatalog.OBC.CANCEL, false) },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("CANCEL", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
