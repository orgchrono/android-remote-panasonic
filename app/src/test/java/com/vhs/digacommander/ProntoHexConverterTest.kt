package com.vhs.digacommander

import com.vhs.digacommander.data.catalog.PanasonicDigaCatalog
import com.vhs.digacommander.data.ir.KaseikyoProtocol
import com.vhs.digacommander.data.ir.ProntoHexConverter
import com.vhs.digacommander.data.model.DigaDevice
import com.vhs.digacommander.data.model.RemoteMode
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test suite validating bit-accurate Kaseikyo 48-bit pulse synthesis,
 * Pronto Hex parsing, carrier frequency computation, and macro catalog integrity.
 */
class ProntoHexConverterTest {
    @Test
    fun testProntoHexCarrierFrequencyConversion() {
        // Frequency code 0x0070 (112 decimal) -> 4145146 / 112 = 37010 Hz
        val signal1 = ProntoHexConverter.convert(PanasonicDigaCatalog.DIGIT_1)
        assertEquals(37010, signal1.carrierFrequency)

        // POWER code frequency validation
        val signalPower = ProntoHexConverter.convert(PanasonicDigaCatalog.POWER)
        assertEquals(37010, signalPower.carrierFrequency)
    }

    @Test
    fun testPatternMicrosecondsLengthAndTiming() {
        // Kaseikyo frame consists of 1 lead-in pair + 48 data bit pairs + 1 stop pair = 50 pairs = 100 entries
        val signal = ProntoHexConverter.convert(PanasonicDigaCatalog.DIGIT_1)
        assertEquals(100, signal.patternMicroseconds.size)

        // Lead-in Mark: 0x0080 = 128 cycles at ~37 kHz -> ~3459 µs
        val leadInMark = signal.patternMicroseconds[0]
        assertTrue("Lead-in mark must be approximately 3459 µs, was: $leadInMark", leadInMark in 3300..3600)

        // Lead-in Space: 0x0040 = 64 cycles at ~37 kHz -> ~1729 µs
        val leadInSpace = signal.patternMicroseconds[1]
        assertTrue("Lead-in space must be approximately 1729 µs, was: $leadInSpace", leadInSpace in 1650..1800)

        // Logical bit 0 (0010 0010): 16 cycles mark / 16 cycles space -> ~432 µs mark / 432 µs space
        val bitMark = signal.patternMicroseconds[2]
        val bitSpace = signal.patternMicroseconds[3]
        assertTrue("Bit mark must be ~432 µs, was: $bitMark", bitMark in 410..450)
        assertTrue("Bit space must be ~432 µs, was: $bitSpace", bitSpace in 410..450)
    }

