package com.vhs.digacommander.presentation.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vhs.digacommander.data.catalog.PanasonicDigaCatalog
import com.vhs.digacommander.data.model.MacroCommand
import com.vhs.digacommander.data.model.RemoteMode
import com.vhs.digacommander.presentation.constant.DigaUiDimensions
import com.vhs.digacommander.presentation.constant.DigaUiStrings
import com.vhs.digacommander.presentation.theme.DigaOutline
import com.vhs.digacommander.presentation.theme.DigaSurface
import com.vhs.digacommander.presentation.theme.DigaTextPrimary
import com.vhs.digacommander.presentation.theme.DigaTextSecondary
import com.vhs.digacommander.presentation.ui.components.ChoiceRow
import com.vhs.digacommander.presentation.ui.components.DigaBodyText
import com.vhs.digacommander.presentation.ui.components.DigaCard
import com.vhs.digacommander.presentation.ui.components.KeyStyle
import com.vhs.digacommander.presentation.ui.components.RemoteKey
import com.vhs.digacommander.presentation.ui.components.keyStyle

/**
 * Advanced Hacks, Multi-Region Unlock, and Automated Recovery tab.
 * Includes automated OSD NVRAM channel restoration (SET 2 / SET 3 -> SET 1),
 * DVD Region Free multi-region patch, Child Lock override, and TVGOS bypass.
 *
 * Uses a balanced two-column grid on tablets and a single scrolling column on phones.
 */
@Composable
fun HacksTab(
    activeMode: RemoteMode,
    showMultiRegionConfirm: Boolean,
    showResetConfirm: Boolean,
    showAutoRestoreSet2Confirm: Boolean,
    showAutoRestoreSet3Confirm: Boolean,
    onSetMultiRegionConfirmVisible: (Boolean) -> Unit,
    onSetResetConfirmVisible: (Boolean) -> Unit,
    onSetAutoRestoreSet2ConfirmVisible: (Boolean) -> Unit,
    onSetAutoRestoreSet3ConfirmVisible: (Boolean) -> Unit,
    onModeSelected: (RemoteMode) -> Unit,
    onRunMacro: (commands: List<MacroCommand>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isWide = LocalConfiguration.current.screenWidthDp.dp >= DigaUiDimensions.BreakpointWide
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier =
                Modifier
                    .widthIn(max = DigaUiDimensions.MaxContentWidth)
                    .fillMaxWidth()
                    .padding(DigaUiDimensions.PaddingScreen),
            verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingScreenSections),
        ) {
            if (isWide) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingSections),
                ) {
                    Column(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingScreenSections),
                    ) {
                        AutoRecoveryCard(
                            activeMode = activeMode,
                            onSetAutoRestoreSet2ConfirmVisible = onSetAutoRestoreSet2ConfirmVisible,
                            onSetAutoRestoreSet3ConfirmVisible = onSetAutoRestoreSet3ConfirmVisible,
                            onModeSelected = onModeSelected,
                        )
                        ChildLockCard(onRunMacro = onRunMacro)
                        FactoryResetCard(
                            onSetResetConfirmVisible = onSetResetConfirmVisible,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Column(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingScreenSections),
                    ) {
                        MultiRegionCard(
                            onSetMultiRegionConfirmVisible = onSetMultiRegionConfirmVisible,
                            onRunMacro = onRunMacro,
                        )
                        TvgosBypassCard(
                            onRunMacro = onRunMacro,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            } else {
                AutoRecoveryCard(
                    activeMode = activeMode,
                    onSetAutoRestoreSet2ConfirmVisible = onSetAutoRestoreSet2ConfirmVisible,
                    onSetAutoRestoreSet3ConfirmVisible = onSetAutoRestoreSet3ConfirmVisible,
                    onModeSelected = onModeSelected,
                )
                MultiRegionCard(
                    onSetMultiRegionConfirmVisible = onSetMultiRegionConfirmVisible,
                    onRunMacro = onRunMacro,
                )
                ChildLockCard(onRunMacro = onRunMacro)
                TvgosBypassCard(onRunMacro = onRunMacro)
                FactoryResetCard(onSetResetConfirmVisible = onSetResetConfirmVisible)
            }
        }
    }

    // =========================================================================
    // MODAL DIALOGS
    // =========================================================================

    if (showAutoRestoreSet2Confirm) {
        HacksConfirmDialog(
            title = DigaUiStrings.DIALOG_SET2_TITLE,
            body = DigaUiStrings.DIALOG_SET2_BODY,
            confirmLabel = DigaUiStrings.DIALOG_START_RECOVERY,
            confirmStyle = KeyStyle.Success,
            onConfirm = {
                onSetAutoRestoreSet2ConfirmVisible(false)
                onRunMacro(PanasonicDigaCatalog.Macros.AUTO_RESTORE_SET2_TO_SET1)
            },
            onDismiss = { onSetAutoRestoreSet2ConfirmVisible(false) },
        )
    }

    if (showAutoRestoreSet3Confirm) {
        HacksConfirmDialog(
            title = DigaUiStrings.DIALOG_SET3_TITLE,
            body = DigaUiStrings.DIALOG_SET3_BODY,
            confirmLabel = DigaUiStrings.DIALOG_START_RECOVERY,
            confirmStyle = KeyStyle.Warning,
            onConfirm = {
                onSetAutoRestoreSet3ConfirmVisible(false)
                onRunMacro(PanasonicDigaCatalog.Macros.AUTO_RESTORE_SET3_TO_SET1)
            },
            onDismiss = { onSetAutoRestoreSet3ConfirmVisible(false) },
        )
    }

    if (showMultiRegionConfirm) {
        HacksConfirmDialog(
            title = DigaUiStrings.DIALOG_MULTI_REGION_TITLE,
            body = DigaUiStrings.DIALOG_MULTI_REGION_BODY,
            confirmLabel = DigaUiStrings.DIALOG_CONFIRM,
            confirmStyle = KeyStyle.Accent,
            onConfirm = {
                onSetMultiRegionConfirmVisible(false)
                onRunMacro(PanasonicDigaCatalog.Macros.MULTI_REGION_HACK)
            },
            onDismiss = { onSetMultiRegionConfirmVisible(false) },
        )
    }

    if (showResetConfirm) {
        HacksConfirmDialog(
            title = DigaUiStrings.DIALOG_RESET_TITLE,
            body = DigaUiStrings.DIALOG_RESET_BODY,
            confirmLabel = DigaUiStrings.DIALOG_EXECUTE_RESET,
            confirmStyle = KeyStyle.Danger,
            titleColor = KeyStyle.Danger.headerAccent(),
            onConfirm = {
                onSetResetConfirmVisible(false)
                onRunMacro(PanasonicDigaCatalog.Macros.FACTORY_RESET)
            },
            onDismiss = { onSetResetConfirmVisible(false) },
        )
    }
}

