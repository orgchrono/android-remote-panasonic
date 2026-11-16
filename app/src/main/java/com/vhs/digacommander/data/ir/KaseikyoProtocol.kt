package com.vhs.digacommander.data.ir

import com.vhs.digacommander.data.constant.DigaTimings
import com.vhs.digacommander.data.model.DigaDevice
import com.vhs.digacommander.data.model.RemoteMode

/**
 * Functional synthesizer for the Matsushita/Panasonic Kaseikyo 48-bit Infrared Protocol.
 */
object KaseikyoProtocol {
    const val CARRIER_FREQUENCY_HZ: Int = DigaTimings.CARRIER_MATSUSHITA_HZ
    const val SUBDEVICE_PRIMARY: Int = 0x00
    const val SUBDEVICE_EXTENDED: Int = 0x01
    const val SUBDEVICE_COMBO_AUX: Int = 0x05

    private const val OEM_1: Int = 0x02
    private const val OEM_2: Int = 0x20

    /**
     * Computes the exact 6-byte Matsushita Kaseikyo wire frame `[02 20 D S F (D^S^F)]`.
     */
    fun buildPayloadRaw(
        device: Int,
        subdevice: Int,
        command: Int,
    ): IntArray {
        val devByte = device and 0xFF
        val subByte = subdevice and 0xFF
        val cmdByte = command and 0xFF
        val checksum = devByte xor subByte xor cmdByte
        return intArrayOf(OEM_1, OEM_2, devByte, subByte, cmdByte, checksum)
    }

    private fun buildPayload(
        device: Int,
        mode: RemoteMode,
        command: Int,
        subdeviceGroup: Int,
    ): IntArray = buildPayloadRaw(device, mode.subdevice or (subdeviceGroup and 0x0F), command)

    /**
     * Pure function synthesizing an alternating Mark/Space interval array (100 elements).
     *
     * @param device Target hardware device (e.g. DVD_HDD 0xB0 or VCR 0x90).
     * @param mode Target remote control channel mode (MODE_1, MODE_2, or MODE_3).
     * @param command 8-bit Original Button Code (OBC).
     * @param subdeviceGroup Low nibble of byte 3 ([SUBDEVICE_PRIMARY] 0x00, [SUBDEVICE_EXTENDED] 0x01, or [SUBDEVICE_COMBO_AUX] 0x05).
     * @return IntArray of exactly 100 microsecond intervals.
     */
    fun synthesizePulsePattern(
        device: Int = DigaDevice.DVD_HDD.id,
        mode: RemoteMode = RemoteMode.MODE_1,
        command: Int,
        subdeviceGroup: Int = SUBDEVICE_PRIMARY,
    ): IntArray = synthesizePulsePatternFromPayload(buildPayload(device, mode, command, subdeviceGroup))

    /**
     * Synthesizes an alternating Mark/Space interval array from explicit device and subdevice bytes.
     */
    fun synthesizePulsePatternRaw(
        device: Int,
        subdevice: Int,
        command: Int,
    ): IntArray = synthesizePulsePatternFromPayload(buildPayloadRaw(device, subdevice, command))

    private fun synthesizePulsePatternFromPayload(payload: IntArray): IntArray {
        val pattern = IntArray(DigaTimings.KASEIKYO_FRAME_TRANSITION_COUNT)
        var index = 0

        // Lead-in framing
        pattern[index++] = DigaTimings.PULSE_LEAD_IN_MARK_US
        pattern[index++] = DigaTimings.PULSE_LEAD_IN_SPACE_US

        // 48 data bits (LSB-first per byte)
        for (byteVal in payload) {
            for (bitPos in 0 until 8) {
                val bit = (byteVal shr bitPos) and 1
                pattern[index++] = DigaTimings.PULSE_BIT_MARK_US
                pattern[index++] = if (bit == 1) DigaTimings.PULSE_BIT_1_SPACE_US else DigaTimings.PULSE_BIT_0_SPACE_US
            }
        }

        // Final trail pulse
        pattern[index++] = DigaTimings.PULSE_TRAIL_MARK_US
        pattern[index] = DigaTimings.PULSE_TRAIL_SPACE_US

        return pattern
    }

    /**
     * Synthesizes an exact 104-word Philips Pronto Hex string representation.
     */
    fun synthesizeProntoHex(
        device: Int = DigaDevice.DVD_HDD.id,
        mode: RemoteMode = RemoteMode.MODE_1,
        command: Int,
        subdeviceGroup: Int = SUBDEVICE_PRIMARY,
    ): String = encodeProntoHexFromPayload(buildPayload(device, mode, command, subdeviceGroup))

    /**
     * Synthesizes a 104-word Pronto Hex string from explicit device and subdevice bytes.
     */
    fun synthesizeProntoHexRaw(
        device: Int,
        subdevice: Int,
        command: Int,
    ): String = encodeProntoHexFromPayload(buildPayloadRaw(device, subdevice, command))

    private fun encodeProntoHexFromPayload(payload: IntArray): String {
        val words = ArrayList<String>(DigaTimings.PRONTO_TOTAL_WORD_COUNT)
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
