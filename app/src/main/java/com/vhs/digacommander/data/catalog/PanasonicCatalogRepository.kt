package com.vhs.digacommander.data.catalog

import android.content.Context
import com.vhs.digacommander.data.model.CatalogCommandSpec
import com.vhs.digacommander.data.model.KeyProtocol
import com.vhs.digacommander.data.model.PanasonicCatalogCodeset
import com.vhs.digacommander.data.model.PanasonicCategory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream

/**
 * Offline repository indexing all Panasonic IR codesets & models bundled in
 * `assets/panasonic_ir_catalog.dat.gz`.
 *
 * Designed without Android-only JSON dependencies so it runs and is verified identically
 * on both Android hardware and JVM JUnit tests.
 */
class PanasonicCatalogRepository(
    private val openCatalogStream: () -> InputStream,
) {
    @Volatile
    private var cachedCodesets: List<PanasonicCatalogCodeset>? = null

    /**
     * Loads and caches all codesets from the compressed catalog asset.
     */
    fun getAllCodesets(): List<PanasonicCatalogCodeset> {
        cachedCodesets?.let { return it }
        synchronized(this) {
            cachedCodesets?.let { return it }
            val loaded = parseCatalogStream(openCatalogStream())
            cachedCodesets = loaded
            return loaded
        }
    }

    /**
     * Searches codesets matching all whitespace-separated query tokens within [category].
     */
    fun search(
        query: String,
        category: PanasonicCategory = PanasonicCategory.ALL,
        limit: Int = DEFAULT_SEARCH_LIMIT,
    ): List<PanasonicCatalogCodeset> {
        val all = getAllCodesets()
        val tokens =
            query
                .trim()
                .lowercase()
                .split(WhitespaceRegex)
                .filter { it.isNotEmpty() }
        val filtered =
            all.asSequence().filter { cs ->
                val catOk = category == PanasonicCategory.ALL || cs.category == category
                catOk && (tokens.isEmpty() || tokens.all { token -> token in cs.searchBlobLower })
            }
        val queryLower = query.trim().lowercase()
        val sorted =
            if (queryLower.isEmpty()) {
                filtered.toList()
            } else {
                filtered.sortedBy { cs -> rankMatch(cs, queryLower) }.toList()
            }
        return if (limit > 0) sorted.take(limit) else sorted
    }

    /**
     * Finds a codeset by its exact ID (e.g. `"curated_diga_eh55_eur7659"` or `"h_fb6987aefd97"`).
     */
    fun findById(id: String): PanasonicCatalogCodeset? = getAllCodesets().firstOrNull { it.id == id }

    /**
     * Returns `(codesetCount, totalModelAliases)` across the entire bundled database.
     */
    fun stats(): Pair<Int, Int> {
        val all = getAllCodesets()
        return all.size to all.sumOf { it.models.size }
    }

    private fun rankMatch(
        cs: PanasonicCatalogCodeset,
        queryLower: String,
    ): Int {
        val exact = cs.models.any { it.lowercase() == queryLower }
        if (exact) return 0
        val curated = cs.id.startsWith("curated_")
        val prefix = cs.models.any { it.lowercase().startsWith(queryLower) }
        return when {
            curated && prefix -> 1
            prefix -> 2
            curated -> 3
            else -> 4
        }
    }

    companion object {
        const val ASSET_FILENAME: String = "panasonic_ir_catalog.dat.gz"
        const val HEADER_V1: String = "PANASONIC_IR_CATALOG_V1"
        private const val DEFAULT_SEARCH_LIMIT: Int = 80
        private const val HEX_RADIX: Int = 16
        private val WhitespaceRegex = Regex("\\s+")

        fun fromAndroidAssets(context: Context): PanasonicCatalogRepository {
            val appCtx = context.applicationContext
            return PanasonicCatalogRepository { appCtx.assets.open(ASSET_FILENAME) }
        }

        internal fun parseCatalogStream(rawStream: InputStream): List<PanasonicCatalogCodeset> {
            val results = ArrayList<PanasonicCatalogCodeset>(1800)
            GZIPInputStream(rawStream).use { gzip ->
                BufferedReader(InputStreamReader(gzip, Charsets.UTF_8)).use { reader ->
                    val header = reader.readLine()?.trim()
                    if (header != HEADER_V1) {
                        return emptyList()
                    }
                    var currentId: String? = null
                    var currentCategory = PanasonicCategory.OTHER
                    var currentModels: List<String> = emptyList()
                    var currentCommands = ArrayList<CatalogCommandSpec>()

                    fun flushCurrent() {
                        val id = currentId ?: return
                        if (currentCommands.isNotEmpty()) {
                            results.add(
                                PanasonicCatalogCodeset(
                                    id = id,
                                    category = currentCategory,
                                    models = currentModels,
                                    commands = currentCommands,
                                ),
                            )
                        }
                    }

                    var line: String? = reader.readLine()
                    while (line != null) {
                        val parts = line.split('\t')
                        when (parts.firstOrNull()) {
                            "C" ->
                                if (parts.size >= 4) {
                                    flushCurrent()
                                    currentId = parts[1]
                                    currentCategory = PanasonicCategory.fromCode(parts[2])
                                    currentModels = parts[3].split('|').filter { it.isNotEmpty() }
                                    currentCommands = ArrayList()
                                }
                            "K" ->
                                if (parts.size >= 5) {
                                    val dev = parts[2].toIntOrNull(HEX_RADIX)
                                    val sub = parts[3].toIntOrNull(HEX_RADIX)
                                    val obc = parts[4].toIntOrNull(HEX_RADIX)
                                    if (dev != null && sub != null && obc != null) {
                                        currentCommands.add(
                                            CatalogCommandSpec(
                                                name = parts[1],
                                                protocol = KeyProtocol.KASEIKYO,
                                                device = dev and 0xFF,
                                                subdevice = sub and 0xFF,
                                                obc = obc and 0xFF,
                                            ),
                                        )
                                    }
                                }
                            "P" ->
                                if (parts.size >= 3) {
                                    currentCommands.add(
                                        CatalogCommandSpec(
                                            name = parts[1],
                                            protocol = KeyProtocol.PRONTO_RAW,
                                            prontoHex = parts[2],
                                        ),
                                    )
                                }
                        }
                        line = reader.readLine()
                    }
                    flushCurrent()
                }
            }
            return results
        }
    }
}
