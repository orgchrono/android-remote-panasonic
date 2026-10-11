# 🗺️ Panasonic DIGA Multi-Model Expansion Plan & Engineering Dossier

This document establishes the in-depth technical research, signal engineering, and architectural roadmap enabling **Panasonic DIGA Commander** to support dozens of Panasonic DIGA recorders and players spanning multiple hardware generations (**DMR-E**, **DMR-ES**, **DMR-EH**, **DMR-EX**, **DMR-EZ**, **VHS/DVD Combos**, and **Blu-ray DMR-BW** lines).

---

## 1. Mathematical Fundamentals of the 48-Bit Kaseikyo Protocol

All Panasonic optical disc recorders and players manufactured between 1998 and 2018 implement the Matsushita flavor of the Japanese **Kaseikyo 48-bit** infrared transmission standard with a nominal carrier frequency of **$37.010\text{ kHz}$**.

The universal IRP (Infra-Red Protocol) notation for Panasonic DIGA transmissions is:
$$\{37\text{k}, 432\}<1, -1, 1, -3>(8, -4, 2:8, 32:8, D:8, S:8, F:8, (D \oplus S \oplus F):8, 1, -173)+$$

### 1.1 48-Bit Frame Anatomy
Each transmission frame consists of 6 data bytes transmitted LSB-first (Least Significant Bit first), totaling 24 Mark/Space burst pairs (48 data bit intervals) preceded by 1 lead-in pair and followed by 1 trailing stop pulse = **100 alternating pulse transitions**:

| Byte Position | Field Description | Bit Width | Fixed / Dynamic Value | Engineering Function |
| :--- | :--- | :--- | :--- | :--- |
| **Byte 1** | OEM Manufacturer Low | 8 bits | `0x02` (decimal 2) | Matsushita / Panasonic vendor identifier |
| **Byte 2** | OEM Manufacturer High | 8 bits | `0x20` (decimal 32) | Matsushita Kaseikyo identifier |
| **Byte 3** | **Device Code ($D$)** | 8 bits | `0xB0` (176) or `0x90` (144) | Primary deck hardware target address |
| **Byte 4** | **Subdevice Code ($S$)** | 8 bits | `0x00`, `0x10`, or `0x20` | Remote pairing channel (SET 1, 2, or 3) |
| **Byte 5** | **Command ($F$)** | 8 bits | `0x00` through `0xFF` | Original Button Code (OBC) |
| **Byte 6** | **Integrity Checksum** | 8 bits | $D \oplus S \oplus F$ | Strict bitwise XOR parity byte |

---

## 2. Decoded Remote Modes: SET 1, SET 2, and SET 3

A major source of confusion for DIGA owners and repair technicians occurs when a recorder displays **`SET 1`**, **`SET 2`**, or **`SET 3`** on its front VFD panel and refuses to respond to remote commands.

### Technical Root Cause:
Panasonic DIGA recorders modify their internal **Subdevice register ($S$)** when the user selects an alternate remote control channel in the system Setup menu (designed to prevent multiple Panasonic units in the same rack from responding simultaneously):

| VFD Display Status | Subdevice Hex ($S$) | Subdevice Decimal | Physical Remote Key Combination |
| :--- | :--- | :--- | :--- |
| **SET 1** (Factory Default) | `0x00` | 0 | Hold `ENTER / OK` + `1` for 5 seconds |
| **SET 2** | `0x10` | 16 | Hold `ENTER / OK` + `2` for 5 seconds |
| **SET 3** | `0x20` | 32 | Hold `ENTER / OK` + `3` for 5 seconds |

### 💡 Architectural Solution:
Rather than hardcoding Subdevice `0x00`, our pure functional protocol synthesizer (`KaseikyoProtocol.kt`) computes pulse timing dynamically for any target mode:
`[ MODE 1 (SET 1) ] | [ MODE 2 (SET 2) ] | [ MODE 3 (SET 3) ]`

This enables the operator to switch transmission channels instantaneously or trigger our **Automated 1-Touch OSD Recovery Macros** (`AUTO_RESTORE_SET2_TO_SET1` and `AUTO_RESTORE_SET3_TO_SET1`) to restore units trapped in Set 2 or Set 3 back to factory default Set 1 without manual OSD navigation!

