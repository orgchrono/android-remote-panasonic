package com.vhs.digacommander.data.ir

import com.vhs.digacommander.data.constant.DigaTimings
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

    /**
     * Synthesizes and transmits an arbitrary 48-bit Kaseikyo frame with explicit device and
     * subdevice bytes (e.g. extended Subdevice 0x01 `EXIT`, Combo 0x05 `INPUT_VHS`, or 0xA0 Audio).
     */
    fun transmitKaseikyoRaw(
        device: Int = DigaDevice.DVD_HDD.id,
        mode: RemoteMode = RemoteMode.MODE_1,
        command: Int,
        subdeviceGroup: Int = KaseikyoProtocol.SUBDEVICE_PRIMARY,
        respectMode: Boolean = true,
    ): Result<Unit> {
        val effectiveSubdevice = if (respectMode) mode.subdevice or (subdeviceGroup and 0x0F) else subdeviceGroup and 0xFF
        val pattern = KaseikyoProtocol.synthesizePulsePatternRaw(device, effectiveSubdevice, command)
        return transmit(IrSignal(DigaTimings.CARRIER_MATSUSHITA_HZ, pattern))
    }
}
