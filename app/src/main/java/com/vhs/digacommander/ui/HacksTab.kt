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
        // --- CARTÃO PRINCIPAL: HACK MULTI-REGIÃO (DVD REGION FREE) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "★ HACK MULTI-REGIÃO (DVD REGION FREE)",
                        color = DigaPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Transforma o DMR-EH55 em leitor de DVD Região 0 (Livre para todas as regiões globais: R1, R2, R3, R4).\n\n" +
                           "Condições Obrigatórias:\n" +
                           "• Aparelho ligado;\n" +
                           "• Gaveta óptica aberta e sem nenhum disco dentro;\n" +
                           "• Sequência de 12 comandos com atraso de 500 ms (protegida pelo pipeline anti-brick).",
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
            }
        }

        // --- DESTRAVAR CHILD LOCK / X-HOLD ---
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
                    text = "Se os botões físicos do aparelho pararam de responder com o display indicando 'LOCKED' ou 'X-HOLD', transmita o comando ENTER contínuo para destravar o microcontrolador.",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val commands = List(10) {
                            DigaMacroPipeline.MacroCommand("Pulso ENTER (${it + 1}/10)", PanasonicIrCodes.ENTER_OK, 400L)
                        }
                        onRunMacro("Destravar Child Lock (ENTER 5s)", commands)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Transmitir Desbloqueio [ENTER Contínuo]", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }

        // --- GUIA BYPASS TVGOS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "⏱ BYPASS TVGOS (BUSCA NOTURNA / RUÍDO DE DISCO)",
                    color = DigaBlueLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "O TV Guide On Screen tenta ligar o disco rígido às 3:00 AM para baixar guias analógicos fora do ar, desgastando o motor.\n" +
                           "Solução: Vá em Setup -> TV Guide -> Zip Code e digite '00000' ou '99999' para desativar permanentemente o agendador.",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        // --- RESET DE FÁBRICA / REINICIALIZAÇÃO NVRAM (99) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "⚠ HARD RESET / RESTAURAÇÃO DE FÁBRICA (99)",
                    color = DigaRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Apenas utilize dentro do Service Mode. Limpa toda a EEPROM/NVRAM e restaura o microcontrolador SH-4 para os padrões de fábrica de saída de linha.",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
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
                    "Certifique-se de que o DMR-EH55 está ligado e com a bandeja de DVD vazia.\n\n" +
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
                    "O gravador deve estar previamente em Service Mode para aceitar este comando.\n\n" +
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
