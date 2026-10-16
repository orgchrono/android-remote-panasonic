package com.vhs.digacommander.presentation.constant

/**
 * Single Source of Truth (SSOT) for all UI string constants, section labels,
 * button texts, dialog messages, and virtual VFD status telemetry.
 *
 * Glyph policy: plain ASCII/Latin text only. Symbols such as play/eject/power are rendered with
 * Material vector icons, because Unicode symbol glyphs are missing from the Samsung/Android 5-6
 * system fonts and show up as empty boxes.
 */
object DigaUiStrings {
    // App Header & Tabs
    const val APP_TITLE: String = "DIGA Commander Pro"
    const val TAB_REMOTE: String = "Remote"
    const val TAB_SERVICE: String = "Service"
    const val TAB_HACKS: String = "Recovery"
    const val TAB_IR: String = "IR Link"

    // VFD Telemetry & Status
    const val VFD_HEADER_TITLE: String = "DIGA"
    const val VFD_SEPARATOR: String = "  -  "
    const val VFD_BADGE_SEPARATOR: String = " | "
    const val VFD_READY: String = "READY"
    const val VFD_ONLINE: String = "DIGA 37.01 kHz KASEIKYO"
    const val VFD_INITIALIZING: String = "INIT"
    const val VFD_TRANSMITTING_PULSE: String = "TX 48-BIT KASEIKYO"
    const val VFD_PULSE_SENT: String = "PULSE SENT"
    const val VFD_COMPLETED: String = "COMPLETED"
    const val VFD_COMPLETED_SUB: String = "SEQUENCE TRANSMITTED"
    const val VFD_ABORTED: String = "ABORTED"
    const val VFD_ABORTED_SUB: String = "CANCELLED BY OPERATOR"
    const val VFD_ERROR: String = "IR ERROR"
    const val VFD_EMULATOR_MODE: String = "NO IR"
    const val VFD_IR_ONLINE_37K: String = "IR 37.01 kHz"
    const val VFD_IR_ONLINE_LIMITED: String = "IR READY"
    const val VFD_IR_ADAPTED_PREFIX: String = "IR "
    const val VFD_IR_ADAPTED_SUFFIX: String = " kHz"
    const val VFD_STEP_PREFIX: String = "STEP "
    const val VFD_SENDING_PREFIX: String = "TX: "
    const val VFD_CHANNEL_PREFIX: String = "CHANNEL: "
    const val VFD_SUBDEVICE_PREFIX: String = "SUBDEVICE 0x"
    const val SUFFIX_DECK_VCR: String = " (VCR)"
    const val VFD_FAILED_TRANSMIT_PULSE: String = "Failed to transmit pulse"
    const val VFD_BEACON_ACTIVE: String = "IR BEACON ACTIVE"
    const val VFD_BEACON_HINT: String = "CHECK WITH PHONE CAMERA"
    const val VFD_BEACON_DONE: String = "BEACON FINISHED"
    const val VFD_BEACON_DONE_SUB: String = "CHECK CAMERA RESULT"
    const val BANNER_NO_IR: String = "No IR emitter detected. Tap to open IR Link diagnostics."

    // Status & Macro Execution
    const val LABEL_ACTIVE_PREFIX: String = "ACTIVE: "
    const val MACRO_EXECUTING_PREFIX: String = "Macro"
    const val BTN_ABORT: String = "Abort"

    // Remote Tab Sections
    const val SECTION_PROFILE_MODE: String = "RECORDER PROFILE & IR CHANNEL"
    const val SECTION_SYSTEM_DRIVE: String = "SYSTEM & DRIVE SELECTION"
    const val SECTION_NAVIGATION_MENUS: String = "NAVIGATION & MENUS"
    const val SECTION_TRANSPORT: String = "TRANSPORT DECK"
    const val SECTION_VCR_DECK: String = "VCR DECK (COMBO VHS / S-VHS - DEVICE 0x90)"
    const val SECTION_EDITING_FEATURES: String = "DIGA EDITING & ADVANCED FEATURES"
    const val SECTION_RECORDING_BROADCAST: String = "RECORDING & BROADCAST"
    const val SECTION_NUMERIC_KEYPAD: String = "NUMERIC KEYPAD"

