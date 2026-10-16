package com.vhs.digacommander.data.catalog

import com.vhs.digacommander.data.constant.DigaTimings
import com.vhs.digacommander.data.ir.KaseikyoProtocol
import com.vhs.digacommander.data.model.DigaDevice
import com.vhs.digacommander.data.model.MacroCommand
import com.vhs.digacommander.data.model.RemoteMode

/**
 * Master catalog of Panasonic DIGA IR commands, OBC (Original Button Codes),
 * benchmark Pronto Hex strings, and automated bench service macros.
 */
object PanasonicDigaCatalog {
    /**
     * 8-bit Original Button Codes (OBC / Command bytes) in Matsushita Kaseikyo protocol.
     */
    object OBC {
        const val POWER = 0x3D
        const val POWER_ON = 0x3E
        const val POWER_OFF = 0x3F
        const val EJECT = 0x2B
        const val STATUS = 0x21
        const val DISPLAY = 0x22
        const val DRIVE_SELECT = 0x8C
        const val DIRECT_NAVIGATOR = 0x62
        const val FUNCTIONS = 0xCB
        const val TOP_MENU = 0x20
        const val OPTION_SUB_MENU = 0x2C
        const val UP = 0x85
        const val DOWN = 0x86
        const val LEFT = 0x87
        const val RIGHT = 0x88
        const val ENTER_OK = 0x89
        const val RETURN = 0x8B
        const val EXIT = 0xA5
        const val GUIDE = 0x68
        const val PROG_CHECK = 0x6E
        const val PLAY = 0x38
        const val STOP = 0x39
        const val PAUSE = 0x3A
        const val FAST_FORWARD = 0x34
        const val REWIND = 0x35
        const val SKIP_NEXT = 0x36
        const val SKIP_PREV = 0x37
        const val TIME_SLIP = 0x29
        const val MANUAL_SKIP_30S = 0x51
        const val MANUAL_REPLAY_10S = 0x52
        const val CREATE_CHAPTER = 0x56
        const val DELETE_TITLE = 0x55
        const val REC = 0x3C
        const val REC_MODE = 0xA7
        const val EXT_LINK = 0xA6
        const val INPUT_SELECT = 0xA2
        const val CH_UP = 0x2E
        const val CH_DOWN = 0x2F
        const val AUDIO = 0x24
        const val SUBTITLE = 0x28
        const val DIGIT_1 = 0x10
        const val DIGIT_2 = 0x11
        const val DIGIT_3 = 0x12
        const val DIGIT_4 = 0x13
        const val DIGIT_5 = 0x14
        const val DIGIT_6 = 0x15
        const val DIGIT_7 = 0x16
        const val DIGIT_8 = 0x17
        const val DIGIT_9 = 0x18
        const val DIGIT_0 = 0x19
        const val MEDIA_SERVER = 0x77
        const val CANCEL = 0x8A

        // TV control commands (Device 0x01)
        const val VOL_UP = 0x20
        const val VOL_DOWN = 0x21

        // VCR deck commands (Device 0x90)
        const val VCR_TRACKING_PLUS = 0x1A
        const val VCR_TRACKING_MINUS = 0x1B
        const val VCR_DUB_VHS_TO_DVD = 0xEC
        const val VCR_DUB_DVD_TO_VHS = 0xED
    }

    /**
     * Synthesizes a bit-perfect Pronto Hex code for the specified parameters.
     */
    fun getProntoHex(
        command: Int,
        mode: RemoteMode = RemoteMode.MODE_1,
        device: DigaDevice = DigaDevice.DVD_HDD,
    ): String = KaseikyoProtocol.synthesizeProntoHex(device.id, mode, command)

