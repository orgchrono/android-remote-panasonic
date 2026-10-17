package com.vhs.digacommander.presentation.ui.tabs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eject
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Forward30
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Input
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vhs.digacommander.data.catalog.PanasonicDigaCatalog.OBC
import com.vhs.digacommander.data.model.DigaModelProfile
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
            )
        }
    }
}

// --- Key catalogues (immutable, allocated once) --------------------------------------------------

private val systemPrimaryRow =
    listOf(
        RemoteKeySpec(
            DigaUiStrings.BTN_POWER,
            DigaUiStrings.CMD_LABEL_POWER,
            OBC.POWER,
            KeyStyle.Danger,
            Icons.Filled.PowerSettingsNew,
            weight = 1.4f
        ),
        RemoteKeySpec(DigaUiStrings.BTN_EJECT, DigaUiStrings.CMD_LABEL_EJECT, OBC.EJECT, icon = Icons.Filled.Eject),
        RemoteKeySpec(DigaUiStrings.BTN_STATUS, DigaUiStrings.CMD_LABEL_STATUS, OBC.STATUS, icon = Icons.Filled.Info),
        RemoteKeySpec(DigaUiStrings.BTN_DISPLAY, DigaUiStrings.CMD_LABEL_DISPLAY, OBC.DISPLAY),
    )

private val systemSelectRow =
    listOf(
        RemoteKeySpec(
            DigaUiStrings.BTN_DRIVE_SELECT_ALL,
            DigaUiStrings.CMD_LABEL_DRIVE_SELECT,
            OBC.DRIVE_SELECT,
            KeyStyle.Primary,
            Icons.Filled.Autorenew,
            weight = 1.5f,
        ),
        RemoteKeySpec(DigaUiStrings.BTN_INPUT_SELECT, DigaUiStrings.CMD_LABEL_INPUT_SELECT, OBC.INPUT_SELECT, icon = Icons.Filled.Input),
    )

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

private val transportMainRow =
    listOf(
        RemoteKeySpec(
            DigaUiStrings.BTN_PLAY,
            DigaUiStrings.CMD_LABEL_PLAY,
            OBC.PLAY,
            KeyStyle.Success,
            Icons.Filled.PlayArrow,
            weight = 1.2f
        ),
        RemoteKeySpec(DigaUiStrings.BTN_PAUSE, DigaUiStrings.CMD_LABEL_PAUSE, OBC.PAUSE, KeyStyle.Warning, Icons.Filled.Pause),
        RemoteKeySpec(DigaUiStrings.BTN_STOP, DigaUiStrings.CMD_LABEL_STOP, OBC.STOP, KeyStyle.DangerDark, Icons.Filled.Stop),
    )

private val transportSeekRow =
    listOf(
        RemoteKeySpec(
            DigaUiStrings.BTN_SKIP_PREV,
            DigaUiStrings.CMD_LABEL_SKIP_PREV,
            OBC.SKIP_PREV,
            icon = Icons.Filled.SkipPrevious,
            iconOnly = true
        ),
        RemoteKeySpec(
            DigaUiStrings.BTN_REWIND,
            DigaUiStrings.CMD_LABEL_REWIND,
            OBC.REWIND,
            icon = Icons.Filled.FastRewind,
            iconOnly = true
        ),
        RemoteKeySpec(
            DigaUiStrings.BTN_FAST_FORWARD,
            DigaUiStrings.CMD_LABEL_FAST_FORWARD,
            OBC.FAST_FORWARD,
            icon = Icons.Filled.FastForward,
            iconOnly = true,
        ),
        RemoteKeySpec(
            DigaUiStrings.BTN_SKIP_NEXT,
            DigaUiStrings.CMD_LABEL_SKIP_NEXT,
            OBC.SKIP_NEXT,
            icon = Icons.Filled.SkipNext,
            iconOnly = true
        ),
    )

private val vcrDeckRow =
    listOf(
        RemoteKeySpec(
            DigaUiStrings.BTN_VCR_DRIVE,
            DigaUiStrings.CMD_LABEL_VCR_DRIVE,
            OBC.DRIVE_SELECT,
            KeyStyle.Warning,
            weight = 1.5f,
            isVcr = true,
        ),
        RemoteKeySpec(DigaUiStrings.BTN_VCR_EJECT, DigaUiStrings.CMD_LABEL_VCR_EJECT, OBC.EJECT, icon = Icons.Filled.Eject, isVcr = true),
    )

