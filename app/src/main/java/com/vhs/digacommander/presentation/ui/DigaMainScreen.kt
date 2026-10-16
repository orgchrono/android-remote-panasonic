package com.vhs.digacommander.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.vhs.digacommander.data.model.MacroExecutionState
import com.vhs.digacommander.presentation.constant.DigaUiDimensions
import com.vhs.digacommander.presentation.constant.DigaUiStrings
import com.vhs.digacommander.presentation.theme.DigaBlueLight
import com.vhs.digacommander.presentation.theme.DigaDarkBackground
import com.vhs.digacommander.presentation.theme.DigaOutline
import com.vhs.digacommander.presentation.theme.DigaStatusError
import com.vhs.digacommander.presentation.theme.DigaSurface
import com.vhs.digacommander.presentation.theme.DigaSurfaceVariant
import com.vhs.digacommander.presentation.theme.DigaTextMuted
import com.vhs.digacommander.presentation.theme.DigaTextPrimary
import com.vhs.digacommander.presentation.ui.components.KeyStyle
import com.vhs.digacommander.presentation.ui.components.RemoteKey
import com.vhs.digacommander.presentation.ui.components.VfdDisplay
import com.vhs.digacommander.presentation.ui.tabs.HacksTab
import com.vhs.digacommander.presentation.ui.tabs.IrDiagnosticsTab
import com.vhs.digacommander.presentation.ui.tabs.RemoteTab
import com.vhs.digacommander.presentation.ui.tabs.ServiceTab
import com.vhs.digacommander.presentation.viewmodel.DigaViewModel
import com.vhs.digacommander.presentation.viewmodel.IrLinkStatus

/**
 * Top-level destinations. The enum order is the tab index stored in the UI state (SSOT for navigation).
 */
private enum class DigaTab(
    val label: String,
    val icon: ImageVector,
) {
    REMOTE(DigaUiStrings.TAB_REMOTE, Icons.Filled.SettingsRemote),
    SERVICE(DigaUiStrings.TAB_SERVICE, Icons.Filled.Build),
    RECOVERY(DigaUiStrings.TAB_HACKS, Icons.Filled.Restore),
    IR_LINK(DigaUiStrings.TAB_IR, Icons.Filled.SettingsInputAntenna),
}

/**
 * Main application screen composing the single-line VFD status bar, macro execution monitor,
 * and the functional tabs (Remote, Service Bench, Recovery, IR Link).
 *
 * Phones get a bottom navigation bar; tablets (the target hardware) get a side navigation rail.
 */