    @Test
    fun testStandardMirroredDigaCommandsIntegrity() {
        val standardCodes =
            listOf(
                "DRIVE_SELECT" to PanasonicDigaCatalog.DRIVE_SELECT,
                "DIRECT_NAVIGATOR" to PanasonicDigaCatalog.DIRECT_NAVIGATOR,
                "FUNCTIONS" to PanasonicDigaCatalog.FUNCTIONS,
                "TOP_MENU" to PanasonicDigaCatalog.TOP_MENU,
                "OPTION_SUB_MENU" to PanasonicDigaCatalog.OPTION_SUB_MENU,
                "SETUP" to PanasonicDigaCatalog.SETUP,
                "TIME_SLIP" to PanasonicDigaCatalog.TIME_SLIP,
                "MANUAL_SKIP_30S" to PanasonicDigaCatalog.MANUAL_SKIP_30S,
                "MANUAL_REPLAY_10S" to PanasonicDigaCatalog.MANUAL_REPLAY_10S,
                "CREATE_CHAPTER" to PanasonicDigaCatalog.CREATE_CHAPTER,
                "DELETE_TITLE" to PanasonicDigaCatalog.DELETE_TITLE,
                "REC" to PanasonicDigaCatalog.REC,
                "REC_MODE" to PanasonicDigaCatalog.REC_MODE,
                "F_REC" to PanasonicDigaCatalog.F_REC,
                "EXT_LINK" to PanasonicDigaCatalog.EXT_LINK,
                "INPUT_SELECT" to PanasonicDigaCatalog.INPUT_SELECT,
                "CH_UP" to PanasonicDigaCatalog.CH_UP,
                "CH_DOWN" to PanasonicDigaCatalog.CH_DOWN,
                "ADD_DEL_CH" to PanasonicDigaCatalog.ADD_DEL_CH,
                "PLAY" to PanasonicDigaCatalog.PLAY,
                "STOP" to PanasonicDigaCatalog.STOP,
                "PAUSE" to PanasonicDigaCatalog.PAUSE,
                "FAST_FORWARD" to PanasonicDigaCatalog.FAST_FORWARD,
                "REWIND" to PanasonicDigaCatalog.REWIND,
                "SKIP_NEXT" to PanasonicDigaCatalog.SKIP_NEXT,
                "SKIP_PREV" to PanasonicDigaCatalog.SKIP_PREV,
                "AUDIO" to PanasonicDigaCatalog.AUDIO,
                "SUBTITLE" to PanasonicDigaCatalog.SUBTITLE,
                "STATUS" to PanasonicDigaCatalog.STATUS,
                "DISPLAY" to PanasonicDigaCatalog.DISPLAY,
                "GUIDE" to PanasonicDigaCatalog.GUIDE,
                "PROG_CHECK" to PanasonicDigaCatalog.PROG_CHECK,
                "CANCEL" to PanasonicDigaCatalog.CANCEL,
                "EXIT" to PanasonicDigaCatalog.EXIT,
                "POWER_ON" to PanasonicDigaCatalog.POWER_ON,
                "POWER_OFF" to PanasonicDigaCatalog.POWER_OFF,
                "TELETEXT_RED" to PanasonicDigaCatalog.TELETEXT_RED,
                "TELETEXT_GREEN" to PanasonicDigaCatalog.TELETEXT_GREEN,
                "TELETEXT_YELLOW" to PanasonicDigaCatalog.TELETEXT_YELLOW,
                "TELETEXT_BLUE" to PanasonicDigaCatalog.TELETEXT_BLUE,
                "JDM_ND2_SERVICE" to PanasonicDigaCatalog.JDM_ND2_SERVICE,
            )

        for ((name, prontoHex) in standardCodes) {
            val converted = ProntoHexConverter.convert(prontoHex)
            assertTrue("$name: Carrier must be ~37kHz or ~38kHz", converted.carrierFrequency in 36000..39000)
            assertEquals("$name: Pattern must contain exactly 100 pulse transitions", 100, converted.patternMicroseconds.size)
            assertTrue("$name: Valid lead-in mark", converted.patternMicroseconds[0] in 3200..3500)
        }
    }

