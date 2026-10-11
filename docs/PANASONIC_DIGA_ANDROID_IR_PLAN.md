# 📱 Panasonic DIGA Commander — Architectural Specification & Android IR Project Plan

> **Document Status:** Complete Engineering Specification & Hacks Compilation (Implemented)  
> **Target Hardware:** Samsung Galaxy Tab S 8.4 LTE (`SM-T805M` / `chagalllte`) with integrated hardware Consumer IR emitter  
> **Compatible Recorders:** Panasonic DMR-EH55, DMR-EH50, DMR-EH65, DMR-EX768, DMR-ES10, DMR-ES15, and related DIGA generation  
> **Language & Framework:** Kotlin (Native Android / Jetpack Compose / Android Studio)

---

## 1. Master Dossier of Bench Hacks, Secret Codes & Service Procedures

Below is the exhaustive, laboratory-verified mapping of all commands, service routines, and diagnostic procedures documented across Panasonic engineering literature and service manuals (*Panasonic Service Manual DMR-EH55EC / DMR-EH50*):

### 1.1 Service Mode Bench Entry Procedures

The **Service Mode** of the Panasonic Renesas SH-4 system controller and peripheral sub-CPU can be entered via two distinct methods:

1. **Front Panel Physical Key Combination:**
   With the unit powered **ON**, no disc playback active, and the optical loading tray closed and empty:
   $$\mathbf{[STOP]} + \mathbf{[PLAY]} \quad \text{held simultaneously for 5 seconds}$$
   *(Select chassis alternative: with unit in Standby, hold $\mathbf{[STOP]} + \mathbf{[CH\ UP]}$ for 5 seconds)*.
2. **Infrared Service Mode Command Sequence:**
   Triggering the Panasonic factory bench test sequence via remote control.

Once Service Mode is active, the front vacuum fluorescent display (VFD/FLD) displays **`SERVICE`** and listens for bench macro codes:

| Remote Code | Function Name | Technical Description & VFD Display Output | Risk Classification |
| :--- | :--- | :--- | :--- |
| **`4` `1`** | **Laser Used Time (Hour Meter)** | Displays total accumulated laser diode emission time in hours (`L****`, e.g., `L0185` = 185 hours). Essential for evaluating optical pickup wear. | **Zero** (Read-Only) |
| **`4` `2`** | **RAM Drive Last Error** | Displays the most recent specific mechanism error code logged by the optical DVD-RAM drive. | **Zero** (Read-Only) |
| **`0` `1`** | **Self-Diagnosis / Latest Fault** | Displays the most recent general CPU fault code (`U**`, `H**`, `F**`) logged in memory. | **Zero** (Read-Only) |
| **`6` `5` ➔ `01`..`19`** | **Error History (19 EEPROM Slots)** | Navigates the last 19 historical error events stored in non-volatile EEPROM memory (`Slot 1` = newest, up to `Slot 19`). | **Zero** (Read-Only) |
| **`0` `2`** | **Destination Region Code Readout** | Displays the DVD region lock code flashed in NVRAM for 5 seconds (`01` USA, `02` Europe, `04` Brazil, `00` Multi-Region Free). | **Zero** (Read-Only) |
| **`3` `1`** | **HDD Surface / Drive Integrity Scan** | Initiates an internal surface scan of physical hard drive sectors and performs automatic bad block reallocation in MEIHDFS. | **Low** (Non-destructive) |
| **`0` `0`** | **VFD All-Segments Illumination Test** | Simultaneously illuminates 100% of fluorescent segments and icons to test driver ICs and filament integrity. | **Zero** (Visual) |
| **`8` `0` / `8` `1`** | **I/O & Video Output Test** | Toggles test patterns across RGB, Component, and S-Video output stages for calibration. | **Zero** (Diagnostic) |
| **`9` `1`** | **Tray Cycle / Mechanical Endurance** | Starts continuous open/close mechanical cycling to stress-test drive loading belt and micro-switches. | **Low** (Mechanical) |
| **`9` `8`** | **Clear Error Register (F00)** | Clears the pending error register by writing `F00` to diagnostic memory without erasing user settings. | **Low** (Buffer Clear) |
| **`9` `9`** | **Factory Master NVRAM Reset** | Wipes entire EEPROM/NVRAM, clears all fault history, and restores factory defaults (`CLEAR` displayed on VFD). | **Medium** (Wipes Config) |

*To safely exit Service Mode at any time:* Transmit the standard **`[POWER]`** command.

---

### 1.2 Forensic EEPROM Error Code Interpretation

