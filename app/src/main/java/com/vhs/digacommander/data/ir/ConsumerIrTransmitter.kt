package com.vhs.digacommander.data.ir

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.ConsumerIrManager
import android.os.Build
import com.vhs.digacommander.data.constant.DigaTimings
import com.vhs.digacommander.data.model.DigaDevice
import com.vhs.digacommander.data.model.IrCapabilities
import com.vhs.digacommander.data.model.IrDeviceInfo
import com.vhs.digacommander.data.model.IrFrequencyRange
import com.vhs.digacommander.data.model.IrSignal
import com.vhs.digacommander.data.model.IrTransmitConfig
import com.vhs.digacommander.data.model.RemoteMode
import java.io.File
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit

/**
 * Raised when the platform exposes no usable infrared emitter.
 */
class IrUnavailableException(
    message: String = DEFAULT_MESSAGE,
) : IllegalStateException(message) {
    companion object {
        const val DEFAULT_MESSAGE: String = "Consumer IR hardware unavailable on this device"
    }
}

/**
 * Direct hardware transmitter interfacing with Android's [ConsumerIrManager], Samsung's legacy
 * `irda` system service, and the Samsung Exynos `/sys/class/sec/sec_ir/ir_send` kernel node
 * (`ir_remote_con_mc96` on GT-N8000 / Exynos 4412 and `ice4_fpga` on SM-T805M / Exynos 5420).
 */