    // Remote Buttons (UI Display Labels)
    const val BTN_POWER: String = "POWER"
    const val BTN_EJECT: String = "EJECT"
    const val BTN_STATUS: String = "STATUS"
    const val BTN_DISPLAY: String = "DISP"
    const val BTN_DRIVE_SELECT_ALL: String = "DRIVE SELECT (HDD / DVD / SD)"
    const val BTN_INPUT_SELECT: String = "INPUT (AV/DV)"
    const val BTN_DIRECT_NAV: String = "DIRECT NAV"
    const val BTN_FUNCTIONS: String = "FUNCTIONS"
    const val BTN_TOP_MENU: String = "TOP MENU"
    const val BTN_OPTION: String = "OPTION"
    const val BTN_UP: String = "Up"
    const val BTN_DOWN: String = "Down"
    const val BTN_LEFT: String = "Left"
    const val BTN_RIGHT: String = "Right"
    const val BTN_OK: String = "OK"
    const val BTN_RETURN: String = "RETURN"
    const val BTN_EXIT: String = "EXIT"
    const val BTN_GUIDE: String = "GUIDE"
    const val BTN_PROG_CHECK: String = "PROG"
    const val BTN_PLAY: String = "PLAY"
    const val BTN_PAUSE: String = "PAUSE"
    const val BTN_STOP: String = "STOP"
    const val BTN_SKIP_PREV: String = "Previous"
    const val BTN_REWIND: String = "Rewind"
    const val BTN_FAST_FORWARD: String = "Fast forward"
    const val BTN_SKIP_NEXT: String = "Next"
    const val BTN_TIME_SLIP: String = "TIME SLIP"
    const val BTN_REPLAY_10S: String = "-10 s"
    const val BTN_SKIP_30S: String = "+30 s"
    const val BTN_CREATE_CHAPTER: String = "CREATE CH."
    const val BTN_DELETE_TITLE: String = "DELETE"
    const val BTN_AUDIO: String = "AUDIO"
    const val BTN_SUBTITLE: String = "SUBTITLE"
    const val BTN_REC: String = "REC"
    const val BTN_REC_MODE: String = "REC MODE (XP/SP)"
    const val BTN_EXT_LINK: String = "EXT LINK"
    const val BTN_CH_UP: String = "CH +"
    const val BTN_CH_DOWN: String = "CH -"
    const val BTN_VOL_UP: String = "VOL +"
    const val BTN_VOL_DOWN: String = "VOL -"
    const val BTN_MEDIA: String = "MEDIA"
    const val KEYPAD_ZERO: String = "0"
    const val BTN_CANCEL: String = "CANCEL"

    // VCR Buttons (UI Display Labels)
    const val BTN_VCR_DRIVE: String = "DRIVE: VHS"
    const val BTN_VCR_EJECT: String = "EJECT VHS"
    const val BTN_VCR_PLAY: String = "VHS PLAY"
    const val BTN_VCR_PAUSE: String = "PAUSE"
    const val BTN_VCR_STOP: String = "STOP"
    const val BTN_VCR_REWIND: String = "VHS REW"
    const val BTN_VCR_FF: String = "VHS FF"
    const val BTN_VCR_REC: String = "REC"
    const val BTN_VCR_TRACKING_UP: String = "TRACKING +"
    const val BTN_VCR_TRACKING_DOWN: String = "TRACKING -"
    const val BTN_DUB_VHS_TO_DVD: String = "DUB: VHS to DVD"
    const val BTN_DUB_DVD_TO_VHS: String = "DUB: DVD to VHS"

