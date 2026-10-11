package com.vhs.digacommander.ui

import androidx.compose.foundation.background
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- SEÇÃO SUPERIOR: ENERGIA & SISTEMA ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "SISTEMA",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onSendCommand("POWER", PanasonicIrCodes.POWER) },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("POWER ⏻", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onSendCommand("EJECT", PanasonicIrCodes.EJECT) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("EJECT ⏏")
                    }

                    Button(
                        onClick = { onSendCommand("STATUS", PanasonicIrCodes.STATUS) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("STATUS")
                    }
                }
            }
        }

        // --- SEÇÃO D-PAD & MENUS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NAVEGAÇÃO",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { onSendCommand("TOP MENU", PanasonicIrCodes.TOP_MENU) },
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("TOP MENU", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { onSendCommand("FUNCTIONS", PanasonicIrCodes.FUNCTIONS) },
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("FUNCTIONS", fontSize = 11.sp)
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { onSendCommand("RETURN", PanasonicIrCodes.RETURN) },
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("RETURN", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { onSendCommand("EXIT", PanasonicIrCodes.EXIT) },
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("EXIT", fontSize = 11.sp)
                    }
                }
            }
        }

        // --- SEÇÃO TRANSPORTE (PLAY / STOP / PAUSE) ---
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onSendCommand("PLAY ▶", PanasonicIrCodes.PLAY) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("PLAY ▶", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onSendCommand("STOP ■", PanasonicIrCodes.STOP) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaRedDark),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("STOP ■", fontWeight = FontWeight.Bold)
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
                        onClick = { onSendCommand("AUDIO", PanasonicIrCodes.AUDIO) },
                        modifier = Modifier.weight(0.9f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("AUDIO", fontSize = 11.sp)
                    }
                }
            }
        }

        // --- SEÇÃO TECLADO NUMÉRICO ---
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
