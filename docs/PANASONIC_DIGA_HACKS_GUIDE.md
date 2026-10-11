# Panasonic DIGA DMR-EH55 — Comprehensive Engineering Dossier: Service Modes, Diagnostic Hacks & IR Protocol Specification

---

## 1. Executive Overview

This engineering dossier documents the internal service diagnostics, hidden firmware routines, hardware hacks, and complete infrared remote control command catalog for the **Panasonic DMR-EH55** (and sibling DIGA platforms including **DMR-EH50, DMR-EH65, DMR-EX77, DMR-ES10, and DMR-ES15**).

Manufactured by Matsushita Electric Industrial Co., Ltd., the Panasonic DIGA series relies on the **Hitachi / Renesas SuperH SH-4 (HD6417760)** 32-bit RISC central processor, coupled with an LSI MPEG-2 hardware codec, ATA HDD controller, and an internal Matsushita DVD-RAM/DVD-R optical drive engine communicating over internal SPI/I²C and ATAPI buses.

Accessing non-volatile EEPROM registers, laser operating hour counters, error histories, and regional parameters requires transmitting discrete **Matsushita/Kaseikyo 48-bit** pulse trains with rigid timing constraints.

---

## 2. Infrared Physical & Protocol Specification (Kaseikyo 48-bit)

Panasonic audio/video equipment communicates via the **Kaseikyo (Japanese Association of Electronic Industries) 48-bit** infrared protocol.

### 2.1 Modulation & Pulse-Distance Timing
- **Carrier Frequency**: `37.010 kHz` (Period: $T = 27.02\,\mu\text{s}$; Philips Pronto frequency divisor code `0x0070` = 112 decimal).
- **Lead-in (Preamble)**:
  - **Mark**: $128 \times T = 3459\,\mu\text{s}$ (Pronto code `0080`)
  - **Space**: $64 \times T = 1729\,\mu\text{s}$ (Pronto code `0040`)
- **Logical Bit 0**:
  - **Mark**: $16 \times T = 432\,\mu\text{s}$ (Pronto code `0010`)
  - **Space**: $16 \times T = 432\,\mu\text{s}$ (Pronto code `0010`) — Ratio 1:1
- **Logical Bit 1**:
  - **Mark**: $16 \times T = 432\,\mu\text{s}$ (Pronto code `0010`)
  - **Space**: $48 \times T = 1297\,\mu\text{s}$ (Pronto code `0030`) — Ratio 1:3
- **Lead-out (Trailing Stop)**:
  - **Mark**: $16 \times T = 432\,\mu\text{s}$ (Pronto code `0010`)
  - **Space / Inter-frame Gap**: $\approx 74.7\,\text{ms}$ (Pronto code `0ACD`)

### 2.2 Frame Architecture (6 Bytes / 48 Bits, LSB-First)

$$\text{Frame} = [\text{OEM}_1]\,[\text{OEM}_2]\,[\text{Device}]\,[\text{Subdevice}]\,[\text{Command}]\,[\text{Checksum}]$$

| Byte Index | Field Name | Panasonic Constant | Binary Representation (LSB First) | Description |
|:---:|:---|:---:|:---:|:---|
| **Byte 0** | OEM Vendor 1 | `0x02` | `0 1 0 0 0 0 0 0` | Matsushita Vendor Identification LSB |
| **Byte 1** | OEM Vendor 2 | `0x20` | `0 0 0 0 0 1 0 0` | Matsushita Vendor Identification MSB |
| **Byte 2** | Device ID | `0xB0` | `0 0 0 0 1 1 0 1` | DIGA DVD / HDD Recorder Family Base Address |
| **Byte 3** | Subdevice ID | `0x00` | `0 0 0 0 0 0 0 0` | Primary Subsystem Control Group |
| **Byte 4** | Command | `CMD` | $b_0\,b_1\,b_2\,b_3\,b_4\,b_5\,b_6\,b_7$ | Discrete Function Command Byte |
| **Byte 5** | Parity Checksum | `CHK` | $c_0\,c_1\,c_2\,c_3\,c_4\,c_5\,c_6\,c_7$ | Parity Checksum: $\text{CHK} = \text{Device} \oplus \text{Subdevice} \oplus \text{Command}$ |

> [!NOTE]
> Each standard transmission frame consists of exactly **50 pulse burst-pairs** (1 lead-in pair + 48 data bit pairs + 1 lead-out pair), formatted as **100 hex words** preceded by the 4-word Pronto header (`0000 0070 0000 0032`), totaling exactly **104 space-delimited hex words**.

