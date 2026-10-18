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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.vhs.digacommander.data.model.RemoteMode
import com.vhs.digacommander.presentation.constant.DigaUiDimensions

/**
 * Main Remote Control tab mirroring the physical Panasonic DIGA controller.
 *
 * Layout adapts to the viewport:
 * - On tablets / wide screens (`screenWidthDp >= BreakpointWide`), renders a mathematically balanced
 *   two-column grid (3 cards and 9 row-equivalents per column) synchronized with [IntrinsicSize.Max]
 *   so both columns start and finish at the exact same pixel height.
 * - On smartphones / narrow screens, stacks all sections cleanly in a single scrollable column.
 */
@Composable
fun RemoteTab(
    isVcrDeck: Boolean,
    activeMode: RemoteMode,
    onToggleVcrDeck: () -> Unit,
    onModeSelected: (RemoteMode) -> Unit,
    onSendCommand: SendCommand,
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
                        verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingSections),
                    ) {
                        SystemSection(
                            activeMode = activeMode,
                            isVcrDeck = isVcrDeck,
                            onModeSelected = onModeSelected,
                            onToggleVcrDeck = onToggleVcrDeck,
                            onSend = onSendCommand,
                        )
                        NavigationSection(onSend = onSendCommand)
                        RecordingSection(
                            isVcrDeck = isVcrDeck,
                            onSend = onSendCommand,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Column(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingSections),
                    ) {
                        TransportSection(
                            isVcrDeck = isVcrDeck,
                            onSend = onSendCommand,
                        )
                        EditingSection(onSend = onSendCommand)
                        KeypadSection(
                            onSend = onSendCommand,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingSections)) {
                    SystemSection(
                        activeMode = activeMode,
                        isVcrDeck = isVcrDeck,
                        onModeSelected = onModeSelected,
                        onToggleVcrDeck = onToggleVcrDeck,
                        onSend = onSendCommand,
                    )
                    NavigationSection(onSend = onSendCommand)
                    TransportSection(
                        isVcrDeck = isVcrDeck,
                        onSend = onSendCommand,
                    )
                    RecordingSection(
                        isVcrDeck = isVcrDeck,
                        onSend = onSendCommand,
                    )
                    EditingSection(onSend = onSendCommand)
                    KeypadSection(onSend = onSendCommand)
                }
            }
        }
    }
}