---

## 3. Hardware Device Addressing Matrix ($D$)

| Target Mechanism / Unit Type | Device Code ($D$) | Typical Subdevice ($S$) | Representative Models |
| :--- | :--- | :--- | :--- |
| **DVD / HDD Deck (Standard DIGA)** | `0xB0` (176) | `0x00` / `0x10` / `0x20` | DMR-E50, E55, E85H, ES10, ES15, EH50, EH55, EH65, EX77, EZ48V |
| **VCR Deck (VHS/DVD Combos)** | `0x90` (144) | `0x00` / `0x10` / `0x20` | DMR-ES30V, ES35V, EH75V, EZ47V, EZ48V, NV-VP30, NV-VP31, NV-VP33 |
| **Panasonic Television Control** | `0x01` (1) or `0x08` (8) | `0x00` | Front `TV POWER`, `TV VOL +/-`, `TV AV` top remote buttons |
| **Blu-ray DIGA Units (Extension)** | `0xB0` (176) | `0x00` (Extended OBCs) | DMR-BW500, DMR-BWT700, DMR-BZT710 |

---

## 4. Taxonomy of Panasonic DIGA Generations (2001 – 2012)

### Family 1: DMR-E Series (Generations 1 & 2 — 2001 to 2004)
* **Representative Models:**
  * *DVD-RAM / DVD-R Standalones:* DMR-E20, DMR-E30, DMR-E50, DMR-E55, DMR-E60, DMR-E65.
  * *DVD + HDD Flagships:* DMR-E80H (40GB), DMR-E85H (80GB), DMR-E95H (160GB), DMR-E100H (120GB).
* **Engineering Notes:**
  * Support for DVD-RAM (Type 1 caddy & Type 2/4 bare discs) and DVD-R.
  * Dedicated transport keys: `TIME SLIP` (Chasing Playback on DVD-RAM), `F-REC` (Flexible Recording rate calculation), `CM SKIP`.
  * **Service Bench Codes:** Identical service routines (`41` laser hours, `01` fault recall, `02` region, `99` master clear).
  * *Bench Alert:* Electrolytic capacitor `C1601` on the DMR-E85H switch-mode power supply frequently develops high ESR, causing the notorious "PLEASE WAIT" boot loop.

### Family 2: DMR-ES Series (TBC Pass-Through Champions — 2005 to 2006)
* **Representative Models:**
  * DMR-ES10, DMR-ES15, DMR-ES20, DMR-ES25.
* **Significance in VHS Digitization & Archival:**
  * The **DMR-ES10** and **DMR-ES15** are universally celebrated across archival communities (*DigitalFAQ*, *VideoHelp*).
  * Equipped with the proprietary **Matsushita MN673794 (DVP)** processor featuring **full-frame Line TBC (Time Base Correction)** and an aggressive horizontal jitter synchronizer on analog inputs (LINE 1 / LINE 2).
  * Widely utilized as transparent pass-through stabilizers:
    $$\text{VCR (Composite/S-Video)} \longrightarrow \text{DMR-ES10/15} \longrightarrow \text{Capture Card (S-Video/Component)}$$
* **Key Commands:**
  * `INPUT SELECT` (Toggle Line 1, Line 2, DV).
  * `STATUS` and `DISPLAY` (Verify input field frequency and aspect ratio).
  * `FUNCTIONS` (Configure Line-in Noise Reduction - Line-in NR).

### Family 3: DMR-EH Series (Flagship HDD + DVD Workhorses — 2005 to 2008)
* **Representative Models:**
  * DMR-EH50, DMR-EH52, DMR-EH55, DMR-EH60, DMR-EH65, DMR-EH67, DMR-EH68, DMR-EH69.
* **Engineering Notes:**
  * Hard drives from 80 GB to 250 GB formatted under Panasonic's proprietary **MEIHDFS** filesystem.
  * SD Card slot for JPEG picture viewing and transfer.
  * HDMI 1080p upscaling video output (EH65, EH67, and later models).
  * Most complete editing toolset: `CREATE CHAPTER`, `DELETE TITLE`, `MANUAL SKIP (+30s)`, `MANUAL REPLAY (-10s)`.
  * Fully supports the **12-step Multi-Region Hack** and **TVGOS 00000 Zip Code Bypass**.