---

## 3. Remote Control Hardware Requirements

Depending on the operation being executed, the following remote control transmitters are utilized:

### 3.1 Standard Physical Remote (EUR7729KG0)
- **Included With**: DMR-EH55, DMR-EH55S, DMR-EH55P.
- **Transmitter Type**: Standard handheld consumer IR remote emitting Kaseikyo 48-bit codes.
- **Capabilities**: Navigating standard disc menus, playback deck, timer programming, manual skip (+30s), replay (-10s), time slip, and numeric entry (1 to 0).
- **Limitations**: **Lacks discrete service keys**. The physical remote cannot transmit non-standard commands (such as `MEDIA SERVER / 0x04` or discrete power-on/off codes), making standalone execution of advanced multi-region hacks impossible on the original plastic remote alone.

### 3.2 Programmable Android Consumer IR Transmitter (SM-T805M / DIGA Commander)
- **Hardware**: Samsung Galaxy Tab S 10.5 LTE (SM-T805M) or Galaxy S4/S5/Note 4 featuring a built-in hardware Consumer IR (CIR) LED emitter (`android.hardware.ConsumerIrManager`).
- **Software**: **Diga Commander** (Native Kotlin Jetpack Compose app) or **irplus** using the supplied `Panasonic_DMR_EH55_Commander.irplus` profile.
- **Capabilities**: Capable of transmitting all 52 discrete Panasonic IR commands, as well as chaining automated macro pipelines with strict monotonic microsecond/millisecond delays.

---

## 4. Master Catalog of Service Functions & Hacks

### 4.1 Multi-Region (DVD Region Free / Region 0) Unlock Hack

| Property | Specification |
|:---|:---|
| **Target Function** | Permanent Multi-Region (Region Free / Region 0) DVD-Video Playback |
| **Required Remote** | Programmable IR Blaster (Android CIR Blaster running DIGA Commander / irplus) |
| **Initial Unit State** | Unit powered **ON**; Optical Tray **OPEN**; Tray completely **EMPTY** (no disc loaded) |
| **Exact Key Sequence** | `1` $\to$ `2` $\to$ `3` $\to$ `4` $\to$ `5` $\to$ `6` $\to$ `7` $\to$ `8` $\to$ `9` $\to$ `0` $\to$ `AUDIO` $\to$ `MEDIA SERVER` |
| **Monotonic Delay** | Rigid **500 ms** pause between every single command |

#### Internal Microcontroller Operation
1. The Panasonic SuperH SH-4 operating system monitors infrared interrupts while the tray switch indicates `TRAY_OPEN` and the optical sled limit switch is engaged.
2. Ingesting the 10-digit ascending sequence `0x10..0x19` fills the temporary engineering command buffer.
3. Transmitting `AUDIO` (`0x33`) primes the regional override subroutine.
4. Transmitting the discrete `MEDIA SERVER` pulse (`Command 0x04`, `Checksum 0xB4`) immediately triggers the firmware EEPROM write routine.
5. The CPU clears the regional lockout byte in the non-volatile EEPROM from its factory setting (`0x01` USA/Canada, `0x02` Europe/Japan, `0x04` Latin America/Australia) to **`0x00` (All Regions / Region Free)**.

#### VFD Display Readout & Effect
- The Front Panel Vacuum Fluorescent Display (VFD) flashes momentarily or remains on `OPEN`.
- **Verification**: Close the tray and enter Service Mode. Transmit `0` followed by `2`. The display will read `00`, confirming successful region unlock.
- **Operational Result**: The unit permanently plays DVD-Video titles encoded for Region 1, 2, 3, 4, 5, and 6 without regional mismatch errors (`U72` / "This disc cannot be played in this region").

---

### 4.2 Service Mode Entry (Diagnostic Self-Check)

| Property | Specification |
|:---|:---|
| **Target Function** | Enter Low-Level Service Diagnostic State |
| **Required Remote** | Original Remote (EUR7729KG0) or Android DIGA Commander |
| **Initial Unit State** | Unit powered **ON**; Deck in **STOP** mode; Optical Tray closed and empty |
| **Method A (Front Panel)**| On the physical front panel of the recorder, press and hold **`[STOP]`**, and simultaneously press and hold **`[PLAY ▶]`** for at least **5 seconds**. |
| **Method B (Standby Entry)**| With unit in **Standby** (Off), press and hold **`[STOP]`** on front panel, and press **`[CH ▲]`** on front panel for at least **5 seconds**. |

