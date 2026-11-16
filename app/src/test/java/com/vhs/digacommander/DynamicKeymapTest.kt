package com.vhs.digacommander

import com.vhs.digacommander.data.catalog.PanasonicCatalogRepository
import com.vhs.digacommander.data.keymap.KeymapWorkspaceCodec
import com.vhs.digacommander.data.keymap.KeymapWorkspaceState
import com.vhs.digacommander.data.model.DynamicRemoteKey
import com.vhs.digacommander.data.model.KeyProtocol
import com.vhs.digacommander.data.model.KeySection
import com.vhs.digacommander.data.model.KeyTone
import com.vhs.digacommander.data.model.PanasonicCategory
import com.vhs.digacommander.data.model.RemoteMode
import com.vhs.digacommander.domain.keymap.DynamicKeymapAssembler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * End-to-end verification of the bundled all-Panasonic offline catalog (`panasonic_ir_catalog.dat.gz`),
 * self-assembling dynamic keymaps, runtime workspace serialization, and `.irplus` / Flipper `.ir` import/export.
 */
class DynamicKeymapTest {
    private fun locateRepoAsset(relFromApp: String): File {
        val candidates =
            listOf(
                File(relFromApp),
                File("app/$relFromApp"),
                File("../$relFromApp"),
            )
        return candidates.firstOrNull { it.exists() }
            ?: throw AssertionError("Asset not found in any candidate path for $relFromApp (cwd=${System.getProperty("user.dir")})")
    }

    private fun locateProfile(filename: String): File {
        val candidates =
            listOf(
                File("profiles/$filename"),
                File("../profiles/$filename"),
            )
        return candidates.firstOrNull { it.exists() }
            ?: throw AssertionError("Profile not found: $filename (cwd=${System.getProperty("user.dir")})")
    }

    @Test
    fun testBundledOfflineCatalogContainsAllPanasonicModelsAndCategories() {
        val assetFile = locateRepoAsset("src/main/assets/${PanasonicCatalogRepository.ASSET_FILENAME}")
        val repo = PanasonicCatalogRepository { assetFile.inputStream() }
        val (codesetCount, modelCount) = repo.stats()

        assertTrue("Expected >= 1700 codesets, got $codesetCount", codesetCount >= 1700)
        assertTrue("Expected >= 14000 Panasonic models, got $modelCount", modelCount >= 14000)

        assertTrue(repo.search("DMR-EH55", PanasonicCategory.DIGA).isNotEmpty())
        assertTrue(repo.search("EUR7659Y70", PanasonicCategory.ALL).isNotEmpty())
        assertTrue(repo.search("DMR-ES15", PanasonicCategory.DIGA).isNotEmpty())
        assertTrue(repo.search("DMR-EH75V", PanasonicCategory.COMBO).isNotEmpty())
        assertTrue(repo.search("DMR-ES46V", PanasonicCategory.COMBO).isNotEmpty())
        assertTrue(repo.search("TX-P50", PanasonicCategory.TV).isNotEmpty())
        assertTrue(repo.search("NV-HV60", PanasonicCategory.VCR).isNotEmpty())
        assertTrue(repo.search("SA-", PanasonicCategory.AUDIO).isNotEmpty())
    }

    @Test
    fun testAutoAssemblyAndLiveKeyCustomizationRoundTrip() {
        val assetFile = locateRepoAsset("src/main/assets/${PanasonicCatalogRepository.ASSET_FILENAME}")
        val repo = PanasonicCatalogRepository { assetFile.inputStream() }

        val combo = repo.findById("curated_diga_combo_vhs_dvd")
        assertNotNull(combo)
        val assembled = DynamicKeymapAssembler.assembleFromCodeset(combo!!)

        assertTrue(assembled.keys.any { it.device == 0x90 && it.subdevice == 0x05 && it.obc == 0xE2 })
        assertTrue(assembled.keys.any { it.device == 0x90 && it.obc == 0xB1 })
        assertTrue(assembled.keys.any { it.device == 0x80 && it.obc == 0x20 })

        // Verify SET 2 mode composition preserves Extended Subdevice 0x01 on Exit -> 0x11
        val exitKey = assembled.keys.first { it.label.equals("Exit", ignoreCase = true) }
        assertEquals("02 20 B0 11 51 F0", exitKey.formatWirePreview(RemoteMode.MODE_2))

        // Verify pure-Kotlin persistence codec round-trips modified keys cleanly
        val editedKey =
            DynamicRemoteKey(
                id = "live_custom_1",
                label = "MY\tCUSTOM\nKEY",
                section = KeySection.CUSTOM,
                protocol = KeyProtocol.PRONTO_RAW,
                prontoHex = "0000 0070 0000 0002 0080 0040 0010 0ACD",
                tone = KeyTone.WARNING,
                confirmRequired = true,
            )
        val state =
            KeymapWorkspaceState(
                activeProfileId = assembled.id,
                profiles = listOf(assembled.copy(keys = assembled.keys + editedKey)),
            )
        val decoded = KeymapWorkspaceCodec.decode(KeymapWorkspaceCodec.encode(state))
        assertEquals(assembled.id, decoded.activeProfileId)
        val restoredEdited = decoded.profiles.first().keys.last()
        assertEquals(editedKey.label, restoredEdited.label)
        assertEquals(editedKey.prontoHex, restoredEdited.prontoHex)
        assertEquals(true, restoredEdited.confirmRequired)
    }

    @Test
    fun testImportAllIrplusProfilesAndFlipperIrFormats() {
        val irplusNames =
            listOf(
                "Panasonic_DMR_EH55_Commander.irplus",
                "Panasonic_DIGA_Combo_VHS_DVD_EH75V_ES30V.irplus",
                "Panasonic_DIGA_ES10_ES15_TBC.irplus",
                "Panasonic_DIGA_Universal_Modes_1_2_3.irplus",
            )
        for (fn in irplusNames) {
            val xml = locateProfile(fn).readText(Charsets.UTF_8)
            val profile = DynamicKeymapAssembler.importFromIrplusXml(xml)
            assertTrue("$fn must import non-empty buttons", profile.keys.isNotEmpty())
            assertTrue("$fn buttons should decode cleanly to Kaseikyo", profile.keys.all { it.protocol == KeyProtocol.KASEIKYO })
            val exported = DynamicKeymapAssembler.exportToIrplusXml(profile)
            val reimported = DynamicKeymapAssembler.importFromIrplusXml(exported)
            assertEquals(profile.keys.size, reimported.keys.size)
        }

        val flipperSample =
            """
            Filetype: IR signals file
            Version: 1
            name: Exit
            type: parsed
            protocol: Kaseikyo
            address: B1 02 20 01
            command: 10 01 00 00
            #
            name: TV_Vol_Up
            type: parsed
            protocol: Kaseikyo
            address: 80 02 20 00
            command: 00 02 00 00
            """.trimIndent()
        val fromFlipper = DynamicKeymapAssembler.importFromFlipperIr(flipperSample, "Flipper Sample")
        assertEquals(2, fromFlipper.keys.size)
        val exit = fromFlipper.keys.first { it.label == "Exit" }
        assertEquals(0xB0, exit.device)
        assertEquals(0x01, exit.subdevice)
        assertEquals(0x51, exit.obc)
        val vol = fromFlipper.keys.first { it.label.contains("Vol", ignoreCase = true) }
        assertEquals(0x80, vol.device)
        assertEquals(0x20, vol.obc)
    }
}
