package com.vhs.digacommander.data.model

/**
 * Inclusive carrier frequency range (Hz) reported by the IR emitter driver.
 */
data class IrFrequencyRange(
    val minHz: Int,
    val maxHz: Int,
) {
    operator fun contains(frequencyHz: Int): Boolean = frequencyHz in minHz..maxHz
}

/**
 * Identification of the physical device and ROM, used for diagnostics only.
 */
data class IrDeviceInfo(
    val manufacturer: String = "",
    val model: String = "",
    val hardware: String = "",
    val androidRelease: String = "",
    val sdkInt: Int = 0,
)

/**
 * Snapshot of what the platform reports about the infrared emitter.
 *
 * @property hasEmitter Result of `ConsumerIrManager.hasIrEmitter()`.
 * @property serviceFound True when the system exposes the consumer IR service at all.
 * @property ranges Carrier frequency ranges accepted by the driver (empty when unknown).
 */
data class IrCapabilities(
    val hasEmitter: Boolean = false,
    val serviceFound: Boolean = false,
    val ranges: List<IrFrequencyRange> = emptyList(),
    val device: IrDeviceInfo = IrDeviceInfo(),
)
