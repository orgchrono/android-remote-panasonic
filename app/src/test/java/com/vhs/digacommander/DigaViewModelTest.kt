package com.vhs.digacommander

import com.vhs.digacommander.data.catalog.PanasonicDigaCatalog
import com.vhs.digacommander.data.constant.DigaTimings
import com.vhs.digacommander.data.ir.InMemoryIrConfigStore
import com.vhs.digacommander.data.keymap.InMemoryKeymapStore
import com.vhs.digacommander.data.model.DigaDevice
import com.vhs.digacommander.data.model.DynamicRemoteKey
import com.vhs.digacommander.data.model.IrTimingUnit
import com.vhs.digacommander.data.model.IrTransmitConfig
import com.vhs.digacommander.data.model.KeyProtocol
import com.vhs.digacommander.data.model.KeySection
import com.vhs.digacommander.data.model.KeyTone
import com.vhs.digacommander.data.model.RemoteMode
import com.vhs.digacommander.domain.keymap.DynamicKeymapAssembler
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
        assertFalse(state.isVcrDeck)
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
    fun testDeckToggleAndModeSelection() {
        val fakeTransmitter = FakeIrTransmitter(isAvailable = true)
        val pipeline = DigaMacroPipeline(fakeTransmitter)
        val viewModel = DigaViewModel(fakeTransmitter, pipeline)

        viewModel.onToggleVcrDeck()
        assertTrue(viewModel.uiState.value.isVcrDeck)
        assertEquals(DigaUiStrings.VFD_DECK_VCR_MAIN, viewModel.uiState.value.vfdMainText)
        assertEquals(DigaUiStrings.VFD_DECK_VCR_SUB, viewModel.uiState.value.vfdSubText)

        viewModel.onToggleVcrDeck()
        assertFalse(viewModel.uiState.value.isVcrDeck)
        assertEquals(DigaUiStrings.VFD_DECK_DVD_MAIN, viewModel.uiState.value.vfdMainText)
        assertEquals(DigaUiStrings.VFD_DECK_DVD_SUB, viewModel.uiState.value.vfdSubText)

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
            viewModel.sendCommand("STOP", PanasonicDigaCatalog.OBC.STOP, isVcr = false)
            viewModel.sendCommand("AUDIO", PanasonicDigaCatalog.OBC.AUDIO, isVcr = false)
            viewModel.sendCommand("DISPLAY", PanasonicDigaCatalog.OBC.DISPLAY, isVcr = false)
            viewModel.sendCommand("ENTER", PanasonicDigaCatalog.OBC.ENTER_OK, isVcr = false)
            viewModel.sendCommand("FUNCTIONS", PanasonicDigaCatalog.OBC.FUNCTIONS, isVcr = false)
            viewModel.sendCommand("MEDIA_SERVER", PanasonicDigaCatalog.OBC.MEDIA_SERVER, isVcr = false)
            advanceUntilIdle()

            assertEquals(7, fakeTransmitter.transmittedKaseikyo.size)
            assertEquals(
                listOf(0x0A, 0x00, 0x33, 0x92, 0x82, 0x57, 0x04),
                fakeTransmitter.transmittedKaseikyo.map { it.third },
            )
            assertTrue(fakeTransmitter.transmittedKaseikyo.all { it.first == DigaDevice.DVD_HDD && it.second == RemoteMode.MODE_1 })
            assertEquals(DigaUiStrings.VFD_PULSE_SENT, viewModel.uiState.value.vfdSubText)
        }

    @Test
    fun testVcrCommandDispatchesWithVcrDevice() =
        runTest(testDispatcher) {
            val fakeTransmitter = FakeIrTransmitter(isAvailable = true)
            val pipeline = DigaMacroPipeline(fakeTransmitter, scope = this)
            val viewModel = DigaViewModel(fakeTransmitter, pipeline, ioDispatcher = testDispatcher)

            viewModel.onToggleVcrDeck()
            viewModel.sendCommand("PLAY", PanasonicDigaCatalog.OBC.PLAY)
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
    fun testDiscreteSpanPreserves37010AndOutOfSpanAdapts() {
        val spanTransmitter = FakeIrTransmitter(isAvailable = true, supportedFrequencies = listOf(36000, 38000))
        val spanVm = DigaViewModel(spanTransmitter, DigaMacroPipeline(spanTransmitter), ioDispatcher = testDispatcher)
        assertEquals(IrLinkStatus.ONLINE, spanVm.uiState.value.irLinkStatus)
        assertEquals(37010, spanVm.uiState.value.resolvedCarrierHz)

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

    @Test
    fun testVcrRewindFastForwardExtendedExitAndTv0x80Dispatch() =
        runTest(testDispatcher) {
            assertEquals(0x80, DigaDevice.TV.id)

            val fakeTransmitter = FakeIrTransmitter(isAvailable = true)
            val pipeline = DigaMacroPipeline(fakeTransmitter, scope = this)
            val viewModel = DigaViewModel(fakeTransmitter, pipeline, ioDispatcher = testDispatcher)

            viewModel.onToggleVcrDeck()
            viewModel.sendCommand(DigaUiStrings.CMD_LABEL_REWIND, PanasonicDigaCatalog.OBC.REWIND)
            viewModel.sendCommand(DigaUiStrings.CMD_LABEL_FAST_FORWARD, PanasonicDigaCatalog.OBC.FAST_FORWARD)
            viewModel.onToggleVcrDeck()
            viewModel.sendCommand(DigaUiStrings.CMD_LABEL_EXIT, PanasonicDigaCatalog.OBC.EXIT)
            advanceUntilIdle()

            assertEquals(2, fakeTransmitter.transmittedKaseikyo.size)
            assertEquals(
                DigaDevice.VCR to 0x02,
                fakeTransmitter.transmittedKaseikyo[0].first to fakeTransmitter.transmittedKaseikyo[0].third,
            )
            assertEquals(
                DigaDevice.VCR to 0x03,
                fakeTransmitter.transmittedKaseikyo[1].first to fakeTransmitter.transmittedKaseikyo[1].third,
            )

            assertEquals(1, fakeTransmitter.transmittedSignals.size)
            val exitPattern = fakeTransmitter.transmittedSignals[0].patternMicroseconds
            val expectedExit = KaseikyoPulseDecoder.decode(exitPattern)
            assertEquals(listOf(0x02, 0x20, 0xB0, 0x01, 0x51, 0xE0), expectedExit)
        }

    @Test
    fun testDynamicKeymapRuntimeEditsAndTransmission() =
        runTest(testDispatcher) {
            val fakeTransmitter = FakeIrTransmitter(isAvailable = true)
            val pipeline = DigaMacroPipeline(fakeTransmitter, scope = this)
            val keymapStore = InMemoryKeymapStore()
            val viewModel =
                DigaViewModel(
                    transmitter = fakeTransmitter,
                    macroPipeline = pipeline,
                    ioDispatcher = testDispatcher,
                    keymapStore = keymapStore,
                )

            val customKey =
                DynamicRemoteKey(
                    id = "custom_vhs_in",
                    label = "INPUT VHS",
                    section = KeySection.POWER_INPUT,
                    protocol = KeyProtocol.KASEIKYO,
                    device = 0x90,
                    subdevice = 0x05,
                    obc = 0xE2,
                    respectRemoteMode = true,
                    tone = KeyTone.WARNING,
                )
            viewModel.toggleMapEditMode()
            assertTrue(viewModel.uiState.value.isMapEditMode)
            viewModel.upsertDynamicKey(customKey)
            viewModel.sendDynamicKey(customKey)
            advanceUntilIdle()

            assertEquals(1, fakeTransmitter.transmittedSignals.size)
            assertEquals(
                listOf(0x02, 0x20, 0x90, 0x05, 0xE2, 0x77),
                KaseikyoPulseDecoder.decode(fakeTransmitter.transmittedSignals[0].patternMicroseconds),
            )

            // Verify persisted across fresh ViewModel instance
            val reloadedVm =
                DigaViewModel(
                    transmitter = FakeIrTransmitter(),
                    macroPipeline = DigaMacroPipeline(FakeIrTransmitter(), scope = this),
                    ioDispatcher = testDispatcher,
                    keymapStore = keymapStore,
                )
            assertTrue(reloadedVm.uiState.value.activeDynamicProfile.keys.any { it.id == "custom_vhs_in" })
            assertTrue(DynamicKeymapAssembler.exportToIrplusXml(reloadedVm.uiState.value.activeDynamicProfile).contains("INPUT VHS"))
        }

    private object KaseikyoPulseDecoder {
        fun decode(pattern: IntArray): List<Int> =
            List(6) { byteIdx ->
                var v = 0
                for (bit in 0 until 8) {
                    val space = pattern[2 + (byteIdx * 8 + bit) * 2 + 1]
                    if (space > 900) v = v or (1 shl bit)
                }
                v
            }
    }
}