    @Test
    fun testAllCatalogMacrosIntegrity() {
        val macros =
            listOf(
                PanasonicDigaCatalog.Macros.LASER_HOURS,
                PanasonicDigaCatalog.Macros.RAM_DRIVE_LAST_ERROR,
                PanasonicDigaCatalog.Macros.ERROR_HISTORY_LATEST,
                PanasonicDigaCatalog.Macros.ERROR_HISTORY_SLOT_1,
                PanasonicDigaCatalog.Macros.ERROR_HISTORY_SLOT_2,
                PanasonicDigaCatalog.Macros.ERROR_HISTORY_SLOT_3,
                PanasonicDigaCatalog.Macros.ERROR_HISTORY_SLOT_4,
                PanasonicDigaCatalog.Macros.ERROR_HISTORY_SLOT_5,
                PanasonicDigaCatalog.Macros.REGION_READOUT,
                PanasonicDigaCatalog.Macros.HDD_SURFACE_TEST,
                PanasonicDigaCatalog.Macros.VFD_SEGMENTS_TEST,
                PanasonicDigaCatalog.Macros.VIDEO_IO_TEST_80,
                PanasonicDigaCatalog.Macros.VIDEO_IO_TEST_81,
                PanasonicDigaCatalog.Macros.TRAY_CYCLE_TEST,
                PanasonicDigaCatalog.Macros.CLEAR_ERROR_F00,
                PanasonicDigaCatalog.Macros.MULTI_REGION_HACK,
                PanasonicDigaCatalog.Macros.VERIFY_REGION_FREE,
                PanasonicDigaCatalog.Macros.CHILD_LOCK_UNLOCK,
                PanasonicDigaCatalog.Macros.AUTO_RESTORE_SET2_TO_SET1,
                PanasonicDigaCatalog.Macros.AUTO_RESTORE_SET3_TO_SET1,
                PanasonicDigaCatalog.Macros.FACTORY_RESET,
                PanasonicDigaCatalog.Macros.TVGOS_DISABLE_ZIP,
            )

        for (macro in macros) {
            assertTrue("Macro list must not be empty", macro.isNotEmpty())
            for (step in macro) {
                val converted = ProntoHexConverter.convert(step.prontoHex)
                assertTrue("Carrier must be 37kHz or 38kHz", converted.carrierFrequency in 36000..39000)
                assertEquals("Pulse pattern must have 100 transitions", 100, converted.patternMicroseconds.size)
                assertTrue("Inter-frame delay must be positive", step.postDelayMs > 0)
            }
        }
    }

    @Test
    fun testAutomatedOsdRecoveryMacrosSet2AndSet3() {
        val macroSet2 = PanasonicDigaCatalog.Macros.AUTO_RESTORE_SET2_TO_SET1
        assertEquals(10, macroSet2.size)
        // Verify all steps in Mode 2 have correct Kaseikyo subdevice framing
        for (step in macroSet2) {
            val signal = ProntoHexConverter.convert(step.prontoHex)
            assertEquals(100, signal.patternMicroseconds.size)
        }

        val macroSet3 = PanasonicDigaCatalog.Macros.AUTO_RESTORE_SET3_TO_SET1
        assertEquals(11, macroSet3.size)
        for (step in macroSet3) {
            val signal = ProntoHexConverter.convert(step.prontoHex)
            assertEquals(100, signal.patternMicroseconds.size)
        }
    }

    @Test
    fun testKaseikyoProtocolParametricCreation() {
        val pulsesDvdMode1 =
            KaseikyoProtocol.synthesizePulsePattern(
                DigaDevice.DVD_HDD.id,
                RemoteMode.MODE_1,
                PanasonicDigaCatalog.OBC.POWER,
            )
        assertEquals(100, pulsesDvdMode1.size)
        assertEquals(3459, pulsesDvdMode1[0]) // Lead-in mark
        assertEquals(1729, pulsesDvdMode1[1]) // Lead-in space
        assertEquals(432, pulsesDvdMode1[2]) // Bit mark

        // Validate that synthesized Pronto Hex parses bit-accurately
        val prontoHex =
            KaseikyoProtocol.synthesizeProntoHex(
                DigaDevice.DVD_HDD.id,
                RemoteMode.MODE_1,
                PanasonicDigaCatalog.OBC.POWER,
            )
        val converted = ProntoHexConverter.convert(prontoHex)
        assertEquals(37010, converted.carrierFrequency)
        assertEquals(100, converted.patternMicroseconds.size)
    }

    @Test
    fun testRemoteModesAndVcrAddressing() {
        // Test modes 1, 2, and 3
        for (mode in RemoteMode.entries) {
            val code = PanasonicDigaCatalog.getProntoHex(PanasonicDigaCatalog.OBC.PLAY, mode = mode)
            val signal = ProntoHexConverter.convert(code)
            assertEquals(37010, signal.carrierFrequency)
            assertEquals(100, signal.patternMicroseconds.size)
        }

        // Test VCR Deck addressing (Device 0x90)
        val vcrPlayCode =
            PanasonicDigaCatalog.getProntoHex(
                PanasonicDigaCatalog.OBC.PLAY,
                mode = RemoteMode.MODE_1,
                device = DigaDevice.VCR,
            )
        val vcrSignal = ProntoHexConverter.convert(vcrPlayCode)
        assertEquals(37010, vcrSignal.carrierFrequency)
        assertEquals(100, vcrSignal.patternMicroseconds.size)
    }

