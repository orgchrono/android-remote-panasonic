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
        "Android reports that this device has no usable infrared emitter, so the app cannot send anything.\n\n" +
            "- Stock Samsung firmware (e.g. SM-T805M): the IR LED or its driver is faulty or disabled.\n" +
            "- Custom ROM (e.g. LineageOS on GT-N8000): the ROM must ship the consumer IR HAL and the " +
            "feature flag android.hardware.consumerir. Flash a build that lists 'consumerir' as working.\n" +
            "- Emulators and phones without an IR LED always show this message."
    const val VERDICT_ADAPTED_TITLE: String = "Emitter ready (carrier adapted)"
    const val VERDICT_ADAPTED_BODY: String =
        "The driver does not accept Panasonic's exact 37.01 kHz carrier, so the app emits the nearest " +
            "supported frequency. Recorders normally tolerate this small deviation; keep the tablet close " +
            "(under 2 m) and aimed at the recorder."
    const val VERDICT_ONLINE_TITLE: String = "Emitter ready"
    const val VERDICT_ONLINE_BODY: String =
        "The driver accepts the Panasonic carrier natively. Continue with the camera test to confirm the " +
            "LED really fires."

    // Camera test
    const val CAMERA_STEPS: String =
        "1. Open the camera app on another phone and use the FRONT camera (it usually has no IR filter).\n" +
            "2. Aim it at the tablet's IR window, normally on the top edge. Dim the room lights.\n" +
            "3. Press the button below. A steady violet flicker on the camera screen means the LED fires.\n" +
            "4. No light at all? The problem is the emitter or ROM, not the timing: see the status above."
    const val BTN_BEACON_START: String = "START IR BEACON (4 s)"
    const val BTN_BEACON_STOP: String = "STOP BEACON"
    const val BEACON_NOTE: String = "The beacon is addressed to an unused device id, so no recorder or TV reacts to it."

    // Calibration
    const val CALIBRATION_INTRO: String =
        "All modes emit strict Panasonic DIGA Kaseikyo pulses (37.01 kHz, 128/64/16/48 cycles) at the physical LED. " +
            "Select the driver mode matching your Samsung Exynos tablet:"
    const val LABEL_TIMING_UNIT: String = "Samsung Exynos IR driver mode"
    const val UNIT_MICROSECONDS: String = "Standard (us)"
    const val UNIT_CYCLES: String = "MC96 Cycles (N8000)"
    const val UNIT_EXYNOS_FPGA: String = "FPGA 27x (T805M)"
    const val HINT_TIMING_UNIT: String =
        "- Standard (us): AOSP ConsumerIrManager contract. Use on non-Samsung devices or standard ROMs.\n" +
            "- MC96 Cycles (N8000): For Galaxy Note 10.1 (GT-N8000) on LineageOS 18.1 / KitKat (128, 64, 16, 48 cycles).\n" +
            "- FPGA 27x (T805M): For Galaxy Tab S (SM-T805M) to compensate for HAL + ice4_fpga.c double-division."
    const val LABEL_REPEAT: String = "Frames per key press"
    const val REPEAT_SUFFIX: String = "x"
    const val HINT_REPEAT: String = "Send each frame more than once if the recorder only reacts to some presses or from very close."
    const val BTN_RESCAN: String = "RE-SCAN EMITTER"
}
