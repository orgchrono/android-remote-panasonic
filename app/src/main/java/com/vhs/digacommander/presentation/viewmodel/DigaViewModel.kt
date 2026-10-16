package com.vhs.digacommander.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vhs.digacommander.data.catalog.PanasonicDigaCatalog
import com.vhs.digacommander.data.constant.DigaTimings
import com.vhs.digacommander.data.ir.InMemoryIrConfigStore
import com.vhs.digacommander.data.ir.IrConfigStore
import com.vhs.digacommander.data.ir.IrTransmitter
import com.vhs.digacommander.data.model.DigaDevice
import com.vhs.digacommander.data.model.DigaModelProfile
import com.vhs.digacommander.data.model.IrCapabilities
import com.vhs.digacommander.data.model.IrTimingUnit
import com.vhs.digacommander.data.model.IrTransmitConfig
import com.vhs.digacommander.data.model.MacroCommand
import com.vhs.digacommander.data.model.MacroExecutionState
import com.vhs.digacommander.data.model.RemoteMode
import com.vhs.digacommander.domain.pipeline.DigaMacroPipeline
import com.vhs.digacommander.presentation.constant.DigaUiStrings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Semantic health of the infrared link, so the UI never has to parse display text.
 */
enum class IrLinkStatus {
    /** No emitter exposed by the platform (emulator, missing HAL, tablet without IR LED). */
    NO_HARDWARE,

    /** Emitter present, nominal carrier is emitted after being adapted to the driver ranges. */
    ADAPTED,

    /** Emitter present and the nominal Panasonic carrier is natively supported. */
    ONLINE,
}

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
    val irLinkStatus: IrLinkStatus = IrLinkStatus.NO_HARDWARE,
    val irCapabilities: IrCapabilities = IrCapabilities(),
    val irConfig: IrTransmitConfig = IrTransmitConfig(),
    val resolvedCarrierHz: Int = DigaTimings.CARRIER_MATSUSHITA_HZ,
    val beaconRunning: Boolean = false,
    val macroState: MacroExecutionState = MacroExecutionState.Idle,
    val showMultiRegionConfirm: Boolean = false,
    val showResetConfirm: Boolean = false,
    val showAutoRestoreSet2Confirm: Boolean = false,
    val showAutoRestoreSet3Confirm: Boolean = false,
)

/**
 * MVVM ViewModel managing application state and user intents with Unidirectional Data Flow (UDF).
 *
 * All hardware calls are blocking, so they run on [ioDispatcher] and are serialized by a [Mutex]
 * (the emitter can only radiate one frame at a time).
 */
