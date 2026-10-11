package com.vhs.digacommander.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vhs.digacommander.data.model.MacroExecutionState
import com.vhs.digacommander.presentation.theme.*
import com.vhs.digacommander.presentation.ui.components.VfdDisplay
import com.vhs.digacommander.presentation.ui.tabs.HacksTab
import com.vhs.digacommander.presentation.ui.tabs.RemoteTab
import com.vhs.digacommander.presentation.ui.tabs.ServiceTab
import com.vhs.digacommander.presentation.viewmodel.DigaViewModel

/**
 * Main application screen composing the VFD virtual display, macro execution monitor,
 * and the three functional tabs (Remote, Service Bench, Hacks & Recovery).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigaMainScreen(
    viewModel: DigaViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DIGA Commander Pro",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DigaSurfaceVariant,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Text(
                                text = "${uiState.activeProfile.id} | ${uiState.activeMode.label}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DigaBlueLight
                            )
                        }
                    }
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
                    selected = uiState.selectedTabIndex == 0,
                    onClick = { viewModel.onTabSelected(0) },
                    label = { Text("Remote") },
                    icon = { Text("🎮", fontSize = 18.sp) }
                )
                NavigationBarItem(
                    selected = uiState.selectedTabIndex == 1,
                    onClick = { viewModel.onTabSelected(1) },
                    label = { Text("Service") },
                    icon = { Text("⚙", fontSize = 18.sp) }
                )
                NavigationBarItem(
                    selected = uiState.selectedTabIndex == 2,
                    onClick = { viewModel.onTabSelected(2) },
                    label = { Text("Hacks & Recovery") },
                    icon = { Text("★", fontSize = 18.sp) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DigaDarkBackground)
        ) {
            // Retro Fluorescent VFD Display Header
            VfdDisplay(
                mainText = uiState.vfdMainText,
                subText = uiState.vfdSubText,
                irStatusText = uiState.irStatusText,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Active Macro Progress Banner
            if (uiState.macroState is MacroExecutionState.Running) {
                val running = uiState.macroState as MacroExecutionState.Running
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
                                text = "Macro Executing (${running.step}/${running.totalSteps})",
                                color = DigaBlueLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Button(
                                onClick = { viewModel.cancelMacro() },
                                colors = ButtonDefaults.buttonColors(containerColor = DigaRed),
                                modifier = Modifier.height(32.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp)
                            ) {
                                Text("Abort", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { running.step.toFloat() / running.totalSteps.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = DigaBlue,
                            trackColor = DigaSurfaceVariant
                        )
                    }
                }
            }

            // Tab Content Body
            Box(modifier = Modifier.fillMaxSize()) {
                when (uiState.selectedTabIndex) {
                    0 -> RemoteTab(
                        activeProfile = uiState.activeProfile,
                        activeMode = uiState.activeMode,
                        onProfileSelected = viewModel::onProfileSelected,
                        onModeSelected = viewModel::onModeSelected,
                        onSendCommand = viewModel::sendCommand
                    )
                    1 -> ServiceTab(
                        onRunMacro = viewModel::runMacro,
                        onSendCommand = viewModel::sendCommand
                    )
                    2 -> HacksTab(
                        activeMode = uiState.activeMode,
                        showMultiRegionConfirm = uiState.showMultiRegionConfirm,
                        showResetConfirm = uiState.showResetConfirm,
                        showAutoRestoreSet2Confirm = uiState.showAutoRestoreSet2Confirm,
                        showAutoRestoreSet3Confirm = uiState.showAutoRestoreSet3Confirm,
                        onSetMultiRegionConfirmVisible = viewModel::setMultiRegionConfirmVisible,
                        onSetResetConfirmVisible = viewModel::setResetConfirmVisible,
                        onSetAutoRestoreSet2ConfirmVisible = viewModel::setAutoRestoreSet2ConfirmVisible,
                        onSetAutoRestoreSet3ConfirmVisible = viewModel::setAutoRestoreSet3ConfirmVisible,
                        onModeSelected = viewModel::onModeSelected,
                        onRunMacro = viewModel::runMacro
                    )
                }
            }
        }
    }
}