#### Internal Operation & Result
- The system controller pauses background tuner scheduling, inhibits AV recording, and puts the front display driver into raw hexadecimal/alphanumeric telemetry mode.
- The VFD displays `SERVICE`, `00_000`, or a blinking cursor awaiting a 2-digit diagnostic code from the remote control.

---

### 4.3 Optical Pickup Laser Operating Hours Counter (Code 41)

| Property | Specification |
|:---|:---|
| **Target Function** | Inquire Cumulative Laser Diode Emission Time |
| **Required Remote** | Numeric Keypad (`4` then `1`) |
| **Initial Unit State** | Active in **Service Mode** (Section 4.2) |
| **Key Sequence** | Send `DIGIT_4` (`0x13`), wait 500 ms, send `DIGIT_1` (`0x10`) |

#### Internal Microcontroller Operation
- Reads the non-volatile 16-bit cumulative runtime register dedicated to the laser optical pick-up unit (OPU) laser diode emission time from the EEPROM.

#### VFD Display Readout & Diagnostic Significance
- The VFD display formats the readout as **`L****`**, where `****` represents the elapsed operating hours in decimal.
  - *Example*: **`L0185`** indicates **185 hours** of cumulative laser diode firing time.
  - *Threshold Guide*:
    - `< 500 hours`: Excellent optical condition (pristine OPU).
    - `1,000 – 2,500 hours`: Moderate wear; laser current auto-gain compensation active.
    - `> 3,500 hours`: High degradation risk; optical focus/tracking failures (`U11`, `H02`) may become frequent on DVD-R/RAM media.

---

### 4.4 DVD-RAM Optical Drive Last Error Diagnosis (Code 42)

| Property | Specification |
|:---|:---|
| **Target Function** | Inquire Last Optical Drive Hardware Error |
| **Required Remote** | Numeric Keypad (`4` then `2`) |
| **Initial Unit State** | Active in **Service Mode** |
| **Key Sequence** | Send `DIGIT_4` (`0x13`), wait 500 ms, send `DIGIT_2` (`0x11`) |

#### Internal Operation & Diagnostic Significance
- Queries the internal DVD-RAM drive subsystem microprocessor over the ATAPI bridge.
- The VFD outputs the last recorded optical drive mechanism error (focus error, tracking error, spindle servo lock failure, or sled motor limit timeout).
- If no optical drive failure is recorded, the display indicates `0000` or `NO ERR`.

---

### 4.5 General Fault History Diagnosis — Latest Error (Code 01)

| Property | Specification |
|:---|:---|
| **Target Function** | Inquire Most Recent System Fault Code Recorded in EEPROM |
| **Required Remote** | Numeric Keypad (`0` then `1`) |
| **Initial Unit State** | Active in **Service Mode** |
| **Key Sequence** | Send `DIGIT_0` (`0x19`), wait 500 ms, send `DIGIT_1` (`0x10`) |

#### Internal Operation & Diagnostic Significance
- Reads the head of the non-volatile cyclic fault ring buffer.
- The VFD immediately displays the last fatal or non-fatal self-diagnostic fault code that caused the machine to halt, reboot, or display an alert:
  - **`U**`**: User alert (dirty disc, temperature warning, copy-protection lockout).
  - **`H**`**: Mechanical or servo mechanism failure (tray jammed, spindle motor stalled).
  - **`F**`**: Microprocessor, communication, or power rail failure (communication bus lockup, system power down).
- Refer to Section 5 for the comprehensive diagnostic code reference table.

---

### 4.6 Extended 19-Slot EEPROM Historical Error Buffer Dump (Code 65)

| Property | Specification |
|:---|:---|
| **Target Function** | Step Through Historical Error Buffer (Last 19 Fault Events) |
| **Required Remote** | Numeric Keypad |
| **Initial Unit State** | Active in **Service Mode** |
| **Key Sequence** | Transmit `DIGIT_6` + `DIGIT_5` (Mode 65), followed by Slot number `01` to `19` |
| **Macro Pipeline** | `[6] -> [5] -> [0] -> [1]` (Slot 1: Most Recent Error) <br> `[6] -> [5] -> [0] -> [2]` (Slot 2: Second Prior Error) <br> ... up to Slot 19 |

