package com.vhs.digacommander.core.ir

/**
 * Universal parametric pulse and Pronto Hex generator for the Matsushita/Panasonic
 * Kaseikyo 48-bit Infrared Protocol.
 *
 * Physical Modulation:
 * - Carrier Frequency: 37.010 kHz (~27.02 us period)
 * - Lead-in: 3459 us Mark, 1729 us Space (8 units / 4 units)
 * - Logical 0: 432 us Mark, 432 us Space (1:1 ratio)
 * - Logical 1: 432 us Mark, 1297 us Space (1:3 ratio)
 * - 48 data bits = 6 bytes transmitted LSB first:
 *     [0x02, 0x20, Device, Subdevice, Command, (Device ^ Subdevice ^ Command)]
 * - Trail: 432 us Mark, ~74700 us Space
 */
object KaseikyoPulseGenerator {

    const val CARRIER_FREQUENCY_HZ = 37010

    // Standard Panasonic Device IDs
    const val DEVICE_DVD_HDD = 0xB0      // 176 - Standard DIGA DVD/HDD Deck
    const val DEVICE_VCR = 0x90          // 144 - VHS Deck on combo recorders (ES30V, EH75V, EZ48V)
    const val DEVICE_TV = 0x01           // 1   - Panasonic Viera TV control

    // Remote Modes (Subdevice IDs) - Resolves SET 1 / SET 2 / SET 3 locks
    const val SUBDEVICE_MODE_1 = 0x00    // Mode 1 (Factory default, "SET 1")
    const val SUBDEVICE_MODE_2 = 0x10    // Mode 2 ("SET 2")
    const val SUBDEVICE_MODE_3 = 0x20    // Mode 3 ("SET 3")

    // Timings in microseconds
    private const val LEAD_IN_MARK = 3459
    private const val LEAD_IN_SPACE = 1729
    private const val BIT_MARK = 432
    private const val BIT_0_SPACE = 432
    private const val BIT_1_SPACE = 1297
    private const val TRAIL_MARK = 432
    private const val TRAIL_SPACE = 74720

    /**
     * Generates an array of alternating Mark and Space intervals in microseconds
     * suitable for direct transmission via Android's [android.hardware.ConsumerIrManager.transmit].
     *
     * @param device Device ID (e.g. 0xB0 for DVD/HDD, 0x90 for VCR)
     * @param subdevice Subdevice ID (0x00 for Mode 1, 0x10 for Mode 2, 0x20 for Mode 3)
     * @param command Command / OBC code (0x00..0xFF)
     * @return IntArray of exactly 100 microsecond intervals.
     */
    fun generatePulsePattern(device: Int, subdevice: Int, command: Int): IntArray {
        val oem1 = 0x02
        val oem2 = 0x20
        val checksum = (device xor subdevice xor command) and 0xFF
        val payload = intArrayOf(oem1, oem2, device and 0xFF, subdevice and 0xFF, command and 0xFF, checksum)

        // 1 lead-in pair (2) + 48 bit pairs (96) + 1 trail pair (2) = 100 pulses
        val pattern = IntArray(100)
        var idx = 0

        // Lead-in
        pattern[idx++] = LEAD_IN_MARK
        pattern[idx++] = LEAD_IN_SPACE

        // 48 bits (LSB first for each byte)
        for (byteVal in payload) {
            for (bitPos in 0 until 8) {
                val bit = (byteVal shr bitPos) and 1
                pattern[idx++] = BIT_MARK
                pattern[idx++] = if (bit == 1) BIT_1_SPACE else BIT_0_SPACE
            }
        }

        // Trail pulse
        pattern[idx++] = TRAIL_MARK
        pattern[idx] = TRAIL_SPACE

        return pattern
    }

    /**
     * Generates a bit-perfect Philips Pronto Hex string formatted as 104 hexadecimal words.
     */
    fun generateProntoHex(device: Int, subdevice: Int, command: Int): String {
        val oem1 = 0x02
        val oem2 = 0x20
        val checksum = (device xor subdevice xor command) and 0xFF
        val payload = intArrayOf(oem1, oem2, device and 0xFF, subdevice and 0xFF, command and 0xFF, checksum)

        val words = ArrayList<String>(104)
        words.add("0000")
        words.add("0070") // 37.010 kHz carrier
        words.add("0000")
        words.add("0032") // 50 burst pairs = 100 pulses

        // Lead-in (128 cycles mark, 64 cycles space)
        words.add("0080")
        words.add("0040")

        // 48 data bits
        for (byteVal in payload) {
            for (bitPos in 0 until 8) {
                val bit = (byteVal shr bitPos) and 1
                words.add("0010") // 16 cycles mark
                words.add(if (bit == 1) "0030" else "0010") // 48 or 16 cycles space
            }
        }

        // Trail pulse and frame space
        words.add("0010")
        words.add("0ACD")

        return words.joinToString(" ")
    }
}
