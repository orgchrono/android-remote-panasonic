package com.vhs.digacommander.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vhs.digacommander.data.catalog.PanasonicDigaCatalog
import com.vhs.digacommander.data.ir.ConsumerIrTransmitter
import com.vhs.digacommander.data.model.*
import com.vhs.digacommander.domain.pipeline.DigaMacroPipeline
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
    val vfdMainText: String = "READY",
    val vfdSubText: String = "DIGA COMMANDER ONLINE",
    val irStatusText: String = "INITIALIZING",
    val macroState: MacroExecutionState = MacroExecutionState.Idle,
    val showMultiRegionConfirm: Boolean = false,
    val showResetConfirm: Boolean = false,
    val showAutoRestoreSet2Confirm: Boolean = false,
    val showAutoRestoreSet3Confirm: Boolean = false
)

/**
 * MVVM ViewModel managing application state and user intents with Unidirectional Data Flow (UDF).
 */
class DigaViewModel(
    private val transmitter: ConsumerIrTransmitter,
    private val macroPipeline: DigaMacroPipeline = DigaMacroPipeline(transmitter)
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DigaUiState(irStatusText = computeIrStatus())
    )
    val uiState: StateFlow<DigaUiState> = _uiState.asStateFlow()

    init {
        // Observe pipeline states reactively and reflect on virtual VFD
        viewModelScope.launch {
            macroPipeline.state.collect { state ->
                _uiState.update { current ->
                    val (main, sub) = when (state) {
                        is MacroExecutionState.Idle -> current.vfdMainText to current.vfdSubText
                        is MacroExecutionState.Running -> "STEP ${state.step}/${state.totalSteps}" to "SENDING: ${state.currentLabel}"
                        is MacroExecutionState.Completed -> "COMPLETED" to "SEQUENCE TRANSMITTED SUCCESSFULLY"
                        is MacroExecutionState.Cancelled -> "ABORTED" to "CANCELLED BY OPERATOR"
                        is MacroExecutionState.Error -> "ERROR" to state.message
                    }
                    current.copy(
                        macroState = state,
                        vfdMainText = main,
                        vfdSubText = sub
                    )
                }
            }
        }
    }

    private fun computeIrStatus(): String = when {
        !transmitter.isAvailable -> "EMULATOR MODE (NO IR HARDWARE)"
        transmitter.isFrequencySupported(37000) -> "IR HARDWARE: ONLINE (37 kHz)"
        else -> "IR HARDWARE: ONLINE (LIMITED)"
    }

    fun onProfileSelected(profile: DigaModelProfile) {
        _uiState.update { it.copy(activeProfile = profile) }
    }

    fun onModeSelected(mode: RemoteMode) {
        _uiState.update {
            it.copy(
                activeMode = mode,
                vfdMainText = "CHANNEL: ${mode.label}",
                vfdSubText = "ACTIVE SUBDEVICE: 0x${mode.subdevice.toString(16).uppercase()}"
            )
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    fun sendCommand(label: String, command: Int, isVcr: Boolean = false) {
        val mode = _uiState.value.activeMode
        val device = if (isVcr) DigaDevice.VCR else DigaDevice.DVD_HDD
        val channelSuffix = if (mode != RemoteMode.MODE_1) " [${mode.label}]" else ""
        val deckSuffix = if (isVcr) " (VCR)" else ""

        _uiState.update {
            it.copy(
                vfdMainText = "$label$deckSuffix$channelSuffix",
                vfdSubText = "TRANSMITTING 48-BIT KASEIKYO PULSE"
            )
        }

        val result = transmitter.transmitKaseikyo(device, mode, command)
        result.onFailure { error ->
            _uiState.update {
                it.copy(
                    vfdMainText = "IR ERROR",
                    vfdSubText = error.message ?: "Failed to transmit pulse"
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
