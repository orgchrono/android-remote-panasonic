package com.vhs.digacommander.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vhs.digacommander.core.ir.PanasonicIrCodes
import com.vhs.digacommander.ui.theme.*

@Composable
fun RemoteTab(
    onSendCommand: (label: String, prontoHex: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeMode by remember { mutableIntStateOf(1) } // 1 (SET 1), 2 (SET 2), 3 (SET 3)
    var activeProfile by remember { mutableStateOf("DMR-EH55") }

    fun send(label: String, cmd: Int, isVcr: Boolean = false) {
        val code = PanasonicIrCodes.getCode(cmd, activeMode, isVcr)
        val modeSuffix = if (activeMode != 1) " [SET $activeMode]" else ""
        val vcrSuffix = if (isVcr) " (VCR)" else ""
        onSendCommand("$label$vcrSuffix$modeSuffix", code)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- SEÇÃO 0: PERFIL DE GRAVADOR & CANAL IR (SET 1 / 2 / 3) ---
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
                        text = "PERFIL DE GRAVADOR DIGA",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "CANAL ATIVO: SET $activeMode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = when (activeMode) {
                            1 -> DigaBlueLight
                            2 -> DigaGreen
                            else -> DigaAmber
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Linha de Perfis
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val profiles = listOf("DMR-EH55", "ES10/ES15", "COMBO VHS", "UNIVERSAL")
                    profiles.forEach { profile ->
                        val isSelected = activeProfile == profile
                        Button(
                            onClick = { activeProfile = profile },
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
                                profile,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Linha de Canais (SET 1, SET 2, SET 3)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        1 to ("SET 1 (Padrão)" to DigaBlue),
                        2 to ("SET 2" to DigaGreen),
                        3 to ("SET 3" to DigaAmber)
                    ).forEach { (modeNum, labelAndColor) ->
                        val (label, accentColor) = labelAndColor
                        val isSelected = activeMode == modeNum
                        Button(
                            onClick = { activeMode = modeNum },
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
                                label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // --- SEÇÃO 1: SISTEMA & SELEÇÃO DE UNIDADE (DRIVE) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "SISTEMA & SELEÇÃO DE DRIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { send("POWER", PanasonicIrCodes.Commands.POWER) },
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("POWER ⏻", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { send("EJECT", PanasonicIrCodes.Commands.EJECT) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("EJECT ⏏", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { send("STATUS", PanasonicIrCodes.Commands.STATUS) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("STATUS", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { send("DISPLAY", PanasonicIrCodes.Commands.DISPLAY) },
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
                        onClick = { send("DRIVE SELECT (HDD/DVD/SD)", PanasonicIrCodes.Commands.DRIVE_SELECT) },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("DRIVE SELECT ⟳ (HDD / DVD / SD)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = { send("INPUT SELECT", PanasonicIrCodes.Commands.INPUT_SELECT) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("INPUT ⮂ (AV/DV)", fontSize = 11.sp)
                    }
                }
            }
        }

        // --- SEÇÃO 2: NAVEGAÇÃO & MENUS (D-PAD) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NAVEGAÇÃO & MENUS",
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
                        onClick = { send("DIRECT NAVIGATOR", PanasonicIrCodes.Commands.DIRECT_NAVIGATOR) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaBlueLight),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("DIRECT NAV", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    Button(
                        onClick = { send("FUNCTIONS", PanasonicIrCodes.Commands.FUNCTIONS) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("FUNCTIONS", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { send("TOP MENU", PanasonicIrCodes.Commands.TOP_MENU) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("TOP MENU", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { send("OPTION", PanasonicIrCodes.Commands.OPTION_SUB_MENU) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("OPTION", fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // D-PAD Cross
                Button(
                    onClick = { send("UP ▲", PanasonicIrCodes.Commands.UP) },
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
                        onClick = { send("LEFT ◀", PanasonicIrCodes.Commands.LEFT) },
                        modifier = Modifier.size(width = 72.dp, height = 56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("◀", fontSize = 18.sp)
                    }

                    Button(
                        onClick = { send("OK / ENTER", PanasonicIrCodes.Commands.ENTER_OK) },
                        modifier = Modifier.size(72.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaBlue),
                        shape = CircleShape
                    ) {
                        Text("OK", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Button(
                        onClick = { send("RIGHT ▶", PanasonicIrCodes.Commands.RIGHT) },
                        modifier = Modifier.size(width = 72.dp, height = 56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("▶", fontSize = 18.sp)
                    }
                }

                Button(
                    onClick = { send("DOWN ▼", PanasonicIrCodes.Commands.DOWN) },
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
                        onClick = { send("RETURN", PanasonicIrCodes.Commands.RETURN) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("RETURN", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { send("EXIT", PanasonicIrCodes.Commands.EXIT) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("EXIT", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { send("GUIDE", PanasonicIrCodes.Commands.GUIDE) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("GUIDE", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { send("PROG / CHECK", PanasonicIrCodes.Commands.PROG_CHECK) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("PROG", fontSize = 10.sp)
                    }
                }
            }
        }

        // --- SEÇÃO 3: TRANSPORTE & DECK ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "TRANSPORTE & DECK",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Play / Pause / Stop
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { send("PLAY ▶", PanasonicIrCodes.Commands.PLAY) },
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("PLAY ▶", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { send("PAUSE ❚❚", PanasonicIrCodes.Commands.PAUSE) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("PAUSE ❚❚", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { send("STOP ■", PanasonicIrCodes.Commands.STOP) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaRedDark),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("STOP ■", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Rewind / Fast Forward / Skip Prev / Skip Next
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { send("SKIP PREV |◀", PanasonicIrCodes.Commands.SKIP_PREV) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("|◀", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { send("REWIND ◀◀", PanasonicIrCodes.Commands.REWIND) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("◀◀", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { send("FAST FORWARD ▶▶", PanasonicIrCodes.Commands.FAST_FORWARD) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("▶▶", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { send("SKIP NEXT ▶|", PanasonicIrCodes.Commands.SKIP_NEXT) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("▶|", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // --- SEÇÃO EXTRA: DECK VCR (COMBO VHS / S-VHS - DEVICE 0x90) ---
        if (activeProfile == "COMBO VHS") {
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
                            text = "📼 DECK VCR (COMBO VHS / S-VHS - DEVICE 0x90)",
                            style = MaterialTheme.typography.labelSmall,
                            color = DigaAmber,
                            fontWeight = FontWeight.Bold
                        )
                        Text("CANAL SET $activeMode", fontSize = 10.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Linha 1: Seleção de Drive VHS & Eject VHS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { send("DRIVE SELECT (VHS)", PanasonicIrCodes.Commands.DRIVE_SELECT, isVcr = true) },
                            modifier = Modifier.weight(1.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("DRIVE SELECT: VHS 📼", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                        }

                        Button(
                            onClick = { send("VHS EJECT ⏏", PanasonicIrCodes.Commands.EJECT, isVcr = true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("EJECT VHS ⏏", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Linha 2: Transporte VHS (Play / Pause / Stop)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { send("VHS PLAY ▶", PanasonicIrCodes.Commands.PLAY, isVcr = true) },
                            modifier = Modifier.weight(1.2f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("VHS PLAY ▶", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { send("VHS PAUSE ❚❚", PanasonicIrCodes.Commands.PAUSE, isVcr = true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("PAUSE ❚❚", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { send("VHS STOP ■", PanasonicIrCodes.Commands.STOP, isVcr = true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaRedDark),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("STOP ■", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Linha 3: Retroceder / Avançar / Gravar VHS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { send("VHS REWIND ◀◀", PanasonicIrCodes.Commands.REWIND, isVcr = true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("VHS ◀◀", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { send("VHS FF ▶▶", PanasonicIrCodes.Commands.FAST_FORWARD, isVcr = true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("VHS ▶▶", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { send("VHS REC ●", PanasonicIrCodes.Commands.REC, isVcr = true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaRed),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("REC ●", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Linha 4: Tracking Analógico
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { send("TRACKING ▲", PanasonicIrCodes.Commands.VCR_TRACKING_PLUS, isVcr = true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaBlue),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("TRACKING ▲", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { send("TRACKING ▼", PanasonicIrCodes.Commands.VCR_TRACKING_MINUS, isVcr = true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaBlue),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("TRACKING ▼", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Linha 5: Cópia Direta 1-Toque (Dubbing)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { send("DUBBING: VHS ➔ DVD", PanasonicIrCodes.Commands.VCR_DUB_VHS_TO_DVD) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("DUB: VHS ➔ DVD 📀", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }

                        Button(
                            onClick = { send("DUBBING: DVD ➔ VHS", PanasonicIrCodes.Commands.VCR_DUB_DVD_TO_VHS) },
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

        // --- SEÇÃO 4: RECURSOS EXCLUSIVOS DIGA & EDIÇÃO ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "RECURSOS DIGA & EDIÇÃO",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { send("TIME SLIP ⌛", PanasonicIrCodes.Commands.TIME_SLIP) },
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("TIME SLIP ⌛", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Black)
                    }

                    Button(
                        onClick = { send("MANUAL REPLAY (-10s)", PanasonicIrCodes.Commands.MANUAL_REPLAY_10S) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("↶ -10s", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { send("MANUAL SKIP (+30s)", PanasonicIrCodes.Commands.MANUAL_SKIP_30S) },
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
                        onClick = { send("CREATE CHAPTER", PanasonicIrCodes.Commands.CREATE_CHAPTER) },
                        modifier = Modifier.weight(1.1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("CREATE CH.", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { send("DELETE TITLE", PanasonicIrCodes.Commands.DELETE_TITLE) },
                        modifier = Modifier.weight(1.1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaRedDark),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("DELETE 🗑", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { send("AUDIO", PanasonicIrCodes.Commands.AUDIO) },
                        modifier = Modifier.weight(0.9f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("AUDIO ♫", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { send("SUBTITLE", PanasonicIrCodes.Commands.SUBTITLE) },
                        modifier = Modifier.weight(0.9f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("SUBTITLE", fontSize = 10.sp)
                    }
                }
            }
        }

        // --- SEÇÃO 5: GRAVAÇÃO & SINTONIZADOR ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "GRAVAÇÃO & SINTONIZADOR",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { send("REC ●", PanasonicIrCodes.Commands.REC) },
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaRed),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("REC ●", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { send("REC MODE (XP/SP/LP/EP)", PanasonicIrCodes.Commands.REC_MODE) },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("REC MODE (XP/SP)", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { send("EXT LINK", PanasonicIrCodes.Commands.EXT_LINK) },
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
                        onClick = { send("CH UP ▲", PanasonicIrCodes.Commands.CH_UP) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("CH ▲", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { send("CH DOWN ▼", PanasonicIrCodes.Commands.CH_DOWN) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("CH ▼", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // --- SEÇÃO 6: TECLADO NUMÉRICO ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TECLADO NUMÉRICO",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                val digits = listOf(
                    listOf("1" to PanasonicIrCodes.Commands.DIGIT_1, "2" to PanasonicIrCodes.Commands.DIGIT_2, "3" to PanasonicIrCodes.Commands.DIGIT_3),
                    listOf("4" to PanasonicIrCodes.Commands.DIGIT_4, "5" to PanasonicIrCodes.Commands.DIGIT_5, "6" to PanasonicIrCodes.Commands.DIGIT_6),
                    listOf("7" to PanasonicIrCodes.Commands.DIGIT_7, "8" to PanasonicIrCodes.Commands.DIGIT_8, "9" to PanasonicIrCodes.Commands.DIGIT_9)
                )

                for (row in digits) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for ((label, cmd) in row) {
                            Button(
                                onClick = { send(label, cmd) },
                                modifier = Modifier.weight(1f).height(46.dp),
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
                        onClick = { send("MEDIA SERVER", PanasonicIrCodes.Commands.MEDIA_SERVER) },
                        modifier = Modifier.weight(1f).height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("MEDIA", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { send("0", PanasonicIrCodes.Commands.DIGIT_0) },
                        modifier = Modifier.weight(1f).height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("0", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { send("CANCEL", PanasonicIrCodes.Commands.CANCEL) },
                        modifier = Modifier.weight(1f).height(46.dp),
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