@Composable
fun DigaMainScreen(
    viewModel: DigaViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = DigaTab.entries

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= DigaUiDimensions.BreakpointWide

        Scaffold(
            containerColor = DigaDarkBackground,
            bottomBar = {
                if (!isWide) {
                    NavigationBar(containerColor = DigaSurface) {
                        tabs.forEachIndexed { index, tab ->
                            NavigationBarItem(
                                selected = uiState.selectedTabIndex == index,
                                onClick = { viewModel.onTabSelected(index) },
                                label = { Text(tab.label, maxLines = 1, fontSize = DigaUiDimensions.TextSizeVfdStatus) },
                                icon = { Icon(tab.icon, contentDescription = null) },
                                colors = navigationItemColors(),
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                if (isWide) {
                    NavigationRail(
                        containerColor = DigaSurface,
                        modifier = Modifier.widthIn(min = DigaUiDimensions.WidthNavigationRail),
                    ) {
                        Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingButtons))
                        tabs.forEachIndexed { index, tab ->
                            NavigationRailItem(
                                selected = uiState.selectedTabIndex == index,
                                onClick = { viewModel.onTabSelected(index) },
                                label = { Text(tab.label, maxLines = 1, fontSize = DigaUiDimensions.TextSizeVfdStatus) },
                                icon = { Icon(tab.icon, contentDescription = null) },
                                colors = railItemColors(),
                            )
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    // Single-line Fluorescent VFD Top Bar (centered, width-capped on tablets)
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                        Column(modifier = Modifier.widthIn(max = DigaUiDimensions.MaxContentWidth)) {
                            VfdDisplay(
                                mainText = uiState.vfdMainText,
                                subText = uiState.vfdSubText,
                                irStatusText = uiState.irStatusText,
                                irLinkStatus = uiState.irLinkStatus,
                                badgeText = "${uiState.activeProfile.id}${DigaUiStrings.VFD_BADGE_SEPARATOR}${uiState.activeMode.label}",
                                modifier =
                                    Modifier.padding(
                                        horizontal = DigaUiDimensions.PaddingHorizontalDefault,
                                        vertical = DigaUiDimensions.PaddingCompactV,
                                    ),
                            )
                            if (uiState.irLinkStatus == IrLinkStatus.NO_HARDWARE &&
                                uiState.selectedTabIndex != DigaTab.IR_LINK.ordinal
                            ) {
                                NoIrBanner(onClick = { viewModel.onTabSelected(DigaTab.IR_LINK.ordinal) })
                            }
                            val macro = uiState.macroState
                            if (macro is MacroExecutionState.Running) {
                                MacroProgressBanner(macro, onAbort = viewModel::cancelMacro)
                            }
                        }
                    }

                    // Tab Content Body
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (tabs[uiState.selectedTabIndex.coerceIn(0, tabs.lastIndex)]) {
                            DigaTab.REMOTE ->
                                RemoteTab(
                                    activeProfile = uiState.activeProfile,
                                    activeMode = uiState.activeMode,
                                    onProfileSelected = viewModel::onProfileSelected,
                                    onModeSelected = viewModel::onModeSelected,
                                    onSendCommand = viewModel::sendCommand,
                                )
                            DigaTab.SERVICE ->
                                ServiceTab(
                                    onRunMacro = viewModel::runMacro,
                                    onSendCommand = viewModel::sendCommand,
                                )
                            DigaTab.RECOVERY ->
                                HacksTab(
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
                                    onRunMacro = viewModel::runMacro,
                                )
                            DigaTab.IR_LINK ->
                                IrDiagnosticsTab(
                                    state = uiState,
                                    onTimingUnitSelected = viewModel::onTimingUnitSelected,
                                    onRepeatSelected = viewModel::onRepeatCountSelected,
                                    onBeaconToggled = viewModel::onBeaconToggled,
                                    onRescan = viewModel::onIrRescanRequested,
                                )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun navigationItemColors() =
    NavigationBarItemDefaults.colors(
        selectedIconColor = DigaDarkBackground,
        selectedTextColor = DigaBlueLight,
        indicatorColor = DigaBlueLight,
        unselectedIconColor = DigaTextMuted,
        unselectedTextColor = DigaTextMuted,
    )

@Composable
private fun railItemColors() =
    NavigationRailItemDefaults.colors(
        selectedIconColor = DigaDarkBackground,
        selectedTextColor = DigaBlueLight,
        indicatorColor = DigaBlueLight,
        unselectedIconColor = DigaTextMuted,
        unselectedTextColor = DigaTextMuted,
    )

/**
 * Persistent, tappable warning shown while Android reports no IR emitter, so a silent
 * "buttons do nothing" situation is never left unexplained.
 */
@Composable
private fun NoIrBanner(onClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = DigaUiDimensions.PaddingHorizontalDefault,
                    vertical = DigaUiDimensions.SpacingMicro,
                ).heightIn(min = DigaUiDimensions.HeightBannerCompact)
                .background(DigaSurface, RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact))
                .clickable(onClick = onClick)
                .padding(
                    horizontal = DigaUiDimensions.PaddingCardCompact,
                    vertical = DigaUiDimensions.PaddingCompactV,
                ),
        horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.Warning, contentDescription = null, tint = DigaStatusError)
        Text(
            text = DigaUiStrings.BANNER_NO_IR,
            color = DigaTextPrimary,
            fontSize = DigaUiDimensions.TextSizeDescription,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun MacroProgressBanner(
    running: MacroExecutionState.Running,
    onAbort: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = DigaUiDimensions.PaddingHorizontalDefault,
                    vertical = DigaUiDimensions.SpacingMicro,
                ),
        colors = CardDefaults.cardColors(containerColor = DigaSurface),
        border = BorderStroke(DigaUiDimensions.BorderWidthHairline, DigaOutline),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = DigaUiDimensions.PaddingCardCompact,
                        vertical = DigaUiDimensions.PaddingCompactV,
                    ),
            horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingRows),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${DigaUiStrings.MACRO_EXECUTING_PREFIX} ${running.step}/${running.totalSteps}",
                color = DigaBlueLight,
                fontWeight = FontWeight.Bold,
                fontSize = DigaUiDimensions.TextSizeDescription,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            LinearProgressIndicator(
                progress = { running.step.toFloat() / running.totalSteps.toFloat() },
                modifier =
                    Modifier
                        .weight(1f)
                        .height(DigaUiDimensions.HeightProgressBar),
                color = DigaBlueLight,
                trackColor = DigaSurfaceVariant,
            )
            RemoteKey(
                label = DigaUiStrings.BTN_ABORT,
                onClick = onAbort,
                style = KeyStyle.Danger,
                height = DigaUiDimensions.HeightMiniButton,
                modifier = Modifier.padding(PaddingValues(start = DigaUiDimensions.SpacingMicro)),
            )
        }
    }
}
