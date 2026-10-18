package com.vhs.digacommander.data.model

import com.vhs.digacommander.data.constant.DigaTimings

/**
 * Encoding unit for pulse intervals handed to the platform infrared transmitter.
 *
 * The standard Android contract is [MICROSECONDS]. Certain hardware controllers and legacy
 * HAL implementations interpret intervals as carrier wave cycle counts ([CARRIER_CYCLES]) or
 * divide twice by the carrier period across HAL and kernel ([PERIOD_SCALED]).
 */
enum class IrTimingUnit {
    /** Standard Android ConsumerIrManager contract: alternating mark/space durations in microseconds. */
    MICROSECONDS,

    /** Cycle-register controller mode: pulse durations expressed in carrier-wave cycle counts. */
    CARRIER_CYCLES,

    /**
     * Period-scaled compensation mode: pre-multiplies durations by the carrier period
     * (1_000_000 / carrierHz) for drivers that divide in both the HAL and kernel FPGA layer.
     */
    PERIOD_SCALED,
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
