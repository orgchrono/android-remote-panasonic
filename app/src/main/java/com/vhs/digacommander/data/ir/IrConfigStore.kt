package com.vhs.digacommander.data.ir

import android.content.Context
import com.vhs.digacommander.data.constant.DigaTimings
import com.vhs.digacommander.data.model.IrTimingUnit
import com.vhs.digacommander.data.model.IrTransmitConfig

/**
 * Persistence port for [IrTransmitConfig] so a calibrated infrared setup survives restarts.
 */
interface IrConfigStore {
    fun load(): IrTransmitConfig

    fun save(config: IrTransmitConfig)
}

/**
 * Volatile store used by tests and previews.
 */
class InMemoryIrConfigStore(
    private var current: IrTransmitConfig = IrTransmitConfig(),
) : IrConfigStore {
    override fun load(): IrTransmitConfig = current

    override fun save(config: IrTransmitConfig) {
        current = config
    }
}

/**
 * [android.content.SharedPreferences]-backed store.
 */
class SharedPrefsIrConfigStore(
    context: Context,
    private val fallbackConfig: IrTransmitConfig = IrTransmitConfig(),
) : IrConfigStore {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun load(): IrTransmitConfig {
        val savedUnitName = prefs.getString(KEY_TIMING_UNIT_V2, null)
        val unit =
            when (savedUnitName) {
                null -> fallbackConfig.timingUnit
                LEGACY_FPGA_UNIT_NAME -> IrTimingUnit.PERIOD_SCALED
                else -> runCatching { IrTimingUnit.valueOf(savedUnitName) }.getOrDefault(fallbackConfig.timingUnit)
            }
        val repeat =
            prefs
                .getInt(KEY_REPEAT_COUNT, fallbackConfig.repeatCount)
                .coerceIn(DigaTimings.DEFAULT_FRAME_REPEAT, DigaTimings.MAX_FRAME_REPEAT)
        return IrTransmitConfig(timingUnit = unit, repeatCount = repeat)
    }

    override fun save(config: IrTransmitConfig) {
        prefs
            .edit()
            .putString(KEY_TIMING_UNIT_V2, config.timingUnit.name)
            .putInt(KEY_REPEAT_COUNT, config.repeatCount)
            .apply()
    }

    private companion object {
        const val PREFS_NAME = "diga_ir_config"
        const val KEY_TIMING_UNIT_V2 = "timing_unit_v2"
        const val KEY_REPEAT_COUNT = "repeat_count"
        const val LEGACY_FPGA_UNIT_NAME = "EXYNOS_FPGA"
    }
}
