package com.vhs.digacommander.domain.keymap

import com.vhs.digacommander.data.catalog.PanasonicDigaCatalog
import com.vhs.digacommander.data.ir.KaseikyoProtocol
import com.vhs.digacommander.data.model.CatalogCommandSpec
import com.vhs.digacommander.data.model.DigaDevice
import com.vhs.digacommander.data.model.DynamicKeymapProfile
import com.vhs.digacommander.data.model.DynamicRemoteKey
import com.vhs.digacommander.data.model.KeyProtocol
import com.vhs.digacommander.data.model.KeySection
import com.vhs.digacommander.data.model.KeyTone
import com.vhs.digacommander.data.model.PanasonicCatalogCodeset
import com.vhs.digacommander.data.model.PanasonicCategory
import com.vhs.digacommander.data.model.RemoteMode

/**
 * Self-assembling keymap builder that organizes any Panasonic codeset, `.irplus` XML, or
 * Flipper `.ir` capture into structured, runtime-editable remote sections.
 */
object DynamicKeymapAssembler {
    private val WhitespaceRegex = Regex("\\s+")
    private val ButtonTagRegex = Regex("<button\\b([^>]*)>(.*?)</button>", setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE))
    private val LabelAttrRegex = Regex("""label\s*=\s*"([^"]*)"""", RegexOption.IGNORE_CASE)
    private val ModelAttrRegex = Regex("""model\s*=\s*"([^"]*)"""", RegexOption.IGNORE_CASE)
    private val CodeTagRegex = Regex("<code>(.*?)</code>", setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE))

    /**
     * Auto-assembles a [DynamicKeymapProfile] from a [PanasonicCatalogCodeset], sorting keys into
     * intuitive functional groups and preserving exact wire addressing.
     */
    fun assembleFromCodeset(
        codeset: PanasonicCatalogCodeset,
        preferredModelName: String? = null,
    ): DynamicKeymapProfile {
        val displayTitle = preferredModelName?.takeIf { it.isNotBlank() } ?: codeset.primaryDisplayName
        val seenSpecs = HashSet<String>()
        val scoredKeys =
            codeset.commands.mapIndexedNotNull { idx, cmd ->
                val dedupKey =
                    if (cmd.protocol == KeyProtocol.KASEIKYO) {
                        "${cmd.name.lowercase()}|K|${cmd.device}|${cmd.subdevice}|${cmd.obc}"
                    } else {
                        "${cmd.name.lowercase()}|P|${cmd.prontoHex.orEmpty()}"
                    }
                if (!seenSpecs.add(dedupKey)) return@mapIndexedNotNull null
                val classification = classifyCommand(cmd)
                val respectMode =
                    cmd.protocol == KeyProtocol.KASEIKYO &&
                        (cmd.device == DigaDevice.DVD_HDD.id || cmd.device == DigaDevice.VCR.id) &&
                        (cmd.subdevice and 0xF0) == 0x00
                classification.sortKey to
                    DynamicRemoteKey(
                        id = "${codeset.id}_$idx",
                        label = prettifyCommandLabel(cmd.name),
                        section = classification.section,
                        protocol = cmd.protocol,
                        device = cmd.device,
                        subdevice = cmd.subdevice,
                        obc = cmd.obc,
                        respectRemoteMode = respectMode,
                        prontoHex = cmd.prontoHex,
                        tone = classification.tone,
                        confirmRequired = classification.confirmRequired,
                    )
            }
        val sortedKeys =
            scoredKeys
                .sortedWith(
                    compareBy<Pair<Int, DynamicRemoteKey>>(
                        { it.second.section.sortOrder },
                        { it.first },
                        { it.second.label.lowercase() },
                    ),
                ).map { it.second }

        return DynamicKeymapProfile(
            id = "profile_${codeset.id}",
            name = displayTitle,
            sourceCodesetId = codeset.id,
            category = codeset.category,
            keys = sortedKeys,
            isUserModified = false,
        )
    }

    /**
     * Fallback assembled profile used when the offline asset catalog is not injected (e.g. light unit tests).
     */
    fun assembleFallbackDigaProfile(): DynamicKeymapProfile {
        val core =
            listOf(
                CatalogCommandSpec("PowerToggle", KeyProtocol.KASEIKYO, DigaDevice.DVD_HDD.id, 0x00, PanasonicDigaCatalog.OBC.POWER),
                CatalogCommandSpec("Eject", KeyProtocol.KASEIKYO, DigaDevice.DVD_HDD.id, 0x00, PanasonicDigaCatalog.OBC.EJECT),
                CatalogCommandSpec("DriveSelect", KeyProtocol.KASEIKYO, DigaDevice.DVD_HDD.id, 0x00, PanasonicDigaCatalog.OBC.DRIVE_SELECT),
                CatalogCommandSpec("InputSelect", KeyProtocol.KASEIKYO, DigaDevice.DVD_HDD.id, 0x00, PanasonicDigaCatalog.OBC.INPUT_SELECT),
                CatalogCommandSpec("Functions", KeyProtocol.KASEIKYO, DigaDevice.DVD_HDD.id, 0x00, PanasonicDigaCatalog.OBC.FUNCTIONS),
                CatalogCommandSpec(
                    "DirectNavigator",
                    KeyProtocol.KASEIKYO,
                    DigaDevice.DVD_HDD.id,
                    0x00,
                    PanasonicDigaCatalog.OBC.DIRECT_NAVIGATOR,
                ),
                CatalogCommandSpec("DirectionUp", KeyProtocol.KASEIKYO, DigaDevice.DVD_HDD.id, 0x00, PanasonicDigaCatalog.OBC.UP),
                CatalogCommandSpec("DirectionDown", KeyProtocol.KASEIKYO, DigaDevice.DVD_HDD.id, 0x00, PanasonicDigaCatalog.OBC.DOWN),
                CatalogCommandSpec("DirectionLeft", KeyProtocol.KASEIKYO, DigaDevice.DVD_HDD.id, 0x00, PanasonicDigaCatalog.OBC.LEFT),
                CatalogCommandSpec("DirectionRight", KeyProtocol.KASEIKYO, DigaDevice.DVD_HDD.id, 0x00, PanasonicDigaCatalog.OBC.RIGHT),
                CatalogCommandSpec("Ok", KeyProtocol.KASEIKYO, DigaDevice.DVD_HDD.id, 0x00, PanasonicDigaCatalog.OBC.ENTER_OK),
                CatalogCommandSpec("Return", KeyProtocol.KASEIKYO, DigaDevice.DVD_HDD.id, 0x00, PanasonicDigaCatalog.OBC.RETURN),
                CatalogCommandSpec(
                    "Exit",
                    KeyProtocol.KASEIKYO,
                    DigaDevice.DVD_HDD.id,
                    KaseikyoProtocol.SUBDEVICE_EXTENDED,
                    PanasonicDigaCatalog.OBC.EXIT,
                ),
                CatalogCommandSpec("Play", KeyProtocol.KASEIKYO, DigaDevice.DVD_HDD.id, 0x00, PanasonicDigaCatalog.OBC.PLAY),
                CatalogCommandSpec("Pause", KeyProtocol.KASEIKYO, DigaDevice.DVD_HDD.id, 0x00, PanasonicDigaCatalog.OBC.PAUSE),
                CatalogCommandSpec("Stop", KeyProtocol.KASEIKYO, DigaDevice.DVD_HDD.id, 0x00, PanasonicDigaCatalog.OBC.STOP),
                CatalogCommandSpec("Record", KeyProtocol.KASEIKYO, DigaDevice.DVD_HDD.id, 0x00, PanasonicDigaCatalog.OBC.REC),
                CatalogCommandSpec("TvVolumeUp", KeyProtocol.KASEIKYO, DigaDevice.TV.id, 0x00, PanasonicDigaCatalog.OBC.VOL_UP),
                CatalogCommandSpec("TvVolumeDown", KeyProtocol.KASEIKYO, DigaDevice.TV.id, 0x00, PanasonicDigaCatalog.OBC.VOL_DOWN),
            )
        return assembleFromCodeset(
            PanasonicCatalogCodeset(
                id = "fallback_diga_eh55",
                category = PanasonicCategory.DIGA,
                models = listOf("DMR-EH55 (Built-in Fallback)"),
                commands = core,
            ),
        )
    }

    /**
     * Imports an `.irplus` XML profile into an editable [DynamicKeymapProfile].
     */
    fun importFromIrplusXml(
        xmlText: String,
        profileName: String? = null,
    ): DynamicKeymapProfile {
        val detectedModel = ModelAttrRegex.find(xmlText)?.groupValues?.getOrNull(1)?.trim().orEmpty()
        val name = profileName?.takeIf { it.isNotBlank() } ?: detectedModel.ifEmpty { "Perfil .irplus Importado" }
        val specs = ArrayList<CatalogCommandSpec>()
        for (match in ButtonTagRegex.findAll(xmlText)) {
            val attrs = match.groupValues[1]
            val body = match.groupValues[2]
            val label = decodeXmlEntities(LabelAttrRegex.find(attrs)?.groupValues?.getOrNull(1).orEmpty()).trim()
            val codeText =
                decodeXmlEntities(CodeTagRegex.find(body)?.groupValues?.getOrNull(1) ?: body)
                    .trim()
            if (label.isEmpty() || codeText.isEmpty() || ';' in codeText) continue
            val kas = decodeKaseikyoFromPronto(codeText)
            if (kas != null) {
                specs.add(CatalogCommandSpec(label, KeyProtocol.KASEIKYO, kas.first, kas.second, kas.third))
            } else {
                specs.add(CatalogCommandSpec(label, KeyProtocol.PRONTO_RAW, prontoHex = codeText))
            }
        }
        val uid = "imp_irplus_${Integer.toHexString(name.hashCode() xor specs.size)}"
        return assembleFromCodeset(
            PanasonicCatalogCodeset(
                id = uid,
                category = PanasonicCategory.OTHER,
                models = listOf(name),
                commands = specs,
            ),
            preferredModelName = name,
        ).copy(isUserModified = true)
    }

    /**
     * Imports a Flipper Zero `.ir` file (`protocol: Kaseikyo`) into an editable [DynamicKeymapProfile].
     */
    fun importFromFlipperIr(
        irText: String,
        profileName: String = "Imported Flipper (.ir)",
    ): DynamicKeymapProfile {
        val specs = ArrayList<CatalogCommandSpec>()
        var cur = HashMap<String, String>()

        fun commitCurrent() {
            val name = cur["name"]?.trim().orEmpty()
            val prot = cur["protocol"]?.trim().orEmpty()
            if (name.isNotEmpty() && prot.equals("Kaseikyo", ignoreCase = true)) {
                val decoded = decodeFlipperKaseikyo(cur["address"].orEmpty(), cur["command"].orEmpty())
                if (decoded != null) {
                    specs.add(CatalogCommandSpec(name, KeyProtocol.KASEIKYO, decoded.first, decoded.second, decoded.third))
                }
            }
            cur = HashMap()
        }
        for (rawLine in irText.lineSequence()) {
            val line = rawLine.trim()
            if (line.isEmpty() || line.startsWith("#")) {
                commitCurrent()
            } else {
                val colonIdx = line.indexOf(':')
                if (colonIdx > 0) {
                    cur[line.substring(0, colonIdx).trim().lowercase()] = line.substring(colonIdx + 1).trim()
                }
            }
        }
        commitCurrent()
        val uid = "imp_flipper_${Integer.toHexString(profileName.hashCode() xor specs.size)}"
        return assembleFromCodeset(
            PanasonicCatalogCodeset(
                id = uid,
                category = PanasonicCategory.OTHER,
                models = listOf(profileName),
                commands = specs,
            ),
            preferredModelName = profileName,
        ).copy(isUserModified = true)
    }

    /**
     * Exports any [DynamicKeymapProfile] back into `.irplus` XML for backup or external apps.
     */
    fun exportToIrplusXml(
        profile: DynamicKeymapProfile,
        activeMode: RemoteMode = RemoteMode.MODE_1,
    ): String {
        val sb = StringBuilder()
        sb.appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
        sb.appendLine("<irplus>")
        sb.appendLine("""    <device manufacturer="Panasonic" model="${escapeXml(profile.name)}">""")
        for (key in profile.keys) {
            val pronto =
                if (key.protocol == KeyProtocol.KASEIKYO) {
                    KaseikyoProtocol.synthesizeProntoHexRaw(key.device, key.effectiveSubdevice(activeMode), key.obc)
                } else {
                    key.prontoHex.orEmpty()
                }
            if (pronto.isBlank()) continue
            sb.appendLine("""        <button label="${escapeXml(key.label)}" color="${toneToIrplusHexColor(key.tone)}">""")
            sb.appendLine("""            <code>$pronto</code>""")
            sb.appendLine("        </button>")
        }
        sb.appendLine("    </device>")
        sb.appendLine("</irplus>")
        return sb.toString()
    }

    /**
     * Decodes a 48-bit Matsushita Kaseikyo Pronto Hex frame back to `(device, subdevice, obc)`.
     */
    fun decodeKaseikyoFromPronto(pronto: String): Triple<Int, Int, Int>? {
        val tokens = pronto.trim().split(WhitespaceRegex).filter { it.isNotEmpty() }
        if (tokens.size < 104 || tokens.size % 2 != 0) return null
        val words =
            IntArray(tokens.size) { idx ->
                tokens[idx].toIntOrNull(16) ?: return null
            }
        if (words[0] != 0 || words[1] == 0) return null
        val pairsOffset = 4
        if (words.size < pairsOffset + 100) return null
        val hdrMark = words[pairsOffset]
        val hdrSpace = words[pairsOffset + 1]
        if (hdrMark < 50 || hdrMark <= hdrSpace) return null

        val bytes = IntArray(6)
        for (byteIdx in 0 until 6) {
            var b = 0
            for (bitIdx in 0 until 8) {
                val pIdx = pairsOffset + 2 + (byteIdx * 8 + bitIdx) * 2
                val mark = words[pIdx]
                val space = words[pIdx + 1]
                if (mark == 0) return null
                if (space > mark * 2) {
                    b = b or (1 shl bitIdx)
                }
            }
            bytes[byteIdx] = b
        }
        if (bytes[0] != 0x02 || bytes[1] != 0x20) return null
        val checksum = bytes[2] xor bytes[3] xor bytes[4]
        if (checksum != bytes[5]) return null
        return Triple(bytes[2], bytes[3], bytes[4])
    }

    /**
     * Decodes Flipper Zero's `address` / `command` hex fields into Kaseikyo `(device, subdevice, obc)`.
     */
    fun decodeFlipperKaseikyo(
        addressHex: String,
        commandHex: String,
    ): Triple<Int, Int, Int>? {
        val addr = addressHex.trim().split(WhitespaceRegex).mapNotNull { it.toIntOrNull(16) }
        val cmd = commandHex.trim().split(WhitespaceRegex).mapNotNull { it.toIntOrNull(16) }
        if (addr.size < 4 || cmd.size < 2) return null
        if (addr[1] != 0x02 || addr[2] != 0x20) return null
        val dev = addr[0] and 0xFE
        val sub = (addr[0] and 0x01) or ((addr[3] ushr 2) shl 1)
        val obc = ((addr[3] and 0x03) shl 6) or ((cmd[1] and 0x0F) shl 4) or ((cmd[0] ushr 4) and 0x0F)
        return Triple(dev, sub, obc)
    }

    private data class CommandClassification(
        val section: KeySection,
        val sortKey: Int,
        val tone: KeyTone,
        val confirmRequired: Boolean = false,
    )

    private fun classifyCommand(cmd: CatalogCommandSpec): CommandClassification {
        val raw = cmd.name.trim()
        val low = raw.lowercase()
        val compact = low.replace(Regex("[^a-z0-9]"), "")
        val isDestructive =
            "format" in low ||
                "reset" in low ||
                "erase" in low ||
                compact == "delete" ||
                compact == "dvderase"

        if (isDestructive) {
            return CommandClassification(KeySection.RECORDING_EDIT, 90, KeyTone.DANGER_DARK, confirmRequired = true)
        }
        if (compact in setOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9") ||
            compact.startsWith("digit") && compact.removePrefix("digit").all { it.isDigit() }
        ) {
            val digit = compact.last()
            val order = if (digit == '0') 10 else (digit - '0')
            return CommandClassification(KeySection.NUMERIC, order, KeyTone.NEUTRAL)
        }

        return when {
            "power" in compact || compact in setOf("standby", "sleep", "off", "on") -> {
                val powerOrd =
                    when {
                        "off" in compact -> 2
                        "on" in compact -> 1
                        else -> 0
                    }
                CommandClassification(KeySection.POWER_INPUT, powerOrd, KeyTone.DANGER)
            }
            "eject" in compact || "openclose" in compact ->
                CommandClassification(KeySection.POWER_INPUT, 10, KeyTone.NEUTRAL)
            "drive" in compact || "input" in compact || "vcr/tv" in low || "vhs" in compact || "hdmi" in compact ->
                CommandClassification(KeySection.POWER_INPUT, 20, KeyTone.PRIMARY)
            compact in setOf("ok", "enter", "select") || "select" == compact ->
                CommandClassification(KeySection.NAVIGATION, 0, KeyTone.PRIMARY)
            "up" in compact || "down" in compact || "left" in compact || "right" in compact ->
                if ("vol" in compact || "ch" in compact || "track" in compact || "page" in compact) {
                    classifyNonDpad(compact, low)
                } else {
                    val ord =
                        when {
                            "up" in compact -> 1
                            "down" in compact -> 2
                            "left" in compact -> 3
                            else -> 4
                        }
                    CommandClassification(KeySection.NAVIGATION, ord, KeyTone.NEUTRAL)
                }
            compact in setOf("return", "back", "exit", "cancel", "clear") ->
                CommandClassification(KeySection.NAVIGATION, 10, KeyTone.NEUTRAL)
            "directnav" in compact || "topmenu" in compact || "functions" in compact || "menu" in compact ||
                "setup" in compact || "option" in compact || "guide" in compact ->
                CommandClassification(KeySection.NAVIGATION, 20, KeyTone.INFO)
            "play" == compact || compact.startsWith("dvdplay") || compact.startsWith("vcrplay") ->
                CommandClassification(KeySection.TRANSPORT, 0, KeyTone.SUCCESS)
            "pause" in compact ->
                CommandClassification(KeySection.TRANSPORT, 1, KeyTone.NEUTRAL)
            "stop" in compact ->
                CommandClassification(KeySection.TRANSPORT, 2, KeyTone.DANGER_DARK)
            "rew" in compact || "fastback" in compact ->
                CommandClassification(KeySection.TRANSPORT, 3, KeyTone.NEUTRAL)
            "fastf" in compact || compact == "ff" || compact == "fwd" ->
                CommandClassification(KeySection.TRANSPORT, 4, KeyTone.NEUTRAL)
            "prev" in compact || "skipb" in compact || "chapterprev" in compact ->
                CommandClassification(KeySection.TRANSPORT, 5, KeyTone.NEUTRAL)
            "next" in compact || "skipf" in compact || "chapternext" in compact ->
                CommandClassification(KeySection.TRANSPORT, 6, KeyTone.NEUTRAL)
            "rec" in compact && "mode" !in compact && "check" !in compact && "list" !in compact ->
                CommandClassification(KeySection.TRANSPORT, 7, KeyTone.DANGER)
            else -> classifyNonDpad(compact, low)
        }
    }

    private fun classifyNonDpad(
        compact: String,
        low: String,
    ): CommandClassification =
        when {
            "vol" in compact || "mute" in compact -> {
                val volOrd =
                    when {
                        "mute" in compact -> 2
                        "up" in compact -> 0
                        else -> 1
                    }
                CommandClassification(KeySection.TUNING_AUDIO, volOrd, KeyTone.INFO)
            }
            "channel" in compact || compact.startsWith("ch") ->
                CommandClassification(KeySection.TUNING_AUDIO, 10, KeyTone.NEUTRAL)
            "audio" in compact || "subtitle" in compact || "sap" in compact || "angle" in compact ->
                CommandClassification(KeySection.TUNING_AUDIO, 20, KeyTone.NEUTRAL)
            "track" in compact ->
                CommandClassification(KeySection.RECORDING_EDIT, 5, KeyTone.PRIMARY)
            "dub" in compact || "timeslip" in compact || "skip" in compact || "replay" in compact ||
                "chapter" in compact || "schedule" in compact || "timer" in compact || "recmode" in compact ||
                "frec" in compact || "flex" in compact || "extlink" in compact || "status" in compact ||
                "display" in compact || "info" in compact ->
                CommandClassification(KeySection.RECORDING_EDIT, 10, KeyTone.WARNING)
            compact in setOf("red", "green", "yellow", "blue") ->
                CommandClassification(KeySection.CUSTOM, 0, KeyTone.ACCENT)
            "text" in low || "jdm" in low || "internet" in low || "viera" in low || "netflix" in low ->
                CommandClassification(KeySection.CUSTOM, 10, KeyTone.ACCENT)
            else -> CommandClassification(KeySection.CUSTOM, 50, KeyTone.NEUTRAL)
        }

    private fun prettifyCommandLabel(raw: String): String =
        raw
            .replace("_", " ")
            .replace(Regex("([a-z])([A-Z])"), "$1 $2")
            .replace(Regex("\\s+"), " ")
            .trim()
            .take(28)

    private fun escapeXml(input: String): String =
        input
            .replace("&", "&amp;")
            .replace("\"", "&quot;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")

    private fun decodeXmlEntities(input: String): String =
        input
            .replace("&quot;", "\"")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&amp;", "&")

    private fun toneToIrplusHexColor(tone: KeyTone): String =
        when (tone) {
            KeyTone.NEUTRAL -> "#FF37474F"
            KeyTone.PRIMARY -> "#FF0277BD"
            KeyTone.INFO -> "#FF0288D1"
            KeyTone.SUCCESS -> "#FF2E7D32"
            KeyTone.WARNING -> "#FFF57C00"
            KeyTone.DANGER -> "#FFD32F2F"
            KeyTone.DANGER_DARK -> "#FFC62828"
            KeyTone.ACCENT -> "#FF7B1FA2"
        }
}