#### Internal Operation & Diagnostic Significance
- Panasonic DIGA firmware stores an internal log of up to **19 consecutive failure events** in non-volatile memory, preserved across power disconnects.
- Inspecting these slots allows an engineer to determine whether an intermittent hardware failure (e.g., `F54` HDD communication timeout) occurred repeatedly during unattended timer recordings.

---

### 4.7 Clear Error Log / Reset Error Register (Code 98)

| Property | Specification |
|:---|:---|
| **Target Function** | Reset Current Active Error Flag to F00 |
| **Required Remote** | Numeric Keypad (`9` then `8`) |
| **Initial Unit State** | Active in **Service Mode** |
| **Key Sequence** | Send `DIGIT_9` (`0x18`), wait 500 ms, send `DIGIT_8` (`0x17`) |

#### Internal Operation & Result
- Clears the active error register in RAM and writes `0x00` (`F00`) to the primary telemetry block.
- The VFD displays **`F00`**, verifying that the pending error flag has been cleared.
- Essential for unlocking the unit when a temporary mechanical timeout (such as a dirty drive belt causing tray hesitation) put the firmware into a protective lockout loop.

---

### 4.8 Destination Model Code & Region Verification (Code 02)

| Property | Specification |
|:---|:---|
| **Target Function** | Verify Regional Setting & Factory Destination Byte |
| **Required Remote** | Numeric Keypad (`0` then `2`) |
| **Initial Unit State** | Active in **Service Mode** |
| **Key Sequence** | Send `DIGIT_0` (`0x19`), wait 500 ms, send `DIGIT_2` (`0x11`) |

#### Internal Operation & Result
- Reads the hardware destination code programmed during factory manufacturing.
- The VFD displays the active region identifier:
  - **`00`**: Region Free (Multi-Region Hack Active).
  - **`01`**: North America (Region 1, NTSC).
  - **`02`**: Europe, United Kingdom, Japan (Region 2, PAL / NTSC).
  - **`03`**: Southeast Asia, Hong Kong (Region 3, NTSC).
  - **`04`**: Latin America, Australia (Region 4, NTSC / PAL).

---

### 4.9 HDD Surface & SMART Sector Integrity Scan (Code 31)

| Property | Specification |
|:---|:---|
| **Target Function** | Initiate Internal IDE Hard Drive Surface Read & Sector Scan |
| **Required Remote** | Numeric Keypad (`3` then `1`) |
| **Initial Unit State** | Active in **Service Mode** |
| **Key Sequence** | Send `DIGIT_3` (`0x12`), wait 500 ms, send `DIGIT_1` (`0x10`) |

#### Internal Microcontroller Operation
- Issues an ATA diagnostic command directly to the internal IDE/EIDE hard drive (factory 200 GB Western Digital / Seagate drive).
- The ATA controller scans the physical platters, reading sector checksums and triggering the drive's firmware to reallocate bad sectors to the spare G-List table.

#### VFD Display Readout & Result
- The display flashes **`31 TEST`** or **`SCAN HDD`** for several seconds up to several minutes depending on test depth.
- Upon completion, the display returns:
  - **`HDD OK`**: Drive passed SMART health checks and surface read tests.
  - **`HDD NG`** (No Good): Drive has uncorrectable bad sectors, head degradation, or ATA communication timeouts.

---

### 4.10 VFD Vacuum Fluorescent Display 100% Segment Test (Code 00)

| Property | Specification |
|:---|:---|
| **Target Function** | Illuminate All VFD Grids, Phosphor Segments & Icons |
| **Required Remote** | Numeric Keypad (`0` then `0`) |
| **Initial Unit State** | Active in **Service Mode** |
| **Key Sequence** | Send `DIGIT_0` (`0x19`), wait 500 ms, send `DIGIT_0` (`0x19`) |

#### Internal Operation & Result
- Powers on all anode grids and cathode filaments simultaneously at full duty cycle.
- All display segments, progress bars, clock digits, recording indicators (`● REC`, `HDD`, `DVD`, `SD`, `XP`, `SP`, `LP`, `EP`, `HDMI`) light up at maximum brightness.
- Allows immediate visual identification of burned-out display segments, dimmed phosphor wear patterns, or malfunctioning VFD driver IC channels.

---

### 4.11 Video Encoder / DAC Bus Test Patterns (Codes 80 & 81)

