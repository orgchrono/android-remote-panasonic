package com.vhs.digacommander.data.model

/**
 * Supported hardware profiles for Panasonic DIGA recorder families.
 */
enum class DigaModelProfile(
    val id: String,
    val displayName: String,
    val hasHdd: Boolean,
    val hasVcr: Boolean,
    val hasTbc: Boolean,
) {
    DMR_EH55("DMR-EH55", "DMR-EH55 (HDD/DVD Flagship)", true, false, false),
    DMR_ES10_ES15("ES10/ES15", "DMR-ES10/ES15 (TBC Pass-Through)", false, false, true),
    COMBO_VHS_DVD("COMBO VHS", "Combo VHS/DVD (ES30V/EH75V/EZ48V)", true, true, false),
    UNIVERSAL("UNIVERSAL", "Universal DIGA (All Models)", true, true, true),
    ;

    companion object {
        fun fromId(id: String): DigaModelProfile = entries.find { it.id.equals(id, ignoreCase = true) } ?: DMR_EH55
    }
}
