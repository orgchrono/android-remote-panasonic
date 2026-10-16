package com.vhs.digacommander.data.model

/**
 * Primary Device ID addresses in the Panasonic Kaseikyo 48-bit IR protocol.
 */
enum class DigaDevice(val id: Int) {
    /** 0xB0 (176) - Main DVD/HDD recorder deck */
    DVD_HDD(0xB0),

    /** 0x90 (144) - VCR deck on dual-deck combo recorders (e.g. ES30V, EH75V, EZ48V, NV-VP) */
    VCR(0x90),

    /** 0x01 (1) - Panasonic Viera television controls */
    TV(0x01),

    /** 0xFE (254) - Unassigned address used by the camera test beacon; no equipment reacts to it. */
    DIAGNOSTIC(0xFE),
}
