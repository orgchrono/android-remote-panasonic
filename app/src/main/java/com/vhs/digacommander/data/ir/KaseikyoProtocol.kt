package com.vhs.digacommander.data.ir

import com.vhs.digacommander.data.model.DigaDevice
import com.vhs.digacommander.data.model.RemoteMode

/**
 * Functional synthesizer for the Matsushita/Panasonic Kaseikyo 48-bit Infrared Protocol.
 *
 * Mathematical Framing:
 * Carrier Frequency: 37.010 kHz (~27.02 us period)
 * Lead-in: 3459 us Mark, 1729 us Space (8 units / 4 units)
 * Logic 0: 432 us Mark, 432 us Space (1:1 unit ratio)
 * Logic 1: 432 us Mark, 1297 us Space (1:3 unit ratio)
 * Payload: 6 bytes transmitted LSB-first:
 *   [0x02, 0x20, Device, Subdevice, Command, Checksum = Device ^ Subdevice ^ Command]
 * Trail: 432 us Mark, ~74700 us Space
 */
object KaseikyoProtocol {

    const val CARRIER_FREQUENCY_HZ = 37010

    private const val OEM_1 = 0x02
    private const val OEM_2 = 0x20

    private const val LEAD_IN_MARK_US = 3459
    private const val LEAD_IN_SPACE_US = 1729
    private const val BIT_MARK_US = 432
    private const val BIT_0_SPACE_US = 432
    private const val BIT_1_SPACE_US = 1297
    private const val TRAIL_MARK_US = 432
    private const val TRAIL_SPACE_US = 74720

    /**
     * Pure function synthesizing an alternating Mark/Space interval array (100 elements).
     *
     * @param device Target hardware device (e.g. DVD_HDD 0xB0 or VCR 0x90).
     * @param mode Target remote control channel mode (MODE_1, MODE_2, or MODE_3).
     * @param command 8-bit Original Button Code (OBC).
     * @return IntArray of exactly 100 microsecond intervals.
     */
    fun synthesizePulsePattern(
        device: Int = DigaDevice.DVD_HDD.id,
        mode: RemoteMode = RemoteMode.MODE_1,
        command: Int
    ): IntArray {
        val devByte = device and 0xFF
        val subByte = mode.subdevice and 0xFF
        val cmdByte = command and 0xFF
        val checksum = devByte xor subByte xor cmdByte
        val payload = intArrayOf(OEM_1, OEM_2, devByte, subByte, cmdByte, checksum)

        val pattern = IntArray(100)
        var index = 0

        // Lead-in framing
        pattern[index++] = LEAD_IN_MARK_US
        pattern[index++] = LEAD_IN_SPACE_US

        // 48 data bits (LSB-first per byte)
        for (byteVal in payload) {
            for (bitPos in 0 until 8) {
                val bit = (byteVal shr bitPos) and 1
                pattern[index++] = BIT_MARK_US
                pattern[index++] = if (bit == 1) BIT_1_SPACE_US else BIT_0_SPACE_US
            }
        }

        // Final trail pulse
        pattern[index++] = TRAIL_MARK_US
        pattern[index] = TRAIL_SPACE_US

        return pattern
    }

    /**
     * Synthesizes an exact 104-word Philips Pronto Hex string representation.
     */
    fun synthesizeProntoHex(
        device: Int = DigaDevice.DVD_HDD.id,
        mode: RemoteMode = RemoteMode.MODE_1,
        command: Int
    ): String {
        val devByte = device and 0xFF
        val subByte = mode.subdevice and 0xFF
        val cmdByte = command and 0xFF
        val checksum = devByte xor subByte xor cmdByte
        val payload = intArrayOf(OEM_1, OEM_2, devByte, subByte, cmdByte, checksum)

        val words = ArrayList<String>(104)
        words.add("0000") // Learned raw format
        words.add("0070") // 37.010 kHz carrier clock divisor
        words.add("0000") // Burst pair 1 count
        words.add("0032") // Burst pair 2 count = 50 pairs = 100 pulses

        // Lead-in (128 cycles mark, 64 cycles space)
        words.add("0080")
        words.add("0040")

        // 48 payload bits
        for (byteVal in payload) {
            for (bitPos in 0 until 8) {
                val bit = (byteVal shr bitPos) and 1
                words.add("0010") // 16 cycles mark
                words.add(if (bit == 1) "0030" else "0010") // 48 or 16 cycles space
            }
        }

        // Trail pulse and frame padding
        words.add("0010")
        words.add("0ACD")

        return words.joinToString(" ")
    }
}
