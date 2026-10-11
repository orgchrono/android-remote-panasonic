package com.vhs.digacommander.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vhs.digacommander.core.ir.ConsumerIrTransmitter
import com.vhs.digacommander.core.ir.DigaMacroPipeline
import com.vhs.digacommander.core.ir.MacroExecutionState
import com.vhs.digacommander.ui.components.VfdDisplay
import com.vhs.digacommander.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigaApp(
    transmitter: ConsumerIrTransmitter,
    macroPipeline: DigaMacroPipeline
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var currentVfdMain by remember { mutableStateOf("PRONTO") }
    var currentVfdSub by remember { mutableStateOf("DIGA COMMANDER ATIVO") }

    val macroState by macroPipeline.state.collectAsState()

    // Atualiza o display VFD virtual de acordo com a máquina de estados
    LaunchedEffect(macroState) {
        when (val state = macroState) {
            is MacroExecutionState.Idle -> {
                // Mantém o último estado ou volta para repouso
            }
            is MacroExecutionState.Running -> {
                currentVfdMain = "PASSO ${state.step}/${state.totalSteps}"
                currentVfdSub = "TRANSMITINDO: ${state.currentLabel}"
            }
            is MacroExecutionState.Completed -> {
                currentVfdMain = "CONCLUÍDO"
                currentVfdSub = "SEQUÊNCIA TRANSMITIDA COM SUCESSO"
            }
            is MacroExecutionState.Cancelled -> {
                currentVfdMain = "CANCELADO"
                currentVfdSub = "MACRO INTERROMPIDA PELO OPERADOR"
            }
            is MacroExecutionState.Error -> {
                currentVfdMain = "ERRO"
                currentVfdSub = state.message
            }
        }
    }

    val irStatus = remember {
        if (transmitter.isAvailable) {
            val supported = transmitter.isFrequencySupported(38000)
            if (supported) "IR HARDWARE: ONLINE (38 kHz)" else "IR HARDWARE: ONLINE (LIMITADO)"
        } else {
            "MODO EMULADOR (SEM HARDWARE IR)"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DIGA Commander",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DigaSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DigaSurface
            ) {
                NavigationBarItem(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    label = { Text("Controle") },
                    icon = { Text("🎮", fontSize = 18.sp) }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    label = { Text("Diagnóstico") },
                    icon = { Text("⚙", fontSize = 18.sp) }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    label = { Text("Hacks & Rec") },
                    icon = { Text("★", fontSize = 18.sp) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DigaDarkBackground)
        ) {
            // VFD Display no topo da tela (sempre visível)
            VfdDisplay(
                mainText = currentVfdMain,
                subText = currentVfdSub,
                irStatusText = irStatus,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Painel de Execução de Macro Ativa (com barra de progresso e botão abortar)
            if (macroState is MacroExecutionState.Running) {
                val running = macroState as MacroExecutionState.Running
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = DigaSurface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Macro em Execução (${running.step}/${running.totalSteps})",
                                color = DigaBlueLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Button(
                                onClick = { macroPipeline.cancelMacro() },
                                colors = ButtonDefaults.buttonColors(containerColor = DigaRed),
                                modifier = Modifier.height(32.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp)
                            ) {
                                Text("Abortar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { running.step.toFloat() / running.totalSteps.toFloat() },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = DigaBlue,
                            trackColor = DigaSurfaceVariant
                        )
                    }
                }
            }

            // Abas de Conteúdo
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTabIndex) {
                    0 -> RemoteTab(
                        onSendCommand = { label, code ->
                            currentVfdMain = label
                            currentVfdSub = "TRANSMITIDO (PULSO ÚNICO)"
                            try {
                                transmitter.transmitPronto(code)
                            } catch (e: Exception) {
                                currentVfdSub = "ERRO: ${e.message}"
                            }
                        }
                    )
                    1 -> ServiceTab(
                        onRunMacro = { title, commands ->
                            macroPipeline.executeMacro(commands)
                        },
                        onSendCommand = { label, code ->
                            currentVfdMain = label
                            currentVfdSub = "TRANSMITIDO"
                            try {
                                transmitter.transmitPronto(code)
                            } catch (e: Exception) {
                                currentVfdSub = "ERRO: ${e.message}"
                            }
                        }
                    )
                    2 -> HacksTab(
                        onRunMacro = { title, commands ->
                            macroPipeline.executeMacro(commands)
                        },
                        onSendCommand = { label, code ->
                            currentVfdMain = label
                            currentVfdSub = "TRANSMITIDO"
                            try {
                                transmitter.transmitPronto(code)
                            } catch (e: Exception) {
                                currentVfdSub = "ERRO: ${e.message}"
                            }
                        }
                    )
                }
            }
        }
    }
}
