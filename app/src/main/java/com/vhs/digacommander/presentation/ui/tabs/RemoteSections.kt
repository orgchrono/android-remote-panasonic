package com.vhs.digacommander.presentation.ui.tabs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Input
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eject
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Forward30
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.vhs.digacommander.data.catalog.PanasonicDigaCatalog.OBC
import com.vhs.digacommander.data.model.RemoteMode
import com.vhs.digacommander.presentation.constant.DigaUiDimensions
import com.vhs.digacommander.presentation.constant.DigaUiStrings
import com.vhs.digacommander.presentation.theme.DigaAmber
import com.vhs.digacommander.presentation.theme.DigaBlue
import com.vhs.digacommander.presentation.theme.DigaBlueLight
import com.vhs.digacommander.presentation.theme.DigaOutline
import com.vhs.digacommander.presentation.theme.DigaSurfaceVariant
import com.vhs.digacommander.presentation.theme.DigaTextPrimary
import com.vhs.digacommander.presentation.ui.components.ChoiceRow
import com.vhs.digacommander.presentation.ui.components.DigaCard
import com.vhs.digacommander.presentation.ui.components.KeyStyle
import com.vhs.digacommander.presentation.ui.components.RemoteKey
import com.vhs.digacommander.presentation.ui.components.keyStyle
import com.vhs.digacommander.presentation.ui.components.textColor
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/** Callback signature shared by every key: telemetry label, Original Button Code, VCR deck flag. */
typealias SendCommand = (label: String, command: Int, isVcr: Boolean) -> Unit

/**
 * Declarative description of one remote key. Layout is data, not copy-pasted widget trees.
 *
 * @property commandLabel Text shown on the VFD telemetry while the pulse is sent.
 * @property weight Relative width inside its row.
 */
data class RemoteKeySpec(
    val label: String,
    val commandLabel: String,
    val obc: Int,
    val style: KeyStyle = KeyStyle.Neutral,
    val icon: ImageVector? = null,
    val iconOnly: Boolean = false,
    val weight: Float = 1f,
    val isVcr: Boolean = false,
)

/**
 * Renders [keys] as one evenly spaced row of [RemoteKey]s.
 */
@Composable
fun KeyRow(
    keys: List<RemoteKeySpec>,
    onSend: SendCommand,
    modifier: Modifier = Modifier,
    height: Dp = DigaUiDimensions.HeightKey,
    fontSize: TextUnit = DigaUiDimensions.TextSizeKey,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
    ) {
        keys.forEach { key ->
            RemoteKey(
                label = key.label,
                onClick = { onSend(key.commandLabel, key.obc, key.isVcr) },
                modifier = Modifier.weight(key.weight),
                style = key.style,
                icon = key.icon,
                iconOnly = key.iconOnly,
                height = height,
                fontSize = fontSize,
            )
        }
    }
}

// --- Key catalogues (immutable, allocated once) --------------------------------------------------

private val menuRow =
    listOf(
        RemoteKeySpec(DigaUiStrings.BTN_DIRECT_NAV, DigaUiStrings.CMD_LABEL_DIRECT_NAV, OBC.DIRECT_NAVIGATOR, KeyStyle.Info),
        RemoteKeySpec(DigaUiStrings.BTN_FUNCTIONS, DigaUiStrings.CMD_LABEL_FUNCTIONS, OBC.FUNCTIONS),
        RemoteKeySpec(DigaUiStrings.BTN_TOP_MENU, DigaUiStrings.CMD_LABEL_TOP_MENU, OBC.TOP_MENU),
        RemoteKeySpec(DigaUiStrings.BTN_OPTION, DigaUiStrings.CMD_LABEL_OPTION, OBC.OPTION_SUB_MENU),
    )

private val navigationFooterRow =
    listOf(
        RemoteKeySpec(DigaUiStrings.BTN_RETURN, DigaUiStrings.CMD_LABEL_RETURN, OBC.RETURN),
        RemoteKeySpec(DigaUiStrings.BTN_EXIT, DigaUiStrings.CMD_LABEL_EXIT, OBC.EXIT),
        RemoteKeySpec(DigaUiStrings.BTN_GUIDE, DigaUiStrings.CMD_LABEL_GUIDE, OBC.GUIDE),
        RemoteKeySpec(DigaUiStrings.BTN_PROG_CHECK, DigaUiStrings.CMD_LABEL_PROG_CHECK, OBC.PROG_CHECK),
    )

