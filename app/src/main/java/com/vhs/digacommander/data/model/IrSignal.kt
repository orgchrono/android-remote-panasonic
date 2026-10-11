package com.vhs.digacommander.data.model

/**
 * Immutable carrier frequency and timing interval representation for ConsumerIrManager.
 *
 * @property carrierFrequency Carrier modulation frequency in Hertz (e.g. 37010 Hz for Panasonic).
 * @property patternMicroseconds Alternating Mark and Space durations in microseconds.
 */
data class IrSignal(
    val carrierFrequency: Int,
    val patternMicroseconds: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IrSignal) return false
        return carrierFrequency == other.carrierFrequency &&
                patternMicroseconds.contentEquals(other.patternMicroseconds)
    }

    override fun hashCode(): Int = 31 * carrierFrequency + patternMicroseconds.contentHashCode()
}