    // Command Dispatch Telemetry Labels (OBC Dispatch Tracking)
    const val CMD_LABEL_POWER: String = "POWER"
    const val CMD_LABEL_EJECT: String = "EJECT"
    const val CMD_LABEL_STATUS: String = "STATUS"
    const val CMD_LABEL_DISPLAY: String = "DISPLAY"
    const val CMD_LABEL_DRIVE_SELECT: String = "DRIVE SELECT (HDD/DVD/SD)"
    const val CMD_LABEL_INPUT_SELECT: String = "INPUT SELECT"
    const val CMD_LABEL_DIRECT_NAV: String = "DIRECT NAVIGATOR"
    const val CMD_LABEL_FUNCTIONS: String = "FUNCTIONS"
    const val CMD_LABEL_TOP_MENU: String = "TOP MENU"
    const val CMD_LABEL_OPTION: String = "OPTION"
    const val CMD_LABEL_UP: String = "UP"
    const val CMD_LABEL_DOWN: String = "DOWN"
    const val CMD_LABEL_LEFT: String = "LEFT"
    const val CMD_LABEL_RIGHT: String = "RIGHT"
    const val CMD_LABEL_OK: String = "OK / ENTER"
    const val CMD_LABEL_RETURN: String = "RETURN"
    const val CMD_LABEL_EXIT: String = "EXIT"
    const val CMD_LABEL_GUIDE: String = "GUIDE"
    const val CMD_LABEL_PROG_CHECK: String = "PROG / CHECK"
    const val CMD_LABEL_PLAY: String = "PLAY"
    const val CMD_LABEL_PAUSE: String = "PAUSE"
    const val CMD_LABEL_STOP: String = "STOP"
    const val CMD_LABEL_SKIP_PREV: String = "SKIP PREV"
    const val CMD_LABEL_REWIND: String = "REWIND"
    const val CMD_LABEL_FAST_FORWARD: String = "FAST FORWARD"
    const val CMD_LABEL_SKIP_NEXT: String = "SKIP NEXT"
    const val CMD_LABEL_TIME_SLIP: String = "TIME SLIP"
    const val CMD_LABEL_REPLAY_10S: String = "MANUAL REPLAY (-10s)"
    const val CMD_LABEL_SKIP_30S: String = "MANUAL SKIP (+30s)"
    const val CMD_LABEL_CREATE_CHAPTER: String = "CREATE CHAPTER"
    const val CMD_LABEL_DELETE_TITLE: String = "DELETE TITLE"
    const val CMD_LABEL_AUDIO: String = "AUDIO"
    const val CMD_LABEL_SUBTITLE: String = "SUBTITLE"
    const val CMD_LABEL_REC: String = "REC"
    const val CMD_LABEL_REC_MODE: String = "REC MODE (XP/SP/LP/EP)"
    const val CMD_LABEL_EXT_LINK: String = "EXT LINK"
    const val CMD_LABEL_CH_UP: String = "CH UP"
    const val CMD_LABEL_CH_DOWN: String = "CH DOWN"
    const val CMD_LABEL_VOL_UP: String = "VOL UP"
    const val CMD_LABEL_VOL_DOWN: String = "VOL DOWN"
    const val CMD_LABEL_MEDIA_SERVER: String = "MEDIA SERVER"
    const val CMD_LABEL_CANCEL: String = "CANCEL"
    const val CMD_LABEL_VCR_DRIVE: String = "DRIVE SELECT (VHS)"
    const val CMD_LABEL_VCR_EJECT: String = "VHS EJECT"
    const val CMD_LABEL_VCR_PLAY: String = "VHS PLAY"
    const val CMD_LABEL_VCR_PAUSE: String = "PAUSE"
    const val CMD_LABEL_VCR_STOP: String = "STOP"
    const val CMD_LABEL_VCR_REWIND: String = "VHS REWIND"
    const val CMD_LABEL_VCR_FF: String = "VHS FF"
    const val CMD_LABEL_VCR_REC: String = "REC"
    const val CMD_LABEL_VCR_TRACKING_UP: String = "TRACKING UP"
    const val CMD_LABEL_VCR_TRACKING_DOWN: String = "TRACKING DOWN"
    const val CMD_LABEL_VCR_DUB_VHS_TO_DVD: String = "DUBBING: VHS to DVD"
    const val CMD_LABEL_VCR_DUB_DVD_TO_VHS: String = "DUBBING: DVD to VHS"