private val editingPlaybackRow =
    listOf(
        RemoteKeySpec(
            DigaUiStrings.BTN_TIME_SLIP,
            DigaUiStrings.CMD_LABEL_TIME_SLIP,
            OBC.TIME_SLIP,
            KeyStyle.Warning,
            Icons.Filled.Schedule,
            weight = 1.3f,
        ),
        RemoteKeySpec(
            DigaUiStrings.BTN_REPLAY_10S,
            DigaUiStrings.CMD_LABEL_REPLAY_10S,
            OBC.MANUAL_REPLAY_10S,
            icon = Icons.Filled.Replay10,
        ),
        RemoteKeySpec(DigaUiStrings.BTN_SKIP_30S, DigaUiStrings.CMD_LABEL_SKIP_30S, OBC.MANUAL_SKIP_30S, icon = Icons.Filled.Forward30),
    )

private val editingToolsRow =
    listOf(
        RemoteKeySpec(DigaUiStrings.BTN_CREATE_CHAPTER, DigaUiStrings.CMD_LABEL_CREATE_CHAPTER, OBC.CREATE_CHAPTER, weight = 1.1f),
        RemoteKeySpec(
            DigaUiStrings.BTN_DELETE_TITLE,
            DigaUiStrings.CMD_LABEL_DELETE_TITLE,
            OBC.DELETE_TITLE,
            KeyStyle.DangerDark,
            Icons.Filled.Delete,
            weight = 1.1f,
        ),
        RemoteKeySpec(DigaUiStrings.BTN_AUDIO, DigaUiStrings.CMD_LABEL_AUDIO, OBC.AUDIO, weight = 0.9f),
        RemoteKeySpec(DigaUiStrings.BTN_SUBTITLE, DigaUiStrings.CMD_LABEL_SUBTITLE, OBC.SUBTITLE, weight = 0.9f),
    )

private val keypadRows =
    listOf(
        listOf("1" to OBC.DIGIT_1, "2" to OBC.DIGIT_2, "3" to OBC.DIGIT_3),
        listOf("4" to OBC.DIGIT_4, "5" to OBC.DIGIT_5, "6" to OBC.DIGIT_6),
        listOf("7" to OBC.DIGIT_7, "8" to OBC.DIGIT_8, "9" to OBC.DIGIT_9),
    ).map { row -> row.map { (digit, obc) -> RemoteKeySpec(digit, digit, obc) } }

private val keypadFooterRow =
    listOf(
        RemoteKeySpec(DigaUiStrings.BTN_MEDIA, DigaUiStrings.CMD_LABEL_MEDIA_SERVER, OBC.MEDIA_SERVER),
        RemoteKeySpec(DigaUiStrings.KEYPAD_ZERO, DigaUiStrings.KEYPAD_ZERO, OBC.DIGIT_0),
        RemoteKeySpec(DigaUiStrings.BTN_CANCEL, DigaUiStrings.CMD_LABEL_CANCEL, OBC.CANCEL),
    )

// --- Sections ------------------------------------------------------------------------------------

/**
 * Combined System, IR Channel (SET 1 / SET 2 / SET 3), and Drive Selection card (3 rows).
 * Eliminates the redundant Recorder Profile selector while keeping IR channel sets and deck
 * bus switching (DVD/HDD 0xB0 vs VHS/VCR 0x90) immediately accessible.
 */
