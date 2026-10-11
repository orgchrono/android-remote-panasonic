package com.vhs.digacommander

import com.vhs.digacommander.core.ir.PanasonicIrCodes
import com.vhs.digacommander.core.ir.ProntoHexConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProntoHexConverterTest {

    @Test
    fun testProntoHexCarrierFrequencyConversion() {
        // Código 1: freqCode = 006D (109 decimal) -> ~38029 Hz
        val signal1 = ProntoHexConverter.convert(PanasonicIrCodes.DIGIT_1)
        assertEquals(38029, signal1.carrierFrequency)

        // Código POWER: freqCode = 0070 (112 decimal) -> ~37010 Hz
        val signalPower = ProntoHexConverter.convert(PanasonicIrCodes.POWER)
        assertEquals(37010, signalPower.carrierFrequency)
    }

    @Test
    fun testPatternMicrosecondsLengthAndTiming() {
        // DIGIT 1 possui 100 palavras de dados (50 burst-pairs)
        val signal = ProntoHexConverter.convert(PanasonicIrCodes.DIGIT_1)
        assertEquals(100, signal.patternMicroseconds.size)

        // Primeiro pulso (Lead-in Mark): 0x007D = 125 ciclos a 38029 Hz -> ~3287 µs
        val leadInMark = signal.patternMicroseconds[0]
        assertTrue("Lead-in mark deve ser próximo de 3287 µs, obtido: $leadInMark", leadInMark in 3200..3400)

        // Segundo pulso (Lead-in Space): 0x0030 = 48 ciclos a 38029 Hz -> ~1262 µs
        val leadInSpace = signal.patternMicroseconds[1]
        assertTrue("Lead-in space deve ser próximo de 1262 µs, obtido: $leadInSpace", leadInSpace in 1200..1300)

        // Bit lógico 0 (0010 0010): 16 ciclos mark / 16 ciclos space -> ~421 µs mark / 421 µs space
        val bitMark = signal.patternMicroseconds[2]
        val bitSpace = signal.patternMicroseconds[3]
        assertTrue("Bit mark deve ser ~421 µs, obtido: $bitMark", bitMark in 400..440)
        assertTrue("Bit space deve ser ~421 µs, obtido: $bitSpace", bitSpace in 400..440)
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
            PanasonicIrCodes.Macros.REGION_READOUT,
            PanasonicIrCodes.Macros.HDD_SURFACE_TEST,
            PanasonicIrCodes.Macros.VFD_SEGMENTS_TEST,
            PanasonicIrCodes.Macros.TRAY_CYCLE_TEST,
            PanasonicIrCodes.Macros.CLEAR_ERROR_F00,
            PanasonicIrCodes.Macros.MULTI_REGION_HACK,
            PanasonicIrCodes.Macros.CHILD_LOCK_UNLOCK,
            PanasonicIrCodes.Macros.SYNC_REMOTE_MODE_2,
            PanasonicIrCodes.Macros.FACTORY_RESET
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
}