    // Pre-synthesized benchmark codes in Mode 1
    val POWER = getProntoHex(OBC.POWER)
    val EJECT = getProntoHex(OBC.EJECT)
    val STATUS = getProntoHex(OBC.STATUS)
    val DISPLAY = getProntoHex(OBC.DISPLAY)
    val DRIVE_SELECT = getProntoHex(OBC.DRIVE_SELECT)
    val DIRECT_NAVIGATOR = getProntoHex(OBC.DIRECT_NAVIGATOR)
    val FUNCTIONS = getProntoHex(OBC.FUNCTIONS)
    val TOP_MENU = getProntoHex(OBC.TOP_MENU)
    val OPTION_SUB_MENU = getProntoHex(OBC.OPTION_SUB_MENU)
    val UP = getProntoHex(OBC.UP)
    val DOWN = getProntoHex(OBC.DOWN)
    val LEFT = getProntoHex(OBC.LEFT)
    val RIGHT = getProntoHex(OBC.RIGHT)
    val ENTER_OK = getProntoHex(OBC.ENTER_OK)
    val RETURN = getProntoHex(OBC.RETURN)
    val EXIT = getProntoHex(OBC.EXIT)
    val GUIDE = getProntoHex(OBC.GUIDE)
    val PROG_CHECK = getProntoHex(OBC.PROG_CHECK)
    val PLAY = getProntoHex(OBC.PLAY)
    val STOP = getProntoHex(OBC.STOP)
    val PAUSE = getProntoHex(OBC.PAUSE)
    val FAST_FORWARD = getProntoHex(OBC.FAST_FORWARD)
    val REWIND = getProntoHex(OBC.REWIND)
    val SKIP_NEXT = getProntoHex(OBC.SKIP_NEXT)
    val SKIP_PREV = getProntoHex(OBC.SKIP_PREV)
    val TIME_SLIP = getProntoHex(OBC.TIME_SLIP)
    val MANUAL_SKIP_30S = getProntoHex(OBC.MANUAL_SKIP_30S)
    val MANUAL_REPLAY_10S = getProntoHex(OBC.MANUAL_REPLAY_10S)
    val CREATE_CHAPTER = getProntoHex(OBC.CREATE_CHAPTER)
    val DELETE_TITLE = getProntoHex(OBC.DELETE_TITLE)
    val REC = getProntoHex(OBC.REC)
    val REC_MODE = getProntoHex(OBC.REC_MODE)
    val EXT_LINK = getProntoHex(OBC.EXT_LINK)
    val INPUT_SELECT = getProntoHex(OBC.INPUT_SELECT)
    val CH_UP = getProntoHex(OBC.CH_UP)
    val CH_DOWN = getProntoHex(OBC.CH_DOWN)
    val AUDIO = getProntoHex(OBC.AUDIO)
    val SUBTITLE = getProntoHex(OBC.SUBTITLE)
    val DIGIT_1 = getProntoHex(OBC.DIGIT_1)
    val DIGIT_2 = getProntoHex(OBC.DIGIT_2)
    val DIGIT_3 = getProntoHex(OBC.DIGIT_3)
    val DIGIT_4 = getProntoHex(OBC.DIGIT_4)
    val DIGIT_5 = getProntoHex(OBC.DIGIT_5)
    val DIGIT_6 = getProntoHex(OBC.DIGIT_6)
    val DIGIT_7 = getProntoHex(OBC.DIGIT_7)
    val DIGIT_8 = getProntoHex(OBC.DIGIT_8)
    val DIGIT_9 = getProntoHex(OBC.DIGIT_9)
    val DIGIT_0 = getProntoHex(OBC.DIGIT_0)
    val MEDIA_SERVER = getProntoHex(OBC.MEDIA_SERVER)
    val CANCEL = getProntoHex(OBC.CANCEL)
    val VOL_UP = getProntoHex(OBC.VOL_UP, device = DigaDevice.TV)
    val VOL_DOWN = getProntoHex(OBC.VOL_DOWN, device = DigaDevice.TV)

    /**
     * Automated Bench Macros and Diagnostic Routines.
     */
    object Macros {
        // ==========================================
        // 🩺 OPTICAL HEALTH & LASER HOUR COUNTER
        // ==========================================