    @Test
    fun testVerifiedDigaWireBytesBitByBit() {
        val verifiedCases =
            listOf(
                "POWER" to (PanasonicDigaCatalog.POWER to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x3D, 0x8D)),
                "POWER_ON" to (PanasonicDigaCatalog.POWER_ON to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x3E, 0x8E)),
                "POWER_OFF" to (PanasonicDigaCatalog.POWER_OFF to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x3F, 0x8F)),
                "EJECT" to (PanasonicDigaCatalog.EJECT to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x01, 0xB1)),
                "STATUS" to (PanasonicDigaCatalog.STATUS to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x5F, 0xEF)),
                "DISPLAY" to (PanasonicDigaCatalog.DISPLAY to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x92, 0x22)),
                "DRIVE_SELECT" to (PanasonicDigaCatalog.DRIVE_SELECT to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x2C, 0x9C)),
                "DIRECT_NAVIGATOR" to (PanasonicDigaCatalog.DIRECT_NAVIGATOR to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x9B, 0x2B)),
                "FUNCTIONS" to (PanasonicDigaCatalog.FUNCTIONS to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x57, 0xE7)),
                "TOP_MENU" to (PanasonicDigaCatalog.TOP_MENU to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x9B, 0x2B)),
                "OPTION_SUB_MENU" to (PanasonicDigaCatalog.OPTION_SUB_MENU to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x80, 0x30)),
                "SETUP" to (PanasonicDigaCatalog.SETUP to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x94, 0x24)),
                "UP" to (PanasonicDigaCatalog.UP to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x85, 0x35)),
                "DOWN" to (PanasonicDigaCatalog.DOWN to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x86, 0x36)),
                "LEFT" to (PanasonicDigaCatalog.LEFT to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x87, 0x37)),
                "RIGHT" to (PanasonicDigaCatalog.RIGHT to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x88, 0x38)),
                "ENTER_OK" to (PanasonicDigaCatalog.ENTER_OK to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x82, 0x32)),
                "RETURN" to (PanasonicDigaCatalog.RETURN to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x81, 0x31)),
                "CANCEL" to (PanasonicDigaCatalog.CANCEL to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x83, 0x33)),
                "GUIDE" to (PanasonicDigaCatalog.GUIDE to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x7C, 0xCC)),
                "PROG_CHECK" to (PanasonicDigaCatalog.PROG_CHECK to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x7C, 0xCC)),
                "INPUT_SELECT" to (PanasonicDigaCatalog.INPUT_SELECT to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x50, 0xE0)),
                "PLAY" to (PanasonicDigaCatalog.PLAY to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x0A, 0xBA)),
                "STOP" to (PanasonicDigaCatalog.STOP to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x00, 0xB0)),
                "PAUSE" to (PanasonicDigaCatalog.PAUSE to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x06, 0xB6)),
                "REC" to (PanasonicDigaCatalog.REC to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x08, 0xB8)),
                "REC_MODE" to (PanasonicDigaCatalog.REC_MODE to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x51, 0xE1)),
                "F_REC" to (PanasonicDigaCatalog.F_REC to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0xC7, 0x77)),
                "EXT_LINK" to (PanasonicDigaCatalog.EXT_LINK to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x23, 0x93)),
                "SKIP_PREV" to (PanasonicDigaCatalog.SKIP_PREV to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x49, 0xF9)),
                "SKIP_NEXT" to (PanasonicDigaCatalog.SKIP_NEXT to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x4A, 0xFA)),
                "REWIND" to (PanasonicDigaCatalog.REWIND to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x04, 0xB4)),
                "FAST_FORWARD" to (PanasonicDigaCatalog.FAST_FORWARD to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x05, 0xB5)),
                "TIME_SLIP" to (PanasonicDigaCatalog.TIME_SLIP to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x4B, 0xFB)),
                "MANUAL_SKIP_30S" to (PanasonicDigaCatalog.MANUAL_SKIP_30S to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x4C, 0xFC)),
                "MANUAL_REPLAY_10S" to (PanasonicDigaCatalog.MANUAL_REPLAY_10S to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x8B, 0x3B)),
                "CREATE_CHAPTER" to (PanasonicDigaCatalog.CREATE_CHAPTER to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x58, 0xE8)),
                "DELETE_TITLE" to (PanasonicDigaCatalog.DELETE_TITLE to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x44, 0xF4)),
                "AUDIO" to (PanasonicDigaCatalog.AUDIO to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x33, 0x83)),
                "SUBTITLE" to (PanasonicDigaCatalog.SUBTITLE to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x91, 0x21)),
                "CH_UP" to (PanasonicDigaCatalog.CH_UP to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x34, 0x84)),
                "CH_DOWN" to (PanasonicDigaCatalog.CH_DOWN to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x35, 0x85)),
                "ADD_DEL_CH" to (PanasonicDigaCatalog.ADD_DEL_CH to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x36, 0x86)),
                "DIGIT_1" to (PanasonicDigaCatalog.DIGIT_1 to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x10, 0xA0)),
                "DIGIT_0" to (PanasonicDigaCatalog.DIGIT_0 to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x19, 0xA9)),
                "MEDIA_SERVER" to (PanasonicDigaCatalog.MEDIA_SERVER to intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x04, 0xB4)),
            )

        for ((name, pair) in verifiedCases) {
            val (prontoHex, expectedWireBytes) = pair
            val decodedPronto = decodeProntoWireBytes(prontoHex)
            assertArrayEquals("$name: Pronto Hex wire bytes mismatch", expectedWireBytes, decodedPronto)

            val converted = ProntoHexConverter.convert(prontoHex)
            val decodedPulses = decodePulsePatternWireBytes(converted.patternMicroseconds)
            assertArrayEquals("$name: Decoded pulse pattern wire bytes mismatch", expectedWireBytes, decodedPulses)

            val directPulses =
                KaseikyoProtocol.synthesizePulsePattern(
                    device = expectedWireBytes[2],
                    mode = RemoteMode.MODE_1,
                    command = expectedWireBytes[4],
                )
            assertArrayEquals(
                "$name: synthesizePulsePattern wire bytes mismatch",
                expectedWireBytes,
                decodePulsePatternWireBytes(directPulses),
            )
        }
    }

    @Test
    fun testExtendedSubdevice01AndMultiModeWireBytes() {
        val extendedCases =
            listOf(
                "TELETEXT_RED" to (PanasonicDigaCatalog.TELETEXT_RED to intArrayOf(0x02, 0x20, 0xB0, 0x01, 0x41, 0xF0)),
                "TELETEXT_GREEN" to (PanasonicDigaCatalog.TELETEXT_GREEN to intArrayOf(0x02, 0x20, 0xB0, 0x01, 0x42, 0xF3)),
                "TELETEXT_YELLOW" to (PanasonicDigaCatalog.TELETEXT_YELLOW to intArrayOf(0x02, 0x20, 0xB0, 0x01, 0x43, 0xF2)),
                "TELETEXT_BLUE" to (PanasonicDigaCatalog.TELETEXT_BLUE to intArrayOf(0x02, 0x20, 0xB0, 0x01, 0x44, 0xF5)),
                "JDM_ND2_SERVICE" to (PanasonicDigaCatalog.JDM_ND2_SERVICE to intArrayOf(0x02, 0x20, 0xB0, 0x01, 0x82, 0x33)),
            )

        for ((name, pair) in extendedCases) {
            val (prontoHex, expected) = pair
            assertArrayEquals("$name: Pronto Hex mismatch", expected, decodeProntoWireBytes(prontoHex))
            val directPulses =
                KaseikyoProtocol.synthesizePulsePattern(
                    device = DigaDevice.DVD_HDD.id,
                    mode = RemoteMode.MODE_1,
                    command = expected[4],
                    subdeviceGroup = KaseikyoProtocol.SUBDEVICE_EXTENDED,
                )
            assertArrayEquals("$name: Pulse pattern mismatch", expected, decodePulsePatternWireBytes(directPulses))
        }

        // Mode 2 (0x10) and Mode 3 (0x20) verification for corrected OBCs
        assertArrayEquals(
            intArrayOf(0x02, 0x20, 0xB0, 0x10, 0x57, 0xF7),
            decodeProntoWireBytes(PanasonicDigaCatalog.getProntoHex(PanasonicDigaCatalog.OBC.FUNCTIONS, RemoteMode.MODE_2)),
        )
        assertArrayEquals(
            intArrayOf(0x02, 0x20, 0xB0, 0x20, 0x82, 0x12),
            decodeProntoWireBytes(PanasonicDigaCatalog.getProntoHex(PanasonicDigaCatalog.OBC.ENTER_OK, RemoteMode.MODE_3)),
        )
    }

    @Test
    fun testMacrosEmitVerifiedWireSequences() {
        val multiRegionBytes = PanasonicDigaCatalog.Macros.MULTI_REGION_HACK.map { decodeProntoWireBytes(it.prontoHex) }
        val expectedMultiRegionObcs = listOf(0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x33, 0x04)
        assertEquals(expectedMultiRegionObcs, multiRegionBytes.map { it[4] })
        assertArrayEquals(intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x33, 0x83), multiRegionBytes[10])
        assertArrayEquals(intArrayOf(0x02, 0x20, 0xB0, 0x00, 0x04, 0xB4), multiRegionBytes[11])

        val set2Bytes = PanasonicDigaCatalog.Macros.AUTO_RESTORE_SET2_TO_SET1.map { decodeProntoWireBytes(it.prontoHex) }
        assertTrue(set2Bytes.all { it[3] == 0x10 })
        assertEquals(listOf(0x57, 0x86, 0x82, 0x86, 0x82, 0x86, 0x82, 0x85, 0x82, 0x81), set2Bytes.map { it[4] })

        val set3Bytes = PanasonicDigaCatalog.Macros.AUTO_RESTORE_SET3_TO_SET1.map { decodeProntoWireBytes(it.prontoHex) }
        assertTrue(set3Bytes.all { it[3] == 0x20 })
        assertEquals(listOf(0x57, 0x86, 0x82, 0x86, 0x82, 0x86, 0x82, 0x85, 0x85, 0x82, 0x81), set3Bytes.map { it[4] })
    }

    private fun decodeProntoWireBytes(prontoHex: String): IntArray {
        val words = prontoHex.trim().split("\\s+".toRegex())
        assertEquals(104, words.size)
        assertEquals(listOf("0000", "0070", "0000", "0032", "0080", "0040"), words.subList(0, 6))
        assertEquals(listOf("0010", "0ACD"), words.subList(102, 104))

        return IntArray(6) { byteIndex ->
            var value = 0
            for (bitPos in 0 until 8) {
                val wordIndex = 6 + (byteIndex * 8 + bitPos) * 2
                assertEquals("0010", words[wordIndex])
                val spaceWord = words[wordIndex + 1]
                val bit =
                    when (spaceWord) {
                        "0030" -> 1
                        "0010" -> 0
                        else -> throw AssertionError("Unexpected Pronto space word: $spaceWord")
                    }
                value = value or (bit shl bitPos)
            }
            value
        }
    }

    private fun decodePulsePatternWireBytes(pattern: IntArray): IntArray {
        assertEquals(100, pattern.size)
        return IntArray(6) { byteIndex ->
            var value = 0
            for (bitPos in 0 until 8) {
                val pulseIndex = 2 + (byteIndex * 8 + bitPos) * 2
                val mark = pattern[pulseIndex]
                val space = pattern[pulseIndex + 1]
                assertTrue("Bit mark must be ~432 us, was $mark", mark in 400..470)
                val bit = if (space > 900) 1 else 0
                value = value or (bit shl bitPos)
            }
            value
        }
    }
}
