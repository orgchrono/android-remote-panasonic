package com.vhs.digacommander.data.ir

import com.vhs.digacommander.data.constant.DigaTimings
import com.vhs.digacommander.data.model.IrDeviceInfo
import com.vhs.digacommander.data.model.IrFrequencyRange
import com.vhs.digacommander.data.model.IrTimingUnit
import kotlin.math.abs

/**
 * Pure functions adapting a nominal Panasonic DIGA Kaseikyo IR signal to platform emitter drivers
 * while preserving strict 37.010 kHz protocol timing at the physical IR LED.
 * Kept free of Android framework types so all hardware adaptations are 100% unit-testable on the JVM.
 */
object IrSignalAdapter {
    /**
     * Checks whether [requestedHz] lies within the hardware's overall carrier span.
     *
     * Certain HAL headers publish discrete point entries (e.g. 30, 33, 36, 38, 40, 56 kHz),
     * yet the underlying hardware clock dividers accept any continuous frequency within the
     * min..max span (including Panasonic's exact 37010 Hz).
     */
    fun isCarrierSupported(
        requestedHz: Int,
        ranges: List<IrFrequencyRange>,
    ): Boolean {
        if (ranges.isEmpty() || ranges.any { requestedHz in it }) return true
        val minSupported = ranges.minOf { it.minHz }
        val maxSupported = ranges.maxOf { it.maxHz }
        return requestedHz in minSupported..maxSupported
    }

    /**
     * Resolves the carrier frequency handed to the driver.
     *
     * Preserves [requestedHz] (37010 Hz for Panasonic DIGA) whenever it falls within the hardware's
     * min..max frequency span so the physical Kaseikyo signal is never distorted. Only clamps to
     * the nearest boundary if [requestedHz] lies completely outside the hardware span.
     */
    fun resolveCarrier(
        requestedHz: Int,
        ranges: List<IrFrequencyRange>,
    ): Int {
        if (isCarrierSupported(requestedHz, ranges)) return requestedHz
        return ranges
            .flatMap { listOf(it.minHz, it.maxHz) }
            .minByOrNull { abs(it - requestedHz) } ?: requestedHz
    }

    /**
     * Selects the optimal default [IrTimingUnit] for `ConsumerIrManager.transmit` based on the
     * detected kernel driver architecture and SoC platform family:
     * - Cycle-register I2C MCU controllers: [IrTimingUnit.CARRIER_CYCLES]
     * - Period-dividing FPGA controllers: [IrTimingUnit.PERIOD_SCALED]
     * - Standard Android ConsumerIrManager implementations: [IrTimingUnit.MICROSECONDS]
     */
    fun recommendedTimingUnit(
        device: IrDeviceInfo,
        hasSecIrNode: Boolean = false,
        hasFpgaNode: Boolean = false,
    ): IrTimingUnit {
        if (isPeriodDividingFpgaDriver(device, hasFpgaNode)) {
            return IrTimingUnit.PERIOD_SCALED
        }
        if (isCycleRegisterMcuDriver(device, hasSecIrNode, hasFpgaNode)) {
            return IrTimingUnit.CARRIER_CYCLES
        }
        return IrTimingUnit.MICROSECONDS
    }

    /**
     * Returns true when the platform uses an FPGA kernel driver (`ice4_fpga`) that divides
     * sysfs/HAL pulse durations by `1_000_000 / ir_freq` in kernel space.
     */
    fun isPeriodDividingFpgaDriver(
        device: IrDeviceInfo,
        hasFpgaNode: Boolean = false,
    ): Boolean {
        if (hasFpgaNode) return true
        val platform = device.hardware.lowercase()
        return FPGA_SOC_PLATFORMS.any { it in platform }
    }

    /**
     * Returns true when the platform uses a cycle-register I2C microcontroller driver (`mc96`)
     * that expects raw carrier cycle counts rather than microsecond durations.
     */
    fun isCycleRegisterMcuDriver(
        device: IrDeviceInfo,
        hasSecIrNode: Boolean = false,
        hasFpgaNode: Boolean = false,
    ): Boolean {
        if (hasSecIrNode && !hasFpgaNode && !isPeriodDividingFpgaDriver(device, false)) return true
        val platform = device.hardware.lowercase()
        return CYCLE_MCU_SOC_PLATFORMS.any { it in platform }
    }

