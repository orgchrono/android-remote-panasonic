package com.vhs.digacommander.presentation.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vhs.digacommander.data.model.DigaModelProfile
import com.vhs.digacommander.data.model.RemoteMode
import com.vhs.digacommander.presentation.constant.DigaUiDimensions

/**
 * Main Remote Control tab mirroring the physical Panasonic EUR7729KG0 controller.
 *
 * Layout adapts to the window: a single scrolling column on narrow screens and two balanced
 * columns on tablets (the target hardware is a 10" Exynos tablet), so keys keep a comfortable
 * size instead of stretching across 2500 px.
 */
@Composable
fun RemoteTab(
    activeProfile: DigaModelProfile,
    activeMode: RemoteMode,
    onProfileSelected: (DigaModelProfile) -> Unit,
    onModeSelected: (RemoteMode) -> Unit,
    onSendCommand: SendCommand,
    modifier: Modifier = Modifier,
) {
    val hasVcr = activeProfile == DigaModelProfile.COMBO_VHS_DVD || activeProfile == DigaModelProfile.UNIVERSAL

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= DigaUiDimensions.BreakpointWide
        Box(
            modifier =
                Modifier
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
                    Row(horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingSections)) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingSections),
                        ) {
                            ProfileSection(activeProfile, activeMode, onProfileSelected, onModeSelected)
                            SystemSection(onSendCommand)
                            NavigationSection(onSendCommand)
                            RecordingSection(onSendCommand)
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingSections),
                        ) {
                            TransportSection(onSendCommand)
                            if (hasVcr) VcrSection(activeMode, onSendCommand)
                            EditingSection(onSendCommand)
                            KeypadSection(onSendCommand)
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingSections)) {
                        ProfileSection(activeProfile, activeMode, onProfileSelected, onModeSelected)
                        SystemSection(onSendCommand)
                        NavigationSection(onSendCommand)
                        RecordingSection(onSendCommand)
                        TransportSection(onSendCommand)
                        if (hasVcr) VcrSection(activeMode, onSendCommand)
                        EditingSection(onSendCommand)
                        KeypadSection(onSendCommand)
                    }
                }
            }
        }
    }
}
