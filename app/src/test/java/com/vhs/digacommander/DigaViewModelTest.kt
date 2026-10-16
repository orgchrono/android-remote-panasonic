package com.vhs.digacommander

import com.vhs.digacommander.data.catalog.PanasonicDigaCatalog
import com.vhs.digacommander.data.constant.DigaTimings
import com.vhs.digacommander.data.ir.InMemoryIrConfigStore
import com.vhs.digacommander.data.model.DigaDevice
import com.vhs.digacommander.data.model.DigaModelProfile
import com.vhs.digacommander.data.model.IrTimingUnit
import com.vhs.digacommander.data.model.IrTransmitConfig
import com.vhs.digacommander.data.model.RemoteMode
import com.vhs.digacommander.domain.pipeline.DigaMacroPipeline
import com.vhs.digacommander.presentation.constant.DigaUiStrings
import com.vhs.digacommander.presentation.viewmodel.DigaViewModel
import com.vhs.digacommander.presentation.viewmodel.IrLinkStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit test suite verifying [DigaViewModel] state mutations,
 * Unidirectional Data Flow (UDF), and IR transmission dispatch.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DigaViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialUiStateWithOnlineHardware() {
        val fakeTransmitter = FakeIrTransmitter(isAvailable = true, supportedFrequencies = listOf(37010))
        val pipeline = DigaMacroPipeline(fakeTransmitter)
        val viewModel = DigaViewModel(fakeTransmitter, pipeline)

        val state = viewModel.uiState.value
        assertEquals(DigaModelProfile.DMR_EH55, state.activeProfile)
        assertEquals(RemoteMode.MODE_1, state.activeMode)
        assertEquals(0, state.selectedTabIndex)
        assertEquals(DigaUiStrings.VFD_READY, state.vfdMainText)
        assertEquals(DigaUiStrings.VFD_IR_ONLINE_37K, state.irStatusText)
    }

    @Test
    fun testInitialUiStateInEmulatorMode() {
        val fakeTransmitter = FakeIrTransmitter(isAvailable = false)
        val pipeline = DigaMacroPipeline(fakeTransmitter)
        val viewModel = DigaViewModel(fakeTransmitter, pipeline)

        val state = viewModel.uiState.value
        assertEquals(DigaUiStrings.VFD_EMULATOR_MODE, state.irStatusText)
    }

    @Test
    fun testProfileAndModeSelection() {
        val fakeTransmitter = FakeIrTransmitter(isAvailable = true)
        val pipeline = DigaMacroPipeline(fakeTransmitter)
        val viewModel = DigaViewModel(fakeTransmitter, pipeline)

        viewModel.onProfileSelected(DigaModelProfile.COMBO_VHS_DVD)
        assertEquals(DigaModelProfile.COMBO_VHS_DVD, viewModel.uiState.value.activeProfile)

        viewModel.onModeSelected(RemoteMode.MODE_2)
        assertEquals(RemoteMode.MODE_2, viewModel.uiState.value.activeMode)
        assertEquals("CHANNEL: SET 2", viewModel.uiState.value.vfdMainText)
        assertEquals("SUBDEVICE 0x10", viewModel.uiState.value.vfdSubText)
    }

    @Test
    fun testTabSelectionAndDialogToggles() {
        val fakeTransmitter = FakeIrTransmitter(isAvailable = true)
        val pipeline = DigaMacroPipeline(fakeTransmitter)
        val viewModel = DigaViewModel(fakeTransmitter, pipeline)

        viewModel.onTabSelected(2)
        assertEquals(2, viewModel.uiState.value.selectedTabIndex)

        viewModel.setMultiRegionConfirmVisible(true)
        assertTrue(viewModel.uiState.value.showMultiRegionConfirm)
        viewModel.setMultiRegionConfirmVisible(false)
        assertFalse(viewModel.uiState.value.showMultiRegionConfirm)

        viewModel.setAutoRestoreSet2ConfirmVisible(true)
        assertTrue(viewModel.uiState.value.showAutoRestoreSet2Confirm)
        viewModel.setAutoRestoreSet2ConfirmVisible(false)
        assertFalse(viewModel.uiState.value.showAutoRestoreSet2Confirm)
    }

    @Test
    fun testSendCommandDispatchesToTransmitter() =
        runTest(testDispatcher) {
            val fakeTransmitter = FakeIrTransmitter(isAvailable = true)
            val pipeline = DigaMacroPipeline(fakeTransmitter, scope = this)
            val viewModel = DigaViewModel(fakeTransmitter, pipeline, ioDispatcher = testDispatcher)

            viewModel.sendCommand("PLAY", PanasonicDigaCatalog.OBC.PLAY, isVcr = false)
            advanceUntilIdle()

            assertEquals(1, fakeTransmitter.transmittedKaseikyo.size)
            val transmission = fakeTransmitter.transmittedKaseikyo[0]
            assertEquals(DigaDevice.DVD_HDD, transmission.first)
            assertEquals(RemoteMode.MODE_1, transmission.second)
            assertEquals(PanasonicDigaCatalog.OBC.PLAY, transmission.third)
            assertEquals(DigaUiStrings.VFD_PULSE_SENT, viewModel.uiState.value.vfdSubText)
        }

    @Test
    fun testVcrCommandDispatchesWithVcrDevice() =
        runTest(testDispatcher) {
            val fakeTransmitter = FakeIrTransmitter(isAvailable = true)
            val pipeline = DigaMacroPipeline(fakeTransmitter, scope = this)
            val viewModel = DigaViewModel(fakeTransmitter, pipeline, ioDispatcher = testDispatcher)

            viewModel.sendCommand("VHS PLAY", PanasonicDigaCatalog.OBC.PLAY, isVcr = true)
            advanceUntilIdle()

            assertEquals(1, fakeTransmitter.transmittedKaseikyo.size)
            val transmission = fakeTransmitter.transmittedKaseikyo[0]
            assertEquals(DigaDevice.VCR, transmission.first)
        }

    @Test
    fun testVolumeCommandDispatchesWithTvDevice() =
        runTest(testDispatcher) {
            val fakeTransmitter = FakeIrTransmitter(isAvailable = true)
            val pipeline = DigaMacroPipeline(fakeTransmitter, scope = this)
            val viewModel = DigaViewModel(fakeTransmitter, pipeline, ioDispatcher = testDispatcher)

            viewModel.sendCommand(DigaUiStrings.CMD_LABEL_VOL_UP, PanasonicDigaCatalog.OBC.VOL_UP)
            advanceUntilIdle()

            assertEquals(1, fakeTransmitter.transmittedKaseikyo.size)
            val transmission = fakeTransmitter.transmittedKaseikyo[0]
            assertEquals(DigaDevice.TV, transmission.first)
            assertEquals(PanasonicDigaCatalog.OBC.VOL_UP, transmission.third)
            assertEquals(DigaUiStrings.VFD_PULSE_SENT, viewModel.uiState.value.vfdSubText)
        }

    @Test
    fun testTransmitFailureIsReportedOnVfd() =
        runTest(testDispatcher) {
            val fakeTransmitter = FakeIrTransmitter(isAvailable = true, shouldFail = true)
            val viewModel =
                DigaViewModel(fakeTransmitter, DigaMacroPipeline(fakeTransmitter, scope = this), ioDispatcher = testDispatcher)

            viewModel.sendCommand("PLAY", PanasonicDigaCatalog.OBC.PLAY)
            advanceUntilIdle()

            assertEquals(DigaUiStrings.VFD_ERROR, viewModel.uiState.value.vfdMainText)
        }

    @Test
    fun testSamsungSpanPreserves37010AndOutOfSpanAdapts() {
        val samsungSpanTransmitter = FakeIrTransmitter(isAvailable = true, supportedFrequencies = listOf(36000, 38000))
        val samsungVm = DigaViewModel(samsungSpanTransmitter, DigaMacroPipeline(samsungSpanTransmitter), ioDispatcher = testDispatcher)
        assertEquals(IrLinkStatus.ONLINE, samsungVm.uiState.value.irLinkStatus)
        assertEquals(37010, samsungVm.uiState.value.resolvedCarrierHz)

        val narrowTransmitter = FakeIrTransmitter(isAvailable = true, supportedFrequencies = listOf(38000, 40000))
        val narrowVm = DigaViewModel(narrowTransmitter, DigaMacroPipeline(narrowTransmitter), ioDispatcher = testDispatcher)
        assertEquals(IrLinkStatus.ADAPTED, narrowVm.uiState.value.irLinkStatus)
        assertEquals(38000, narrowVm.uiState.value.resolvedCarrierHz)
    }

    @Test
    fun testMissingEmitterIsReportedAsNoHardware() {
        val fakeTransmitter = FakeIrTransmitter(isAvailable = false)
        val viewModel = DigaViewModel(fakeTransmitter, DigaMacroPipeline(fakeTransmitter), ioDispatcher = testDispatcher)

        assertEquals(IrLinkStatus.NO_HARDWARE, viewModel.uiState.value.irLinkStatus)
    }

    @Test
    fun testCalibrationIsAppliedAndPersisted() {
        val fakeTransmitter = FakeIrTransmitter(isAvailable = true)
        val store = InMemoryIrConfigStore()
        val viewModel =
            DigaViewModel(fakeTransmitter, DigaMacroPipeline(fakeTransmitter), store, testDispatcher)

        viewModel.onTimingUnitSelected(IrTimingUnit.CARRIER_CYCLES)
        viewModel.onRepeatCountSelected(99)

        assertEquals(IrTimingUnit.CARRIER_CYCLES, fakeTransmitter.config.timingUnit)
        assertEquals(DigaTimings.MAX_FRAME_REPEAT, fakeTransmitter.config.repeatCount)
        assertEquals(fakeTransmitter.config, store.load())
        assertEquals(fakeTransmitter.config, viewModel.uiState.value.irConfig)
    }

    @Test
    fun testStoredConfigIsRestoredOnStart() {
        val fakeTransmitter = FakeIrTransmitter(isAvailable = true)
        val saved = IrTransmitConfig(IrTimingUnit.CARRIER_CYCLES, 2)

        DigaViewModel(fakeTransmitter, DigaMacroPipeline(fakeTransmitter), InMemoryIrConfigStore(saved), testDispatcher)

        assertEquals(saved, fakeTransmitter.config)
    }

    @Test
    fun testBeaconTransmitsOnlyToDiagnosticAddress() =
        runTest(testDispatcher) {
            val fakeTransmitter = FakeIrTransmitter(isAvailable = true)
            val viewModel =
                DigaViewModel(fakeTransmitter, DigaMacroPipeline(fakeTransmitter, scope = this), ioDispatcher = testDispatcher)

            viewModel.onBeaconToggled()
            advanceUntilIdle()

            assertEquals(DigaTimings.BEACON_FRAME_COUNT, fakeTransmitter.transmittedKaseikyo.size)
            assertTrue(fakeTransmitter.transmittedKaseikyo.all { it.first == DigaDevice.DIAGNOSTIC })
            assertFalse(viewModel.uiState.value.beaconRunning)
        }
}
