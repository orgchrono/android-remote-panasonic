package com.vhs.digacommander.data.constant

/**
 * Single Source of Truth (SSOT) for all physical IR timings, carrier frequencies,
 * and hardware safety delays.
 *
 * All time values are defined in milliseconds or microseconds as annotated.
 */
object DigaTimings {
    // Carrier Frequencies (Hz)
    const val CARRIER_MATSUSHITA_HZ: Int = 37010
    const val CARRIER_NOMINAL_38K_HZ: Int = 38029
    const val CARRIER_FREQ_DIVISOR_BASE: Double = 0.241246

    // Physical Pulse Durations (Microseconds)
    const val PULSE_LEAD_IN_MARK_US: Int = 3459
    const val PULSE_LEAD_IN_SPACE_US: Int = 1729
    const val PULSE_BIT_MARK_US: Int = 432
    const val PULSE_BIT_0_SPACE_US: Int = 432
    const val PULSE_BIT_1_SPACE_US: Int = 1297
    const val PULSE_TRAIL_MARK_US: Int = 432
    const val PULSE_TRAIL_SPACE_US: Int = 74720

    // Monotonic Anti-Brick Inter-Frame Delays (Milliseconds)
    const val DELAY_STANDARD_COMMAND_MS: Long = 100L
    const val DELAY_SERVICE_DIGIT_MS: Long = 500L
    const val DELAY_FAST_ZIP_DIGIT_MS: Long = 300L
    const val DELAY_OSD_NAV_STEP_MS: Long = 600L
    const val DELAY_OSD_SUBMENU_ENTER_MS: Long = 800L
    const val DELAY_OSD_INITIAL_OPEN_MS: Long = 1200L

    // Pulse Count Rules
    const val KASEIKYO_FRAME_TRANSITION_COUNT: Int = 100
    const val KASEIKYO_PAYLOAD_BYTE_COUNT: Int = 6
    const val KASEIKYO_DATA_BIT_COUNT: Int = 48
    const val PRONTO_HEADER_WORD_COUNT: Int = 4
    const val PRONTO_TOTAL_WORD_COUNT: Int = 104
}
