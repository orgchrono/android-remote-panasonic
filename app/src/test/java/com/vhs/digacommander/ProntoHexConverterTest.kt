package com.vhs.digacommander

import com.vhs.digacommander.data.catalog.PanasonicDigaCatalog
import com.vhs.digacommander.data.ir.KaseikyoProtocol
import com.vhs.digacommander.data.ir.ProntoHexConverter
import com.vhs.digacommander.data.model.DigaDevice
import com.vhs.digacommander.data.model.RemoteMode
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
        val standardCodes = listOf(
            "DRIVE_SELECT" to PanasonicDigaCatalog.DRIVE_SELECT,
            "DIRECT_NAVIGATOR" to PanasonicDigaCatalog.DIRECT_NAVIGATOR,
            "FUNCTIONS" to PanasonicDigaCatalog.FUNCTIONS,
            "TOP_MENU" to PanasonicDigaCatalog.TOP_MENU,
            "OPTION_SUB_MENU" to PanasonicDigaCatalog.OPTION_SUB_MENU,
            "TIME_SLIP" to PanasonicDigaCatalog.TIME_SLIP,
            "MANUAL_SKIP_30S" to PanasonicDigaCatalog.MANUAL_SKIP_30S,
            "MANUAL_REPLAY_10S" to PanasonicDigaCatalog.MANUAL_REPLAY_10S,
            "CREATE_CHAPTER" to PanasonicDigaCatalog.CREATE_CHAPTER,
            "DELETE_TITLE" to PanasonicDigaCatalog.DELETE_TITLE,
            "REC" to PanasonicDigaCatalog.REC,
            "REC_MODE" to PanasonicDigaCatalog.REC_MODE,
            "EXT_LINK" to PanasonicDigaCatalog.EXT_LINK,
            "INPUT_SELECT" to PanasonicDigaCatalog.INPUT_SELECT,
            "CH_UP" to PanasonicDigaCatalog.CH_UP,
            "CH_DOWN" to PanasonicDigaCatalog.CH_DOWN,
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
            "PROG_CHECK" to PanasonicDigaCatalog.PROG_CHECK
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
        val macros = listOf(
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
            PanasonicDigaCatalog.Macros.TVGOS_DISABLE_ZIP
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
        val pulsesDvdMode1 = KaseikyoProtocol.synthesizePulsePattern(
            DigaDevice.DVD_HDD.id,
            RemoteMode.MODE_1,
            PanasonicDigaCatalog.OBC.POWER
        )
        assertEquals(100, pulsesDvdMode1.size)
        assertEquals(3459, pulsesDvdMode1[0]) // Lead-in mark
        assertEquals(1729, pulsesDvdMode1[1]) // Lead-in space
        assertEquals(432, pulsesDvdMode1[2])  // Bit mark

        // Validate that synthesized Pronto Hex parses bit-accurately
        val prontoHex = KaseikyoProtocol.synthesizeProntoHex(
            DigaDevice.DVD_HDD.id,
            RemoteMode.MODE_1,
            PanasonicDigaCatalog.OBC.POWER
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
        val vcrPlayCode = PanasonicDigaCatalog.getProntoHex(
            PanasonicDigaCatalog.OBC.PLAY,
            mode = RemoteMode.MODE_1,
            device = DigaDevice.VCR
        )
        val vcrSignal = ProntoHexConverter.convert(vcrPlayCode)
        assertEquals(37010, vcrSignal.carrierFrequency)
        assertEquals(100, vcrSignal.patternMicroseconds.size)
    }
}
