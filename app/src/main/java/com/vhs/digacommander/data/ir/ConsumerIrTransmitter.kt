package com.vhs.digacommander.data.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import com.vhs.digacommander.data.model.DigaDevice
import com.vhs.digacommander.data.model.IrSignal
import com.vhs.digacommander.data.model.RemoteMode

/**
 * Direct hardware transmitter interfacing with Android's [ConsumerIrManager].
 */
class ConsumerIrTransmitter(context: Context) {

    private val irManager: ConsumerIrManager? =
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

    /**
     * True if the host Android device possesses physical infrared emitting hardware.
     */
    val isAvailable: Boolean
        get() = irManager?.hasIrEmitter() == true

    /**
     * Checks if a carrier frequency (e.g. ~37 kHz for Panasonic) is supported by the hardware emitter.
     */
    fun isFrequencySupported(frequencyHz: Int): Boolean {
        if (!isAvailable) return false
        val ranges = irManager?.carrierFrequencies ?: return false
        return ranges.any { frequencyHz in it.minFrequency..it.maxFrequency }
    }

    /**
     * Transmits a low-level [IrSignal].
     */
    fun transmit(signal: IrSignal): Result<Unit> = runCatching {
        check(isAvailable) { "Consumer IR hardware unavailable on this device" }
        irManager?.transmit(signal.carrierFrequency, signal.patternMicroseconds)
    }

    /**
     * Parses and transmits a Philips Pronto Hex formatted string.
     */
    fun transmitPronto(prontoHex: String): Result<Unit> =
        ProntoHexConverter.convertCatching(prontoHex).mapCatching { signal ->
            transmit(signal).getOrThrow()
        }

    /**
     * Direct parameterized transmission synthesizing Kaseikyo pulses with zero string allocation.
     */
    fun transmitKaseikyo(
        device: DigaDevice = DigaDevice.DVD_HDD,
        mode: RemoteMode = RemoteMode.MODE_1,
        command: Int
    ): Result<Unit> = runCatching {
        check(isAvailable) { "Consumer IR hardware unavailable on this device" }
        val pattern = KaseikyoProtocol.synthesizePulsePattern(device.id, mode, command)
        irManager?.transmit(KaseikyoProtocol.CARRIER_FREQUENCY_HZ, pattern)
    }
}