@Composable
fun SystemSection(
    activeMode: RemoteMode,
    isVcrDeck: Boolean,
    onModeSelected: (RemoteMode) -> Unit,
    onToggleVcrDeck: () -> Unit,
    onSend: SendCommand,
    modifier: Modifier = Modifier,
) {
    val systemPrimaryRow =
        remember(isVcrDeck) {
            listOf(
                RemoteKeySpec(
                    DigaUiStrings.BTN_POWER,
                    DigaUiStrings.CMD_LABEL_POWER,
                    OBC.POWER,
                    KeyStyle.Danger,
                    Icons.Filled.PowerSettingsNew,
                    weight = 1.3f,
                ),
                RemoteKeySpec(
                    DigaUiStrings.BTN_EJECT,
                    DigaUiStrings.CMD_LABEL_EJECT,
                    OBC.EJECT,
                    icon = Icons.Filled.Eject,
                    isVcr = isVcrDeck,
                ),
                RemoteKeySpec(DigaUiStrings.BTN_STATUS, DigaUiStrings.CMD_LABEL_STATUS, OBC.STATUS, icon = Icons.Filled.Info),
                RemoteKeySpec(DigaUiStrings.BTN_DISPLAY, DigaUiStrings.CMD_LABEL_DISPLAY, OBC.DISPLAY),
            )
        }

    DigaCard(
        title = DigaUiStrings.SECTION_SYSTEM_DRIVE,
        modifier = modifier,
        trailing = {
            Text(
                text = "${DigaUiStrings.LABEL_ACTIVE_PREFIX}${activeMode.label}",
                fontWeight = FontWeight.Bold,
                fontSize = DigaUiDimensions.TextSizeVfdSub,
                color = activeMode.textColor(),
            )
        },
    ) {
        // Row 1: IR Channel Sets (SET 1 / SET 2 / SET 3)
        ChoiceRow(
            options = RemoteMode.entries,
            selected = activeMode,
            labelOf = { it.label },
            onSelect = onModeSelected,
            accentOf = { it.keyStyle() },
            height = DigaUiDimensions.HeightKey,
        )

        // Row 2: Power, Eject, Status, Display
        KeyRow(systemPrimaryRow, onSend)

        // Row 3: Drive Select, Active Deck Target (DVD/HDD vs VHS/VCR), and Input Select
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
        ) {
            RemoteKey(
                label = DigaUiStrings.BTN_DRIVE_SELECT_ALL,
                onClick = { onSend(DigaUiStrings.CMD_LABEL_DRIVE_SELECT, OBC.DRIVE_SELECT, isVcrDeck) },
                modifier = Modifier.weight(1.1f),
                style = KeyStyle.Primary,
                icon = Icons.Filled.Autorenew,
            )
            RemoteKey(
                label = if (isVcrDeck) DigaUiStrings.BTN_DECK_VHS else DigaUiStrings.BTN_DECK_DVD,
                onClick = onToggleVcrDeck,
                modifier = Modifier.weight(1.1f),
                style = if (isVcrDeck) KeyStyle.Warning else KeyStyle.Neutral,
                icon = Icons.Filled.SwapHoriz,
            )
            RemoteKey(
                label = DigaUiStrings.BTN_INPUT_SELECT,
                onClick = { onSend(DigaUiStrings.CMD_LABEL_INPUT_SELECT, OBC.INPUT_SELECT, false) },
                modifier = Modifier.weight(1f),
                icon = Icons.AutoMirrored.Filled.Input,
            )
        }
    }
}

@Composable
fun NavigationSection(
    onSend: SendCommand,
    modifier: Modifier = Modifier,
) {
    DigaCard(
        title = DigaUiStrings.SECTION_NAVIGATION_MENUS,
        modifier = modifier,
    ) {
        KeyRow(menuRow, onSend)
        DPadCluster(onSend)
        KeyRow(navigationFooterRow, onSend)
    }
}

private enum class TargetDpadSection {
    NONE,
    UP,
    DOWN,
    LEFT,
    RIGHT,
    OK,
}

/**
 * Circumscribed circular D-Pad around a circular center OK button.
 *
 * The outer directional ring is divided by four diagonal lines at NE (315 deg), SE (45 deg),
 * SW (135 deg), and NW (225 deg), separating the North (UP), South (DOWN), East (RIGHT),
 * and West (LEFT) navigation vectors.
 */
