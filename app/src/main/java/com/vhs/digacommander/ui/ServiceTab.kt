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
        // --- CARTÃO DE INSTRUÇÕES DE ENTRADA NO SERVICE MODE ---
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
                           "2. No painel frontal do gravador, segure simultaneamente [STOP] + [PLAY] por 5 segundos.\n" +
                           "3. O display VFD exibirá 'SERVICE' ou entrará em modo de escuta.\n" +
                           "4. Dispare as funções abaixo e confira a leitura no display frontal.",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // --- CARTÕES DE DIAGNÓSTICO E HORÍMETRO ---
        // 1. Horímetro Laser (41)
        ServiceCard(
            title = "HORAS LASER (41)",
            description = "Exibe o tempo total acumulado em horas de emissão do diodo óptico (ex: L0185 = 185h). Essencial para avaliar a vida útil da cabeça.",
            buttonText = "Executar Macro [4] ➔ [1]",
            containerColor = DigaSurface,
            accentColor = DigaBlue,
            onClick = {
                onRunMacro("Horímetro Laser (41)", PanasonicIrCodes.Macros.LASER_HOURS)
            }
        )

        // 2. Histórico de Falhas (01)
        ServiceCard(
            title = "HISTÓRICO DE ERROS (01)",
            description = "Percorre os registros de erro salvos na EEPROM (códigos U**, H**, F**), revelando problemas térmicos ou de barramento.",
            buttonText = "Executar Macro [0] ➔ [1]",
            containerColor = DigaSurface,
            accentColor = DigaBlue,
            onClick = {
                onRunMacro("Histórico de Falhas (01)", PanasonicIrCodes.Macros.ERROR_HISTORY)
            }
        )

        // 3. Leitura de Região (02)
        ServiceCard(
            title = "LER REGIÃO ATUAL (02)",
            description = "Exibe no display a região do leitor (04 = Brasil/América Latina, 01 = EUA, 02 = Europa, 00 = Multi-Região).",
            buttonText = "Executar Macro [0] ➔ [2]",
            containerColor = DigaSurface,
            accentColor = DigaGreen,
            onClick = {
                onRunMacro("Leitura de Região (02)", PanasonicIrCodes.Macros.REGION_READOUT)
            }
        )

        // 4. Teste de Disco HDD (31)
        ServiceCard(
            title = "TESTE DE HDD (31)",
            description = "Dispara a varredura interna de setores de gravação do disco rígido e realocação de blocos defeituosos no MEIHDFS.",
            buttonText = "Executar Macro [3] ➔ [1]",
            containerColor = DigaSurface,
            accentColor = DigaGreen,
            onClick = {
                onRunMacro("Teste de Superfície HDD (31)", PanasonicIrCodes.Macros.HDD_SURFACE_TEST)
            }
        )

        // 5. Teste VFD Segments (00)
        ServiceCard(
            title = "TESTE DISPLAY VFD (00)",
            description = "Acende todos os segmentos do display fluorescente frontal para checar trilhas e filamentos danificados.",
            buttonText = "Executar Macro [0] ➔ [0]",
            containerColor = DigaSurface,
            accentColor = DigaSurfaceVariant,
            onClick = {
                onRunMacro("Teste Segmentos VFD (00)", PanasonicIrCodes.Macros.VFD_SEGMENTS_TEST)
            }
        )

        // SAÍDA SEGURA DO SERVICE MODE
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
fun ServiceCard(
    title: String,
    description: String,
    buttonText: String,
    containerColor: Color,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = Color.LightGray,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(buttonText, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            }
        }
    }
}
