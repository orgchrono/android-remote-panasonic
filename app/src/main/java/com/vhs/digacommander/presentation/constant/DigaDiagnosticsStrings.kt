package com.vhs.digacommander.presentation.constant

/**
 * Single Source of Truth (SSOT) for the IR Link diagnostics screen copy.
 * Kept separate from [DigaUiStrings] (SRP: one feature, one string catalogue).
 */
object DigaDiagnosticsStrings {
    // Cards
    const val CARD_STATUS: String = "IR EMITTER STATUS"
    const val CARD_CAMERA_TEST: String = "STEP 1 - CAMERA TEST"
    const val CARD_CALIBRATION: String = "STEP 2 - CALIBRATION"

    // Status rows
    const val ROW_DEVICE: String = "Device"
    const val ROW_ANDROID: String = "Android"
    const val ROW_SOC: String = "Chipset"
    const val ROW_SERVICE: String = "IR service"
    const val ROW_EMITTER: String = "IR emitter"
    const val ROW_RANGES: String = "Carrier ranges"
    const val ROW_EMITTING_AT: String = "Emitting at"
    const val VALUE_YES: String = "Yes"
    const val VALUE_NO: String = "No"
    const val VALUE_UNKNOWN: String = "unknown"
    const val VALUE_NONE_REPORTED: String = "none reported by driver"
    const val ANDROID_API_PREFIX: String = " (API "
    const val ANDROID_API_SUFFIX: String = ")"
    const val HZ_SUFFIX: String = " Hz"
    const val KHZ_SUFFIX: String = " kHz"
    const val RANGE_SEPARATOR: String = " - "
    const val LIST_SEPARATOR: String = ",  "

    // Verdicts
    const val VERDICT_NO_HARDWARE_TITLE: String = "No IR emitter detected"
    const val VERDICT_NO_HARDWARE_BODY: String =
        "Android reports that this device has no usable infrared emitter, so the app cannot transmit signals.\n\n" +
            "- Verify that your smartphone or tablet has a built-in hardware Consumer IR blaster.\n" +
            "- On custom firmware, ensure the Consumer IR HAL (android.hardware.consumerir) or kernel IR sysfs node is enabled.\n" +
            "- Emulators and devices without an infrared LED always show this message."
    const val VERDICT_ADAPTED_TITLE: String = "Emitter ready (carrier adapted)"
    const val VERDICT_ADAPTED_BODY: String =
        "The driver does not report Panasonic's exact 37.01 kHz carrier natively, so the app emits the nearest " +
            "supported frequency. Recorders normally tolerate this small deviation; keep the device within " +
            "2 meters and aimed directly at the recorder's sensor."
    const val VERDICT_ONLINE_TITLE: String = "Emitter ready"
    const val VERDICT_ONLINE_BODY: String =
        "The driver accepts the Panasonic 37.01 kHz Kaseikyo carrier natively. Continue with the camera test to " +
            "confirm physical LED emission."

    // Camera test
    const val CAMERA_STEPS: String =
        "1. Open the camera app on another device and select the FRONT camera (which normally lacks an IR cut filter).\n" +
            "2. Aim it directly at this device's IR window (typically on the top or side edge) in a dimly lit room.\n" +
            "3. Press the button below. A steady violet flicker on the camera viewfinder confirms the IR LED is firing.\n" +
            "4. If no light appears, check the hardware emitter status above."
    const val BTN_BEACON_START: String = "START IR BEACON (4 s)"
    const val BTN_BEACON_STOP: String = "STOP BEACON"
    const val BEACON_NOTE: String = "The beacon is addressed to an unassigned device ID (0xFE), so no recorder or TV reacts to it."

    // Calibration
    const val CALIBRATION_INTRO: String =
        "All modes emit strict Panasonic DIGA Kaseikyo pulses (37.01 kHz, 128/64/16/48 cycles) at the physical LED. " +
            "The optimal encoding mode is auto-detected from the hardware driver and can be overridden below:"
    const val LABEL_TIMING_UNIT: String = "Pulse timing encoding mode"
    const val UNIT_MICROSECONDS: String = "Standard (us)"
    const val UNIT_CYCLES: String = "Carrier Cycles"
    const val UNIT_PERIOD_SCALED: String = "Period Scaled"
    const val HINT_TIMING_UNIT: String =
        "- Standard (us): Standard Android ConsumerIrManager contract in microseconds (most smartphones and tablets).\n" +
            "- Carrier Cycles: Encodes intervals as carrier wave cycle counts (128, 64, 16, 48) for cycle-register I2C controllers.\n" +
            "- Period Scaled: Pre-scales intervals by the carrier period (27x at 37.01 kHz) to compensate for double-dividing HAL + FPGA drivers."
    const val LABEL_REPEAT: String = "Frames per key press"
    const val REPEAT_SUFFIX: String = "x"
    const val HINT_REPEAT: String = "Send each frame more than once if the recorder only reacts intermittently or from close range."
    const val BTN_RESCAN: String = "RE-SCAN EMITTER"
}
