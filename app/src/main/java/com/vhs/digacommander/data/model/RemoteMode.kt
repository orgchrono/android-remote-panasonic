package com.vhs.digacommander.data.model

/**
 * Panasonic DIGA Remote Control Modes (Subdevice IDs).
 * Dictates which pairing channel the recorder listens to.
 *
 * Mode 1 (Subdevice 0x00): Factory default ("SET 1").
 * Mode 2 (Subdevice 0x10): Alternative channel ("SET 2").
 * Mode 3 (Subdevice 0x20): Alternative channel ("SET 3").
 */
enum class RemoteMode(val subdevice: Int, val label: String) {
    MODE_1(0x00, "SET 1 (Default)"),
    MODE_2(0x10, "SET 2"),
    MODE_3(0x20, "SET 3");

    companion object {
        fun fromInt(mode: Int): RemoteMode = when (mode) {
            2 -> MODE_2
            3 -> MODE_3
            else -> MODE_1
        }
    }
}
