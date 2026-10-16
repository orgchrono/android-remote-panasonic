package com.vhs.digacommander

import com.vhs.digacommander.data.catalog.PanasonicDigaCatalog
import com.vhs.digacommander.data.constant.DigaTimings
import com.vhs.digacommander.data.ir.IrSignalAdapter
import com.vhs.digacommander.data.ir.KaseikyoProtocol
import com.vhs.digacommander.data.model.DigaDevice
import com.vhs.digacommander.data.model.IrDeviceInfo
import com.vhs.digacommander.data.model.IrFrequencyRange
import com.vhs.digacommander.data.model.IrTimingUnit
import com.vhs.digacommander.data.model.RemoteMode
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure-JVM tests for Samsung Exynos (`GT-N8000` MC96 & `SM-T805M` iCE40 FPGA) adaptation and
 * strict Panasonic DIGA Kaseikyo 37.010 kHz protocol fidelity.
 */
class IrSignalAdapterTest {
    private val samsungDiscreteRanges =
        listOf(
            IrFrequencyRange(30000, 30000),
            IrFrequencyRange(33000, 33000),
            IrFrequencyRange(36000, 36000),
            IrFrequencyRange(38000, 38000),
            IrFrequencyRange(40000, 40000),
            IrFrequencyRange(56000, 56000),
        )

    @Test
    fun carrierInsideRangeIsKept() {
        assertEquals(37010, IrSignalAdapter.resolveCarrier(37010, listOf(IrFrequencyRange(30000, 60000))))
    }

    @Test
    fun unknownRangesKeepRequestedCarrier() {
        assertEquals(37010, IrSignalAdapter.resolveCarrier(37010, emptyList()))
    }

    @Test
    fun samsungDiscreteTablePreservesExactDiga37010HzCarrier() {
        assertTrue(IrSignalAdapter.isCarrierSupported(37010, samsungDiscreteRanges))
        assertEquals(37010, IrSignalAdapter.resolveCarrier(37010, samsungDiscreteRanges))
    }

    @Test
    fun carrierOutsideSpanClampsToNearestBoundary() {
        val narrowRanges = listOf(IrFrequencyRange(38000, 38000), IrFrequencyRange(40000, 40000))
        assertEquals(38000, IrSignalAdapter.resolveCarrier(37010, narrowRanges))
        assertEquals(40000, IrSignalAdapter.resolveCarrier(45000, narrowRanges))
    }

    @Test
    fun microsecondPatternPreservesActivePulsesAndCapsTrailFor16BitSafety() {
        val source = intArrayOf(3459, 1729, 432)
        val encoded = IrSignalAdapter.encodePattern(source, 37010, IrTimingUnit.MICROSECONDS)
        assertArrayEquals(source, encoded)
        encoded[0] = 0
        assertEquals(3459, source[0])

        val fullFrame = KaseikyoProtocol.synthesizePulsePattern(DigaDevice.DVD_HDD.id, RemoteMode.MODE_1, PanasonicDigaCatalog.OBC.POWER)
        val encodedFull = IrSignalAdapter.encodePattern(fullFrame, 37010, IrTimingUnit.MICROSECONDS)
        assertEquals(3459, encodedFull[0])
        assertEquals(1729, encodedFull[1])
        assertEquals(432, encodedFull[2])
        assertTrue("All elements must fit in 16-bit uint16_t", encodedFull.all { it <= DigaTimings.MAX_UINT16_VALUE })
    }

    @Test
    fun cyclePatternProducesExactPanasonicDigaKaseikyoCycles() {
        // At 37010 Hz: 3459 us = 128 cycles, 1729 us = 64 cycles, 432 us = 16 cycles, 1297 us = 48 cycles
        val encoded = IrSignalAdapter.encodePattern(intArrayOf(3459, 1729, 432, 1297), 37010, IrTimingUnit.CARRIER_CYCLES)
        assertArrayEquals(intArrayOf(128, 64, 16, 48), encoded)
    }

    @Test
    fun exynosFpgaPatternCompensatesDoubleDivisionToExactDigaCycles() {
        val fullFrame = KaseikyoProtocol.synthesizePulsePattern(DigaDevice.DVD_HDD.id, RemoteMode.MODE_1, PanasonicDigaCatalog.OBC.POWER)
        val encoded = IrSignalAdapter.encodePattern(fullFrame, 37010, IrTimingUnit.EXYNOS_FPGA)

        // Total duration must stay under AOSP ConsumerIrService.MAX_XMIT_TIME (2_000_000)
        val totalSum = encoded.sumOf { it.toLong() }
        assertTrue(
            "Total scaled pattern sum ($totalSum) must be <= ${DigaTimings.MAX_AOSP_XMIT_TIME_US}",
            totalSum <= DigaTimings.MAX_AOSP_XMIT_TIME_US,
        )

        // Simulate LineageOS ConsumerIr.cpp division (/ 27) followed by ice4_fpga.c division (/ 27)
        val factor = 1_000_000 / 37010 // 27
        val afterHal = encoded.map { it / factor }
        val afterKernel = afterHal.map { it / factor }

        assertEquals(128, afterKernel[0]) // Lead-in mark = 128 cycles
        assertEquals(64, afterKernel[1]) // Lead-in space = 64 cycles
        assertEquals(16, afterKernel[2]) // Bit mark = 16 cycles
        assertTrue("No pulse in the 100-element frame may become zero after double division", afterKernel.all { it > 0 })
    }

    @Test
    fun cyclePatternNeverProducesZero() {
        val encoded = IrSignalAdapter.encodePattern(intArrayOf(1), 37010, IrTimingUnit.CARRIER_CYCLES)
        assertEquals(1, encoded[0])
    }

    @Test
    fun hardwareDetectionSelectsCorrectModeForN8000AndT805M() {
        val n8000 = IrDeviceInfo(manufacturer = "samsung", model = "GT-N8000", hardware = "smdk4x12", sdkInt = 30)
        assertEquals(IrTimingUnit.CARRIER_CYCLES, IrSignalAdapter.recommendedTimingUnit(n8000))

        val t805m = IrDeviceInfo(manufacturer = "samsung", model = "SM-T805M", hardware = "universal5420", sdkInt = 23)
        assertEquals(IrTimingUnit.EXYNOS_FPGA, IrSignalAdapter.recommendedTimingUnit(t805m))

        val sysfsMc96 = IrSignalAdapter.formatSysfsPayload(intArrayOf(3459, 1729, 432), 37010, expectsCyclesInSysfs = true)
        assertEquals("37010,128,64,16", sysfsMc96)

        val sysfsFpga = IrSignalAdapter.formatSysfsPayload(intArrayOf(3459, 1729, 432), 37010, expectsCyclesInSysfs = false)
        assertEquals("37010,3459,1729,432", sysfsFpga)
    }
}
