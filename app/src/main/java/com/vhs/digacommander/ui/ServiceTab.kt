package com.vhs.digacommander.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.vhs.digacommander.core.ir.DigaMacroPipeline
import com.vhs.digacommander.core.ir.PanasonicIrCodes
import com.vhs.digacommander.ui.theme.*

@Composable
fun ServiceTab(
    onRunMacro: (title: String, commands: List<DigaMacroPipeline.MacroCommand>) -> Unit,
    onSendCommand: (label: String, prontoHex: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- INSTRUÇÕES DE ENTRADA EM SERVICE MODE ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "⚙ PROCEDIMENTO DE ENTRADA EM SERVICE MODE",
                    color = DigaBlueLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "1. Com o aparelho ligado e gaveta vazia, certifique-se de estar em STOP.\n" +
                           "2. No painel frontal do gravador, segure [STOP] + [PLAY] por 5 segundos.\n" +
                           "   (Em alguns modelos: [STOP] + [CH UP] em Standby por 5 segundos).\n" +
                           "3. O display VFD exibirá 'SERVICE' ou entrará em modo de escuta.\n" +
                           "4. Dispare as macros abaixo para inspecionar os parâmetros no display.",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        // --- SEÇÃO 1: SAÚDE ÓPTICA & HORÍMETRO ---
        Text(
            text = "🩺 SAÚDE ÓPTICA & HORÍMETRO",
            style = MaterialTheme.typography.labelSmall,
            color = DigaBlueLight,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ServiceActionCard(
                title = "HORAS LASER (41)",
                description = "Tempo total acumulado de emissão do diodo óptico (ex: L0185 = 185h).",
                buttonText = "Executar [4][1]",
                containerColor = DigaSurface,
                accentColor = DigaBlue,
                modifier = Modifier.weight(1f),
                onClick = {
                    onRunMacro("Horímetro Laser (41)", PanasonicIrCodes.Macros.LASER_HOURS)
                }
            )

            ServiceActionCard(
                title = "ERRO RAM DRIVE (42)",
                description = "Exibe o último código de erro da unidade óptica DVD-RAM.",
                buttonText = "Executar [4][2]",
                containerColor = DigaSurface,
                accentColor = DigaBlue,
                modifier = Modifier.weight(1f),
                onClick = {
                    onRunMacro("Último Erro RAM Drive (42)", PanasonicIrCodes.Macros.RAM_DRIVE_LAST_ERROR)
                }
            )
        }

        // --- SEÇÃO 2: HISTÓRICO DE FALHAS (EEPROM) ---
        Text(
            text = "📋 HISTÓRICO DE FALHAS (EEPROM)",
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
                    text = "HISTÓRICO GERAL DE ERROS & LIMPEZA",
                    color = DigaAmber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Lê os códigos de auto-diagnóstico (U**, H**, F**) salvos na memória não volátil.",
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
                            onRunMacro("Último Erro Geral (01)", PanasonicIrCodes.Macros.ERROR_HISTORY_LATEST)
                        },
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Último Erro [0][1]", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            onRunMacro("Limpar Erro para F00 (98)", PanasonicIrCodes.Macros.CLEAR_ERROR_F00)
                        },
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Limpar [9][8] (F00)", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = DigaSurfaceVariant)
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Inspeção de Slots Históricos (Modo 65):",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { onRunMacro("Histórico Slot 1 (65-01)", PanasonicIrCodes.Macros.ERROR_HISTORY_SLOT_1) },
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant)
                    ) { Text("Slot 1", fontSize = 11.sp) }

                    Button(
                        onClick = { onRunMacro("Histórico Slot 2 (65-02)", PanasonicIrCodes.Macros.ERROR_HISTORY_SLOT_2) },
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant)
                    ) { Text("Slot 2", fontSize = 11.sp) }

                    Button(
                        onClick = { onRunMacro("Histórico Slot 3 (65-03)", PanasonicIrCodes.Macros.ERROR_HISTORY_SLOT_3) },
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant)
                    ) { Text("Slot 3", fontSize = 11.sp) }

                    Button(
                        onClick = { onRunMacro("Histórico Slot 4 (65-04)", PanasonicIrCodes.Macros.ERROR_HISTORY_SLOT_4) },
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant)
                    ) { Text("Slot 4", fontSize = 11.sp) }

                    Button(
                        onClick = { onRunMacro("Histórico Slot 5 (65-05)", PanasonicIrCodes.Macros.ERROR_HISTORY_SLOT_5) },
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant)
                    ) { Text("Slot 5", fontSize = 11.sp) }
                }
            }
        }

        // --- SEÇÃO 3: TESTES DE HARDWARE, MECÂNICA & DISPLAY ---
        Text(
            text = "🔬 TESTES DE HARDWARE, HDD & DISPLAY",
            style = MaterialTheme.typography.labelSmall,
            color = DigaGreen,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ServiceActionCard(
                title = "TESTE HDD (31)",
                description = "Varredura física de setores e realocação automática no MEIHDFS.",
                buttonText = "Executar [3][1]",
                containerColor = DigaSurface,
                accentColor = DigaGreen,
                modifier = Modifier.weight(1f),
                onClick = {
                    onRunMacro("Teste Superfície HDD (31)", PanasonicIrCodes.Macros.HDD_SURFACE_TEST)
                }
            )

            ServiceActionCard(
                title = "TESTE DISPLAY (00)",
                description = "Acende 100% dos segmentos e ícones do painel VFD.",
                buttonText = "Executar [0][0]",
                containerColor = DigaSurface,
                accentColor = DigaGreen,
                modifier = Modifier.weight(1f),
                onClick = {
                    onRunMacro("Teste Segmentos VFD (00)", PanasonicIrCodes.Macros.VFD_SEGMENTS_TEST)
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ServiceActionCard(
                title = "CICLO GAVETA (91)",
                description = "Ciclo contínuo mecânico de abertura/fechamento do leitor óptico.",
                buttonText = "Executar [9][1]",
                containerColor = DigaSurface,
                accentColor = DigaSurfaceVariant,
                modifier = Modifier.weight(1f),
                onClick = {
                    onRunMacro("Teste de Ciclo da Gaveta (91)", PanasonicIrCodes.Macros.TRAY_CYCLE_TEST)
                }
            )

            ServiceActionCard(
                title = "I/O VÍDEO (80 / 81)",
                description = "Padrões de calibração RGB / Componente / S-Video.",
                buttonText = "Padrão [8][1]",
                containerColor = DigaSurface,
                accentColor = DigaSurfaceVariant,
                modifier = Modifier.weight(1f),
                onClick = {
                    onRunMacro("Teste Barramento Vídeo 81", PanasonicIrCodes.Macros.VIDEO_IO_TEST_81)
                }
            )
        }

        // --- BOTÃO DE SAÍDA SEGURA DO SERVICE MODE ---
        Button(
            onClick = { onSendCommand("POWER (Sair do Modo de Serviço)", PanasonicIrCodes.POWER) },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DigaRed),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("SAIR DO SERVICE MODE (POWER) ⏻", fontWeight = FontWeight.Bold)
        }
    }
}

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
