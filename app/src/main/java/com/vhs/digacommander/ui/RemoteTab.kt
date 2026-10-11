package com.vhs.digacommander.ui

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
import com.vhs.digacommander.core.ir.PanasonicIrCodes
import com.vhs.digacommander.ui.theme.*

@Composable
fun RemoteTab(
    onSendCommand: (label: String, prontoHex: String) -> Unit,
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
                        onClick = { onSendCommand("POWER", PanasonicIrCodes.POWER) },
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("POWER ⏻", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { onSendCommand("EJECT", PanasonicIrCodes.EJECT) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("EJECT ⏏", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { onSendCommand("STATUS", PanasonicIrCodes.STATUS) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("STATUS", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { onSendCommand("DISPLAY", PanasonicIrCodes.DISPLAY) },
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
                        onClick = { onSendCommand("DRIVE SELECT (HDD/DVD/SD)", PanasonicIrCodes.DRIVE_SELECT) },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("DRIVE SELECT ⟳ (HDD / DVD / SD)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = { onSendCommand("INPUT SELECT", PanasonicIrCodes.INPUT_SELECT) },
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
                        onClick = { onSendCommand("DIRECT NAVIGATOR", PanasonicIrCodes.DIRECT_NAVIGATOR) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaBlueLight),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("DIRECT NAV", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    Button(
                        onClick = { onSendCommand("FUNCTIONS", PanasonicIrCodes.FUNCTIONS) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("FUNCTIONS", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { onSendCommand("TOP MENU", PanasonicIrCodes.TOP_MENU) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("TOP MENU", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { onSendCommand("OPTION", PanasonicIrCodes.OPTION_SUB_MENU) },
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
                    onClick = { onSendCommand("UP ▲", PanasonicIrCodes.UP) },
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
                        onClick = { onSendCommand("LEFT ◀", PanasonicIrCodes.LEFT) },
                        modifier = Modifier.size(width = 72.dp, height = 56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("◀", fontSize = 18.sp)
                    }

                    Button(
                        onClick = { onSendCommand("OK / ENTER", PanasonicIrCodes.ENTER_OK) },
                        modifier = Modifier.size(72.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaBlue),
                        shape = CircleShape
                    ) {
                        Text("OK", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Button(
                        onClick = { onSendCommand("RIGHT ▶", PanasonicIrCodes.RIGHT) },
                        modifier = Modifier.size(width = 72.dp, height = 56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("▶", fontSize = 18.sp)
                    }
                }

                Button(
                    onClick = { onSendCommand("DOWN ▼", PanasonicIrCodes.DOWN) },
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
                        onClick = { onSendCommand("RETURN", PanasonicIrCodes.RETURN) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("RETURN", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { onSendCommand("EXIT", PanasonicIrCodes.EXIT) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("EXIT", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { onSendCommand("GUIDE", PanasonicIrCodes.GUIDE) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("GUIDE", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { onSendCommand("PROG / CHECK", PanasonicIrCodes.PROG_CHECK) },
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
                        onClick = { onSendCommand("PLAY ▶", PanasonicIrCodes.PLAY) },
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("PLAY ▶", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onSendCommand("PAUSE ❚❚", PanasonicIrCodes.PAUSE) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("PAUSE ❚❚", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onSendCommand("STOP ■", PanasonicIrCodes.STOP) },
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
                        onClick = { onSendCommand("SKIP PREV |◀", PanasonicIrCodes.SKIP_PREV) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("|◀", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { onSendCommand("REWIND ◀◀", PanasonicIrCodes.REWIND) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("◀◀", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { onSendCommand("FAST FORWARD ▶▶", PanasonicIrCodes.FAST_FORWARD) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("▶▶", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { onSendCommand("SKIP NEXT ▶|", PanasonicIrCodes.SKIP_NEXT) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("▶|", fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                        onClick = { onSendCommand("TIME SLIP ⌛", PanasonicIrCodes.TIME_SLIP) },
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("TIME SLIP ⌛", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Black)
                    }

                    Button(
                        onClick = { onSendCommand("MANUAL REPLAY (-10s)", PanasonicIrCodes.MANUAL_REPLAY_10S) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("↶ -10s", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onSendCommand("MANUAL SKIP (+30s)", PanasonicIrCodes.MANUAL_SKIP_30S) },
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
                        onClick = { onSendCommand("CREATE CHAPTER", PanasonicIrCodes.CREATE_CHAPTER) },
                        modifier = Modifier.weight(1.1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("CREATE CH.", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { onSendCommand("DELETE TITLE", PanasonicIrCodes.DELETE_TITLE) },
                        modifier = Modifier.weight(1.1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaRedDark),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("DELETE 🗑", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onSendCommand("AUDIO", PanasonicIrCodes.AUDIO) },
                        modifier = Modifier.weight(0.9f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("AUDIO ♫", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { onSendCommand("SUBTITLE", PanasonicIrCodes.SUBTITLE) },
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
                        onClick = { onSendCommand("REC ●", PanasonicIrCodes.REC) },
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaRed),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("REC ●", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { onSendCommand("REC MODE (XP/SP/LP/EP)", PanasonicIrCodes.REC_MODE) },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("REC MODE (XP/SP)", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { onSendCommand("EXT LINK", PanasonicIrCodes.EXT_LINK) },
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
                        onClick = { onSendCommand("CH UP ▲", PanasonicIrCodes.CH_UP) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("CH ▲", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { onSendCommand("CH DOWN ▼", PanasonicIrCodes.CH_DOWN) },
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
                    listOf("1" to PanasonicIrCodes.DIGIT_1, "2" to PanasonicIrCodes.DIGIT_2, "3" to PanasonicIrCodes.DIGIT_3),
                    listOf("4" to PanasonicIrCodes.DIGIT_4, "5" to PanasonicIrCodes.DIGIT_5, "6" to PanasonicIrCodes.DIGIT_6),
                    listOf("7" to PanasonicIrCodes.DIGIT_7, "8" to PanasonicIrCodes.DIGIT_8, "9" to PanasonicIrCodes.DIGIT_9)
                )

                for (row in digits) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for ((label, code) in row) {
                            Button(
                                onClick = { onSendCommand(label, code) },
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
                        onClick = { onSendCommand("MEDIA SERVER", PanasonicIrCodes.MEDIA_SERVER) },
                        modifier = Modifier.weight(1f).height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("MEDIA", fontSize = 10.sp)
                    }

                    Button(
                        onClick = { onSendCommand("0", PanasonicIrCodes.DIGIT_0) },
                        modifier = Modifier.weight(1f).height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("0", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onSendCommand("CANCEL", PanasonicIrCodes.CANCEL) },
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
