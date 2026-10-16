package com.vhs.digacommander.presentation.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.vhs.digacommander.data.constant.DigaTimings
import com.vhs.digacommander.data.model.IrCapabilities
import com.vhs.digacommander.data.model.IrTimingUnit
import com.vhs.digacommander.presentation.constant.DigaDiagnosticsStrings
import com.vhs.digacommander.presentation.constant.DigaUiDimensions
import com.vhs.digacommander.presentation.theme.DigaTextMuted
import com.vhs.digacommander.presentation.theme.DigaTextPrimary
import com.vhs.digacommander.presentation.ui.components.ChoiceRow
import com.vhs.digacommander.presentation.ui.components.DigaBodyText
import com.vhs.digacommander.presentation.ui.components.DigaCard
import com.vhs.digacommander.presentation.ui.components.KeyStyle
import com.vhs.digacommander.presentation.ui.components.RemoteKey
import com.vhs.digacommander.presentation.ui.components.indicatorColor
import com.vhs.digacommander.presentation.viewmodel.DigaUiState
import com.vhs.digacommander.presentation.viewmodel.IrLinkStatus

/**
 * IR Link tab: explains why the emitter does or does not work, provides a camera-verifiable test
 * beacon and exposes the two driver-quirk settings (timing unit, frame repetition).
 */
@Composable
fun IrDiagnosticsTab(
    state: DigaUiState,
    onTimingUnitSelected: (IrTimingUnit) -> Unit,
    onRepeatSelected: (Int) -> Unit,
    onBeaconToggled: () -> Unit,
    onRescan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier =
                Modifier
                    .widthIn(max = DigaUiDimensions.MaxContentWidth)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(DigaUiDimensions.PaddingScreen),
            verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingSections),
        ) {
            StatusCard(state, onRescan)
            CameraTestCard(state, onBeaconToggled)
            CalibrationCard(state, onTimingUnitSelected, onRepeatSelected)
        }
    }
}

@Composable
private fun StatusCard(
    state: DigaUiState,
    onRescan: () -> Unit,
) {
    val link = state.irLinkStatus
    val (title, body) =
        when (link) {
            IrLinkStatus.NO_HARDWARE -> DigaDiagnosticsStrings.VERDICT_NO_HARDWARE_TITLE to DigaDiagnosticsStrings.VERDICT_NO_HARDWARE_BODY
            IrLinkStatus.ADAPTED -> DigaDiagnosticsStrings.VERDICT_ADAPTED_TITLE to DigaDiagnosticsStrings.VERDICT_ADAPTED_BODY
            IrLinkStatus.ONLINE -> DigaDiagnosticsStrings.VERDICT_ONLINE_TITLE to DigaDiagnosticsStrings.VERDICT_ONLINE_BODY
        }
    DigaCard(
        title = DigaDiagnosticsStrings.CARD_STATUS,
        icon = Icons.Filled.SettingsInputAntenna,
        accent = link.indicatorColor(),
    ) {
        Text(
            text = title,
            color = link.indicatorColor(),
            fontWeight = FontWeight.Bold,
            fontSize = DigaUiDimensions.TextSizeHeading,
        )
        DigaBodyText(text = body)
        FactsTable(state)
        RemoteKey(
            label = DigaDiagnosticsStrings.BTN_RESCAN,
            onClick = onRescan,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Filled.Refresh,
        )
    }
}

@Composable
private fun FactsTable(state: DigaUiState) {
    val caps = state.irCapabilities
    val facts =
        listOf(
            DigaDiagnosticsStrings.ROW_DEVICE to deviceName(caps),
            DigaDiagnosticsStrings.ROW_ANDROID to androidName(caps),
            DigaDiagnosticsStrings.ROW_SOC to caps.device.hardware.ifBlank { DigaDiagnosticsStrings.VALUE_UNKNOWN },
            DigaDiagnosticsStrings.ROW_SERVICE to yesNo(caps.serviceFound),
            DigaDiagnosticsStrings.ROW_EMITTER to yesNo(caps.hasEmitter),
            DigaDiagnosticsStrings.ROW_RANGES to rangesText(caps),
            DigaDiagnosticsStrings.ROW_EMITTING_AT to khz(state.resolvedCarrierHz),
        )
    Column(verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingTight)) {
        facts.forEach { (label, value) ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingRows)) {
                Text(
                    text = label,
                    color = DigaTextMuted,
                    fontSize = DigaUiDimensions.TextSizeBody,
                    modifier = Modifier.weight(FACT_LABEL_WEIGHT),
                )
                Text(
                    text = value,
                    color = DigaTextPrimary,
                    fontSize = DigaUiDimensions.TextSizeBody,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(FACT_VALUE_WEIGHT),
                )
            }
        }
    }
}