@Composable
fun CircularTargetDpad(
    onSend: SendCommand,
    modifier: Modifier = Modifier,
    size: Dp = DigaUiDimensions.SizeTargetDpad,
    okSize: Dp = DigaUiDimensions.SizeTargetOk,
) {
    val haptics = LocalHapticFeedback.current
    var pressedSection by remember { mutableStateOf(TargetDpadSection.NONE) }
    val density = LocalDensity.current

    val okSizePx = with(density) { okSize.toPx() }
    val ringGapDp = DigaUiDimensions.SpacingDpadGap
    val ringGapPx = with(density) { ringGapDp.toPx() }
    val strokeWidthPx = with(density) { 1.75.dp.toPx() }
    val dividerWidthPx = with(density) { 2.dp.toPx() }

    // Exact radial midpoint of the circumscribed ring from center
    val midRadiusDp = (size / 2f + okSize / 2f + ringGapDp) / 2f

    Box(
        modifier =
            modifier
                .size(size)
                .pointerInput(size, okSize) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val center = Offset(this.size.width / 2f, this.size.height / 2f)
                        val radiusOuter = this.size.width / 2f
                        val radiusOk = okSizePx / 2f

                        fun resolveSection(pos: Offset): TargetDpadSection {
                            val dx = pos.x - center.x
                            val dy = pos.y - center.y
                            val dist = sqrt(dx * dx + dy * dy)
                            return when {
                                dist <= radiusOk -> TargetDpadSection.OK
                                dist <= radiusOuter -> {
                                    val angle = (Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 360f) % 360f
                                    when {
                                        angle >= 315f || angle < 45f -> TargetDpadSection.RIGHT
                                        angle in 45f..135f -> TargetDpadSection.DOWN
                                        angle in 135f..225f -> TargetDpadSection.LEFT
                                        else -> TargetDpadSection.UP
                                    }
                                }
                                else -> TargetDpadSection.NONE
                            }
                        }

                        var active = resolveSection(down.position)
                        pressedSection = active

                        do {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull()
                            if (change != null && change.pressed) {
                                val current = resolveSection(change.position)
                                if (current != active) {
                                    active = current
                                    pressedSection = current
                                }
                            }
                        } while (event.changes.any { it.pressed })

                        if (active != TargetDpadSection.NONE) {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            when (active) {
                                TargetDpadSection.UP -> onSend(DigaUiStrings.CMD_LABEL_UP, OBC.UP, false)
                                TargetDpadSection.DOWN -> onSend(DigaUiStrings.CMD_LABEL_DOWN, OBC.DOWN, false)
                                TargetDpadSection.LEFT -> onSend(DigaUiStrings.CMD_LABEL_LEFT, OBC.LEFT, false)
                                TargetDpadSection.RIGHT -> onSend(DigaUiStrings.CMD_LABEL_RIGHT, OBC.RIGHT, false)
                                TargetDpadSection.OK -> onSend(DigaUiStrings.CMD_LABEL_OK, OBC.ENTER_OK, false)
                                TargetDpadSection.NONE -> {}
                            }
                        }
                        pressedSection = TargetDpadSection.NONE
                    }
                },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radiusOuter = (this.size.width / 2f) - strokeWidthPx
            val radiusOk = okSizePx / 2f
            val radiusInner = radiusOk + ringGapPx

            val outerRect = Rect(center.x - radiusOuter, center.y - radiusOuter, center.x + radiusOuter, center.y + radiusOuter)
            val innerRect = Rect(center.x - radiusInner, center.y - radiusInner, center.x + radiusInner, center.y + radiusInner)

            // 1. Base circumscribed annular ring
            val fullRingPath =
                Path().apply {
                    addOval(outerRect)
                    addOval(innerRect)
                    fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
                }
            drawPath(fullRingPath, color = DigaSurfaceVariant)

            // 2. Highlight active directional quadrant (North, South, East, West)
            val sectors =
                listOf(
                    Triple(TargetDpadSection.RIGHT, -45f, 90f),
                    Triple(TargetDpadSection.DOWN, 45f, 90f),
                    Triple(TargetDpadSection.LEFT, 135f, 90f),
                    Triple(TargetDpadSection.UP, 225f, 90f),
                )

            for ((section, startAngle, sweep) in sectors) {
                if (pressedSection == section) {
                    val sectorPath =
                        Path().apply {
                            arcTo(outerRect, startAngle, sweep, forceMoveTo = true)
                            arcTo(innerRect, startAngle + sweep, -sweep, forceMoveTo = false)
                            close()
                        }
                    drawPath(sectorPath, color = DigaBlue.copy(alpha = 0.72f))
                    drawPath(sectorPath, color = DigaBlueLight, style = Stroke(width = strokeWidthPx))
                }
            }

            // 3. Outer & Inner circumscribed circle borders
            drawCircle(
                color = DigaOutline,
                radius = radiusOuter,
                center = center,
                style = Stroke(width = strokeWidthPx),
            )
            drawCircle(
                color = DigaOutline,
                radius = radiusInner,
                center = center,
                style = Stroke(width = strokeWidthPx),
            )

            // 4. Diagonal dividers at SE (45 deg), SW (135 deg), NW (225 deg), NE (315 deg)
            val diagonalAngles = floatArrayOf(45f, 135f, 225f, 315f)
            for (deg in diagonalAngles) {
                val rad = Math.toRadians(deg.toDouble())
                val cosA = cos(rad).toFloat()
                val sinA = sin(rad).toFloat()
                val startPt = Offset(center.x + radiusInner * cosA, center.y + radiusInner * sinA)
                val endPt = Offset(center.x + radiusOuter * cosA, center.y + radiusOuter * sinA)
                drawLine(
                    color = DigaOutline,
                    start = startPt,
                    end = endPt,
                    strokeWidth = dividerWidthPx,
                )
            }

            // 5. Center Circular OK Button
            val isOkPressed = pressedSection == TargetDpadSection.OK
            val okFillColor = if (isOkPressed) DigaBlueLight else DigaBlue
            drawCircle(
                color = okFillColor,
                radius = radiusOk,
                center = center,
            )
            drawCircle(
                color = if (isOkPressed) Color.White else DigaBlueLight.copy(alpha = 0.85f),
                radius = radiusOk,
                center = center,
                style = Stroke(width = strokeWidthPx),
            )
        }

        // Directional Arrow Icons centered on the radial midline of North, South, West, East sectors
        Icon(
            imageVector = Icons.Filled.KeyboardArrowUp,
            contentDescription = null,
            tint = if (pressedSection == TargetDpadSection.UP) DigaBlueLight else DigaTextPrimary,
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .offset(y = -midRadiusDp)
                    .size(DigaUiDimensions.SizeDpadArrowIcon),
        )

        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            tint = if (pressedSection == TargetDpadSection.DOWN) DigaBlueLight else DigaTextPrimary,
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .offset(y = midRadiusDp)
                    .size(DigaUiDimensions.SizeDpadArrowIcon),
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = null,
            tint = if (pressedSection == TargetDpadSection.LEFT) DigaBlueLight else DigaTextPrimary,
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .offset(x = -midRadiusDp)
                    .size(DigaUiDimensions.SizeDpadArrowIcon),
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = if (pressedSection == TargetDpadSection.RIGHT) DigaBlueLight else DigaTextPrimary,
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .offset(x = midRadiusDp)
                    .size(DigaUiDimensions.SizeDpadArrowIcon),
        )

        // Center OK Text
        Text(
            text = DigaUiStrings.BTN_OK,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = DigaUiDimensions.TextSizeDpadOk,
            modifier = Modifier.align(Alignment.Center),
        )

        // Accessibility Semantic Overlays
        Box(
            modifier =
                Modifier
                    .size(okSize)
                    .align(Alignment.Center)
                    .semantics {
                        role = Role.Button
                        contentDescription = DigaUiStrings.BTN_OK
                        onClick {
                            onSend(DigaUiStrings.CMD_LABEL_OK, OBC.ENTER_OK, false)
                            true
                        }
                    },
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(DigaUiDimensions.HeightMiniButton)
                    .align(Alignment.TopCenter)
                    .semantics {
                        role = Role.Button
                        contentDescription = DigaUiStrings.BTN_UP
                        onClick {
                            onSend(DigaUiStrings.CMD_LABEL_UP, OBC.UP, false)
                            true
                        }
                    },
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(DigaUiDimensions.HeightMiniButton)
                    .align(Alignment.BottomCenter)
                    .semantics {
                        role = Role.Button
                        contentDescription = DigaUiStrings.BTN_DOWN
                        onClick {
                            onSend(DigaUiStrings.CMD_LABEL_DOWN, OBC.DOWN, false)
                            true
                        }
                    },
        )
        Box(
            modifier =
                Modifier
                    .width(DigaUiDimensions.HeightMiniButton)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
                    .semantics {
                        role = Role.Button
                        contentDescription = DigaUiStrings.BTN_LEFT
                        onClick {
                            onSend(DigaUiStrings.CMD_LABEL_LEFT, OBC.LEFT, false)
                            true
                        }
                    },
        )
        Box(
            modifier =
                Modifier
                    .width(DigaUiDimensions.HeightMiniButton)
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .semantics {
                        role = Role.Button
                        contentDescription = DigaUiStrings.BTN_RIGHT
                        onClick {
                            onSend(DigaUiStrings.CMD_LABEL_RIGHT, OBC.RIGHT, false)
                            true
                        }
                    },
        )
    }
}