    // Service Tab Strings
    const val SERVICE_GUIDE_TITLE: String = "SERVICE MODE BENCH ENTRY PROCEDURE"
    const val SERVICE_GUIDE_BODY: String =
        "1. Turn ON the DIGA recorder, remove any disc, and verify STOP status.\n" +
            "2. On the physical front panel, hold [STOP] + [PLAY] simultaneously for 5 seconds.\n" +
            "   (Alternative for select models: hold [STOP] + [CH UP] in Standby mode).\n" +
            "3. The VFD fluorescent display will indicate 'SERVICE' mode.\n" +
            "4. Trigger diagnostic macros below to read parameters directly on the display."
    const val SERVICE_SECTION_OPTICAL: String = "OPTICAL HEALTH & LASER HOUR COUNTER"
    const val SERVICE_LASER_HOURS_TITLE: String = "LASER HOURS (41)"
    const val SERVICE_LASER_HOURS_DESC: String = "Total cumulative laser diode emission time (e.g., L0185 = 185 hours)."
    const val SERVICE_LASER_HOURS_BTN: String = "Execute [4][1]"
    const val SERVICE_RAM_ERROR_TITLE: String = "RAM DRIVE ERROR (42)"
    const val SERVICE_RAM_ERROR_DESC: String = "Displays the most recent optical drive RAM mechanism error code."
    const val SERVICE_RAM_ERROR_BTN: String = "Execute [4][2]"
    const val SERVICE_SECTION_EEPROM: String = "EEPROM FAULT LOG & SELF-DIAGNOSTICS"
    const val SERVICE_EEPROM_CARD_TITLE: String = "FAULT RECALL & ERROR BUFFER MANAGEMENT"
    const val SERVICE_EEPROM_CARD_DESC: String = "Reads self-diagnostic error codes (U**, H**, F**) stored in NVRAM/EEPROM memory."
    const val SERVICE_LATEST_ERROR_BTN: String = "Latest Error [0][1]"
    const val SERVICE_CLEAR_ERROR_BTN: String = "Clear [9][8] (F00)"
    const val SERVICE_SLOTS_LABEL: String = "Historical Error Log Slots (Mode 65 Buffer):"
    const val SERVICE_SECTION_HARDWARE: String = "HARDWARE DIAGNOSTICS, HDD & DISPLAY"
    const val SERVICE_HDD_TEST_TITLE: String = "HDD TEST (31)"
    const val SERVICE_HDD_TEST_DESC: String = "SMART surface scan and bad sector reallocation in MEIHDFS filesystem."
    const val SERVICE_HDD_TEST_BTN: String = "Execute [3][1]"
    const val SERVICE_VFD_TEST_TITLE: String = "VFD DISPLAY (00)"
    const val SERVICE_VFD_TEST_DESC: String = "Illuminates 100% of fluorescent segments and annunciator icons."
    const val SERVICE_VFD_TEST_BTN: String = "Execute [0][0]"
    const val SERVICE_TRAY_TEST_TITLE: String = "TRAY CYCLE (91)"
    const val SERVICE_TRAY_TEST_DESC: String = "Continuous optical drive load/eject mechanical stress test."
    const val SERVICE_TRAY_TEST_BTN: String = "Execute [9][1]"
    const val SERVICE_VIDEO_IO_TITLE: String = "VIDEO I/O (80 / 81)"
    const val SERVICE_VIDEO_IO_DESC: String = "Hardware video encoder test patterns for RGB/Component/S-Video."
    const val SERVICE_VIDEO_IO_BTN: String = "Pattern [8][1]"
    const val SERVICE_EXIT_BTN: String = "EXIT SERVICE MODE (POWER)"