| Property | Specification |
|:---|:---|
| **Target Function** | Output Diagnostic Color Bars & DAC Test Patterns Without Disc |
| **Required Remote** | Numeric Keypad (`8` then `0` / `8` then `1`) |
| **Initial Unit State** | Active in **Service Mode** |
| **Key Sequence** | Transmit `DIGIT_8` (`0x17`) + `DIGIT_0` (`0x19`) or `DIGIT_8` + `DIGIT_1` (`0x10`) |

#### Internal Operation & Result
- Instructs the internal MPEG video encoder and Digital-to-Analog Converters (DACs) to generate synthesized video test patterns directly onto Component ($YPbPr$), S-Video, and Composite analog video outputs.
- Used on analog video test benches to calibrate oscilloscope video levels ($1.0\,\text{V}_{\text{p-p}}$ termination, $75\,\Omega$) and chroma subcarrier burst without needing a test DVD disc.

---

### 4.12 Disc Tray Loading Mechanical Endurance Cycle (Code 91)

| Property | Specification |
|:---|:---|
| **Target Function** | Stress-Test Optical Drive Loading Motor & Limit Microswitches |
| **Required Remote** | Numeric Keypad (`9` then `1`) |
| **Initial Unit State** | Active in **Service Mode**; Optical tray empty |
| **Key Sequence** | Send `DIGIT_9` (`0x18`), wait 500 ms, send `DIGIT_1` (`0x10`) |

#### Internal Operation & Result
- The system controller engages the tray loading DC motor driver IC into an infinite mechanical test cycle:
  $$\text{Open Tray} \longrightarrow \text{Dwell 3 seconds} \longrightarrow \text{Close Tray} \longrightarrow \text{Engage Spindle Clamp} \longrightarrow \text{Dwell 2 seconds} \longrightarrow \text{Repeat}$$
- Allows verification of the tray drive belt tension, loading gears, and open/close limit microswitches.
- **Exit**: Transmit `STOP` (`0x00`) to halt the cycling routine.

---

### 4.13 Remote Control Code Synchronization (SET 1 / SET 2 / SET 3 Unlocking)

| Property | Specification |
|:---|:---|
| **Target Function** | Synchronize Transmitter Remote ID with Receiver Setup |
| **Symptom** | Display flashes **`SET 1`**, **`SET 2`**, or **`SET 3`** and rejects all commands |
| **Root Cause** | The recorder's remote channel was changed in the Setup menu to avoid interference with other Panasonic gear |
| **Resolution Macro** | Transmit continuous pulses of the matching channel digit (`1`, `2`, or `3`) with `OK/ENTER` |

#### Channel Mapping
- **Display reads `SET 1`**: Transmit `DIGIT_1` repeatedly with `ENTER` $\longrightarrow$ Reconfigures transmitter to DVD 1.
- **Display reads `SET 2`**: Transmit `DIGIT_2` repeatedly with `ENTER` $\longrightarrow$ Reconfigures transmitter to DVD 2.
- **Display reads `SET 3`**: Transmit `DIGIT_3` repeatedly with `ENTER` $\longrightarrow$ Reconfigures transmitter to DVD 3.

---

### 4.14 Front Panel Child Lock Emergency Unlock (X-HOLD / LOCKED)

| Property | Specification |
|:---|:---|
| **Target Function** | Unlock Disabled Front Panel Push-Buttons |
| **Symptom** | Pressing any physical button on the recorder displays **`LOCKED`** or **`X-HOLD`** |
| **Root Cause** | Child lock security lockout was engaged via the remote control |
| **Emergency Sequence** | Transmit a sustained pulse of **`ENTER_OK`** (`0x82`) for **5.0 continuous seconds** (10 pulses at 450 ms interval) |

#### Internal Operation & Result
- The system controller deactivates the front panel input interrupt inhibition flag.
- The display flashes `OFF` and returns to normal, restoring full operation to front panel buttons.

---

### 4.15 Total NVRAM Initialization / Factory Hard Reset (Code 99)

| Property | Specification |
|:---|:---|
| **Target Function** | Full Factory Reset of EEPROM, User Settings, Channel Tables & Timers |
| **Required Remote** | Numeric Keypad (`9` then `9`) in Service Mode, or Front Panel Emergency |
| **Method A (Service Mode)**| In Service Mode, transmit `DIGIT_9` (`0x18`) + `DIGIT_9` (`0x18`) |
| **Method B (Front Panel)**| While unit is powered **ON** and in **STOP**, press and hold **`[STOP]`** and **`[REC ●]`** on the front panel simultaneously for **5.0 seconds** |