@Composable
private fun CameraTestCard(
    state: DigaUiState,
    onBeaconToggled: () -> Unit,
) {
    DigaCard(
        title = DigaDiagnosticsStrings.CARD_CAMERA_TEST,
        icon = Icons.Filled.Visibility,
    ) {
        DigaBodyText(text = DigaDiagnosticsStrings.CAMERA_STEPS)
        val running = state.beaconRunning
        RemoteKey(
            label = if (running) DigaDiagnosticsStrings.BTN_BEACON_STOP else DigaDiagnosticsStrings.BTN_BEACON_START,
            onClick = onBeaconToggled,
            modifier = Modifier.fillMaxWidth(),
            style = if (running) KeyStyle.Danger else KeyStyle.Primary,
            icon = if (running) Icons.Filled.Stop else Icons.Filled.Visibility,
            height = DigaUiDimensions.HeightStandardButton,
        )
        Text(
            text = DigaDiagnosticsStrings.BEACON_NOTE,
            color = DigaTextMuted,
            fontSize = DigaUiDimensions.TextSizeDescription,
            textAlign = TextAlign.Start,
        )
    }
}

@Composable
private fun CalibrationCard(
    state: DigaUiState,
    onTimingUnitSelected: (IrTimingUnit) -> Unit,
    onRepeatSelected: (Int) -> Unit,
) {
    DigaCard(
        title = DigaDiagnosticsStrings.CARD_CALIBRATION,
        icon = Icons.Filled.Tune,
    ) {
        DigaBodyText(text = DigaDiagnosticsStrings.CALIBRATION_INTRO)

        SettingLabel(DigaDiagnosticsStrings.LABEL_TIMING_UNIT)
        ChoiceRow(
            options = IrTimingUnit.entries,
            selected = state.irConfig.timingUnit,
            labelOf = {
                when (it) {
                    IrTimingUnit.MICROSECONDS -> DigaDiagnosticsStrings.UNIT_MICROSECONDS
                    IrTimingUnit.CARRIER_CYCLES -> DigaDiagnosticsStrings.UNIT_CYCLES
                    IrTimingUnit.EXYNOS_FPGA -> DigaDiagnosticsStrings.UNIT_EXYNOS_FPGA
                }
            },
            onSelect = onTimingUnitSelected,
            height = DigaUiDimensions.HeightStandardButton,
        )
        Hint(DigaDiagnosticsStrings.HINT_TIMING_UNIT)

        SettingLabel(DigaDiagnosticsStrings.LABEL_REPEAT)
        ChoiceRow(
            options = (DigaTimings.DEFAULT_FRAME_REPEAT..DigaTimings.MAX_FRAME_REPEAT).toList(),
            selected = state.irConfig.repeatCount,
            labelOf = { "$it${DigaDiagnosticsStrings.REPEAT_SUFFIX}" },
            onSelect = onRepeatSelected,
        )
        Hint(DigaDiagnosticsStrings.HINT_REPEAT)
    }
}

@Composable
private fun SettingLabel(text: String) {
    Text(
        text = text,
        color = DigaTextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = DigaUiDimensions.TextSizeBody,
    )
}

@Composable
private fun Hint(text: String) {
    Text(text = text, color = DigaTextMuted, fontSize = DigaUiDimensions.TextSizeDescription)
}

private fun yesNo(value: Boolean): String = if (value) DigaDiagnosticsStrings.VALUE_YES else DigaDiagnosticsStrings.VALUE_NO

private fun khz(hz: Int): String = "${hz / HZ_PER_KHZ_F}${DigaDiagnosticsStrings.KHZ_SUFFIX}"

private fun deviceName(caps: IrCapabilities): String =
    listOf(caps.device.manufacturer, caps.device.model)
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { DigaDiagnosticsStrings.VALUE_UNKNOWN }

private fun androidName(caps: IrCapabilities): String =
    caps.device.androidRelease.ifBlank { DigaDiagnosticsStrings.VALUE_UNKNOWN } +
        "${DigaDiagnosticsStrings.ANDROID_API_PREFIX}${caps.device.sdkInt}${DigaDiagnosticsStrings.ANDROID_API_SUFFIX}"

private fun rangesText(caps: IrCapabilities): String =
    if (caps.ranges.isEmpty()) {
        DigaDiagnosticsStrings.VALUE_NONE_REPORTED
    } else {
        caps.ranges.joinToString(DigaDiagnosticsStrings.LIST_SEPARATOR) { range ->
            if (range.minHz == range.maxHz) {
                "${range.minHz}${DigaDiagnosticsStrings.HZ_SUFFIX}"
            } else {
                "${range.minHz}${DigaDiagnosticsStrings.RANGE_SEPARATOR}${range.maxHz}${DigaDiagnosticsStrings.HZ_SUFFIX}"
            }
        }
    }

private const val FACT_LABEL_WEIGHT = 0.35f
private const val FACT_VALUE_WEIGHT = 0.65f
private const val HZ_PER_KHZ_F = 1000f