private val vcrTransportMainRow =
    listOf(
        RemoteKeySpec(
            DigaUiStrings.BTN_VCR_PLAY,
            DigaUiStrings.CMD_LABEL_VCR_PLAY,
            OBC.PLAY,
            KeyStyle.Success,
            Icons.Filled.PlayArrow,
            weight = 1.2f,
            isVcr = true,
        ),
        RemoteKeySpec(
            DigaUiStrings.BTN_VCR_PAUSE,
            DigaUiStrings.CMD_LABEL_VCR_PAUSE,
            OBC.PAUSE,
            KeyStyle.Warning,
            Icons.Filled.Pause,
            isVcr = true
        ),
        RemoteKeySpec(
            DigaUiStrings.BTN_VCR_STOP,
            DigaUiStrings.CMD_LABEL_VCR_STOP,
            OBC.STOP,
            KeyStyle.DangerDark,
            Icons.Filled.Stop,
            isVcr = true
        ),
    )

private val vcrTransportSeekRow =
    listOf(
        RemoteKeySpec(
            DigaUiStrings.BTN_VCR_REWIND,
            DigaUiStrings.CMD_LABEL_VCR_REWIND,
            OBC.REWIND,
            icon = Icons.Filled.FastRewind,
            isVcr = true
        ),
        RemoteKeySpec(
            DigaUiStrings.BTN_VCR_FF,
            DigaUiStrings.CMD_LABEL_VCR_FF,
            OBC.FAST_FORWARD,
            icon = Icons.Filled.FastForward,
            isVcr = true
        ),
        RemoteKeySpec(
            DigaUiStrings.BTN_VCR_REC,
            DigaUiStrings.CMD_LABEL_VCR_REC,
            OBC.REC,
            KeyStyle.Danger,
            Icons.Filled.FiberManualRecord,
            isVcr = true,
        ),
    )

private val vcrTrackingRow =
    listOf(
        RemoteKeySpec(
            DigaUiStrings.BTN_VCR_TRACKING_UP,
            DigaUiStrings.CMD_LABEL_VCR_TRACKING_UP,
            OBC.VCR_TRACKING_PLUS,
            KeyStyle.Primary,
            isVcr = true,
        ),
        RemoteKeySpec(
            DigaUiStrings.BTN_VCR_TRACKING_DOWN,
            DigaUiStrings.CMD_LABEL_VCR_TRACKING_DOWN,
            OBC.VCR_TRACKING_MINUS,
            KeyStyle.Primary,
            isVcr = true,
        ),
    )

private val vcrDubRow =
    listOf(
        RemoteKeySpec(
            DigaUiStrings.BTN_DUB_VHS_TO_DVD,
            DigaUiStrings.CMD_LABEL_VCR_DUB_VHS_TO_DVD,
            OBC.VCR_DUB_VHS_TO_DVD,
            KeyStyle.Accent,
            Icons.Filled.SwapHoriz,
        ),
        RemoteKeySpec(
            DigaUiStrings.BTN_DUB_DVD_TO_VHS,
            DigaUiStrings.CMD_LABEL_VCR_DUB_DVD_TO_VHS,
            OBC.VCR_DUB_DVD_TO_VHS,
            KeyStyle.Accent,
            Icons.Filled.SwapHoriz,
        ),
    )