#### Internal Operation & Result
- Clears the internal NVRAM storage partition:
  - Erases all scheduled timer recordings.
  - Clears TV Guide On Screen (TVGOS) EPG database.
  - Resets all video picture settings, Black Level Expansion, and audio dynamic range settings.
  - Restores remote control channel to Default Channel 1.
- The VFD displays **`INIT`** or **`CLEAR`**, and the unit immediately shuts down into Standby.

---

### 4.16 TV Guide On Screen (TVGOS) HDD Wear Elimination Hack (Zip Code 00000)

| Property | Specification |
|:---|:---|
| **Target Function** | Disable Nightly EPG Polling to Eliminate Hard Drive Wear and Noise |
| **Root Cause** | Analog broadcast TV Guide On Screen transmissions were permanently discontinued. The firmware attempts to power on the IDE hard drive every night at 2:00 AM and 3:00 AM, scanning analog tuner frequencies for hours searching for non-existent guide data packets, causing severe bearing wear and noise. |
| **Resolution Sequence**| Navigate to `FUNCTIONS` $\to$ `To Others` $\to$ `Setup` $\to$ `TV Guide` $\to$ `Zip Code` $\to$ Inject **`00000`** $\to$ `ENTER` |

#### Internal Operation & Result
- Entering Zip Code `00000` signals the TVGOS daemon that the unit is outside the broadcast zone.
- The firmware permanently disables the 2:00 AM / 3:00 AM background wake-up routines, silencing the hard drive and dramatically extending the lifetime of the IDE disk.

---

## 5. Panasonic DIGA Self-Diagnostic Error Code Reference Table

When querying error registers via Code `01` or Code `65`, the following table decodes the VFD telemetry:

### 5.1 User & Environmental Alerts (`U**`)
| Code | Failure Classification | Root Cause & Diagnostic Remedy |
|:---:|:---|:---|
| **`U11`** | Disc Read Error / Dirty Lens | The optical pick-up failed to focus on the lead-in track. Disc is scratched, dirty, upside down, or OPU lens requires cleaning with isopropyl alcohol. |
| **`U12`** | Disc Clamping Error | Disc clamp mechanism failed to seat properly over the spindle hub magnet. Check tray loading gears. |
| **`U14`** | Hard Drive Full / Record Inhibit | Hard drive partition has zero remaining recording blocks, or max title limit (999 titles) reached. |
| **`U30`** | Remote Control Code Mismatch | The remote control channel does not match the receiver. Refer to Section 4.13 for synchronization. |
| **`U59`** | Internal Thermal Overload | Internal temperature sensor exceeded safe threshold. The cooling fan is stalled, exhaust port is blocked, or thermistor circuit is faulty. Unit halts recording to protect HDD. |
| **`U72`** | Regional Coding Mismatch | The loaded DVD-Video disc region does not match the player. Refer to Section 4.1 for Multi-Region unlock. |
| **`U73`** | HDMI Handshake Failure | HDCP authentication failure with connected display device. Reseat HDMI cable or power cycle TV. |
| **`U99`** | P-Bus / Communication Timeout | Communication between main CPU and digital signal processor timed out. Unit initiates auto-reboot. |

---

### 5.2 Mechanical & Servo Drive Faults (`H**`)
| Code | Failure Classification | Root Cause & Diagnostic Remedy |
|:---:|:---|:---|
| **`H01`** | Tray Loading Motor Timeout | Tray mechanism failed to open or close within the timeout period. Worn loading belt, broken drive gear, or dirty open/close limit switch. |
| **`H02`** | Optical Spindle Motor Stalled | Spindle motor failed to reach rated speed or FG frequency generator pulses stopped. Defective spindle motor or dried bearing lubricant. |
| **`H03`** | Optical Sled Tracking Timeout | Optical pickup carriage failed to return to inner home position switch (`SLED_HOME`). Clean and re-lubricate sled worm gear. |
| **`H04`** | Focus Tracking Tracking Error | Laser focus actuator or tracking coil open/shorted. Replace optical pick-up block. |
| **`H05`** | Spindle Clamping Sensor Error | Optical clamp sensor failed to register down position. |
| **`H07`** | Fan Motor Stalled | Rear cooling fan tachometer pulses ceased. Check fan connector or replace $12\,\text{V}$ brushless DC fan. |

