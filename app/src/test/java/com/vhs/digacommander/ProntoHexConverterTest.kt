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
        // Verificando o par na posição 2 e 3
        val bitMark = signal.patternMicroseconds[2]
        val bitSpace = signal.patternMicroseconds[3]
        assertTrue("Bit mark deve ser ~421 µs, obtido: $bitMark", bitMark in 400..440)
        assertTrue("Bit space deve ser ~421 µs, obtido: $bitSpace", bitSpace in 400..440)
    }
}