private val editingPlaybackRow =
    listOf(
        RemoteKeySpec(
            DigaUiStrings.BTN_TIME_SLIP,
            DigaUiStrings.CMD_LABEL_TIME_SLIP,
            OBC.TIME_SLIP,
            KeyStyle.Warning,
            Icons.Filled.Schedule,
            weight = 1.3f
        ),
        RemoteKeySpec(
            DigaUiStrings.BTN_REPLAY_10S,
            DigaUiStrings.CMD_LABEL_REPLAY_10S,
            OBC.MANUAL_REPLAY_10S,
            icon = Icons.Filled.Replay10
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

private val recordingRow =
    listOf(
        RemoteKeySpec(
            DigaUiStrings.BTN_REC,
            DigaUiStrings.CMD_LABEL_REC,
            OBC.REC,
            KeyStyle.Danger,
            Icons.Filled.FiberManualRecord,
            weight = 1.2f
        ),
        RemoteKeySpec(DigaUiStrings.BTN_REC_MODE, DigaUiStrings.CMD_LABEL_REC_MODE, OBC.REC_MODE, weight = 1.5f),
        RemoteKeySpec(DigaUiStrings.BTN_EXT_LINK, DigaUiStrings.CMD_LABEL_EXT_LINK, OBC.EXT_LINK, weight = 1.1f),
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

@Composable
fun ProfileSection(
    activeProfile: DigaModelProfile,
    activeMode: RemoteMode,
    onProfileSelected: (DigaModelProfile) -> Unit,
    onModeSelected: (RemoteMode) -> Unit,
) {
    var profileMenuExpanded by remember { mutableStateOf(false) }

    DigaCard(
        title = DigaUiStrings.SECTION_PROFILE_MODE,
        trailing = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
            ) {
                Box {
                    Row(
                        modifier =
                            Modifier
                                .clickable { profileMenuExpanded = true }
                                .padding(
                                    horizontal = DigaUiDimensions.PaddingCompactH,
                                    vertical = DigaUiDimensions.PaddingCompactV,
                                ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingMicro),
                    ) {
                        Text(
                            text = activeProfile.id,
                            fontWeight = FontWeight.Bold,
                            fontSize = DigaUiDimensions.TextSizeVfdSub,
                            color = DigaBlueLight,
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            tint = DigaBlueLight,
                            modifier = Modifier.size(DigaUiDimensions.SizeKeyIconCompact),
                        )
                    }
                    DropdownMenu(
                        expanded = profileMenuExpanded,
                        onDismissRequest = { profileMenuExpanded = false },
                    ) {
                        DigaModelProfile.entries.forEach { profile ->
                            DropdownMenuItem(
                                text = { Text(profile.displayName) },
                                onClick = {
                                    onProfileSelected(profile)
                                    profileMenuExpanded = false
                                },
                            )
                        }
                    }
                }
                Text(
                    text = "${DigaUiStrings.LABEL_ACTIVE_PREFIX}${activeMode.label}",
                    fontWeight = FontWeight.Bold,
                    fontSize = DigaUiDimensions.TextSizeVfdSub,
                    color = activeMode.textColor(),
                )
            }
        },
    ) {
        ChoiceRow(
            options = RemoteMode.entries,
            selected = activeMode,
            labelOf = { it.label },
            onSelect = onModeSelected,
            accentOf = { it.keyStyle() },
        )
    }
}

@Composable
fun SystemSection(onSend: SendCommand) {
    DigaCard(title = DigaUiStrings.SECTION_SYSTEM_DRIVE) {
        KeyRow(systemPrimaryRow, onSend)
        KeyRow(systemSelectRow, onSend)
    }
}

@Composable
fun NavigationSection(onSend: SendCommand) {
    DigaCard(title = DigaUiStrings.SECTION_NAVIGATION_MENUS) {
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
    val gapPx = with(density) { 4.dp.toPx() }
    val strokeWidthPx = with(density) { 1.5.dp.toPx() }

    Box(
        modifier =
            modifier
                .size(size)
                .pointerInput(Unit) {
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
            val radiusOuter = this.size.width / 2f
            val radiusOk = okSizePx / 2f
            val radiusInner = radiusOk + gapPx

            // 4 Directional Sectors (Outer Ring) forming a circumscribed circle
            val gapHalfAngle = 1.5f
            val sweepAngle = 90f - (gapHalfAngle * 2f)

            val sectors =
                listOf(
                    Triple(TargetDpadSection.RIGHT, -45f + gapHalfAngle, sweepAngle),
                    Triple(TargetDpadSection.DOWN, 45f + gapHalfAngle, sweepAngle),
                    Triple(TargetDpadSection.LEFT, 135f + gapHalfAngle, sweepAngle),
                    Triple(TargetDpadSection.UP, 225f + gapHalfAngle, sweepAngle),
                )

            for ((section, startAngle, sweep) in sectors) {
                val isPressed = pressedSection == section
                val sectorPath =
                    Path().apply {
                        val outerRect = Rect(center.x - radiusOuter, center.y - radiusOuter, center.x + radiusOuter, center.y + radiusOuter)
                        val innerRect = Rect(center.x - radiusInner, center.y - radiusInner, center.x + radiusInner, center.y + radiusInner)
                        arcTo(outerRect, startAngle, sweep, forceMoveTo = true)
                        arcTo(innerRect, startAngle + sweep, -sweep, forceMoveTo = false)
                        close()
                    }

                val fillColor = if (isPressed) DigaBlue.copy(alpha = 0.6f) else DigaSurfaceVariant
                drawPath(sectorPath, color = fillColor)
                drawPath(
                    sectorPath,
                    color = if (isPressed) DigaBlueLight else DigaOutline,
                    style = Stroke(width = strokeWidthPx),
                )
            }

            // Center Circular OK Button
            val isOkPressed = pressedSection == TargetDpadSection.OK
            val okFillColor = if (isOkPressed) DigaBlueLight else DigaBlue
            drawCircle(
                color = okFillColor,
                radius = radiusOk,
                center = center,
            )
            drawCircle(
                color = if (isOkPressed) Color.White else DigaOutline,
                radius = radiusOk,
                center = center,
                style = Stroke(width = strokeWidthPx + 0.5f),
            )
        }

        // Overlay Directional Icons
        Icon(
            imageVector = Icons.Filled.KeyboardArrowUp,
            contentDescription = null,
            tint = if (pressedSection == TargetDpadSection.UP) DigaBlueLight else DigaTextPrimary,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 18.dp)
                    .size(DigaUiDimensions.SizeKeyIcon),
        )

        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            tint = if (pressedSection == TargetDpadSection.DOWN) DigaBlueLight else DigaTextPrimary,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 18.dp)
                    .size(DigaUiDimensions.SizeKeyIcon),
        )

        Icon(
            imageVector = Icons.Filled.KeyboardArrowLeft,
            contentDescription = null,
            tint = if (pressedSection == TargetDpadSection.LEFT) DigaBlueLight else DigaTextPrimary,
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 18.dp)
                    .size(DigaUiDimensions.SizeKeyIcon),
        )

        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = if (pressedSection == TargetDpadSection.RIGHT) DigaBlueLight else DigaTextPrimary,
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 18.dp)
                    .size(DigaUiDimensions.SizeKeyIcon),
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