class DigaViewModel(
    private val transmitter: IrTransmitter,
    private val macroPipeline: DigaMacroPipeline = DigaMacroPipeline(transmitter),
    private val configStore: IrConfigStore = InMemoryIrConfigStore(transmitter.config),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    private val transmitMutex = Mutex()
    private var beaconJob: Job? = null

    private val _uiState = MutableStateFlow(DigaUiState())
    val uiState: StateFlow<DigaUiState> = _uiState.asStateFlow()

    init {
        transmitter.updateConfig(configStore.load())
        _uiState.update(::withIrSnapshot)

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

    /**
     * Re-reads emitter capabilities and carrier adaptation from the platform.
     */
    private fun withIrSnapshot(state: DigaUiState): DigaUiState {
        val capabilities = transmitter.capabilities
        val nominal = DigaTimings.CARRIER_MATSUSHITA_HZ
        val resolved = transmitter.resolveCarrier(nominal)
        val link =
            when {
                !capabilities.hasEmitter -> IrLinkStatus.NO_HARDWARE
                transmitter.isFrequencySupported(nominal) -> IrLinkStatus.ONLINE
                else -> IrLinkStatus.ADAPTED
            }
        val text =
            when (link) {
                IrLinkStatus.NO_HARDWARE -> DigaUiStrings.VFD_EMULATOR_MODE
                IrLinkStatus.ONLINE -> DigaUiStrings.VFD_IR_ONLINE_37K
                IrLinkStatus.ADAPTED ->
                    DigaUiStrings.VFD_IR_ADAPTED_PREFIX +
                        "${resolved / HZ_PER_KHZ}" +
                        DigaUiStrings.VFD_IR_ADAPTED_SUFFIX
            }
        return state.copy(
            irStatusText = text,
            irLinkStatus = link,
            irCapabilities = capabilities,
            irConfig = transmitter.config,
            resolvedCarrierHz = resolved,
        )
    }

    fun onProfileSelected(profile: DigaModelProfile) {
        _uiState.update { it.copy(activeProfile = profile) }
    }

    fun onModeSelected(mode: RemoteMode) {
        _uiState.update {
            it.copy(
                activeMode = mode,
                vfdMainText = "${DigaUiStrings.VFD_CHANNEL_PREFIX}${mode.label}",
                vfdSubText = "${DigaUiStrings.VFD_SUBDEVICE_PREFIX}${mode.subdevice.toString(HEX_RADIX).uppercase()}",
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
        val isTv =
            !isVcr && (
                (command == PanasonicDigaCatalog.OBC.VOL_UP && label == DigaUiStrings.CMD_LABEL_VOL_UP) ||
                    (command == PanasonicDigaCatalog.OBC.VOL_DOWN && label == DigaUiStrings.CMD_LABEL_VOL_DOWN)
            )
        val device =
            when {
                isVcr -> DigaDevice.VCR
                isTv -> DigaDevice.TV
                else -> DigaDevice.DVD_HDD
            }
        val channelSuffix = if (mode != RemoteMode.MODE_1 && !isTv) " [${mode.label}]" else ""
        val deckSuffix = if (isVcr) DigaUiStrings.SUFFIX_DECK_VCR else ""
        val displayLabel = "$label$deckSuffix$channelSuffix"

        _uiState.update {
            it.copy(
                vfdMainText = displayLabel,
                vfdSubText = DigaUiStrings.VFD_TRANSMITTING_PULSE,
            )
        }

        viewModelScope.launch(ioDispatcher) {
            val result = transmitMutex.withLock { transmitter.transmitKaseikyo(device, mode, command) }
            _uiState.update { current ->
                result.fold(
                    onSuccess = { current.copy(vfdMainText = displayLabel, vfdSubText = DigaUiStrings.VFD_PULSE_SENT) },
                    onFailure = { error ->
                        current.copy(
                            vfdMainText = DigaUiStrings.VFD_ERROR,
                            vfdSubText = error.message ?: DigaUiStrings.VFD_FAILED_TRANSMIT_PULSE,
                        )
                    },
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

    // --- IR diagnostics & calibration -------------------------------------------------------

    fun onTimingUnitSelected(unit: IrTimingUnit) = applyConfig(transmitter.config.copy(timingUnit = unit))

    fun onRepeatCountSelected(count: Int) =
        applyConfig(
            transmitter.config.copy(
                repeatCount = count.coerceIn(DigaTimings.DEFAULT_FRAME_REPEAT, DigaTimings.MAX_FRAME_REPEAT),
            ),
        )

    fun onIrRescanRequested() {
        _uiState.update(::withIrSnapshot)
    }

    /**
     * Starts (or stops) a ~4.5 s continuous burst addressed to an unassigned device id, which a phone
     * camera can see as a flickering violet light while no equipment reacts to it.
     */
    fun onBeaconToggled() {
        if (beaconJob?.isActive == true) {
            beaconJob?.cancel()
            return
        }
        beaconJob =
            viewModelScope.launch(ioDispatcher) {
                _uiState.update {
                    it.copy(
                        beaconRunning = true,
                        vfdMainText = DigaUiStrings.VFD_BEACON_ACTIVE,
                        vfdSubText = DigaUiStrings.VFD_BEACON_HINT,
                    )
                }
                try {
                    repeat(DigaTimings.BEACON_FRAME_COUNT) {
                        val failure =
                            transmitMutex
                                .withLock {
                                    transmitter.transmitKaseikyo(
                                        DigaDevice.DIAGNOSTIC,
                                        _uiState.value.activeMode,
                                        DigaTimings.BEACON_COMMAND_CODE,
                                    )
                                }.exceptionOrNull()
                        if (failure != null) {
                            _uiState.update {
                                it.copy(
                                    vfdMainText = DigaUiStrings.VFD_ERROR,
                                    vfdSubText = failure.message ?: DigaUiStrings.VFD_FAILED_TRANSMIT_PULSE,
                                )
                            }
                            return@launch
                        }
                        delay(DigaTimings.BEACON_FRAME_INTERVAL_MS)
                    }
                    _uiState.update {
                        it.copy(
                            vfdMainText = DigaUiStrings.VFD_BEACON_DONE,
                            vfdSubText = DigaUiStrings.VFD_BEACON_DONE_SUB,
                        )
                    }
                } finally {
                    _uiState.update { it.copy(beaconRunning = false) }
                }
            }
    }

    private fun applyConfig(config: IrTransmitConfig) {
        transmitter.updateConfig(config)
        configStore.save(config)
        _uiState.update { it.copy(irConfig = config) }
    }

    // --- Confirmation dialogs ---------------------------------------------------------------

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

    override fun onCleared() {
        macroPipeline.cancelMacro()
        super.onCleared()
    }

    private companion object {
        const val HEX_RADIX = 16
        const val HZ_PER_KHZ = 1000
    }
}