/**
 * Navigation cluster pairing the circumscribed circular D-Pad with wide lateral
 * Volume (`VOL +` / `VOL -`) and Channel (`CH +` / `CH -`) rockers that expand to fill
 * all remaining horizontal space on both phones and tablets.
 */
@Composable
private fun DPadCluster(onSend: SendCommand) {
    val isCompactPhone = LocalConfiguration.current.screenWidthDp < 360
    val dpadSize = if (isCompactPhone) DigaUiDimensions.SizeTargetDpadCompact else DigaUiDimensions.SizeTargetDpad
    val okSize = if (isCompactPhone) DigaUiDimensions.SizeTargetOkCompact else DigaUiDimensions.SizeTargetOk
    val flankHeight = if (isCompactPhone) DigaUiDimensions.HeightFlankDpadButtonCompact else DigaUiDimensions.HeightFlankDpadButton

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingDpadFlankGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left Flank: Wide Volume controls (VOL + / VOL -) filling full space up to the circular D-Pad
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingRows),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RemoteKey(
                label = DigaUiStrings.BTN_VOL_UP,
                onClick = { onSend(DigaUiStrings.CMD_LABEL_VOL_UP, OBC.VOL_UP, false) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(flankHeight),
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                fontSize = DigaUiDimensions.TextSizeDpadFlank,
                shape =
                    RoundedCornerShape(
                        topStart = DigaUiDimensions.RadiusTargetFlankOuter,
                        topEnd = DigaUiDimensions.RadiusTargetFlankOuter,
                        bottomStart = DigaUiDimensions.RadiusTargetFlankInner,
                        bottomEnd = DigaUiDimensions.RadiusTargetFlankInner,
                    ),
                contentPadding =
                    PaddingValues(
                        horizontal = DigaUiDimensions.PaddingCompactH,
                        vertical = DigaUiDimensions.PaddingCompactV,
                    ),
                isVerticalContent = true,
            )
            RemoteKey(
                label = DigaUiStrings.BTN_VOL_DOWN,
                onClick = { onSend(DigaUiStrings.CMD_LABEL_VOL_DOWN, OBC.VOL_DOWN, false) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(flankHeight),
                icon = Icons.AutoMirrored.Filled.VolumeDown,
                fontSize = DigaUiDimensions.TextSizeDpadFlank,
                shape =
                    RoundedCornerShape(
                        topStart = DigaUiDimensions.RadiusTargetFlankInner,
                        topEnd = DigaUiDimensions.RadiusTargetFlankInner,
                        bottomStart = DigaUiDimensions.RadiusTargetFlankOuter,
                        bottomEnd = DigaUiDimensions.RadiusTargetFlankOuter,
                    ),
                contentPadding =
                    PaddingValues(
                        horizontal = DigaUiDimensions.PaddingCompactH,
                        vertical = DigaUiDimensions.PaddingCompactV,
                    ),
                isVerticalContent = true,
            )
        }

        // Center: Circumscribed Circular Target D-Pad (sized to exact diameter, no extra wrapper width)
        CircularTargetDpad(
            onSend = onSend,
            size = dpadSize,
            okSize = okSize,
        )

        // Right Flank: Wide Channel controls (CH + / CH -) filling full space from the circular D-Pad to card edge
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingRows),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RemoteKey(
                label = DigaUiStrings.BTN_CH_UP,
                onClick = { onSend(DigaUiStrings.CMD_LABEL_CH_UP, OBC.CH_UP, false) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(flankHeight),
                icon = Icons.Filled.KeyboardArrowUp,
                fontSize = DigaUiDimensions.TextSizeDpadFlank,
                shape =
                    RoundedCornerShape(
                        topStart = DigaUiDimensions.RadiusTargetFlankOuter,
                        topEnd = DigaUiDimensions.RadiusTargetFlankOuter,
                        bottomStart = DigaUiDimensions.RadiusTargetFlankInner,
                        bottomEnd = DigaUiDimensions.RadiusTargetFlankInner,
                    ),
                contentPadding =
                    PaddingValues(
                        horizontal = DigaUiDimensions.PaddingCompactH,
                        vertical = DigaUiDimensions.PaddingCompactV,
                    ),
                isVerticalContent = true,
            )
            RemoteKey(
                label = DigaUiStrings.BTN_CH_DOWN,
                onClick = { onSend(DigaUiStrings.CMD_LABEL_CH_DOWN, OBC.CH_DOWN, false) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(flankHeight),
                icon = Icons.Filled.KeyboardArrowDown,
                fontSize = DigaUiDimensions.TextSizeDpadFlank,
                shape =
                    RoundedCornerShape(
                        topStart = DigaUiDimensions.RadiusTargetFlankInner,
                        topEnd = DigaUiDimensions.RadiusTargetFlankInner,
                        bottomStart = DigaUiDimensions.RadiusTargetFlankOuter,
                        bottomEnd = DigaUiDimensions.RadiusTargetFlankOuter,
                    ),
                contentPadding =
                    PaddingValues(
                        horizontal = DigaUiDimensions.PaddingCompactH,
                        vertical = DigaUiDimensions.PaddingCompactV,
                    ),
                isVerticalContent = true,
            )
        }
    }
}

