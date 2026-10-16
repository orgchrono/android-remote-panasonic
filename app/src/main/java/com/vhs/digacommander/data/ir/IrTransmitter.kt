package com.vhs.digacommander.data.ir

import com.vhs.digacommander.data.model.DigaDevice
import com.vhs.digacommander.data.model.IrCapabilities
import com.vhs.digacommander.data.model.IrSignal
import com.vhs.digacommander.data.model.IrTransmitConfig
import com.vhs.digacommander.data.model.RemoteMode

/**
 * Domain-level contract for infrared hardware transmitters.
 * Decouples coroutine execution pipelines and ViewModels from physical Android hardware APIs.
 *
 * Every transmit call is blocking for the duration of the pulse train, so callers must invoke
 * them from a background dispatcher.
 */
interface IrTransmitter {
    /**
     * True if infrared emitting hardware is present and operational.
     */
    val isAvailable: Boolean

    /**
     * Fresh snapshot of what the platform reports about the emitter (never cached).
     */
    val capabilities: IrCapabilities

    /**
     * Active transmission settings (timing unit, frame repetition).
     */
    val config: IrTransmitConfig

    /**
     * Replaces the active transmission settings.
     */
    fun updateConfig(config: IrTransmitConfig)

    /**
     * Verifies if a given carrier frequency in Hertz is natively supported by the driver.
     */
    fun isFrequencySupported(frequencyHz: Int): Boolean

    /**
     * Frequency that will really be emitted for a nominal [frequencyHz] after driver adaptation.
     */
    fun resolveCarrier(frequencyHz: Int): Int

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