        /** Laser Operating Hours Recall (Service Command 41 - Displays L****) */
        val LASER_HOURS =
            listOf(
                MacroCommand("Laser Hours (Digit 4)", DIGIT_4, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Laser Hours (Digit 1)", DIGIT_1, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )

        /** DVD-RAM Drive Last Error Code Recall (Service Command 42) */
        val RAM_DRIVE_LAST_ERROR =
            listOf(
                MacroCommand("RAM Error (Digit 4)", DIGIT_4, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("RAM Error (Digit 2)", DIGIT_2, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )

        // ==========================================
        // 📋 EEPROM ERROR LOG & SELF-DIAGNOSTICS
        // ==========================================

        /** Latest General Fault Code Recall (Service Command 01 - U-series, H-series, F-series codes) */
        val ERROR_HISTORY_LATEST =
            listOf(
                MacroCommand("Fault Recall (Digit 0)", DIGIT_0, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Fault Recall (Digit 1)", DIGIT_1, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )

        /** Historical Slot 1 Error (65 -> 01) */
        val ERROR_HISTORY_SLOT_1 =
            listOf(
                MacroCommand("Log Buffer (6)", DIGIT_6, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Log Buffer (5)", DIGIT_5, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Slot 01 (0)", DIGIT_0, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Slot 01 (1)", DIGIT_1, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )

        /** Historical Slot 2 Error (65 -> 02) */
        val ERROR_HISTORY_SLOT_2 =
            listOf(
                MacroCommand("Log Buffer (6)", DIGIT_6, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Log Buffer (5)", DIGIT_5, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Slot 02 (0)", DIGIT_0, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Slot 02 (2)", DIGIT_2, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )

        /** Historical Slot 3 Error (65 -> 03) */
        val ERROR_HISTORY_SLOT_3 =
            listOf(
                MacroCommand("Log Buffer (6)", DIGIT_6, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Log Buffer (5)", DIGIT_5, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Slot 03 (0)", DIGIT_0, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Slot 03 (3)", DIGIT_3, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )

        /** Historical Slot 4 Error (65 -> 04) */
        val ERROR_HISTORY_SLOT_4 =
            listOf(
                MacroCommand("Log Buffer (6)", DIGIT_6, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Log Buffer (5)", DIGIT_5, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Slot 04 (0)", DIGIT_0, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Slot 04 (4)", DIGIT_4, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )

        /** Historical Slot 5 Error (65 -> 05) */
        val ERROR_HISTORY_SLOT_5 =
            listOf(
                MacroCommand("Log Buffer (6)", DIGIT_6, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Log Buffer (5)", DIGIT_5, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Slot 05 (0)", DIGIT_0, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Slot 05 (5)", DIGIT_5, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )

        /** Clear Temporary Error Buffer to F00 (Service Command 98) */
        val CLEAR_ERROR_F00 =
            listOf(
                MacroCommand("Clear Error (9)", DIGIT_9, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Clear Error (8)", DIGIT_8, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )

        /** Destination Region Lock Readout (Service Command 02) */
        val REGION_READOUT =
            listOf(
                MacroCommand("Region Code (0)", DIGIT_0, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Region Code (2)", DIGIT_2, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )

        // ==========================================
        // 🔬 HARDWARE TESTS & MECHANICAL CYCLES
        // ==========================================

        /** HDD SMART Surface Diagnostic and Sector Reallocation (Command 31) */
        val HDD_SURFACE_TEST =
            listOf(
                MacroCommand("HDD Test (3)", DIGIT_3, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("HDD Test (1)", DIGIT_1, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )

        /** VFD Fluorescent Display 100% Segments Illumination Test (Command 00) */
        val VFD_SEGMENTS_TEST =
            listOf(
                MacroCommand("VFD Test (0)", DIGIT_0, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("VFD Test (0)", DIGIT_0, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )

        /** Video Output Bus Pattern 80 */
        val VIDEO_IO_TEST_80 =
            listOf(
                MacroCommand("Video Bus (8)", DIGIT_8, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Video Bus (0)", DIGIT_0, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )

        /** Video Output Bus Pattern 81 */
        val VIDEO_IO_TEST_81 =
            listOf(
                MacroCommand("Video Bus (8)", DIGIT_8, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Video Bus (1)", DIGIT_1, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )

        /** Loading Tray Mechanical Endurance Cycle (Command 91) */
        val TRAY_CYCLE_TEST =
            listOf(
                MacroCommand("Tray Cycle (9)", DIGIT_9, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Tray Cycle (1)", DIGIT_1, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )

        // ==========================================
        // ★ MULTI-REGION (DVD REGION FREE) HACK ★
        // ==========================================

        /** 12-Step Automated Multi-Region Unlock Sequence */
        val MULTI_REGION_HACK =
            listOf(
                MacroCommand("Digit 1", DIGIT_1, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Digit 2", DIGIT_2, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Digit 3", DIGIT_3, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Digit 4", DIGIT_4, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Digit 5", DIGIT_5, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Digit 6", DIGIT_6, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Digit 7", DIGIT_7, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Digit 8", DIGIT_8, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Digit 9", DIGIT_9, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Digit 0", DIGIT_0, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Audio", AUDIO, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Commit Unlock", MEDIA_SERVER, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )

        /** Verify Multi-Region Unlock Status */
        val VERIFY_REGION_FREE =
            listOf(
                MacroCommand("Read Region (0)", DIGIT_0, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Read Region (2)", DIGIT_2, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )

        // ==========================================
        // 📡 CHANNEL PAIRING & EMERGENCY LOCK RESCUE
        // ==========================================

        /** Front Panel Child Lock / X-HOLD Override (Sustained ENTER pulses) */
        val CHILD_LOCK_UNLOCK =
            List(10) {
                MacroCommand("Unlock Hold (${it + 1}/10)", ENTER_OK, DigaTimings.DELAY_SERVICE_DIGIT_MS)
            }

        // =========================================================================
        // 🔄 AUTOMATED OSD RECOVERY MACROS (AUTO-RESTORE SET 2 / SET 3 TO SET 1)
        // =========================================================================

        /**
         * Fully automated OSD navigation routine restoring a recorder trapped in SET 2 back to SET 1.
         * Commands are transmitted in Mode 2 (Subdevice 0x10) so the recorder accepts them:
         * 1. FUNCTIONS (Opens OSD Setup Menu)
         * 2. DOWN (Navigates to "To Others" / "Setup")
         * 3. ENTER_OK (Enters Setup)
         * 4. DOWN (Navigates to "Others")
         * 5. ENTER_OK (Enters Others sub-menu)
         * 6. DOWN (Navigates to "Remote Control Code")
         * 7. ENTER_OK (Opens selection list)
         * 8. UP (Moves selection from Set 2 up to Set 1)
         * 9. ENTER_OK (Commits Set 1 to NVRAM)
         * 10. RETURN (Exits OSD menu)
         */
        val AUTO_RESTORE_SET2_TO_SET1 =
            listOf(
                MacroCommand(
                    "Open Functions OSD",
                    getProntoHex(OBC.FUNCTIONS, RemoteMode.MODE_2),
                    DigaTimings.DELAY_OSD_INITIAL_OPEN_MS,
                ),
                MacroCommand(
                    "Down to Setup",
                    getProntoHex(OBC.DOWN, RemoteMode.MODE_2),
                    DigaTimings.DELAY_OSD_NAV_STEP_MS,
                ),
                MacroCommand(
                    "Enter Setup",
                    getProntoHex(OBC.ENTER_OK, RemoteMode.MODE_2),
                    DigaTimings.DELAY_OSD_SUBMENU_ENTER_MS,
                ),
                MacroCommand(
                    "Down to Others",
                    getProntoHex(OBC.DOWN, RemoteMode.MODE_2),
                    DigaTimings.DELAY_OSD_NAV_STEP_MS,
                ),
                MacroCommand(
                    "Enter Others Sub-Menu",
                    getProntoHex(OBC.ENTER_OK, RemoteMode.MODE_2),
                    DigaTimings.DELAY_OSD_SUBMENU_ENTER_MS,
                ),
                MacroCommand(
                    "Down to Remote Code",
                    getProntoHex(OBC.DOWN, RemoteMode.MODE_2),
                    DigaTimings.DELAY_OSD_NAV_STEP_MS,
                ),
                MacroCommand(
                    "Open Remote Code List",
                    getProntoHex(OBC.ENTER_OK, RemoteMode.MODE_2),
                    DigaTimings.DELAY_OSD_NAV_STEP_MS,
                ),
                MacroCommand(
                    "Select Set 1 (Up)",
                    getProntoHex(OBC.UP, RemoteMode.MODE_2),
                    DigaTimings.DELAY_OSD_NAV_STEP_MS,
                ),
                MacroCommand(
                    "Confirm Set 1 (NVRAM Commit)",
                    getProntoHex(OBC.ENTER_OK, RemoteMode.MODE_2),
                    DigaTimings.DELAY_OSD_SUBMENU_ENTER_MS,
                ),
                MacroCommand(
                    "Exit Menu (Return)",
                    getProntoHex(OBC.RETURN, RemoteMode.MODE_2),
                    DigaTimings.DELAY_OSD_NAV_STEP_MS,
                ),
            )

        /**
         * Fully automated OSD navigation routine restoring a recorder trapped in SET 3 back to SET 1.
         * Commands are transmitted in Mode 3 (Subdevice 0x20).
         */
        val AUTO_RESTORE_SET3_TO_SET1 =
            listOf(
                MacroCommand(
                    "Open Functions OSD",
                    getProntoHex(OBC.FUNCTIONS, RemoteMode.MODE_3),
                    DigaTimings.DELAY_OSD_INITIAL_OPEN_MS,
                ),
                MacroCommand(
                    "Down to Setup",
                    getProntoHex(OBC.DOWN, RemoteMode.MODE_3),
                    DigaTimings.DELAY_OSD_NAV_STEP_MS,
                ),
                MacroCommand(
                    "Enter Setup",
                    getProntoHex(OBC.ENTER_OK, RemoteMode.MODE_3),
                    DigaTimings.DELAY_OSD_SUBMENU_ENTER_MS,
                ),
                MacroCommand(
                    "Down to Others",
                    getProntoHex(OBC.DOWN, RemoteMode.MODE_3),
                    DigaTimings.DELAY_OSD_NAV_STEP_MS,
                ),
                MacroCommand(
                    "Enter Others Sub-Menu",
                    getProntoHex(OBC.ENTER_OK, RemoteMode.MODE_3),
                    DigaTimings.DELAY_OSD_SUBMENU_ENTER_MS,
                ),
                MacroCommand(
                    "Down to Remote Code",
                    getProntoHex(OBC.DOWN, RemoteMode.MODE_3),
                    DigaTimings.DELAY_OSD_NAV_STEP_MS,
                ),
                MacroCommand(
                    "Open Remote Code List",
                    getProntoHex(OBC.ENTER_OK, RemoteMode.MODE_3),
                    DigaTimings.DELAY_OSD_NAV_STEP_MS,
                ),
                MacroCommand(
                    "Move towards Set 1 (Up)",
                    getProntoHex(OBC.UP, RemoteMode.MODE_3),
                    DigaTimings.DELAY_OSD_NAV_STEP_MS,
                ),
                MacroCommand(
                    "Select Set 1 (Up)",
                    getProntoHex(OBC.UP, RemoteMode.MODE_3),
                    DigaTimings.DELAY_OSD_NAV_STEP_MS,
                ),
                MacroCommand(
                    "Confirm Set 1 (NVRAM Commit)",
                    getProntoHex(OBC.ENTER_OK, RemoteMode.MODE_3),
                    DigaTimings.DELAY_OSD_SUBMENU_ENTER_MS,
                ),
                MacroCommand(
                    "Exit Menu (Return)",
                    getProntoHex(OBC.RETURN, RemoteMode.MODE_3),
                    DigaTimings.DELAY_OSD_NAV_STEP_MS,
                ),
            )

        // ==========================================
        // 🚨 FACTORY MASTER RESET & SYSTEM HARDENING
        // ==========================================

        /** Factory Master NVRAM Reset (Service Command 99 - Displays CLEAR) */
        val FACTORY_RESET =
            listOf(
                MacroCommand("Master Reset (9)", DIGIT_9, DigaTimings.DELAY_SERVICE_DIGIT_MS),
                MacroCommand("Confirm Clear (9)", DIGIT_9, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )

        /** TVGOS EPG Nightly Spin-Up Bypass (Injects Zip Code 00000) */
        val TVGOS_DISABLE_ZIP =
            listOf(
                MacroCommand("Zip Digit 0", DIGIT_0, DigaTimings.DELAY_FAST_ZIP_DIGIT_MS),
                MacroCommand("Zip Digit 0", DIGIT_0, DigaTimings.DELAY_FAST_ZIP_DIGIT_MS),
                MacroCommand("Zip Digit 0", DIGIT_0, DigaTimings.DELAY_FAST_ZIP_DIGIT_MS),
                MacroCommand("Zip Digit 0", DIGIT_0, DigaTimings.DELAY_FAST_ZIP_DIGIT_MS),
                MacroCommand("Zip Digit 0", DIGIT_0, DigaTimings.DELAY_FAST_ZIP_DIGIT_MS),
                MacroCommand("Confirm Zip (ENTER)", ENTER_OK, DigaTimings.DELAY_SERVICE_DIGIT_MS),
            )
    }
}