/**
 * Unified Transport & VCR Deck card (3 rows).
 * Combines main playback transport, shuttle/skip controls, and VCR-specific tracking + single
 * unified Dubbing button without duplicating Play/Pause/Stop/Rew/FF/Rec/Eject keys.
 */
@Composable
fun TransportSection(
    isVcrDeck: Boolean,
    onSend: SendCommand,
    modifier: Modifier = Modifier,
) {
    val transportMainRow =
        remember(isVcrDeck) {
            listOf(
                RemoteKeySpec(
                    DigaUiStrings.BTN_PLAY,
                    DigaUiStrings.CMD_LABEL_PLAY,
                    OBC.PLAY,
                    KeyStyle.Success,
                    Icons.Filled.PlayArrow,
                    weight = 1.2f,
                    isVcr = isVcrDeck,
                ),
                RemoteKeySpec(
                    DigaUiStrings.BTN_PAUSE,
                    DigaUiStrings.CMD_LABEL_PAUSE,
                    OBC.PAUSE,
                    KeyStyle.Warning,
                    Icons.Filled.Pause,
                    isVcr = isVcrDeck,
                ),
                RemoteKeySpec(
                    DigaUiStrings.BTN_STOP,
                    DigaUiStrings.CMD_LABEL_STOP,
                    OBC.STOP,
                    KeyStyle.DangerDark,
                    Icons.Filled.Stop,
                    isVcr = isVcrDeck,
                ),
            )
        }

    val transportSeekRow =
        remember(isVcrDeck) {
            listOf(
                RemoteKeySpec(
                    DigaUiStrings.BTN_SKIP_PREV,
                    DigaUiStrings.CMD_LABEL_SKIP_PREV,
                    OBC.SKIP_PREV,
                    icon = Icons.Filled.SkipPrevious,
                    iconOnly = true,
                    isVcr = isVcrDeck,
                ),
                RemoteKeySpec(
                    DigaUiStrings.BTN_REWIND,
                    DigaUiStrings.CMD_LABEL_REWIND,
                    OBC.REWIND,
                    icon = Icons.Filled.FastRewind,
                    iconOnly = true,
                    isVcr = isVcrDeck,
                ),
                RemoteKeySpec(
                    DigaUiStrings.BTN_FAST_FORWARD,
                    DigaUiStrings.CMD_LABEL_FAST_FORWARD,
                    OBC.FAST_FORWARD,
                    icon = Icons.Filled.FastForward,
                    iconOnly = true,
                    isVcr = isVcrDeck,
                ),
                RemoteKeySpec(
                    DigaUiStrings.BTN_SKIP_NEXT,
                    DigaUiStrings.CMD_LABEL_SKIP_NEXT,
                    OBC.SKIP_NEXT,
                    icon = Icons.Filled.SkipNext,
                    iconOnly = true,
                    isVcr = isVcrDeck,
                ),
            )
        }

    val vcrToolsRow =
        remember(isVcrDeck) {
            val dubLabel = if (isVcrDeck) DigaUiStrings.BTN_DUB_VHS_TO_DVD else DigaUiStrings.BTN_DUB_DVD_TO_VHS
            val dubCmdLabel = if (isVcrDeck) DigaUiStrings.CMD_LABEL_VCR_DUB_VHS_TO_DVD else DigaUiStrings.CMD_LABEL_VCR_DUB_DVD_TO_VHS
            val dubObc = if (isVcrDeck) OBC.VCR_DUB_VHS_TO_DVD else OBC.VCR_DUB_DVD_TO_VHS
            listOf(
                RemoteKeySpec(
                    DigaUiStrings.BTN_VCR_TRACKING_DOWN,
                    DigaUiStrings.CMD_LABEL_VCR_TRACKING_DOWN,
                    OBC.VCR_TRACKING_MINUS,
                    KeyStyle.Primary,
                    isVcr = true,
                ),
                RemoteKeySpec(
                    DigaUiStrings.BTN_VCR_TRACKING_UP,
                    DigaUiStrings.CMD_LABEL_VCR_TRACKING_UP,
                    OBC.VCR_TRACKING_PLUS,
                    KeyStyle.Primary,
                    isVcr = true,
                ),
                RemoteKeySpec(
                    dubLabel,
                    dubCmdLabel,
                    dubObc,
                    KeyStyle.Accent,
                    Icons.Filled.SwapHoriz,
                    weight = 1.4f,
                    isVcr = false,
                ),
            )
        }

    DigaCard(
        title = DigaUiStrings.SECTION_TRANSPORT,
        modifier = modifier,
        trailing = {
            Text(
                text = if (isVcrDeck) DigaUiStrings.BADGE_DECK_VCR else DigaUiStrings.BADGE_DECK_DVD,
                fontWeight = FontWeight.Bold,
                fontSize = DigaUiDimensions.TextSizeVfdSub,
                color = if (isVcrDeck) DigaAmber else DigaBlueLight,
            )
        },
    ) {
        KeyRow(transportMainRow, onSend, height = DigaUiDimensions.HeightStandardButton)
        KeyRow(transportSeekRow, onSend)
        KeyRow(vcrToolsRow, onSend)
    }
}