---

### 5.3 System Controller & Power Rail Faults (`F**`)
| Code | Failure Classification | Root Cause & Diagnostic Remedy |
|:---:|:---|:---|
| **`F00`** | Normal Operation | No error stored in memory. |
| **`F54`** | ATA / HDD Communication Fault | The SuperH CPU ATA host controller failed to communicate with the internal IDE hard drive. Faulty IDE flat ribbon cable, dead HDD PCB, or degraded electrolytic capacitors on $+12\,\text{V}/+5\,\text{V}$ power supply rails. |
| **`F56`** | DVD-RAM ATAPI Bus Timeout | ATAPI interface to optical drive controller failed to respond during initialization. |
| **`F57`** | MPEG AV Encoder Communication | Digital video processing LSI failed I²C initialization handshake. |
| **`F99`** | Main System Microprocessor Lockup | Fatal CPU exception or watchdog timer reset triggered. Power supply voltage ripple or corrupted NVRAM block. Execute Factory Hard Reset (Section 4.15). |

---

## 6. Complete 52-Command Panasonic IR Quick Reference Table

All commands use Matsushita Kaseikyo 48-bit with **Device `0xB0`** and **Subdevice `0x00`** modulated at **37.010 kHz** (`0070`).

| Constant Identifier | Command (Hex) | Checksum (Hex) | Physical Remote Equivalent | Primary Function Description |
|:---|:---:|:---:|:---|:---|
| `POWER` | `0x3D` | `0x8D` | Power ⏻ | Toggle Main Power (Standby / On) |
| `POWER_ON` | `0x3E` | `0x8E` | Discrete | Discrete Power ON Only |
| `POWER_OFF` | `0x3F` | `0x8F` | Discrete | Discrete Power OFF (Standby) Only |
| `EJECT` | `0x39` | `0x89` | Open / Close ⏏ | Open or Close Optical Disc Tray |
| `STATUS` | `0x29` | `0x99` | Status | On-screen OSD telemetry display |
| `DISPLAY` | `0x92` | `0x22` | Display | Toggle on-screen playback bit-rate & time |
| `DRIVE_SELECT` | `0x2C` | `0x9C` | Drive Select | Cycle active drive: HDD $\to$ DVD $\to$ SD/MMC |
| `DIRECT_NAVIGATOR` | `0x9B` | `0x2B` | Direct Navigator | Open recorded title thumbnail index |
| `FUNCTIONS` | `0x57` | `0xE7` | Functions | Open DIGA main graphical setup menu |
| `TOP_MENU` | `0x25` | `0x95` | Top Menu | Jump to DVD-Video root title menu |
| `OPTION_SUB_MENU` | `0x80` | `0x30` | Option | Open context submenu for active title |
| `UP` | `0x85` | `0x35` | Navigation ▲ | Cursor navigation Up |
| `DOWN` | `0x86` | `0x36` | Navigation ▼ | Cursor navigation Down |
| `LEFT` | `0x87` | `0x37` | Navigation ◀ | Cursor navigation Left |
| `RIGHT` | `0x88` | `0x38` | Navigation ▶ | Cursor navigation Right |
| `ENTER_OK` | `0x82` | `0x32` | OK / Enter | Confirm selection / execute menu item |
| `RETURN` | `0x81` | `0x31` | Return | Return to previous menu level |
| `EXIT` | `0x51` | `0xE1` | Exit | Clear all on-screen graphical menus |
| `GUIDE` | `0x60` | `0xD0` | Guide | Open TV Guide / EPG programming grid |
| `PROG_CHECK` | `0x7C` | `0xCC` | Prog / Check | Display scheduled timer recording list |
| `PLAY` | `0x0A` | `0xBA` | Play ▶ | Begin playback of selected title / media |
| `STOP` | `0x00` | `0xB0` | Stop ■ | Halt playback or active recording |
| `PAUSE` | `0x06` | `0xB6` | Pause ❚❚ | Freeze playback or pause recording |
| `FAST_FORWARD` | `0x4A` | `0xFA` | Fast Forward ▶▶ | 5-speed progressive forward cue search |
| `REWIND` | `0x49` | `0xF9` | Rewind ◀◀ | 5-speed progressive reverse review search |
| `SKIP_NEXT` | `0x05` | `0xB5` | Next ▶\| | Skip forward to next chapter / title marker |
| `SKIP_PREV` | `0x04` | `0xB4` | Prev \|◀ | Skip backward to previous chapter marker |
| `TIME_SLIP` | `0x4B` | `0xFB` | Time Slip | Simultaneous playback of recorded stream |
| `MANUAL_SKIP_30S` | `0x4C` | `0xFC` | Manual Skip (+30s) | Instant commercial skip forward 30 seconds |
| `MANUAL_REPLAY_10S` | `0x8B` | `0x3B` | Manual Replay (-10s) | Instant instant replay backward 10 seconds |
| `CREATE_CHAPTER` | `0x58` | `0xE8` | Create Chapter | Insert manual chapter division mark |
| `DELETE_TITLE` | `0x44` | `0xF4` | Delete | Delete selected recorded title |
| `REC` | `0x08` | `0xB8` | Rec ● | Begin immediate recording on active drive |
| `REC_MODE` | `0x51` | `0xE1` | Rec Mode | Cycle encoding quality: XP $\to$ SP $\to$ LP $\to$ EP |
| `EXT_LINK` | `0x23` | `0x93` | Ext Link | Enable Scart/AV auto-record satellite sync |
| `INPUT_SELECT` | `0x50` | `0xE0` | Input Select | Cycle inputs: AV1 $\to$ AV2 $\to$ AV3 $\to$ AV4 $\to$ DV |
| `CH_UP` | `0x34` | `0x84` | Channel ▲ | Increment tuner channel |
| `CH_DOWN` | `0x35` | `0x85` | Channel ▼ | Decrement tuner channel |
| `AUDIO` | `0x33` | `0x83` | Audio | Cycle audio streams: Stereo $\to$ L $\to$ R $\to$ SAP |
| `SUBTITLE` | `0x71` | `0xC1` | Subtitle | Toggle on-screen closed captions / subtitles |
| `DIGIT_1` | `0x10` | `0xA0` | Number Key 1 | Numeric input 1 / Service code 1 |
| `DIGIT_2` | `0x11` | `0xA1` | Number Key 2 | Numeric input 2 / Service code 2 |
| `DIGIT_3` | `0x12` | `0xA2` | Number Key 3 | Numeric input 3 / Service code 3 |
| `DIGIT_4` | `0x13` | `0xA3` | Number Key 4 | Numeric input 4 / Service code 4 |
| `DIGIT_5` | `0x14` | `0xA4` | Number Key 5 | Numeric input 5 / Service code 5 |
| `DIGIT_6` | `0x15` | `0xA5` | Number Key 6 | Numeric input 6 / Service code 6 |
| `DIGIT_7` | `0x16` | `0xA6` | Number Key 7 | Numeric input 7 / Service code 7 |
| `DIGIT_8` | `0x17` | `0xA7` | Number Key 8 | Numeric input 8 / Service code 8 |
| `DIGIT_9` | `0x18` | `0xA8` | Number Key 9 | Numeric input 9 / Service code 9 |
| `DIGIT_0` | `0x19` | `0xA9` | Number Key 0 | Numeric input 0 / Service code 0 |
| `MEDIA_SERVER` | `0x04` | `0xB4` | Discrete Service Key | Service trigger byte for region unlock |
| `CANCEL` | `0x25` | `0x95` | Cancel / Clear | Cancel programmed timer entry |

---

## 7. Anti-Brick Engineering & Microcontroller Safety

The **Diga Commander** application architecture implements strict defensive mechanisms to prevent buffer overflows, frame collision, and microcontroller corruption:

1. **Thread-Safe Mutex (`kotlinx.coroutines.sync.Mutex`)**:
   - The IR transmitter operates within a strict mutex critical section. If an operator double-taps the display or fires multiple buttons simultaneously, incoming transmission requests are queued serially and never interleaved.
2. **500 ms Monotonic Microcontroller Recovery Delay**:
   - The Panasonic SuperH SH-4 CPU processes incoming IR signals via an interrupt-driven state machine. Ingesting multiple complex 48-bit codes without sufficient dwell time causes buffer truncation in the receiver IC (`IC1101`).
   - The Diga Commander engine enforces an uncompromised **500 ms delay** after each transmission step, ensuring the device has fully decoded and stored each digit before the next pulse train arrives.
3. **Reactive Cancellation**:
   - Macro pipelines evaluate `CoroutineScope.isActive` between every step. If the technician touches "Cancelar" on screen, the pipeline halts immediately prior to emitting the next burst, preventing incomplete parameter sequences.
