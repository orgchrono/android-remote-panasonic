package com.vhs.digacommander

import com.vhs.digacommander.core.ir.PanasonicIrCodes
import com.vhs.digacommander.core.ir.ProntoHexConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProntoHexConverterTest {

    @Test
    fun testProntoHexCarrierFrequencyConversion() {
        // Código 1: freqCode = 0070 (112 decimal) -> ~37010 Hz
        val signal1 = ProntoHexConverter.convert(PanasonicIrCodes.DIGIT_1)
        assertEquals(37010, signal1.carrierFrequency)

        // Código POWER: freqCode = 0070 (112 decimal) -> ~37010 Hz
        val signalPower = ProntoHexConverter.convert(PanasonicIrCodes.POWER)
        assertEquals(37010, signalPower.carrierFrequency)
    }

    @Test
    fun testPatternMicrosecondsLengthAndTiming() {
        // DIGIT 1 possui 100 palavras de dados (50 burst-pairs)
        val signal = ProntoHexConverter.convert(PanasonicIrCodes.DIGIT_1)
        assertEquals(100, signal.patternMicroseconds.size)

        // Primeiro pulso (Lead-in Mark): 0x0080 = 128 ciclos a 37010 Hz -> ~3459 µs
        val leadInMark = signal.patternMicroseconds[0]
        assertTrue("Lead-in mark deve ser próximo de 3459 µs, obtido: $leadInMark", leadInMark in 3300..3600)

        // Segundo pulso (Lead-in Space): 0x0040 = 64 ciclos a 37010 Hz -> ~1729 µs
        val leadInSpace = signal.patternMicroseconds[1]
        assertTrue("Lead-in space deve ser próximo de 1729 µs, obtido: $leadInSpace", leadInSpace in 1650..1800)

        // Bit lógico 0 (0010 0010): 16 ciclos mark / 16 ciclos space -> ~432 µs mark / 432 µs space
        val bitMark = signal.patternMicroseconds[2]
        val bitSpace = signal.patternMicroseconds[3]
        assertTrue("Bit mark deve ser ~432 µs, obtido: $bitMark", bitMark in 410..450)
        assertTrue("Bit space deve ser ~432 µs, obtido: $bitSpace", bitSpace in 410..450)
    }

    @Test
    fun testStandardMirroredDigaCommandsIntegrity() {
        val standardCodes = listOf(
            "DRIVE_SELECT" to PanasonicIrCodes.DRIVE_SELECT,
            "DIRECT_NAVIGATOR" to PanasonicIrCodes.DIRECT_NAVIGATOR,
            "FUNCTIONS" to PanasonicIrCodes.FUNCTIONS,
            "TOP_MENU" to PanasonicIrCodes.TOP_MENU,
            "OPTION_SUB_MENU" to PanasonicIrCodes.OPTION_SUB_MENU,
            "TIME_SLIP" to PanasonicIrCodes.TIME_SLIP,
            "MANUAL_SKIP_30S" to PanasonicIrCodes.MANUAL_SKIP_30S,
            "MANUAL_REPLAY_10S" to PanasonicIrCodes.MANUAL_REPLAY_10S,
            "CREATE_CHAPTER" to PanasonicIrCodes.CREATE_CHAPTER,
            "DELETE_TITLE" to PanasonicIrCodes.DELETE_TITLE,
            "REC" to PanasonicIrCodes.REC,
            "REC_MODE" to PanasonicIrCodes.REC_MODE,
            "EXT_LINK" to PanasonicIrCodes.EXT_LINK,
            "INPUT_SELECT" to PanasonicIrCodes.INPUT_SELECT,
            "CH_UP" to PanasonicIrCodes.CH_UP,
            "CH_DOWN" to PanasonicIrCodes.CH_DOWN,
            "PLAY" to PanasonicIrCodes.PLAY,
            "STOP" to PanasonicIrCodes.STOP,
            "PAUSE" to PanasonicIrCodes.PAUSE,
            "FAST_FORWARD" to PanasonicIrCodes.FAST_FORWARD,
            "REWIND" to PanasonicIrCodes.REWIND,
            "SKIP_NEXT" to PanasonicIrCodes.SKIP_NEXT,
            "SKIP_PREV" to PanasonicIrCodes.SKIP_PREV,
            "AUDIO" to PanasonicIrCodes.AUDIO,
            "SUBTITLE" to PanasonicIrCodes.SUBTITLE,
            "STATUS" to PanasonicIrCodes.STATUS,
            "DISPLAY" to PanasonicIrCodes.DISPLAY,
            "GUIDE" to PanasonicIrCodes.GUIDE,
            "PROG_CHECK" to PanasonicIrCodes.PROG_CHECK
        )

        for ((name, prontoHex) in standardCodes) {
            val converted = ProntoHexConverter.convert(prontoHex)
            assertTrue("$name: Portadora deve ser ~37kHz ou ~38kHz", converted.carrierFrequency in 36000..39000)
            assertTrue("$name: Padrão deve possuir exatamente 100 pulsos", converted.patternMicroseconds.size == 100)
            // Lead-in mark check (~3.2ms)
            assertTrue("$name: Lead-in mark válido", converted.patternMicroseconds[0] in 3200..3500)
        }
    }

    @Test
    fun testAllPredefinedMacrosIntegrity() {
        // Valida que todas as macros possuem passos com códigos Pronto Hex válidos
        val macros = listOf(
            PanasonicIrCodes.Macros.LASER_HOURS,
            PanasonicIrCodes.Macros.RAM_DRIVE_LAST_ERROR,
            PanasonicIrCodes.Macros.ERROR_HISTORY_LATEST,
            PanasonicIrCodes.Macros.ERROR_HISTORY_SLOT_1,
            PanasonicIrCodes.Macros.ERROR_HISTORY_SLOT_2,
            PanasonicIrCodes.Macros.ERROR_HISTORY_SLOT_3,
            PanasonicIrCodes.Macros.ERROR_HISTORY_SLOT_4,
            PanasonicIrCodes.Macros.ERROR_HISTORY_SLOT_5,
            PanasonicIrCodes.Macros.REGION_READOUT,
            PanasonicIrCodes.Macros.HDD_SURFACE_TEST,
            PanasonicIrCodes.Macros.VFD_SEGMENTS_TEST,
            PanasonicIrCodes.Macros.VIDEO_IO_TEST_80,
            PanasonicIrCodes.Macros.VIDEO_IO_TEST_81,
            PanasonicIrCodes.Macros.TRAY_CYCLE_TEST,
            PanasonicIrCodes.Macros.CLEAR_ERROR_F00,
            PanasonicIrCodes.Macros.MULTI_REGION_HACK,
            PanasonicIrCodes.Macros.VERIFY_REGION_FREE,
            PanasonicIrCodes.Macros.CHILD_LOCK_UNLOCK,
            PanasonicIrCodes.Macros.SYNC_REMOTE_MODE_1,
            PanasonicIrCodes.Macros.SYNC_REMOTE_MODE_2,
            PanasonicIrCodes.Macros.SYNC_REMOTE_MODE_3,
            PanasonicIrCodes.Macros.FACTORY_RESET,
            PanasonicIrCodes.Macros.TVGOS_DISABLE_ZIP
        )

        for (macro in macros) {
            assertTrue("Macro não pode ser vazia", macro.isNotEmpty())
            for (step in macro) {
                val converted = ProntoHexConverter.convert(step.prontoHex)
                assertTrue("Portadora deve ser 37kHz ou 38kHz", converted.carrierFrequency in 36000..39000)
                assertTrue("Padrão de pulsos deve possuir pelo menos 50 pulsos", converted.patternMicroseconds.size >= 50)
                assertTrue("Atraso deve ser positivo", step.postDelayMs > 0)
            }
        }
    }

    @Test
    fun testKaseikyoPulseGeneratorParametricCreation() {
        // Valida que o gerador paramétrico produz 100 pulsos exatos
        val pulsesDvdMode1 = com.vhs.digacommander.core.ir.KaseikyoPulseGenerator.generatePulsePattern(
            com.vhs.digacommander.core.ir.KaseikyoPulseGenerator.DEVICE_DVD_HDD,
            com.vhs.digacommander.core.ir.KaseikyoPulseGenerator.SUBDEVICE_MODE_1,
            PanasonicIrCodes.Commands.POWER
        )
        assertEquals(100, pulsesDvdMode1.size)
        assertEquals(3459, pulsesDvdMode1[0]) // Lead-in mark
        assertEquals(1729, pulsesDvdMode1[1]) // Lead-in space
        assertEquals(432, pulsesDvdMode1[2])  // Bit mark

        // Valida que o Pronto Hex gerado dinamicamente é aceito pelo ProntoHexConverter
        val prontoHex = com.vhs.digacommander.core.ir.KaseikyoPulseGenerator.generateProntoHex(
            com.vhs.digacommander.core.ir.KaseikyoPulseGenerator.DEVICE_DVD_HDD,
            com.vhs.digacommander.core.ir.KaseikyoPulseGenerator.SUBDEVICE_MODE_1,
            PanasonicIrCodes.Commands.POWER
        )
        val converted = ProntoHexConverter.convert(prontoHex)
        assertEquals(37010, converted.carrierFrequency)
        assertEquals(100, converted.patternMicroseconds.size)
    }

    @Test
    fun testRemoteModesAndVcrAddressing() {
        // Testa geração para Mode 1, Mode 2 e Mode 3 (SET 1, SET 2, SET 3)
        for (mode in 1..3) {
            val code = PanasonicIrCodes.getCode(PanasonicIrCodes.Commands.PLAY, mode = mode)
            val signal = ProntoHexConverter.convert(code)
            assertEquals(37010, signal.carrierFrequency)
            assertEquals(100, signal.patternMicroseconds.size)
        }

        // Testa geração para VCR Deck (Device 0x90)
        val vcrPlayCode = PanasonicIrCodes.getCode(PanasonicIrCodes.Commands.PLAY, mode = 1, isVcr = true)
        val vcrSignal = ProntoHexConverter.convert(vcrPlayCode)
        assertEquals(37010, vcrSignal.carrierFrequency)
        assertEquals(100, vcrSignal.patternMicroseconds.size)
    }
}
