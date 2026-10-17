# 📱 Panasonic DIGA Commander (Android IR)

A native, high-precision Android application built in **Kotlin** and **Jetpack Compose** following **Clean Architecture (MVVM + Functional Programming)**. Specifically engineered to control, diagnose, and execute service bench macros and firmware hacks on **Panasonic DIGA DVD/HDD recorders** (e.g., **DMR-EH55**, DMR-EH50, DMR-EH65, DMR-EX768, DMR-ES10, DMR-ES15, and VHS/DVD Combos such as DMR-EH75V and DMR-ES30V) using the native hardware infrared emitter (*Consumer IR Blaster*) of the **Samsung Galaxy Tab S 8.4 LTE (`SM-T805M` / `chagalllte`)** running LineageOS 17.1 (Android 10, API 29).

---

## ⚡ Instant Bench Testing (No Compilation Required)

If you already have your **Samsung SM-T805M** tablet running LineageOS 17.1 / Android 10 and want to test the infrared hardware against your DIGA recorder right away:

### Option A: Using Free & Open Source Software (FOSS)
1. Download **[IR Blaster Remote (iodn / nslabs)](https://github.com/iodn/android-ir-blaster)** from [F-Droid / IzzyOnDroid](https://apt.izzysoft.de/fdroid/index/apk/org.nslabs.ir_blaster?repo=main) or the GitHub Releases page (GPL-3.0 licensed).
2. Transfer any of our ready-to-use profiles from the [`profiles/`](profiles/) directory:
   * [`profiles/Panasonic_DMR_EH55_Commander.irplus`](profiles/Panasonic_DMR_EH55_Commander.irplus) (Complete DMR-EH55 physical remote + 12 service macros)
   * [`profiles/Panasonic_DIGA_ES10_ES15_TBC.irplus`](profiles/Panasonic_DIGA_ES10_ES15_TBC.irplus) (Optimized for DMR-ES10 / DMR-ES15 TBC pass-through)
   * [`profiles/Panasonic_DIGA_Combo_VHS_DVD_EH75V_ES30V.irplus`](profiles/Panasonic_DIGA_Combo_VHS_DVD_EH75V_ES30V.irplus) (Dual Deck DVD 0xB0 & VCR 0x90 + dubbing)
   * [`profiles/Panasonic_DIGA_Universal_Modes_1_2_3.irplus`](profiles/Panasonic_DIGA_Universal_Modes_1_2_3.irplus) (Multi-mode pairing and recovery)
3. Open the app, select **Import**, choose the `.irplus` XML file, and begin testing immediately.

### Option B: Using irplus (Google Play)
1. Install **[irplus - WAVE IR Remote](https://play.google.com/store/apps/details?id=net.binarymode.android.irplus)** from the Google Play Store.
2. Tap the 3-dot overflow menu ➔ **Import** ➔ select the profile from [`profiles/`](profiles/).
3. All remote controls and macros will be fully operational.

---

## 🔄 Automated OSD Channel Recovery (SET 2 / SET 3 ➔ SET 1)

### The Problem:
When a Panasonic DIGA recorder is configured for an alternate remote control code, its front VFD panel flashes **`SET 2`** or **`SET 3`** whenever any standard remote button is pressed, refusing all commands. Previously, recovering from this required either:
- Having a physical remote with the exact button combination (`ENTER` + `1`/`2`/`3` held for 3 seconds), or
- Manually navigating the TV on-screen menu if an alternate remote was available.

### The Solution:
Our app features **1-Touch Automated OSD Channel Recovery Macros**:
1. **`AUTO_RESTORE_SET2_TO_SET1`**: Transmits with Subdevice `0x10` (Mode 2) so the locked recorder accepts every keystroke:
   $$\text{FUNCTIONS} \longrightarrow \text{DOWN} \longrightarrow \text{ENTER} \longrightarrow \text{DOWN} \longrightarrow \text{ENTER} \longrightarrow \text{DOWN} \longrightarrow \text{ENTER} \longrightarrow \text{UP} \longrightarrow \text{ENTER} \longrightarrow \text{RETURN}$$
2. **`AUTO_RESTORE_SET3_TO_SET1`**: Transmits with Subdevice `0x20` (Mode 3) to execute the matching recovery traversal.

The recorder automatically navigates its Setup menu, selects **Remote Control Code**, changes the setting back to **Set 1**, and commits the value to NVRAM without manual interaction.

---

## 🧰 Master Catalog of Hacks, Diagnostics & Bench Macros

| Macro / Command | Key Sequence | Technical Description & VFD Display Output |
| :--- | :--- | :--- |
| **Laser Operating Hours** | `[4]` `[1]` | Displays cumulative optical pickup diode laser emission time in hours (`L****`). |
| **RAM Drive Error Code** | `[4]` `[2]` | Displays the most recent error code logged by the optical DVD-RAM drive mechanism. |
| **General Self-Diagnostics** | `[0]` `[1]` | Reads the latest general CPU fault code recorded (`U61`, `H19`, etc.). |
| **EEPROM Error Log (Slots 1–5)** | `[6]` `[5]` ➔ `01`..`05` | Navigates the last 19 historical error events stored in non-volatile EEPROM memory. |
| **Clear Error Buffer to F00** | `[9]` `[8]` | Writes `F00` to the temporary error buffer without erasing user configurations. |
| **Destination Region Readout** | `[0]` `[2]` | Displays active DVD region code for 5 seconds (`01` US, `02` Europe, `04` Brazil, `00` Free). |
| **HDD SMART Surface Scan** | `[3]` `[1]` | Initiates physical disk sector scan and automatic MEIHDFS bad block reallocation. |
| **VFD Display Diagnostic** | `[0]` `[0]` | Illuminates 100% of front-panel vacuum fluorescent display segments and annunciators. |
| **Loading Tray Endurance Cycle** | `[9]` `[1]` | Continuous open/close mechanical cycle to stress-test loading motor and belts. |
| **Video I/O Pattern Validation** | `[8]` `[0]` / `[8]` `[1]` | Outputs calibration patterns for RGB, Component, and S-Video bus inspection. |
| **★ Multi-Region (Region Free) ★** | `1`..`0` ➔ `AUDIO` ➔ `MEDIA` | 12-pulse sequence with 500 ms intervals permanently unlocking Region 0 playback. |
| **Auto-Restore SET 2 ➔ SET 1** | 10-step OSD Sequence | Automated OSD menu traversal on Mode 2 restoring NVRAM back to Set 1. |
| **Auto-Restore SET 3 ➔ SET 1** | 11-step OSD Sequence | Automated OSD menu traversal on Mode 3 restoring NVRAM back to Set 1. |
| **Child Lock Emergency Override** | `[ENTER]` held 5s | Releases front-panel microcontroller lockout (`LOCKED` or `X-HOLD`). |
| **TVGOS Nightly Spin-Up Bypass** | `00000` in Zip Field | Injects `00000` to disable 3:00 AM nightly HDD spin-up for extinct analog EPG broadcasts. |
| **Factory Master NVRAM Reset** | `[9]` `[9]` | Clears all NVRAM/EEPROM parameters and restores factory default state (`CLEAR`). |

---

## 📚 Technical Documentation & Research Dossiers

Detailed engineering documentation is organized inside the [`docs/`](docs/) directory:

* 📄 **[`docs/PANASONIC_DIGA_HACKS_GUIDE.md`](docs/PANASONIC_DIGA_HACKS_GUIDE.md)**:
  * Physics of the 48-bit Matsushita Kaseikyo protocol ($37.010\text{ kHz}$ carrier, pulse-width modulation, XOR checksum architecture).
  * Comprehensive table of 17 service hacks and diagnostics with SuperH SH-4 microcontroller internal operations.
  * Self-diagnostic decoding guide for `U**` (operational), `H**` (servo/hardware), and `F**` (critical bus/EEPROM) error codes.
  * Complete 52-command Pronto Hex cross-reference table.
* 📄 **[`docs/PANASONIC_DIGA_MULTI_MODEL_EXPANSION_PLAN.md`](docs/PANASONIC_DIGA_MULTI_MODEL_EXPANSION_PLAN.md)**:
  * Multi-generation compatibility matrix across DIGA lines (E, ES, EH, EX, EZ, DMR-BW/XW/BWT, and VHS/DVD Combos).
  * Dual-deck addressing for VHS mechanisms (Device `0x90`) and DVD/HDD mechanisms (Device `0xB0`).
* 📄 **[`docs/PANASONIC_DIGA_ANDROID_IR_PLAN.md`](docs/PANASONIC_DIGA_ANDROID_IR_PLAN.md)**:
  * Architectural design document and hardware specifications for the Samsung SM-T805M tablet.

---

## 🏛 Clean Architecture & Kotlin Idioms

The project is structured according to strict **Clean Architecture**, **MVVM**, and **Functional Programming (FP)** principles:

```
app/src/main/java/com/vhs/digacommander/
├── MainActivity.kt                     # Entry point binding ViewModel to Compose lifecycle
├── data/
│   ├── model/                          # Immutable domain and presentation data classes
│   │   ├── IrSignal.kt                 # Immutable carrier frequency + pulse microsecond array
│   │   ├── RemoteMode.kt               # Enum (MODE_1 = 0x00, MODE_2 = 0x10, MODE_3 = 0x20)
│   │   ├── DigaDevice.kt               # Enum (DVD_HDD = 0xB0, VCR = 0x90, TV = 0x01)
│   │   ├── DigaModelProfile.kt         # Enum (DMR_EH55, DMR_ES10_ES15, COMBO_VHS_DVD, UNIVERSAL)
│   │   ├── MacroCommand.kt             # Immutable macro step definition
│   │   └── MacroExecutionState.kt      # Sealed interface (Idle, Running, Completed, Error, Cancelled)
│   ├── ir/
│   │   ├── KaseikyoProtocol.kt         # Pure FP pulse and Pronto Hex synthesizer (referentially transparent)
│   │   ├── ProntoHexConverter.kt       # Pure functional parser converting Pronto strings to IrSignal
│   │   └── ConsumerIrTransmitter.kt    # Hardware abstraction over Android ConsumerIrManager
│   └── catalog/
│       └── PanasonicDigaCatalog.kt     # Master catalog: 52 OBC codes, Pronto strings, and bench macros
├── domain/
│   └── pipeline/
│       └── DigaMacroPipeline.kt        # Serialized Mutex coroutine pipeline with anti-brick delays
└── presentation/
    ├── viewmodel/
    │   └── DigaViewModel.kt            # UDF ViewModel managing immutable StateFlow<DigaUiState>
    ├── theme/
    │   └── DigaTheme.kt                # Fluorescent retro cyan VFD palette and typography
    └── ui/
        ├── DigaMainScreen.kt           # Main scaffold connecting ViewModel to tabs
        ├── components/
        │   └── VfdDisplay.kt           # Parametric retro fluorescent VFD panel
        └── tabs/
            ├── RemoteTab.kt            # Tab 1: EUR7729KG0 mirror, profile selector, mode switcher, VCR deck
            ├── ServiceTab.kt           # Tab 2: Service bench diagnostics, laser hour meter, EEPROM slots
            └── HacksTab.kt             # Tab 3: Automated OSD recovery, Region Free unlock, Child Lock override
```

---

## 🔬 Mathematical Pulse Timing (48-bit Kaseikyo)

The `KaseikyoProtocol` and `ProntoHexConverter` modules synthesize bit-accurate pulse arrays for Android's `ConsumerIrManager.transmit()`:

* **Carrier Frequency:** $37.010\text{ kHz}$ (Frequency code `0070`, divisor 112) or $38.029\text{ kHz}$ (`006D`).
* **Lead-in Mark:** $3{,}459\,\mu\text{s}$ (128 cycles at $37\text{ kHz}$).
* **Lead-in Space:** $1{,}729\,\mu\text{s}$ (64 cycles at $37\text{ kHz}$).
* **Logical Bit 0:** $432\,\mu\text{s}$ Mark / $432\,\mu\text{s}$ Space ($1:1$ ratio).
* **Logical Bit 1:** $432\,\mu\text{s}$ Mark / $1{,}297\,\mu\text{s}$ Space ($1:3$ ratio).
* **Frame Length:** Exactly 100 alternating pulses (50 burst pairs = 48 data bits + lead-in + stop bit).

---

## 🛡 Anti-Brick State Machine & Hardware Safety

Panasonic DIGA recorders feature Renesas SuperH SH-4 microcontrollers with strict interrupt queue depths. The `DigaMacroPipeline` enforces hardware safety:

```
[Idle] ──(Trigger)──> [Running (Step 1/N)] ──(Monotonic Delay)──> [Running (Step 2/N)] ──> [Completed]
  │                           │
  └──(User Abort)─────────────┴───────────────────────────────────────────────────────> [Cancelled]
```

1. **Serialized Mutex:** Prevents overlapping pulse transmissions if buttons are pressed rapidly.
2. **Monotonic Inter-Frame Delays:** $500\text{ ms}$ minimum between service digits; $600\text{–}1200\text{ ms}$ for OSD navigation steps to allow NVRAM EEPROM commits.
3. **Reactive Cancellation:** Operators can abort running macros instantly via the VFD progress banner.
4. **Safety Confirmation Modals:** Destructive or invasive commands (Multi-Region Hack, Factory Reset 99, OSD Recovery) require explicit confirmation.

---

## 🚀 Building in Android Studio

1. Clone the repository:
   ```bash
   git clone https://github.com/orgchrono/android-remote-panasonic.git
   ```
2. Open **Android Studio** and select **Open**, pointing to the cloned directory.
3. Allow Gradle to synchronize dependencies.
4. Connect the **Samsung SM-T805M** tablet via USB with USB Debugging enabled (`adb devices`).
5. Run the **app** configuration (`Shift + F10`) to build and deploy.

---

## 🤖 CI/CD: Automated Pipeline (Pre-Commit, Linter, Build & Release)

The repository follows industry-standard separation of concerns with two dedicated GitHub Actions workflows and local pre-commit hooks:
* **CI Workflow ([`.github/workflows/ci.yml`](.github/workflows/ci.yml)):** Runs on pushes to `main` and Pull Requests. Executes pre-commit hygiene checks ([`.pre-commit-config.yaml`](.pre-commit-config.yaml)), KtLint style auditing (`./gradlew ktlintCheck`), Android Lint (`./gradlew lintDebug`), JUnit unit tests (`./gradlew test`), and verifies the debug build.
* **Release Workflow ([`.github/workflows/release.yml`](.github/workflows/release.yml)):** Runs exclusively on release tags (e.g. `v0.1.0`). Compiles signed production APKs (`Panasonic-DIGA-Commander-v0.1.0.apk`), diagnostic debug APKs (`Panasonic-DIGA-Commander-v0.1.0-debug.apk`), generates cryptographic checksums (`SHA256SUMS.txt`), and publishes the official GitHub Release without duplicate artifacts.

---

## 📄 License

This project is licensed under the Apache License 2.0. Built for preservation, bench servicing, and digital archiving workflows.
