package com.vhs.digacommander.data.ir

import com.vhs.digacommander.data.constant.DigaTimings
import com.vhs.digacommander.data.model.IrDeviceInfo
import com.vhs.digacommander.data.model.IrFrequencyRange
import com.vhs.digacommander.data.model.IrTimingUnit
import kotlin.math.abs

/**
 * Pure functions adapting a nominal Panasonic DIGA Kaseikyo IR signal to concrete Samsung/Exynos
 * emitter drivers while preserving strict 37.010 kHz protocol timing at the physical IR LED.
 * Kept free of Android framework types so all hardware quirks are 100% unit-testable on the JVM.
 */
object IrSignalAdapter {
    /**
     * Checks whether [requestedHz] lies within the hardware's overall carrier span.
     *
     * Samsung's `samsung_ir.h` HAL header publishes six discrete point entries (30, 33, 36, 38,
     * 40, 56 kHz), yet both `ir_remote_con_mc96.c` (GT-N8000) and `ice4_fpga.c` (SM-T805M) accept
     * any continuous frequency in that span (including Panasonic's exact 37010 Hz) and compute
     * the hardware clock divider directly from `carrierFreq`.
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
     * detected Samsung Exynos board/model:
     * - GT-N8000 / Exynos 4412 (`mc96` driver + LineageOS 18.1 non-dividing HAL): [IrTimingUnit.CARRIER_CYCLES]
     * - SM-T805M / Exynos 5420 (`ice4_fpga` driver + dividing HAL): [IrTimingUnit.EXYNOS_FPGA]
     * - All other devices: [IrTimingUnit.MICROSECONDS]
     */
    fun recommendedTimingUnit(device: IrDeviceInfo): IrTimingUnit {
        val signature = "${device.model} ${device.hardware}".lowercase()
        return when {
            MC96_SIGNATURES.any { it in signature } -> IrTimingUnit.CARRIER_CYCLES
            FPGA_SIGNATURES.any { it in signature } -> IrTimingUnit.EXYNOS_FPGA
            else -> IrTimingUnit.MICROSECONDS
        }
    }

    /**
     * Returns true when the device uses the Samsung Exynos 5420 iCE40 FPGA kernel driver
     * (`drivers/ice4_fpga/ice4_fpga.c`), which divides `/sys/class/sec/sec_ir/ir_send` values
     * by `1_000_000 / ir_freq` in kernel space.
     */
    fun isExynosFpgaDevice(
        device: IrDeviceInfo,
        hasIrdaTestNode: Boolean = false,
    ): Boolean {
        if (hasIrdaTestNode) return true
        val signature = "${device.model} ${device.hardware}".lowercase()
        return FPGA_SIGNATURES.any { it in signature }
    }

    /**
     * Encodes a microsecond pattern for `ConsumerIrManager.transmit` according to [unit]:
     * - [IrTimingUnit.MICROSECONDS]: preserves exact Panasonic DIGA microsecond intervals, capping
     *   only the final trailing LED-off quiet gap at [DigaTimings.MAX_HARDWARE_TRAIL_SPACE_US]
     *   (45 000 us) so 16-bit `uint16_t` driver registers (max 65 535) never overflow.
     * - [IrTimingUnit.CARRIER_CYCLES]: converts each interval into exact carrier cycles
     *   (128, 64, 16, 48 cycles at 37.01 kHz) for Samsung `ir_remote_con_mc96` (GT-N8000).
     * - [IrTimingUnit.EXYNOS_FPGA]: pre-scales each microsecond interval by `1_000_000 / carrierHz`
     *   (27x at 37.01 kHz) while keeping total frame sum within [DigaTimings.MAX_AOSP_XMIT_TIME_US],
     *   cancelling the double-division in `ConsumerIr.cpp` + `ice4_fpga.c` (SM-T805M) so the FPGA
     *   emits exact 128, 64, 16, 48 DIGA cycles.
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

            IrTimingUnit.EXYNOS_FPGA -> {
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
     * Formats a comma-separated payload for direct writes to `/sys/class/sec/sec_ir/ir_send`
     * or Samsung's legacy `irda.write_irsend(String)` service:
     * - `ir_remote_con_mc96.c` (GT-N8000, [expectsCyclesInSysfs] = true) expects carrier cycles.
     * - `ice4_fpga.c` (SM-T805M, [expectsCyclesInSysfs] = false) divides by `1_000_000 / freq` in
     *   the kernel, so it expects microseconds directly.
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

    private val MC96_SIGNATURES =
        listOf(
            "n8000",
            "n8010",
            "n8013",
            "n8020",
            "p4note",
            "smdk4x12",
            "smdk4412",
            "exynos4412",
            "p3100",
            "p5100",
            "note 10.1",
        )

    private val FPGA_SIGNATURES =
        listOf(
            "t805",
            "t800",
            "t705",
            "t700",
            "p600",
            "p605",
            "p900",
            "p905",
            "exynos5420",
            "universal5420",
            "chagall",
            "klimt",
        )
}