### Family 4: DMR-EX and DMR-EZ Series (Digital DVB-T / ATSC Tuners — 2006 to 2010)
* **Representative Models:**
  * *Europe (DVB-T Freeview):* DMR-EX75, DMR-EX77, DMR-EX85, DMR-EX87, DMR-EX768, DMR-EX79, DMR-EX89.
  * *Americas (ATSC / Clear QAM):* DMR-EZ27, DMR-EZ28, DMR-EZ37V, DMR-EZ47V, DMR-EZ48V, DMR-EZ49V.
* **Additional Commands:**
  * `GUIDE` (7-day Digital Electronic Program Guide).
  * `DIGITAL / ANALOG` (Switch between RF tuners).
  * `HDMI RESOLUTION` (Toggle 576p, 720p, 1080i, 1080p).
  * `VIERA Link` (HDMI CEC system coordination).

### Family 5: Combo VHS + DVD Dual-Deck Units (Direct Archiving Stations)
* **Representative Models:**
  * NV-VP30, NV-VP31, NV-VP32, NV-VP33 (VCR/DVD combo players).
  * DMR-E75V, DMR-ES30V, DMR-ES35V (DVD recorder + Hi-Fi VCR).
  * DMR-EH75V (Triple combo: 80GB HDD + DVD recorder + Hi-Fi VCR).
  * DMR-EZ47V, DMR-EZ48V (DVB/ATSC digital tuner + HDMI + DVD + VCR).
* **Dual Addressing Architecture:**
  * The chassis incorporates two independent logical IR decoders:
    * DVD/HDD commands respond to Device **`0xB0`**.
    * VCR deck commands respond to Device **`0x90`**.
  * **Dedicated VCR Deck Commands:**
    * `VCR PLAY`, `VCR STOP`, `VCR PAUSE`, `VCR REW`, `VCR FF`, `VCR REC`.
    * `TRACKING +` and `TRACKING -` (Tape head alignment fine-tuning).
    * `ONE-TOUCH DUBBING: VHS ➔ DVD` (Direct hardware dubbing from tape to disc).
    * `ONE-TOUCH DUBBING: DVD ➔ VHS` (Direct hardware dubbing from disc to tape).
    * `DRIVE SELECT: VHS` (Shifts operational display focus to VHS deck).

---

## 5. Clean Architecture Implementation in Kotlin

### 5.1 Pure Functional Protocol Synthesizer (`KaseikyoProtocol.kt`)
Referentially transparent pulse generator computing bit-accurate pulse arrays and Pronto Hex strings:

```kotlin
object KaseikyoProtocol {
    const val CARRIER_FREQUENCY_HZ = 37010

    fun synthesizePulsePattern(
        device: Int = DigaDevice.DVD_HDD.id,
        mode: RemoteMode = RemoteMode.MODE_1,
        command: Int
    ): IntArray { ... }

    fun synthesizeProntoHex(
        device: Int = DigaDevice.DVD_HDD.id,
        mode: RemoteMode = RemoteMode.MODE_1,
        command: Int
    ): String { ... }
}
```

### 5.2 Model Profiles (`DigaModelProfile.kt` & `RemoteMode.kt`)
* **`DigaModelProfile`**: Supports `DMR_EH55`, `DMR_ES10_ES15`, `COMBO_VHS_DVD`, and `UNIVERSAL`.
* **`RemoteMode`**: Encapsulates `MODE_1` (`0x00`), `MODE_2` (`0x10`), and `MODE_3` (`0x20`).

### 5.3 Ready-to-Use XML Profiles (`profiles/`)
Four pre-configured XML profiles ready for immediate import into **irplus** or **IR Blaster Remote (iodn / GPL-3.0)**:
1. `profiles/Panasonic_DMR_EH55_Commander.irplus`
2. `profiles/Panasonic_DIGA_ES10_ES15_TBC.irplus`
3. `profiles/Panasonic_DIGA_Combo_VHS_DVD_EH75V_ES30V.irplus`
4. `profiles/Panasonic_DIGA_Universal_Modes_1_2_3.irplus`
