package com.vhs.digacommander

import com.vhs.digacommander.data.ir.IrTransmitter
import com.vhs.digacommander.data.model.DigaDevice
import com.vhs.digacommander.data.model.IrSignal
import com.vhs.digacommander.data.model.MacroCommand
import com.vhs.digacommander.data.model.MacroExecutionState
import com.vhs.digacommander.data.model.RemoteMode
import com.vhs.digacommander.domain.pipeline.DigaMacroPipeline
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Fake implementation of [IrTransmitter] for unit testing without Android hardware.
 */
class FakeIrTransmitter(
    override var isAvailable: Boolean = true,
    var shouldFail: Boolean = false,
    var supportedFrequencies: List<Int> = listOf(37010, 38029),
) : IrTransmitter {
    val transmittedPronto = mutableListOf<String>()
    val transmittedKaseikyo = mutableListOf<Triple<DigaDevice, RemoteMode, Int>>()

    override fun isFrequencySupported(frequencyHz: Int): Boolean = isAvailable && supportedFrequencies.contains(frequencyHz)

    override fun transmit(signal: IrSignal): Result<Unit> =
        if (!isAvailable || shouldFail) {
            Result.failure(IllegalStateException("Fake IR hardware failure"))
        } else {
            Result.success(Unit)
        }

    override fun transmitPronto(prontoHex: String): Result<Unit> =
        if (!isAvailable || shouldFail) {
            Result.failure(IllegalStateException("Fake IR transmission failure"))
        } else {
            transmittedPronto.add(prontoHex)
            Result.success(Unit)
        }

    override fun transmitKaseikyo(
        device: DigaDevice,
        mode: RemoteMode,
        command: Int,
    ): Result<Unit> =
        if (!isAvailable || shouldFail) {
            Result.failure(IllegalStateException("Fake IR transmission failure"))
        } else {
            transmittedKaseikyo.add(Triple(device, mode, command))
            Result.success(Unit)
        }
}

/**
 * Unit test suite validating the defensive [DigaMacroPipeline] coroutine pipeline:
 * - Sequential execution and state progression.
 * - Hardware availability guard.
 * - Abort / cancellation semantics.
 * - Failure handling and error state propagation.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DigaMacroPipelineTest {
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Test
    fun testMacroExecutionCompletesSuccessfully() =
        testScope.runTest {
            val fakeTransmitter = FakeIrTransmitter(isAvailable = true)
            val pipeline = DigaMacroPipeline(fakeTransmitter, scope = this)

            val commands =
                listOf(
                    MacroCommand("STEP_1", "0000 0070 0000 0032 0080 0040", 50L),
                    MacroCommand("STEP_2", "0000 0070 0000 0032 0080 0040", 50L),
                )

            pipeline.execute(commands)
            advanceUntilIdle()

            assertEquals(MacroExecutionState.Completed, pipeline.state.value)
            assertEquals(2, fakeTransmitter.transmittedPronto.size)
        }

    @Test
    fun testMacroExecutionFailsWhenTransmitterUnavailable() {
        val fakeTransmitter = FakeIrTransmitter(isAvailable = false)
        val pipeline = DigaMacroPipeline(fakeTransmitter, scope = testScope)

        pipeline.execute(listOf(MacroCommand("CMD", "0000 0070", 100L)))

        assertTrue(pipeline.state.value is MacroExecutionState.Error)
        val error = pipeline.state.value as MacroExecutionState.Error
        assertTrue(error.message.contains("unavailable"))
    }

    @Test
    fun testMacroExecutionPropagatesStepFailure() =
        testScope.runTest {
            val fakeTransmitter = FakeIrTransmitter(isAvailable = true, shouldFail = true)
            val pipeline = DigaMacroPipeline(fakeTransmitter, scope = this)

            val commands =
                listOf(
                    MacroCommand("FAIL_STEP", "0000 0070 0000 0032 0080 0040", 50L),
                )

            pipeline.execute(commands)
            advanceUntilIdle()

            assertTrue(pipeline.state.value is MacroExecutionState.Error)
            val error = pipeline.state.value as MacroExecutionState.Error
            assertTrue(error.message.contains("failed at step 1"))
        }

    @Test
    fun testMacroCancellationHaltsExecution() =
        testScope.runTest {
            val fakeTransmitter = FakeIrTransmitter(isAvailable = true)
            val pipeline = DigaMacroPipeline(fakeTransmitter, scope = this)

            val commands =
                listOf(
                    MacroCommand("CMD1", "0000 0070", 500L),
                    MacroCommand("CMD2", "0000 0070", 500L),
                )

            pipeline.execute(commands)
            pipeline.cancelMacro()
            advanceUntilIdle()

            assertEquals(MacroExecutionState.Cancelled, pipeline.state.value)
        }
}