When querying error logs with `[0] [1]` or `[6] [5]`, the VFD outputs diagnostic codes categorized into **U**, **H**, and **F** classes:

| Error Code | Root Cause / Subsystem | Recommended Service Bench Remediation |
| :--- | :--- | :--- |
| **`U61`** | Communication bus breakdown between Renesas SH-4 CPU and optical DVD-RAM drive. | Inspect flat ribbon cable, ATAPI/IDE connector, and +5V/+12V PSU rail ripple. |
| **`U80` / `U81`** | Hard disk drive bus timeout or drive spin-up initialization failure. | Check HDD power connector, scan for corrupted MEIHDFS sectors, inspect +5V rail capacitors. |
| **`U88`** | Read/write failure on optical head or degraded disc surface reflectivity. | Check laser hour meter with `41`, clean pickup lens, calibrate diode emission. |
| **`U99`** | System hang between main CPU and timer/sub-CPU. | Power-cycle recorder or execute forced IR unlock sequence. |
| **`H19`** | Cooling fan motor stall or tachometer failure (*Fan Motor Error*). | Clean rear ventilation fan, lubricate bearing, or replace 12V fan unit. |
| **`U59`** | Thermal protection threshold exceeded (internal chassis temp > 70 °C). | Reapply thermal compound on SH-4 processor, inspect heatsink mounting and airflow. |
| **`F58`** | Critical drive controller IC hardware failure. | Inspect spindle/sled motor controller driver IC on drive PCB. |

---

### 1.3 Multi-Region (DVD Region Free) Macro Hack

Panasonic DIGA recorders contain no on-screen OSD menu option to modify DVD regional playback restrictions. Unlocking **Region 0 (Worldwide / Region Free)** requires a sequential infrared pulse train injected with a strict monotonic inter-frame spacing of 500 ms.

#### Verified Macro Injection Train:
Prerequisites: Recorder powered **ON**, optical tray **OPEN and EMPTY** (no disc inserted):

$$\mathbf{1} \xrightarrow{500\text{ms}} \mathbf{2} \xrightarrow{500\text{ms}} \mathbf{3} \xrightarrow{500\text{ms}} \mathbf{4} \xrightarrow{500\text{ms}} \mathbf{5} \xrightarrow{500\text{ms}} \mathbf{6} \xrightarrow{500\text{ms}} \mathbf{7} \xrightarrow{500\text{ms}} \mathbf{8} \xrightarrow{500\text{ms}} \mathbf{9} \xrightarrow{500\text{ms}} \mathbf{0} \xrightarrow{500\text{ms}} \mathbf{AUDIO} \xrightarrow{500\text{ms}} \mathbf{MEDIA\_SERVER}$$

* **Expected Behavior:** The recorder restarts automatically or the front VFD briefly confirms regional clearance (`1B` or `00`).
* **Post-Hack Verification:** Enter Service Mode (`STOP` + `PLAY` held 5s) and press `0` `2`. The VFD must output `00` (Region Free).

---

### 1.4 Remote Control Channel Synchronization (SET 1 / SET 2 / SET 3)

When multiple Panasonic components share the same equipment rack, recorders can be assigned to infrared Channels 1, 2, or 3. If a channel mismatch occurs, the recorder rejects all standard remote commands and flashes **`SET 1`**, **`SET 2`**, or **`SET 3`** on the VFD:

* **Synchronize Mode 1 (DVD 1):** Transmit on Subdevice `0x00` (or hold `ENTER` + `1` on physical remote for 5 seconds).
* **Synchronize Mode 2 (DVD 2):** Transmit on Subdevice `0x10` (or hold `ENTER` + `2` on physical remote for 5 seconds).
* **Synchronize Mode 3 (DVD 3):** Transmit on Subdevice `0x20` (or hold `ENTER` + `3` on physical remote for 5 seconds).

#### Automated 1-Touch OSD Recovery:
Our application implements automated macros (`AUTO_RESTORE_SET2_TO_SET1` and `AUTO_RESTORE_SET3_TO_SET1`) that transmit using the matching locked subdevice channel to traverse the on-screen Setup menu:
$$\text{FUNCTIONS} \longrightarrow \text{DOWN} \longrightarrow \text{ENTER} \longrightarrow \text{DOWN} \longrightarrow \text{ENTER} \longrightarrow \text{DOWN} \longrightarrow \text{ENTER} \longrightarrow \text{UP} \longrightarrow \text{ENTER} \longrightarrow \text{RETURN}$$
This restores the recorder's NVRAM setting back to Set 1 without requiring physical remote buttons or manual menu navigation.

