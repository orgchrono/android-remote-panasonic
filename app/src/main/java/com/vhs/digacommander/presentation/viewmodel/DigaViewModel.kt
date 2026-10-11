package com.vhs.digacommander.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vhs.digacommander.data.constant.DigaTimings
import com.vhs.digacommander.data.ir.IrTransmitter
import com.vhs.digacommander.data.model.DigaDevice
import com.vhs.digacommander.data.model.DigaModelProfile
import com.vhs.digacommander.data.model.MacroCommand
import com.vhs.digacommander.data.model.MacroExecutionState
import com.vhs.digacommander.data.model.RemoteMode
import com.vhs.digacommander.domain.pipeline.DigaMacroPipeline
import com.vhs.digacommander.presentation.constant.DigaUiStrings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Immutable UI State representing the complete presentation model.
 */
data class DigaUiState(
    val activeProfile: DigaModelProfile = DigaModelProfile.DMR_EH55,
    val activeMode: RemoteMode = RemoteMode.MODE_1,
    val selectedTabIndex: Int = 0,
    val vfdMainText: String = DigaUiStrings.VFD_READY,
    val vfdSubText: String = DigaUiStrings.VFD_ONLINE,
    val irStatusText: String = DigaUiStrings.VFD_INITIALIZING,
    val macroState: MacroExecutionState = MacroExecutionState.Idle,
    val showMultiRegionConfirm: Boolean = false,
    val showResetConfirm: Boolean = false,
    val showAutoRestoreSet2Confirm: Boolean = false,
    val showAutoRestoreSet3Confirm: Boolean = false,
)

/**
 * MVVM ViewModel managing application state and user intents with Unidirectional Data Flow (UDF).
 */
class DigaViewModel(
    private val transmitter: IrTransmitter,
    private val macroPipeline: DigaMacroPipeline = DigaMacroPipeline(transmitter),
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            DigaUiState(irStatusText = computeIrStatus()),
        )
    val uiState: StateFlow<DigaUiState> = _uiState.asStateFlow()

    init {
        // Observe pipeline states reactively and reflect on virtual VFD
        viewModelScope.launch {
            macroPipeline.state.collect { state ->
                _uiState.update { current ->
                    val (main, sub) =
                        when (state) {
                            is MacroExecutionState.Idle -> current.vfdMainText to current.vfdSubText
                            is MacroExecutionState.Running ->
                                "${DigaUiStrings.VFD_STEP_PREFIX}${state.step}/${state.totalSteps}" to
                                    "${DigaUiStrings.VFD_SENDING_PREFIX}${state.currentLabel}"
                            is MacroExecutionState.Completed ->
                                DigaUiStrings.VFD_COMPLETED to DigaUiStrings.VFD_COMPLETED_SUB
                            is MacroExecutionState.Cancelled ->
                                DigaUiStrings.VFD_ABORTED to DigaUiStrings.VFD_ABORTED_SUB
                            is MacroExecutionState.Error ->
                                DigaUiStrings.VFD_ERROR to state.message
                        }
                    current.copy(
                        macroState = state,
                        vfdMainText = main,
                        vfdSubText = sub,
                    )
                }
            }
        }
    }

    private fun computeIrStatus(): String =
        when {
            !transmitter.isAvailable -> DigaUiStrings.VFD_EMULATOR_MODE
            transmitter.isFrequencySupported(DigaTimings.CARRIER_MATSUSHITA_HZ) -> DigaUiStrings.VFD_IR_ONLINE_37K
            else -> DigaUiStrings.VFD_IR_ONLINE_LIMITED
        }

    fun onProfileSelected(profile: DigaModelProfile) {
        _uiState.update { it.copy(activeProfile = profile) }
    }

    fun onModeSelected(mode: RemoteMode) {
        _uiState.update {
            it.copy(
                activeMode = mode,
                vfdMainText = "${DigaUiStrings.VFD_CHANNEL_PREFIX}${mode.label}",
                vfdSubText = "${DigaUiStrings.VFD_SUBDEVICE_PREFIX}${mode.subdevice.toString(16).uppercase()}",
            )
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    fun sendCommand(
        label: String,
        command: Int,
        isVcr: Boolean = false,
    ) {
        val mode = _uiState.value.activeMode
        val device = if (isVcr) DigaDevice.VCR else DigaDevice.DVD_HDD
        val channelSuffix = if (mode != RemoteMode.MODE_1) " [${mode.label}]" else ""
        val deckSuffix = if (isVcr) DigaUiStrings.SUFFIX_DECK_VCR else ""

        _uiState.update {
            it.copy(
                vfdMainText = "$label$deckSuffix$channelSuffix",
                vfdSubText = DigaUiStrings.VFD_TRANSMITTING_PULSE,
            )
        }

        val result = transmitter.transmitKaseikyo(device, mode, command)
        result.onFailure { error ->
            _uiState.update {
                it.copy(
                    vfdMainText = DigaUiStrings.VFD_ERROR,
                    vfdSubText = error.message ?: DigaUiStrings.VFD_FAILED_TRANSMIT_PULSE,
                )
            }
        }
    }

    fun runMacro(commands: List<MacroCommand>) {
        macroPipeline.execute(commands)
    }

    fun cancelMacro() {
        macroPipeline.cancelMacro()
    }

    fun setMultiRegionConfirmVisible(visible: Boolean) {
        _uiState.update { it.copy(showMultiRegionConfirm = visible) }
    }

    fun setResetConfirmVisible(visible: Boolean) {
        _uiState.update { it.copy(showResetConfirm = visible) }
    }

    fun setAutoRestoreSet2ConfirmVisible(visible: Boolean) {
        _uiState.update { it.copy(showAutoRestoreSet2Confirm = visible) }
    }

    fun setAutoRestoreSet3ConfirmVisible(visible: Boolean) {
        _uiState.update { it.copy(showAutoRestoreSet3Confirm = visible) }
    }
}
