package com.vhs.digacommander.data.ir

import com.vhs.digacommander.data.constant.DigaTimings
import com.vhs.digacommander.data.model.IrSignal
import kotlin.math.roundToInt

/**
 * Functional parser converting Philips Pronto Hex timing strings into Android
 * ConsumerIrManager compatible microseconds pulse arrays.
 */
object ProntoHexConverter {
    private const val PRONTO_TIMEBASE_CONSTANT = DigaTimings.CARRIER_FREQ_DIVISOR_BASE

    /**
     * Converts a raw Pronto Hex string into an [IrSignal].
     * Throws [IllegalArgumentException] on invalid formatting.
     */
    fun convert(prontoHex: String): IrSignal {
        val tokens = prontoHex.trim().split("\\s+".toRegex())
        require(tokens.size >= DigaTimings.PRONTO_HEADER_WORD_COUNT) { "Invalid Pronto Hex: missing minimum header words" }

        val formatCode = tokens[0].toInt(16)
        require(formatCode == 0) { "Only raw learned Pronto signals (format 0000) are supported" }

        val freqCode = tokens[1].toInt(16)
        require(freqCode > 0) { "Invalid carrier frequency divisor" }

        // Carrier frequency calculation: f = 1,000,000 / (freqCode * 0.241246)
        val carrierFreq = (1_000_000.0 / (freqCode * PRONTO_TIMEBASE_CONSTANT)).roundToInt()
        val cycleToMicroseconds = 1_000_000.0 / carrierFreq

        val rawDataWords = tokens.subList(DigaTimings.PRONTO_HEADER_WORD_COUNT, tokens.size)
        val pattern =
            IntArray(rawDataWords.size) { index ->
                val cycles = rawDataWords[index].toInt(16)
                (cycles * cycleToMicroseconds).roundToInt()
            }

        return IrSignal(carrierFreq, pattern)
    }

    /**
     * Functional wrapper returning a Kotlin [Result] for pure error handling.
     */
    fun convertCatching(prontoHex: String): Result<IrSignal> =
        runCatching {
            convert(prontoHex)
        }
}