class ConsumerIrTransmitter(
    context: Context,
    initialConfig: IrTransmitConfig = defaultConfigForDevice(),
) : IrTransmitter {
    private val appContext: Context = context.applicationContext

    private val irManager: ConsumerIrManager? =
        runCatching {
            appContext.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager
        }.getOrNull()

    @SuppressLint("WrongConstant")
    private val irdaService: Any? =
        runCatching {
            appContext.getSystemService(SAMSUNG_IRDA_SERVICE)
        }.getOrNull()

    private val irdaWriteMethod: Method? =
        runCatching {
            irdaService?.javaClass?.getMethod(SAMSUNG_IRDA_WRITE_METHOD, String::class.java)
        }.getOrNull()

    private val sysfsIrSend = File(SYSFS_SEC_IR_SEND)
    private val sysfsIrdaTest = File(SYSFS_SEC_IRDA_TEST)

    @Volatile
    private var suUnlockAttempted: Boolean = false

    @Volatile
    override var config: IrTransmitConfig = initialConfig
        private set

    override val isAvailable: Boolean
        get() =
            irManager.hasEmitterSafely() ||
                sysfsIrSend.exists() ||
                irdaWriteMethod != null ||
                readRanges().isNotEmpty()

    override val capabilities: IrCapabilities
        get() =
            IrCapabilities(
                hasEmitter = isAvailable,
                serviceFound = irManager != null || irdaWriteMethod != null || sysfsIrSend.exists(),
                ranges = readRanges(),
                device = readDeviceInfo(),
            )

    override fun updateConfig(config: IrTransmitConfig) {
        this.config = config
    }

    override fun isFrequencySupported(frequencyHz: Int): Boolean =
        isAvailable && IrSignalAdapter.isCarrierSupported(frequencyHz, readRanges())

    override fun resolveCarrier(frequencyHz: Int): Int = IrSignalAdapter.resolveCarrier(frequencyHz, readRanges())

    override fun transmit(signal: IrSignal): Result<Unit> =
        runCatching {
            if (!isAvailable) throw IrUnavailableException()
            val settings = config
            val carrier = resolveCarrier(signal.carrierFrequency)
            val deviceInfo = readDeviceInfo()
            val isFpga = IrSignalAdapter.isExynosFpgaDevice(deviceInfo, sysfsIrdaTest.exists())
            val sysfsPayload =
                IrSignalAdapter.formatSysfsPayload(
                    patternMicroseconds = signal.patternMicroseconds,
                    carrierHz = carrier,
                    expectsCyclesInSysfs = !isFpga,
                )
            val encodedPattern =
                IrSignalAdapter.encodePattern(
                    patternMicroseconds = signal.patternMicroseconds,
                    carrierHz = carrier,
                    unit = settings.timingUnit,
                )

            repeat(settings.repeatCount) {
                transmitSingleFrame(
                    carrier = carrier,
                    encodedPattern = encodedPattern,
                    sysfsPayload = sysfsPayload,
                )
            }
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
            val pattern = KaseikyoProtocol.synthesizePulsePattern(device.id, mode, command)
            transmit(IrSignal(DigaTimings.CARRIER_MATSUSHITA_HZ, pattern)).getOrThrow()
        }

    private fun transmitSingleFrame(
        carrier: Int,
        encodedPattern: IntArray,
        sysfsPayload: String,
    ) {
        // 1. Direct sysfs write to /sys/class/sec/sec_ir/ir_send if accessible.
        if (tryWriteSysfsDirect(sysfsPayload)) return

        // 2. On rooted devices (e.g. LineageOS 18.1 on GT-N8000 where /sys/class/sec/sec_ir/ir_send
        //    is left 0664/0660), perform a one-time best-effort permission unlock.
        if (sysfsIrSend.exists() && !sysfsIrSend.canWrite() && !suUnlockAttempted) {
            unlockSysfsViaSu()
            if (tryWriteSysfsDirect(sysfsPayload)) return
        }

        // 3. Direct root execution via su. Crucial when SELinux enforces untrusted_app separation
        //    or when the ROM's ConsumerIR HAL is missing or failing silently.
        if (sysfsIrSend.exists() && tryWriteSysfsViaSu(sysfsPayload)) return

        // 4. Samsung legacy "irda" system service (IrdaManager.write_irsend) if exposed by TouchWiz.
        if (tryWriteSamsungIrdaService(sysfsPayload)) return

        // 5. Standard Android ConsumerIrManager (with Exynos cycle / FPGA 27x compensation).
        val manager = irManager ?: throw IrUnavailableException()
        manager.transmit(carrier, encodedPattern)
    }

    private fun tryWriteSysfsDirect(payload: String): Boolean =
        runCatching {
            if (!sysfsIrSend.exists() || !sysfsIrSend.canWrite()) return false
            sysfsIrSend.writeText(payload)
            true
        }.getOrDefault(false)

    private fun tryWriteSamsungIrdaService(payload: String): Boolean {
        val service = irdaService ?: return false
        val method = irdaWriteMethod ?: return false
        return runCatching {
            method.invoke(service, payload)
            true
        }.getOrDefault(false)
    }

    private fun unlockSysfsViaSu() {
        suUnlockAttempted = true
        runSuCommand(
            "chmod 0666 $SYSFS_SEC_IR_SEND 2>/dev/null; " +
                "chown system:radio $SYSFS_SEC_IR_SEND 2>/dev/null; " +
                "chmod 0666 $SYSFS_SEC_IR_RESULT 2>/dev/null || true",
        )
    }

    private fun tryWriteSysfsViaSu(payload: String): Boolean {
        // Method A: Pipe directly to su stdin (avoids command-line argument limits and quote issues)
        val piped =
            runCatching {
                val process = ProcessBuilder("su").redirectErrorStream(true).start()
                process.outputStream.bufferedWriter().use { writer ->
                    writer.write("echo '$payload' > $SYSFS_SEC_IR_SEND\n")
                    writer.write("exit\n")
                    writer.flush()
                }
                val finished = process.waitFor(SU_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                if (finished && process.exitValue() == 0) {
                    true
                } else {
                    process.destroy()
                    false
                }
            }.getOrDefault(false)

        if (piped) return true

        // Method B: Fallback via su -c "echo ..."
        return runSuCommand("echo '$payload' > $SYSFS_SEC_IR_SEND")
    }

    private fun runSuCommand(command: String): Boolean =
        runCatching {
            val process = ProcessBuilder("su", "-c", command).redirectErrorStream(true).start()
            val finished = process.waitFor(SU_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            if (!finished) {
                process.destroy()
                false
            } else {
                process.exitValue() == 0
            }
        }.getOrDefault(false)

    private fun readRanges(): List<IrFrequencyRange> =
        runCatching {
            irManager
                ?.carrierFrequencies
                .orEmpty()
                .map { IrFrequencyRange(it.minFrequency, it.maxFrequency) }
        }.getOrDefault(emptyList())

    private fun ConsumerIrManager?.hasEmitterSafely(): Boolean = this != null && runCatching { hasIrEmitter() }.getOrDefault(false)

    companion object {
        private const val SAMSUNG_IRDA_SERVICE = "irda"
        private const val SAMSUNG_IRDA_WRITE_METHOD = "write_irsend"
        private const val SYSFS_SEC_IR_SEND = "/sys/class/sec/sec_ir/ir_send"
        private const val SYSFS_SEC_IR_RESULT = "/sys/class/sec/sec_ir/ir_send_result"
        private const val SYSFS_SEC_IRDA_TEST = "/sys/class/sec/sec_ir/irda_test"
        private const val SU_TIMEOUT_MS = 5000L

        fun readDeviceInfo(): IrDeviceInfo {
            val extra = "${Build.PRODUCT.orEmpty()} ${Build.DEVICE.orEmpty()} ${Build.BOARD.orEmpty()}".trim()
            val fullModel = if (extra.isNotBlank()) "${Build.MODEL.orEmpty()} $extra" else Build.MODEL.orEmpty()
            return IrDeviceInfo(
                manufacturer = Build.MANUFACTURER.orEmpty(),
                model = fullModel,
                hardware = Build.HARDWARE.orEmpty(),
                androidRelease = Build.VERSION.RELEASE.orEmpty(),
                sdkInt = Build.VERSION.SDK_INT,
            )
        }

        fun defaultConfigForDevice(): IrTransmitConfig =
            IrTransmitConfig(
                timingUnit = IrSignalAdapter.recommendedTimingUnit(readDeviceInfo()),
                repeatCount = DigaTimings.DEFAULT_FRAME_REPEAT,
            )
    }
}