@Composable
fun EditingSection(
    onSend: SendCommand,
    modifier: Modifier = Modifier,
) {
    DigaCard(
        title = DigaUiStrings.SECTION_EDITING_FEATURES,
        modifier = modifier,
    ) {
        KeyRow(editingPlaybackRow, onSend)
        KeyRow(editingToolsRow, onSend)
    }
}

@Composable
fun RecordingSection(
    isVcrDeck: Boolean,
    onSend: SendCommand,
    modifier: Modifier = Modifier,
) {
    val recordingRow =
        remember(isVcrDeck) {
            listOf(
                RemoteKeySpec(
                    DigaUiStrings.BTN_REC,
                    DigaUiStrings.CMD_LABEL_REC,
                    OBC.REC,
                    KeyStyle.Danger,
                    Icons.Filled.FiberManualRecord,
                    weight = 1.2f,
                    isVcr = isVcrDeck,
                ),
                RemoteKeySpec(DigaUiStrings.BTN_REC_MODE, DigaUiStrings.CMD_LABEL_REC_MODE, OBC.REC_MODE, weight = 1.5f),
                RemoteKeySpec(DigaUiStrings.BTN_EXT_LINK, DigaUiStrings.CMD_LABEL_EXT_LINK, OBC.EXT_LINK, weight = 1.1f),
            )
        }

    DigaCard(
        title = DigaUiStrings.SECTION_RECORDING_BROADCAST,
        modifier = modifier,
    ) {
        KeyRow(recordingRow, onSend, height = DigaUiDimensions.HeightStandardButton)
    }
}

@Composable
fun KeypadSection(
    onSend: SendCommand,
    modifier: Modifier = Modifier,
) {
    DigaCard(
        title = DigaUiStrings.SECTION_NUMERIC_KEYPAD,
        modifier = modifier,
    ) {
        keypadRows.forEach { row ->
            KeyRow(
                keys = row,
                onSend = onSend,
                height = DigaUiDimensions.HeightKeypadButton,
                fontSize = DigaUiDimensions.TextSizeKeypadDigit,
            )
        }
        KeyRow(keypadFooterRow, onSend, height = DigaUiDimensions.HeightKeypadButton)
    }
}
