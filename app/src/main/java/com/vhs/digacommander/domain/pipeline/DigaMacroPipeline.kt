package com.vhs.digacommander.domain.pipeline

import com.vhs.digacommander.data.ir.IrTransmitter
import com.vhs.digacommander.data.model.MacroCommand
import com.vhs.digacommander.data.model.MacroExecutionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

/**
 * Defensive macro execution pipeline ensuring strict anti-brick safety:
 * 1. Serialized queue access via [Mutex].
 * 2. Monotonic delay windows (default 500 ms) preventing SuperH SH-4 buffer overflow.
 * 3. Reactive coroutine cancellation immediately halting transmissions on operator abort.
 */
class DigaMacroPipeline(
    private val transmitter: IrTransmitter,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) {
    private val executionMutex = Mutex()
    private var currentJob: Job? = null

    private val _state = MutableStateFlow<MacroExecutionState>(MacroExecutionState.Idle)
    val state: StateFlow<MacroExecutionState> = _state.asStateFlow()

    /**
     * Executes an ordered list of [MacroCommand]s sequentially.
     */
    fun execute(commands: List<MacroCommand>) {
        if (!transmitter.isAvailable) {
            _state.value = MacroExecutionState.Error("IR hardware transmitter is unavailable")
            return
        }

        currentJob?.cancel()

        currentJob =
            scope.launch {
                if (!executionMutex.tryLock()) {
                    _state.value = MacroExecutionState.Error("Another macro is already in progress")
                    return@launch
                }

                try {
                    commands.forEachIndexed { index, cmd ->
                        if (!isActive) {
                            _state.value = MacroExecutionState.Cancelled
                            return@launch
                        }

                        _state.value =
                            MacroExecutionState.Running(
                                step = index + 1,
                                totalSteps = commands.size,
                                currentLabel = cmd.label,
                            )

                        val result = transmitter.transmitPronto(cmd.prontoHex)
                        if (result.isFailure) {
                            _state.value =
                                MacroExecutionState.Error(
                                    "Transmission failed at step ${index + 1}: ${result.exceptionOrNull()?.message}",
                                )
                            return@launch
                        }

                        // Monotonic pause between pulses
                        delay(cmd.postDelayMs)
                    }

                    _state.value = MacroExecutionState.Completed
                } catch (e: Exception) {
                    _state.value = MacroExecutionState.Error(e.message ?: "Unknown macro execution error")
                } finally {
                    executionMutex.unlock()
                }
            }
    }

    /**
     * Aborts the currently active macro immediately.
     */
    fun cancelMacro() {
        currentJob?.cancel()
        _state.value = MacroExecutionState.Cancelled
    }
}
