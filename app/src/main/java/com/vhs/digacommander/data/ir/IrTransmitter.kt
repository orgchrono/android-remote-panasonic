package com.vhs.digacommander.data.ir

import com.vhs.digacommander.data.model.DigaDevice
import com.vhs.digacommander.data.model.IrSignal
import com.vhs.digacommander.data.model.RemoteMode

/**
 * Domain-level contract for infrared hardware transmitters.
 * Decouples coroutine execution pipelines and ViewModels from physical Android hardware APIs.
 */
interface IrTransmitter {
    /**
     * True if infrared emitting hardware is present and operational.
     */
    val isAvailable: Boolean

    /**
     * Verifies if a given carrier frequency in Hertz is supported.
     */
    fun isFrequencySupported(frequencyHz: Int): Boolean

    /**
     * Transmits a raw [IrSignal].
     */
    fun transmit(signal: IrSignal): Result<Unit>

    /**
     * Converts and transmits a Philips Pronto Hex encoded pulse sequence.
     */
    fun transmitPronto(prontoHex: String): Result<Unit>

    /**
     * Synthesizes and transmits a 48-bit Kaseikyo pulse sequence directly.
     */
    fun transmitKaseikyo(
        device: DigaDevice = DigaDevice.DVD_HDD,
        mode: RemoteMode = RemoteMode.MODE_1,
        command: Int,
    ): Result<Unit>
}