@Composable
private fun DPadCluster(onSend: SendCommand) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left Flank: Volume controls (VOL + / VOL -) expanded to weight 1f to match navigation columns
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RemoteKey(
                label = DigaUiStrings.BTN_VOL_UP,
                onClick = { onSend(DigaUiStrings.CMD_LABEL_VOL_UP, OBC.VOL_UP, false) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(DigaUiDimensions.HeightFlankDpadButton),
                icon = Icons.Filled.VolumeUp,
                fontSize = DigaUiDimensions.TextSizeButtonCompact,
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
                        .height(DigaUiDimensions.HeightFlankDpadButton),
                icon = Icons.Filled.VolumeDown,
                fontSize = DigaUiDimensions.TextSizeButtonCompact,
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

        // Center: Circular Target D-Pad controller (weight 2f)
        Box(
            modifier = Modifier.weight(2f),
            contentAlignment = Alignment.Center,
        ) {
            CircularTargetDpad(onSend = onSend)
        }

        // Right Flank: Channel controls (CH + / CH -) expanded to weight 1f to match navigation columns
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RemoteKey(
                label = DigaUiStrings.BTN_CH_UP,
                onClick = { onSend(DigaUiStrings.CMD_LABEL_CH_UP, OBC.CH_UP, false) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(DigaUiDimensions.HeightFlankDpadButton),
                icon = Icons.Filled.KeyboardArrowUp,
                fontSize = DigaUiDimensions.TextSizeButtonCompact,
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
                        .height(DigaUiDimensions.HeightFlankDpadButton),
                icon = Icons.Filled.KeyboardArrowDown,
                fontSize = DigaUiDimensions.TextSizeButtonCompact,
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

@Composable
fun TransportSection(onSend: SendCommand) {
    DigaCard(title = DigaUiStrings.SECTION_TRANSPORT) {
        KeyRow(transportMainRow, onSend, height = DigaUiDimensions.HeightStandardButton)
        KeyRow(transportSeekRow, onSend)
    }
}

@Composable
fun VcrSection(
    activeMode: RemoteMode,
    onSend: SendCommand,
) {
    DigaCard(
        title = DigaUiStrings.SECTION_VCR_DECK,
        accent = DigaAmber,
        trailing = {
            Text(
                text = activeMode.label,
                fontSize = DigaUiDimensions.TextSizeVfdStatus,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
    ) {
        KeyRow(vcrDeckRow, onSend)
        KeyRow(vcrTransportMainRow, onSend, height = DigaUiDimensions.HeightStandardButton)
        KeyRow(vcrTransportSeekRow, onSend)
        KeyRow(vcrTrackingRow, onSend)
        KeyRow(vcrDubRow, onSend)
    }
}

@Composable
fun EditingSection(onSend: SendCommand) {
    DigaCard(title = DigaUiStrings.SECTION_EDITING_FEATURES) {
        KeyRow(editingPlaybackRow, onSend)
        KeyRow(editingToolsRow, onSend)
    }
}

@Composable
fun RecordingSection(onSend: SendCommand) {
    DigaCard(title = DigaUiStrings.SECTION_RECORDING_BROADCAST) {
        KeyRow(recordingRow, onSend, height = DigaUiDimensions.HeightStandardButton)
    }
}

@Composable
fun KeypadSection(onSend: SendCommand) {
    DigaCard(title = DigaUiStrings.SECTION_NUMERIC_KEYPAD) {
        keypadRows.forEach { row -> KeyRow(row, onSend, height = DigaUiDimensions.HeightKeypadButton) }
        KeyRow(keypadFooterRow, onSend, height = DigaUiDimensions.HeightKeypadButton)
    }
}
