package com.vhs.digacommander.data.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import com.vhs.digacommander.data.constant.DigaTimings
import com.vhs.digacommander.data.model.DigaDevice
import com.vhs.digacommander.data.model.IrSignal
import com.vhs.digacommander.data.model.RemoteMode

/**
 * Direct hardware transmitter interfacing with Android's [ConsumerIrManager].
 * Implements [IrTransmitter] domain abstraction.
 */
class ConsumerIrTransmitter(context: Context) : IrTransmitter {
    private val irManager: ConsumerIrManager? =
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

    override val isAvailable: Boolean
        get() = irManager?.hasIrEmitter() == true

    override fun isFrequencySupported(frequencyHz: Int): Boolean {
        if (!isAvailable) return false
        val ranges = irManager?.carrierFrequencies ?: return false
        return ranges.any { frequencyHz in it.minFrequency..it.maxFrequency }
    }

    override fun transmit(signal: IrSignal): Result<Unit> =
        runCatching {
            check(isAvailable) { "Consumer IR hardware unavailable on this device" }
            irManager?.transmit(signal.carrierFrequency, signal.patternMicroseconds)
        }

    override fun transmitPronto(prontoHex: String): Result<Unit> =
        ProntoHexConverter.convertCatching(prontoHex).mapCatching { signal ->
            transmit(signal).getOrThrow()
        }

    override fun transmitKaseikyo(
        device: DigaDevice,
        mode: RemoteMode,
        command: Int,
    ): Result<Unit> =
        runCatching {
            check(isAvailable) { "Consumer IR hardware unavailable on this device" }
            val pattern = KaseikyoProtocol.synthesizePulsePattern(device.id, mode, command)
            irManager?.transmit(DigaTimings.CARRIER_MATSUSHITA_HZ, pattern)
        }
}
