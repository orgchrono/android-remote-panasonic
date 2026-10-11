package com.vhs.digacommander.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vhs.digacommander.core.ir.DigaMacroPipeline
import com.vhs.digacommander.core.ir.PanasonicIrCodes
import com.vhs.digacommander.ui.theme.*

@Composable
fun HacksTab(
    onRunMacro: (title: String, commands: List<DigaMacroPipeline.MacroCommand>) -> Unit,
    onSendCommand: (label: String, prontoHex: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMultiRegionConfirm by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. HACK PRINCIPAL: MULTI-REGIÃO (DVD REGION FREE) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "★ HACK MULTI-REGIÃO (DVD REGION FREE - REGIÃO 0)",
                    color = DigaPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Desbloqueia permanentemente o leitor para todas as regiões (R1, R2, R3, R4).\n\n" +
                           "Condições Obrigatórias:\n" +
                           "• Aparelho ligado;\n" +
                           "• Bandeja óptica de DVD aberta e sem nenhum disco dentro;\n" +
                           "• Injeção de 12 comandos com atraso de 500 ms (protegida pelo pipeline anti-brick).",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showMultiRegionConfirm = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DigaPurple),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("DESBLOQUEAR MULTI-REGIÃO (12 PASSOS)", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        onRunMacro("Checar Região Atual (02)", PanasonicIrCodes.Macros.REGION_READOUT)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DigaPurple),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Verificar Região Atual no VFD [0][2] (00 = Livre)", fontSize = 12.sp)
                }
            }
        }

        // --- 2. SINCRONIZAÇÃO DE CÓDIGO DE CONTROLE (SET 1 / SET 2 / SET 3) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "📡 SINCRONIZAÇÃO DE CÓDIGO (ERRO 'SET 1 / SET 2 / SET 3')",
                    color = DigaBlueLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Se o display do gravador piscar 'SET 2' ou 'SET 3' e rejeitar qualquer comando, significa que o aparelho foi configurado para outro canal infravermelho no setup. Sincronize o canal abaixo:",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onRunMacro("Sincronizar Modo 1 (DVD 1)", PanasonicIrCodes.Macros.SYNC_REMOTE_MODE_1)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Canal 1 (DVD1)", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            onRunMacro("Sincronizar Modo 2 (DVD 2)", PanasonicIrCodes.Macros.SYNC_REMOTE_MODE_2)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaBlue),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Canal 2 (SET 2)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onRunMacro("Sincronizar Modo 3 (DVD 3)", PanasonicIrCodes.Macros.SYNC_REMOTE_MODE_3)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Canal 3 (SET 3)", fontSize = 11.sp)
                    }
                }
            }
        }

        // --- 3. DESTRAVAR CHILD LOCK / X-HOLD / LOCKED ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🔒 DESTRAVAR TRAVA DE PAINEL (CHILD LOCK / X-HOLD)",
                    color = DigaAmber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Se os botões físicos frontais do gravador pararam de responder com o display indicando 'LOCKED' ou 'X-HOLD', transmita o pulso de emergência para liberar o microcontrolador.",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        onRunMacro("Destravar Child Lock (ENTER 5s)", PanasonicIrCodes.Macros.CHILD_LOCK_UNLOCK)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Transmitir Desbloqueio [ENTER Sustentado]", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                }
            }
        }

        // --- 4. BYPASS NOTURNO TVGOS (PROTEÇÃO CONTRA DESGASTE DE HDD) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "⏱ BYPASS NOTURNO TVGOS (PROTEÇÃO DE DISCO RÍGIDO)",
                    color = DigaGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "O sistema TV Guide On Screen tenta ligar o HDD às 3:00 AM procurando transmissões analógicas extintas, gerando ruído e desgaste do rolamento.\n" +
                           "Solução: Vá em Setup -> TV Guide -> Zip Code e injete '00000' para desativar permanentemente a busca noturna.",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        onRunMacro("Injetar Zip Code 00000 (TVGOS Bypass)", PanasonicIrCodes.Macros.TVGOS_DISABLE_ZIP)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DigaGreen),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Injetar '00000' + ENTER no Campo Zip", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // --- 5. HARD RESET DE FÁBRICA / REINICIALIZAÇÃO NVRAM (99) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "⚠ HARD RESET / RESTAURAÇÃO TOTAL DE FÁBRICA (99)",
                    color = DigaRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Apenas utilize dentro do Service Mode. Limpa toda a EEPROM/NVRAM, zera histórico de erros e restaura os parâmetros originais de fábrica (exibe 'CLEAR' no display).",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { showResetConfirm = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DigaRed),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Executar Reset de Fábrica (Comando 99)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // MODAL DE CONFIRMAÇÃO: MULTI-REGIÃO
    if (showMultiRegionConfirm) {
        AlertDialog(
            onDismissRequest = { showMultiRegionConfirm = false },
            title = { Text("Confirmar Hack Multi-Região") },
            text = {
                Text(
                    "Certifique-se de que o DMR-EH55 está ligado e com a gaveta de DVD aberta e vazia.\n\n" +
                    "O aplicativo transmitirá a sequência exata de 12 pulsos com intervalo de 500 ms.\n\n" +
                    "Deseja iniciar agora?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showMultiRegionConfirm = false
                        onRunMacro("Hack Multi-Região (DVD Region Free)", PanasonicIrCodes.Macros.MULTI_REGION_HACK)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DigaPurple)
                ) {
                    Text("Confirmar e Iniciar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMultiRegionConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // MODAL DE CONFIRMAÇÃO: FACTORY RESET
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Atenção: Hard Reset de Fábrica (99)", color = DigaRed) },
            text = {
                Text(
                    "Esta operação reinicializa todas as configurações da memória NVRAM do gravador.\n\n" +
                    "O aparelho deve estar previamente em Service Mode para aceitar este comando.\n\n" +
                    "Confirmar envio do código 99?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirm = false
                        onRunMacro("Hard Reset (99)", PanasonicIrCodes.Macros.FACTORY_RESET)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DigaRed)
                ) {
                    Text("Sim, Executar Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
