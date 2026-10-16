package com.vhs.digacommander.data.model

import com.vhs.digacommander.data.constant.DigaTimings

/**
 * Unit in which the pulse pattern is handed to the platform [android.hardware.ConsumerIrManager].
 *
 * The AOSP contract is [MICROSECONDS]. Some Samsung firmwares (TouchWiz era, Exynos tablets)
 * historically interpreted the same array as a number of carrier cycles, so the app lets the
 * operator switch units and validate the result visually with a phone camera.
 */
enum class IrTimingUnit {
    /** Standard AOSP contract: alternating mark/space durations in microseconds. */
    MICROSECONDS,

    /** Samsung MC96 / legacy behaviour (e.g. GT-N8000): durations in carrier-wave cycles. */
    CARRIER_CYCLES,

    /**
     * Samsung Exynos 5420 iCE40 FPGA compensation (e.g. SM-T805M on ROMs where both the
     * ConsumerIR HAL and the ice4_fpga kernel driver divide by 1_000_000 / carrierHz).
     */
    EXYNOS_FPGA,
}

/**
 * Immutable user-tunable transmission settings.
 *
 * @property timingUnit Unit used to encode the pattern for the hardware driver.
 * @property repeatCount How many times each frame is emitted back-to-back (1..[DigaTimings.MAX_FRAME_REPEAT]).
 */
data class IrTransmitConfig(
    val timingUnit: IrTimingUnit = IrTimingUnit.MICROSECONDS,
    val repeatCount: Int = DigaTimings.DEFAULT_FRAME_REPEAT,
) {
    init {
        require(repeatCount in DigaTimings.DEFAULT_FRAME_REPEAT..DigaTimings.MAX_FRAME_REPEAT) {
            "repeatCount must be within ${DigaTimings.DEFAULT_FRAME_REPEAT}..${DigaTimings.MAX_FRAME_REPEAT}"
        }
    }
}