@Composable
private fun AutoRecoveryCard(
    activeMode: RemoteMode,
    onSetAutoRestoreSet2ConfirmVisible: (Boolean) -> Unit,
    onSetAutoRestoreSet3ConfirmVisible: (Boolean) -> Unit,
    onModeSelected: (RemoteMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    DigaCard(
        title = DigaUiStrings.HACKS_AUTO_RECOVERY_TITLE,
        modifier = modifier,
        icon = Icons.Filled.Restore,
        accent = KeyStyle.Primary.headerAccent(),
    ) {
        DigaBodyText(text = DigaUiStrings.HACKS_AUTO_RECOVERY_DESC)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingRows),
        ) {
            RemoteKey(
                label = DigaUiStrings.HACKS_BTN_RESTORE_SET2,
                onClick = { onSetAutoRestoreSet2ConfirmVisible(true) },
                modifier = Modifier.weight(1f),
                style = KeyStyle.Success,
                height = DigaUiDimensions.HeightStandardButton,
            )

            RemoteKey(
                label = DigaUiStrings.HACKS_BTN_RESTORE_SET3,
                onClick = { onSetAutoRestoreSet3ConfirmVisible(true) },
                modifier = Modifier.weight(1f),
                style = KeyStyle.Warning,
                height = DigaUiDimensions.HeightStandardButton,
            )
        }

        HorizontalDivider(color = DigaOutline)

        DigaBodyText(text = DigaUiStrings.HACKS_MANUAL_SYNC_LABEL)

        ChoiceRow(
            options = RemoteMode.entries,
            selected = activeMode,
            labelOf = { it.label },
            onSelect = onModeSelected,
            modifier = Modifier.fillMaxWidth(),
            accentOf = { mode -> mode.keyStyle() },
        )
    }
}

