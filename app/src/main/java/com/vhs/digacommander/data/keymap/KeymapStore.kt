package com.vhs.digacommander.data.keymap

import android.content.Context
import com.vhs.digacommander.data.model.DynamicKeymapProfile
import com.vhs.digacommander.data.model.DynamicRemoteKey
import com.vhs.digacommander.data.model.KeyProtocol
import com.vhs.digacommander.data.model.KeySection
import com.vhs.digacommander.data.model.KeyTone
import com.vhs.digacommander.data.model.PanasonicCategory

/**
 * Snapshot of user-assembled and live-edited dynamic remote keymaps persisted across restarts.
 */
data class KeymapWorkspaceState(
    val activeProfileId: String = "",
    val profiles: List<DynamicKeymapProfile> = emptyList(),
)

/**
 * Persistence contract for [KeymapWorkspaceState].
 */
interface KeymapStore {
    fun load(): KeymapWorkspaceState

    fun save(state: KeymapWorkspaceState)
}

/**
 * In-memory implementation for unit tests and previews.
 */
class InMemoryKeymapStore(
    private var current: KeymapWorkspaceState = KeymapWorkspaceState(),
) : KeymapStore {
    override fun load(): KeymapWorkspaceState = current

    override fun save(state: KeymapWorkspaceState) {
        current = state
    }
}

/**
 * Pure-Kotlin line codec + [android.content.SharedPreferences] store for dynamic keymaps.
 *
 * Uses explicit control-character escaping rather than `org.json` so round-trip serialization can
 * be verified directly inside JVM unit tests with zero Android mocking overhead.
 */
class SharedPrefsKeymapStore(
    context: Context,
) : KeymapStore {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun load(): KeymapWorkspaceState {
        val raw = prefs.getString(KEY_WORKSPACE_V1, null) ?: return KeymapWorkspaceState()
        return KeymapWorkspaceCodec.decode(raw)
    }

    override fun save(state: KeymapWorkspaceState) {
        prefs.edit().putString(KEY_WORKSPACE_V1, KeymapWorkspaceCodec.encode(state)).apply()
    }

    private companion object {
        const val PREFS_NAME = "diga_dynamic_keymaps"
        const val KEY_WORKSPACE_V1 = "workspace_v1"
    }
}

internal object KeymapWorkspaceCodec {
    private const val MAGIC = "KEYMAP_WS_V1"
    private const val HEX_RADIX = 16

    fun encode(state: KeymapWorkspaceState): String {
        val sb = StringBuilder()
        sb.append(MAGIC).append('\n')
        sb.append("A\t").append(escapeField(state.activeProfileId)).append('\n')
        for (p in state.profiles) {
            sb.append("P\t")
                .append(escapeField(p.id)).append('\t')
                .append(escapeField(p.name)).append('\t')
                .append(escapeField(p.sourceCodesetId)).append('\t')
                .append(p.category.code).append('\t')
                .append(if (p.isUserModified) "1" else "0")
                .append('\n')
            for (k in p.keys) {
                sb.append("K\t")
                    .append(escapeField(k.id)).append('\t')
                    .append(escapeField(k.label)).append('\t')
                    .append(k.section.name).append('\t')
                    .append(k.protocol.name).append('\t')
                    .append(k.device.toString(HEX_RADIX)).append('\t')
                    .append(k.subdevice.toString(HEX_RADIX)).append('\t')
                    .append(k.obc.toString(HEX_RADIX)).append('\t')
                    .append(if (k.respectRemoteMode) "1" else "0").append('\t')
                    .append(escapeField(k.prontoHex.orEmpty())).append('\t')
                    .append(k.tone.name).append('\t')
                    .append(if (k.confirmRequired) "1" else "0")
                    .append('\n')
            }
        }
        return sb.toString()
    }

    fun decode(raw: String): KeymapWorkspaceState {
        val lines = raw.lineSequence().iterator()
        if (!lines.hasNext() || lines.next().trim() != MAGIC) {
            return KeymapWorkspaceState()
        }
        var activeId = ""
        val profiles = ArrayList<DynamicKeymapProfile>()
        var currentProfileMeta: ProfileMeta? = null
        var currentKeys = ArrayList<DynamicRemoteKey>()

        fun flushProfile() {
            val meta = currentProfileMeta ?: return
            profiles.add(
                DynamicKeymapProfile(
                    id = meta.id,
                    name = meta.name,
                    sourceCodesetId = meta.sourceCodesetId,
                    category = meta.category,
                    keys = currentKeys,
                    isUserModified = meta.isUserModified,
                ),
            )
        }

        while (lines.hasNext()) {
            val parts = lines.next().split('\t')
            when (parts.firstOrNull()) {
                "A" -> if (parts.size >= 2) activeId = unescapeField(parts[1])
                "P" ->
                    if (parts.size >= 6) {
                        flushProfile()
                        currentProfileMeta =
                            ProfileMeta(
                                id = unescapeField(parts[1]),
                                name = unescapeField(parts[2]),
                                sourceCodesetId = unescapeField(parts[3]),
                                category = PanasonicCategory.fromCode(parts[4]),
                                isUserModified = parts[5] == "1",
                            )
                        currentKeys = ArrayList()
                    }
                "K" ->
                    if (parts.size >= 12) {
                        val prontoVal = unescapeField(parts[9]).ifEmpty { null }
                        currentKeys.add(
                            DynamicRemoteKey(
                                id = unescapeField(parts[1]),
                                label = unescapeField(parts[2]),
                                section = KeySection.fromName(parts[3]),
                                protocol = KeyProtocol.fromName(parts[4]),
                                device = (parts[5].toIntOrNull(HEX_RADIX) ?: 0xB0) and 0xFF,
                                subdevice = (parts[6].toIntOrNull(HEX_RADIX) ?: 0x00) and 0xFF,
                                obc = (parts[7].toIntOrNull(HEX_RADIX) ?: 0x00) and 0xFF,
                                respectRemoteMode = parts[8] == "1",
                                prontoHex = prontoVal,
                                tone = KeyTone.fromName(parts[10]),
                                confirmRequired = parts[11] == "1",
                            ),
                        )
                    }
            }
        }
        flushProfile()
        return KeymapWorkspaceState(activeProfileId = activeId, profiles = profiles)
    }

    private data class ProfileMeta(
        val id: String,
        val name: String,
        val sourceCodesetId: String,
        val category: PanasonicCategory,
        val isUserModified: Boolean,
    )

    private fun escapeField(value: String): String =
        value
            .replace("\\", "\\\\")
            .replace("\t", "\\t")
            .replace("\n", "\\n")
            .replace("\r", "\\r")

    private fun unescapeField(value: String): String {
        val sb = StringBuilder(value.length)
        var i = 0
        while (i < value.length) {
            val ch = value[i]
            if (ch == '\\' && i + 1 < value.length) {
                when (value[i + 1]) {
                    '\\' -> sb.append('\\')
                    't' -> sb.append('\t')
                    'n' -> sb.append('\n')
                    'r' -> sb.append('\r')
                    else -> sb.append(value[i + 1])
                }
                i += 2
            } else {
                sb.append(ch)
                i++
            }
        }
        return sb.toString()
    }
}