    /**
     * Encodes a microsecond pattern for `ConsumerIrManager.transmit` according to [unit]:
     * - [IrTimingUnit.MICROSECONDS]: preserves exact Panasonic DIGA microsecond intervals, capping
     *   only the final trailing LED-off quiet gap at [DigaTimings.MAX_HARDWARE_TRAIL_SPACE_US]
     *   (45 000 us) so 16-bit `uint16_t` driver registers (max 65 535) never overflow.
     * - [IrTimingUnit.CARRIER_CYCLES]: converts each interval into exact carrier cycles
     *   (128, 64, 16, 48 cycles at 37.01 kHz) for cycle-register IR controllers.
     * - [IrTimingUnit.PERIOD_SCALED]: pre-scales each microsecond interval by `1_000_000 / carrierHz`
     *   (27x at 37.01 kHz) while keeping total frame sum within [DigaTimings.MAX_AOSP_XMIT_TIME_US],
     *   cancelling double-division across HAL and kernel FPGA so the emitter outputs exact
     *   128, 64, 16, 48 DIGA cycles.
     */
    fun encodePattern(
        patternMicroseconds: IntArray,
        carrierHz: Int,
        unit: IrTimingUnit,
    ): IntArray {
        if (patternMicroseconds.isEmpty()) return IntArray(0)
        val safeCarrier = carrierHz.coerceAtLeast(1)
        return when (unit) {
            IrTimingUnit.MICROSECONDS ->
                IntArray(patternMicroseconds.size) { index ->
                    val us = patternMicroseconds[index].coerceAtLeast(1)
                    if (index == patternMicroseconds.lastIndex &&
                        patternMicroseconds.size >= DigaTimings.KASEIKYO_FRAME_TRANSITION_COUNT
                    ) {
                        us.coerceAtMost(DigaTimings.MAX_HARDWARE_TRAIL_SPACE_US)
                    } else {
                        us
                    }
                }

            IrTimingUnit.CARRIER_CYCLES ->
                IntArray(patternMicroseconds.size) { index ->
                    val cycles =
                        (patternMicroseconds[index].toLong() * safeCarrier + DigaTimings.MICROS_PER_SECOND / 2) /
                            DigaTimings.MICROS_PER_SECOND
                    cycles.toInt().coerceIn(1, DigaTimings.MAX_UINT16_VALUE)
                }

            IrTimingUnit.PERIOD_SCALED -> {
                val factor = (DigaTimings.MICROS_PER_SECOND / safeCarrier).toInt().coerceAtLeast(1)
                val out = IntArray(patternMicroseconds.size)
                var runningSum = 0L
                val lastIdx = patternMicroseconds.lastIndex
                for (i in 0 until lastIdx) {
                    val scaled = (patternMicroseconds[i].coerceAtLeast(1) * factor)
                    out[i] = scaled
                    runningSum += scaled
                }
                val minTrail = DigaTimings.PULSE_BIT_MARK_US * factor
                val desiredTrail = patternMicroseconds[lastIdx].coerceAtMost(DigaTimings.MAX_HARDWARE_TRAIL_SPACE_US) * factor
                val remainingBudget = (DigaTimings.MAX_AOSP_XMIT_TIME_US - runningSum).toInt()
                out[lastIdx] = desiredTrail.coerceAtMost(remainingBudget.coerceAtLeast(minTrail))
                out
            }
        }
    }

    /**
     * Formats a comma-separated payload for direct writes to sysfs `ir_send` nodes
     * or legacy vendor IR system services:
     * - Cycle-register MCU drivers ([expectsCyclesInSysfs] = true) expect carrier cycles.
     * - Period-dividing FPGA drivers ([expectsCyclesInSysfs] = false) divide by `1_000_000 / freq`
     *   in the kernel, so they expect microseconds directly.
     */
    fun formatSysfsPayload(
        patternMicroseconds: IntArray,
        carrierHz: Int,
        expectsCyclesInSysfs: Boolean,
    ): String {
        val unit = if (expectsCyclesInSysfs) IrTimingUnit.CARRIER_CYCLES else IrTimingUnit.MICROSECONDS
        val encoded = encodePattern(patternMicroseconds, carrierHz, unit)
        val sb = StringBuilder(encoded.size * 6 + 8)
        sb.append(carrierHz)
        for (value in encoded) {
            sb.append(',').append(value)
        }
        return sb.toString()
    }

    /** SoC board platform identifiers using cycle-register I2C IR microcontrollers. */
    private val CYCLE_MCU_SOC_PLATFORMS =
        listOf(
            "4x12",
            "4412",
            "4210",
        )

    /** SoC board platform identifiers using period-dividing iCE40 FPGA IR controllers. */
    private val FPGA_SOC_PLATFORMS =
        listOf(
            "5420",
            "5422",
            "5430",
            "5433",
        )
}