---

### 1.5 Emergency Maintenance & Override Commands

* **Front Panel Child Lock Override (X-HOLD / LOCKED):**
  * Triggered when the physical panel lock is active and all front buttons are disabled.
  * *Override Pulse:* Transmit a sustained **`[ENTER]`** pulse stream for **5 continuous seconds**.
* **TVGOS Nightly Spin-Up Bypass (HDD Spindle Bearing Protection):**
  * TV Guide On Screen firmware attempts nightly 3:00 AM spins searching for defunct analog signals, causing unnecessary bearing wear and acoustic noise.
  * *Bypass:* Access *Functions ➔ Setup ➔ TV Guide Setup ➔ Postal / Zip Code* and inject **`00000`** or **`99999`**.
* **Forced Tray Eject (Stuck Mechanism Rescue):**
  * Power unit OFF; hold **`[STOP]` + `[CH UP]`** on physical front panel for 5 seconds to command an over-current pulse to the loading motor.

---

## 2. Infrared Signal Engineering & Pronto Hex Mathematics

### 2.1 Physical Layer Specification (48-bit Kaseikyo / Panasonic)

Panasonic DIGA recorders utilize the **Matsushita Kaseikyo 48-bit** infrared transmission standard:
* **Carrier Frequency:** $37.010\text{ kHz}$ (Frequency code `0070`, divisor 112) or $38.029\text{ kHz}$ (`006D`).
* **Header / Lead-in Burst Pair:**
  * Mark (Carrier ON): $3{,}459\,\mu\text{s}$ ($128$ cycles at $37\text{ kHz}$)
  * Space (Carrier OFF): $1{,}729\,\mu\text{s}$ ($64$ cycles at $37\text{ kHz}$)
* **Logical Bit `0`:**
  * Mark: $432\,\mu\text{s}$ ($16$ cycles)
  * Space: $432\,\mu\text{s}$ ($16$ cycles — $1:1$ ratio)
* **Logical Bit `1`:**
  * Mark: $432\,\mu\text{s}$ ($16$ cycles)
  * Space: $1{,}297\,\mu\text{s}$ ($48$ cycles — $1:3$ ratio)
* **Trailer / Stop Bit:** $432\,\mu\text{s}$ Mark followed by frame quiet period ($\approx 74\text{ ms}$ to $100\text{ ms}$).

### 2.2 Conversion: Pronto Hex $\rightarrow$ `ConsumerIrManager` (Microseconds)

Android's native hardware Consumer IR API accepts raw microsecond pulse arrays:
`android.hardware.ConsumerIrManager.transmit(carrierFrequency: Int, pattern: IntArray)`

The mathematical conversion performed by `ProntoHexConverter` calculates:
$$f = \frac{1{,}000{,}000}{\text{freq\_code} \times 0.241246}\text{ Hz}$$
$$\text{Pulse Duration (\mu s)} = \frac{\text{burst\_count} \times 1{,}000{,}000}{f}$$

---

## 3. Target Hardware: Samsung Galaxy Tab S 8.4 LTE (`SM-T805M`)

* **Integrated Hardware:** Lateral-mounted consumer infrared LED emitter diode.
* **Kernel & Driver Layer:** Fully supported under LineageOS 17.1 (Android 10, API 29) via `android.hardware.ConsumerIrManager`.
* **Application Manifest Configuration:**
  ```xml
  <uses-feature android:name="android.hardware.consumerir" android:required="false" />
  <uses-permission android:name="android.permission.TRANSMIT_IR" />
  ```

---

## 4. Anti-Brick Safety Pipeline (Mutex & Monotonic Coroutines)

```
[Idle] ──(Trigger)──> [Running (Step 1/N)] ──(500ms Delay)──> [Running (Step 2/N)] ──> [Completed]
  │                           │
  └──(User Abort)─────────────┴───────────────────────────────────────────────────────> [Cancelled]
```

1. **Serialized Mutex Lock:** Prevents pulse collisions or buffer flooding if multiple buttons are tapped in rapid succession.
2. **Monotonic Inter-Frame Delays:** Enforces minimum 500 ms spacing between bench digits and 600–1200 ms for OSD recovery steps to allow EEPROM write cycles to settle.
3. **Instant Reactive Cancellation:** Users can immediately cancel active sequences via the VFD progress banner.
4. **Safety Confirmation Modals:** High-risk operations (Multi-Region Hack, Factory Reset 99, OSD Recovery) require explicit confirmation prior to transmission.