@Composable
private fun MultiRegionCard(
    onSetMultiRegionConfirmVisible: (Boolean) -> Unit,
    onRunMacro: (commands: List<MacroCommand>) -> Unit,
    modifier: Modifier = Modifier,
) {
    DigaCard(
        title = DigaUiStrings.HACKS_MULTI_REGION_TITLE,
        modifier = modifier,
        icon = Icons.Filled.Public,
        accent = KeyStyle.Accent.headerAccent(),
    ) {
        DigaBodyText(text = DigaUiStrings.HACKS_MULTI_REGION_DESC)

        RemoteKey(
            label = DigaUiStrings.HACKS_MULTI_REGION_BTN,
            onClick = { onSetMultiRegionConfirmVisible(true) },
            modifier = Modifier.fillMaxWidth(),
            style = KeyStyle.Accent,
            height = DigaUiDimensions.HeightStandardButton,
        )

        RemoteKey(
            label = DigaUiStrings.HACKS_CHECK_REGION_BTN,
            onClick = { onRunMacro(PanasonicDigaCatalog.Macros.REGION_READOUT) },
            modifier = Modifier.fillMaxWidth(),
            style = KeyStyle.Neutral,
        )
    }
}

@Composable
private fun ChildLockCard(
    onRunMacro: (commands: List<MacroCommand>) -> Unit,
    modifier: Modifier = Modifier,
) {
    DigaCard(
        title = DigaUiStrings.HACKS_CHILD_LOCK_TITLE,
        modifier = modifier,
        icon = Icons.Filled.Lock,
        accent = KeyStyle.Warning.headerAccent(),
    ) {
        DigaBodyText(text = DigaUiStrings.HACKS_CHILD_LOCK_DESC)

        RemoteKey(
            label = DigaUiStrings.HACKS_CHILD_LOCK_BTN,
            onClick = { onRunMacro(PanasonicDigaCatalog.Macros.CHILD_LOCK_UNLOCK) },
            modifier = Modifier.fillMaxWidth(),
            style = KeyStyle.Warning,
        )
    }
}

@Composable
private fun TvgosBypassCard(
    onRunMacro: (commands: List<MacroCommand>) -> Unit,
    modifier: Modifier = Modifier,
) {
    DigaCard(
        title = DigaUiStrings.HACKS_TVGOS_TITLE,
        modifier = modifier,
        icon = Icons.Filled.Schedule,
        accent = KeyStyle.Success.headerAccent(),
    ) {
        DigaBodyText(text = DigaUiStrings.HACKS_TVGOS_DESC)

        RemoteKey(
            label = DigaUiStrings.HACKS_TVGOS_BTN,
            onClick = { onRunMacro(PanasonicDigaCatalog.Macros.TVGOS_DISABLE_ZIP) },
            modifier = Modifier.fillMaxWidth(),
            style = KeyStyle.Success,
        )
    }
}

@Composable
private fun FactoryResetCard(
    onSetResetConfirmVisible: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    DigaCard(
        title = DigaUiStrings.HACKS_RESET_TITLE,
        modifier = modifier,
        icon = Icons.Filled.Warning,
        accent = KeyStyle.Danger.headerAccent(),
    ) {
        DigaBodyText(text = DigaUiStrings.HACKS_RESET_DESC)

        RemoteKey(
            label = DigaUiStrings.HACKS_RESET_BTN,
            onClick = { onSetResetConfirmVisible(true) },
            modifier = Modifier.fillMaxWidth(),
            style = KeyStyle.Danger,
        )
    }
}

/**
 * Themed confirmation dialog shared by every hack: scrollable body, 48 dp minimum touch targets
 * and a confirm label tinted with the accent of the action being confirmed.
 */
@Composable
private fun HacksConfirmDialog(
    title: String,
    body: String,
    confirmLabel: String,
    confirmStyle: KeyStyle,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    titleColor: Color = DigaTextPrimary,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DigaSurface,
        titleContentColor = titleColor,
        textContentColor = DigaTextSecondary,
        title = { Text(title) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(body)
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                modifier = Modifier.heightIn(min = DigaUiDimensions.HeightActionCardButton),
            ) {
                Text(
                    text = confirmLabel,
                    color = confirmStyle.headerAccent(),
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.heightIn(min = DigaUiDimensions.HeightActionCardButton),
            ) {
                Text(text = DigaUiStrings.DIALOG_CANCEL, color = DigaTextSecondary)
            }
        },
    )
}
