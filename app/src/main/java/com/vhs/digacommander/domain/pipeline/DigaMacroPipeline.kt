package com.vhs.digacommander.domain.pipeline

import com.vhs.digacommander.data.ir.IrTransmitter
import com.vhs.digacommander.data.model.MacroCommand
import com.vhs.digacommander.data.model.MacroExecutionState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Defensive macro execution pipeline ensuring strict anti-brick safety:
 * 1. Serialized queue access via [Mutex]; a new macro first cancels *and joins* the previous one,
 *    so the lock is never contended by a half-cancelled predecessor.
 * 2. Monotonic delay windows between frames preventing SuperH SH-4 buffer overflow.
 * 3. Reactive coroutine cancellation immediately halting transmissions on operator abort.
 *
 * Transmissions block for the duration of the pulse train, hence the default [Dispatchers.IO] scope.
 */
class DigaMacroPipeline(
    private val transmitter: IrTransmitter,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
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
            _state.value = MacroExecutionState.Error(ERROR_UNAVAILABLE)
            return
        }

        val previous = currentJob
        currentJob =
            scope.launch {
                previous?.cancelAndJoin()
                executionMutex.withLock {
                    try {
                        runSequence(commands)
                    } catch (cancellation: CancellationException) {
                        _state.value = MacroExecutionState.Cancelled
                        throw cancellation
                    } catch (
                        @Suppress("TooGenericExceptionCaught") error: Exception,
                    ) {
                        _state.value = MacroExecutionState.Error(error.message ?: ERROR_UNKNOWN)
                    }
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

    private suspend fun runSequence(commands: List<MacroCommand>) {
        for ((index, cmd) in commands.withIndex()) {
            currentCoroutineContext().ensureActive()
            _state.value =
                MacroExecutionState.Running(
                    step = index + 1,
                    totalSteps = commands.size,
                    currentLabel = cmd.label,
                )

            val failure = transmitter.transmitPronto(cmd.prontoHex).exceptionOrNull()
            if (failure != null) {
                _state.value =
                    MacroExecutionState.Error(
                        "$ERROR_STEP_PREFIX${index + 1}: ${failure.message}",
                    )
                return
            }

            // Monotonic pause between pulses
            delay(cmd.postDelayMs)
        }
        _state.value = MacroExecutionState.Completed
    }

    companion object {
        const val ERROR_UNAVAILABLE: String = "IR hardware transmitter is unavailable"
        const val ERROR_STEP_PREFIX: String = "Transmission failed at step "
        const val ERROR_UNKNOWN: String = "Unknown macro execution error"
    }
}
