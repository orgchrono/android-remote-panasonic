package com.vhs.digacommander.data.model

import com.vhs.digacommander.data.catalog.PanasonicDigaCatalog
import com.vhs.digacommander.data.ir.KaseikyoProtocol

/**
 * Hardware category groupings covering all Panasonic devices in the bundled IR catalog.
 */
enum class PanasonicCategory(
    val code: String,
    val displayLabel: String,
) {
    ALL("ALL", "All"),
    DIGA("DIGA", "DIGA DVD/HDD"),
    COMBO("COMBO", "VHS+DVD Combo"),
    VCR("VCR", "VCR / VHS"),
    DVD("DVD", "DVD / Blu-Ray"),
    TV("TV", "Viera TV"),
    AUDIO("AUDIO", "Audio / AVR"),
    STB("STB", "Set-Top Box"),
    PROJ("PROJ", "Projector"),
    AC("AC", "Air Cond."),
    OTHER("OTHER", "Other"),
    ;

    companion object {
        fun fromCode(code: String): PanasonicCategory = entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: OTHER
    }
}

/**
 * Logical grouping used when auto-assembling a Panasonic remote control keymap.
 */
enum class KeySection(
    val title: String,
    val sortOrder: Int,
) {
    POWER_INPUT("Power & Inputs", 0),
    NAVIGATION("Navigation & Menus", 1),
    TRANSPORT("Transport & Recording", 2),
    TUNING_AUDIO("Volume, Channels & Audio", 3),
    NUMERIC("Numeric Keypad", 4),
    RECORDING_EDIT("Editing, Timer & Service", 5),
    CUSTOM("Custom & Extended Keys", 6),
    ;

    companion object {
        fun fromName(name: String): KeySection = entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: CUSTOM
    }
}

/**
 * Underlying infrared wire encoding for an assembled or custom button.
 */
enum class KeyProtocol {
    KASEIKYO,
    PRONTO_RAW,
    ;

    companion object {
        fun fromName(name: String): KeyProtocol = entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: KASEIKYO
    }
}

/**
 * Presentation-independent colour role for a dynamic button.
 */
enum class KeyTone {
    NEUTRAL,
    PRIMARY,
    INFO,
    SUCCESS,
    WARNING,
    DANGER,
    DANGER_DARK,
    ACCENT,
    ;

    companion object {
        fun fromName(name: String): KeyTone = entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: NEUTRAL
    }
}

/**
 * Normalized command definition parsed from the bundled offline Panasonic IR catalog.
 */
data class CatalogCommandSpec(
    val name: String,
    val protocol: KeyProtocol,
    val device: Int = PanasonicDigaCatalog.OBC.STOP,
    val subdevice: Int = 0x00,
    val obc: Int = 0x00,
    val prontoHex: String? = null,
)

/**
 * Deduplicated IR codeset mapping one or more physical Panasonic models to their command bank.
 */
data class PanasonicCatalogCodeset(
    val id: String,
    val category: PanasonicCategory,
    val models: List<String>,
    val commands: List<CatalogCommandSpec>,
) {
    val primaryDisplayName: String
        get() = models.firstOrNull() ?: id

    val searchBlobLower: String by lazy(LazyThreadSafetyMode.NONE) {
        "${models.joinToString(" ")} $id ${category.displayLabel}".lowercase()
    }
}

/**
 * Runtime-editable key inside an auto-assembled or user-created remote layout.
 */
data class DynamicRemoteKey(
    val id: String,
    val label: String,
    val section: KeySection = KeySection.CUSTOM,
    val protocol: KeyProtocol = KeyProtocol.KASEIKYO,
    val device: Int = DigaDevice.DVD_HDD.id,
    val subdevice: Int = KaseikyoProtocol.SUBDEVICE_PRIMARY,
    val obc: Int = 0x00,
    val respectRemoteMode: Boolean = true,
    val prontoHex: String? = null,
    val tone: KeyTone = KeyTone.NEUTRAL,
    val confirmRequired: Boolean = false,
) {
    /**
     * Resolves the effective Subdevice byte when [activeMode] (`SET 1/2/3`) is active.
     */
    fun effectiveSubdevice(activeMode: RemoteMode): Int =
        if (respectRemoteMode && protocol == KeyProtocol.KASEIKYO) {
            (activeMode.subdevice or (subdevice and 0x0F)) and 0xFF
        } else {
            subdevice and 0xFF
        }

    /**
     * Produces a concise hexadecimal frame preview (e.g. `"02 20 B0 01 51 E0"` or `"PRONTO 104w"`).
     */
    fun formatWirePreview(activeMode: RemoteMode = RemoteMode.MODE_1): String =
        if (protocol == KeyProtocol.KASEIKYO) {
            val payload = KaseikyoProtocol.buildPayloadRaw(device, effectiveSubdevice(activeMode), obc)
            payload.joinToString(" ") { byte -> byte.toString(HEX_RADIX).uppercase().padStart(2, '0') }
        } else {
            val wordCount = prontoHex?.trim()?.split(Regex("\\s+"))?.size ?: 0
            "PRONTO (${wordCount}w)"
        }

    private companion object {
        const val HEX_RADIX = 16
    }
}

/**
 * Editable, persisted remote control profile assembled from a codeset or created manually.
 */
data class DynamicKeymapProfile(
    val id: String,
    val name: String,
    val sourceCodesetId: String,
    val category: PanasonicCategory,
    val keys: List<DynamicRemoteKey>,
    val isUserModified: Boolean = false,
)