    // Hacks Tab Strings
    const val HACKS_AUTO_RECOVERY_TITLE: String = "AUTOMATED OSD RECOVERY (SET 2 / SET 3 to SET 1)"
    const val HACKS_AUTO_RECOVERY_DESC: String =
        "If your recorder's front VFD flashes 'SET 2' or 'SET 3' and ignores regular remote buttons, " +
            "the NVRAM was locked to an alternate remote code.\n\n" +
            "These 1-touch automated macros transmit the entire OSD setup navigation using the matching " +
            "subdevice frequency (Mode 2 or Mode 3):\n" +
            "FUNCTIONS → DOWN → ENTER (Setup) → DOWN → ENTER (Others) → DOWN → ENTER (Remote Code) → UP → ENTER (Set 1) → RETURN."
    const val HACKS_BTN_RESTORE_SET2: String = "AUTO-RESTORE\nSET 2 to SET 1"
    const val HACKS_BTN_RESTORE_SET3: String = "AUTO-RESTORE\nSET 3 to SET 1"
    const val HACKS_MANUAL_SYNC_LABEL: String = "Manual Remote Channel Switching (Sync Commander Transmitter):"
    const val HACKS_MULTI_REGION_TITLE: String = "MULTI-REGION HACK (DVD REGION FREE / REGION 0)"
    const val HACKS_MULTI_REGION_DESC: String =
        "Permanently unlocks the DVD drive for worldwide playback across all regions (R1, R2, R3, R4, R5, R6).\n\n" +
            "Mandatory Prerequisites:\n" +
            "• DIGA recorder powered ON;\n" +
            "• Disc loading tray OPEN and EMPTY (no media inserted);\n" +
            "• Sequential 12-pulse injection with 500 ms inter-frame timing (anti-brick protected)."
    const val HACKS_MULTI_REGION_BTN: String = "UNLOCK MULTI-REGION (12-STEP MACRO)"
    const val HACKS_CHECK_REGION_BTN: String = "Read Current Region on VFD [0][2] (00 = Region Free)"
    const val HACKS_CHILD_LOCK_TITLE: String = "FRONT PANEL CHILD LOCK OVERRIDE (X-HOLD / LOCKED)"
    const val HACKS_CHILD_LOCK_DESC: String =
        "If physical front-panel buttons are unresponsive and the VFD flashes 'LOCKED' or 'X-HOLD', " +
            "transmit this sustained 5-second pulse sequence to clear the microcontroller lockout."
    const val HACKS_CHILD_LOCK_BTN: String = "Transmit Sustained Unlock [ENTER 5s]"
    const val HACKS_TVGOS_TITLE: String = "TVGOS NIGHTLY SPIN-UP BYPASS (HDD WEAR REDUCTION)"
    const val HACKS_TVGOS_DESC: String =
        "Legacy TV Guide On Screen firmware attempts nightly 3:00 AM spins searching for extinct analog broadcasts, " +
            "causing noise and spindle bearing wear.\n" +
            "Navigate to Setup → TV Guide → Postal / Zip Code and inject '00000' to permanently disable nighttime scanning."
    const val HACKS_TVGOS_BTN: String = "Inject '00000' + ENTER in Zip Field"
    const val HACKS_RESET_TITLE: String = "FACTORY MASTER RESET / NVRAM CLEAR (COMMAND 99)"
    const val HACKS_RESET_DESC: String =
        "Service bench command only. Clears all EEPROM/NVRAM settings, resets error logs, " +
            "and restores factory defaults (displays 'CLEAR' on VFD). Recorder must be in Service Mode."
    const val HACKS_RESET_BTN: String = "Execute Factory Reset (Command 99)"

    // Dialog Confirmation Strings
    const val DIALOG_SET2_TITLE: String = "Confirm SET 2 to SET 1 Recovery"
    const val DIALOG_SET2_BODY: String =
        "This macro transmits 10 sequential navigation steps on Mode 2 (Subdevice 0x10) to automatically " +
            "open the Functions menu, enter Setup -> Others -> Remote Control Code, switch from Set 2 to Set 1, " +
            "and commit to NVRAM.\n\n" +
            "Prerequisites:\n" +
            "• DMR recorder is powered ON;\n" +
            "• Connected TV screen is ON and displaying the recorder's video output;\n" +
            "• Recorder currently shows 'SET 2' when pressing remote buttons.\n\n" +
            "Transmit recovery sequence now?"
    const val DIALOG_SET3_TITLE: String = "Confirm SET 3 to SET 1 Recovery"
    const val DIALOG_SET3_BODY: String =
        "This macro transmits 11 sequential navigation steps on Mode 3 (Subdevice 0x20) to automatically " +
            "open the Functions menu, navigate to Remote Control Code, switch from Set 3 to Set 1, " +
            "and commit to NVRAM.\n\n" +
            "Prerequisites:\n" +
            "• DMR recorder is powered ON;\n" +
            "• Connected TV screen is ON and displaying the recorder's video output;\n" +
            "• Recorder currently shows 'SET 3' when pressing remote buttons.\n\n" +
            "Transmit recovery sequence now?"
    const val DIALOG_MULTI_REGION_TITLE: String = "Confirm Multi-Region Unlock"
    const val DIALOG_MULTI_REGION_BODY: String =
        "Please verify that the DIGA recorder is powered ON, and the DVD tray is OPEN and EMPTY.\n\n" +
            "The app will transmit a precise 12-pulse sequence with 500 ms intervals.\n\n" +
            "Proceed with multi-region unlock?"
    const val DIALOG_RESET_TITLE: String = "Warning: Factory Master Reset (99)"
    const val DIALOG_RESET_BODY: String =
        "This operation re-initializes all NVRAM configurations and resets error history to factory status.\n\n" +
            "The recorder must already be in Service Mode to accept this command.\n\n" +
            "Confirm transmission of reset code 99?"
    const val DIALOG_CONFIRM: String = "Confirm"
    const val DIALOG_START_RECOVERY: String = "Start Recovery"
    const val DIALOG_EXECUTE_RESET: String = "Execute Reset"
    const val DIALOG_CANCEL: String = "Cancel"
}
