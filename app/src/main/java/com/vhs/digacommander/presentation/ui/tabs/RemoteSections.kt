package com.vhs.digacommander.presentation.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.vhs.digacommander.data.catalog.PanasonicDigaCatalog.OBC
import com.vhs.digacommander.data.model.DigaModelProfile
import com.vhs.digacommander.data.model.RemoteMode
import com.vhs.digacommander.presentation.constant.DigaUiDimensions
import com.vhs.digacommander.presentation.constant.DigaUiStrings
import com.vhs.digacommander.presentation.theme.DigaAmber
import com.vhs.digacommander.presentation.ui.components.ChoiceRow
import com.vhs.digacommander.presentation.ui.components.DigaCard
import com.vhs.digacommander.presentation.ui.components.KeyStyle
import com.vhs.digacommander.presentation.ui.components.RemoteKey
import com.vhs.digacommander.presentation.ui.components.keyStyle
import com.vhs.digacommander.presentation.ui.components.textColor

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
    DigaCard(
        title = DigaUiStrings.SECTION_PROFILE_MODE,
        trailing = {
            Text(
                text = "${DigaUiStrings.LABEL_ACTIVE_PREFIX}${activeMode.label}",
                fontWeight = FontWeight.Bold,
                fontSize = DigaUiDimensions.TextSizeVfdSub,
                color = activeMode.textColor(),
            )
        },
    ) {
        ChoiceRow(
            options = DigaModelProfile.entries,
            selected = activeProfile,
            labelOf = { it.id },
            onSelect = onProfileSelected,
            height = DigaUiDimensions.HeightProfileButton,
        )
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

@Composable
private fun DPadCluster(onSend: SendCommand) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left Flank: Volume controls (VOL + / VOL -)
        Column(
            verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingDpadFlankGap),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RemoteKey(
                label = DigaUiStrings.BTN_VOL_UP,
                onClick = { onSend(DigaUiStrings.CMD_LABEL_VOL_UP, OBC.VOL_UP, false) },
                modifier =
                    Modifier.size(
                        width = DigaUiDimensions.WidthDpadFlankButton,
                        height = DigaUiDimensions.HeightDpadFlankButton,
                    ),
                icon = Icons.Filled.VolumeUp,
                fontSize = DigaUiDimensions.TextSizeDpadFlank,
                shape =
                    RoundedCornerShape(
                        topStart = DigaUiDimensions.RadiusDpadFlankOuter,
                        topEnd = DigaUiDimensions.RadiusDpadFlankOuter,
                        bottomStart = DigaUiDimensions.RadiusDpadFlankInner,
                        bottomEnd = DigaUiDimensions.RadiusDpadFlankInner,
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
                    Modifier.size(
                        width = DigaUiDimensions.WidthDpadFlankButton,
                        height = DigaUiDimensions.HeightDpadFlankButton,
                    ),
                icon = Icons.Filled.VolumeDown,
                fontSize = DigaUiDimensions.TextSizeDpadFlank,
                shape =
                    RoundedCornerShape(
                        topStart = DigaUiDimensions.RadiusDpadFlankInner,
                        topEnd = DigaUiDimensions.RadiusDpadFlankInner,
                        bottomStart = DigaUiDimensions.RadiusDpadFlankOuter,
                        bottomEnd = DigaUiDimensions.RadiusDpadFlankOuter,
                    ),
                contentPadding =
                    PaddingValues(
                        horizontal = DigaUiDimensions.PaddingCompactH,
                        vertical = DigaUiDimensions.PaddingCompactV,
                    ),
                isVerticalContent = true,
            )
        }

        // Center: Circular Navigation Wheel (UP, LEFT, OK, RIGHT, DOWN)
        Column(
            verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingDpadGap),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // UP Arc
            RemoteKey(
                label = DigaUiStrings.BTN_UP,
                onClick = { onSend(DigaUiStrings.CMD_LABEL_UP, OBC.UP, false) },
                modifier =
                    Modifier.size(
                        width = DigaUiDimensions.WidthDpadUpDownWheel,
                        height = DigaUiDimensions.HeightDpadUpDownWheel,
                    ),
                icon = Icons.Filled.KeyboardArrowUp,
                iconOnly = true,
                shape =
                    RoundedCornerShape(
                        topStart = DigaUiDimensions.RadiusDpadOuterArc,
                        topEnd = DigaUiDimensions.RadiusDpadOuterArc,
                        bottomStart = DigaUiDimensions.RadiusDpadInnerArc,
                        bottomEnd = DigaUiDimensions.RadiusDpadInnerArc,
                    ),
                contentPadding = PaddingValues(DigaUiDimensions.PaddingZero),
            )

            // Middle Row: LEFT Arc, Circular OK, RIGHT Arc
            Row(
                horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingDpadGap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // LEFT Arc
                RemoteKey(
                    label = DigaUiStrings.BTN_LEFT,
                    onClick = { onSend(DigaUiStrings.CMD_LABEL_LEFT, OBC.LEFT, false) },
                    modifier =
                        Modifier.size(
                            width = DigaUiDimensions.WidthDpadLeftRightWheel,
                            height = DigaUiDimensions.HeightDpadLeftRightWheel,
                        ),
                    icon = Icons.Filled.KeyboardArrowLeft,
                    iconOnly = true,
                    shape =
                        RoundedCornerShape(
                            topStart = DigaUiDimensions.RadiusDpadOuterArc,
                            topEnd = DigaUiDimensions.RadiusDpadInnerArc,
                            bottomStart = DigaUiDimensions.RadiusDpadOuterArc,
                            bottomEnd = DigaUiDimensions.RadiusDpadInnerArc,
                        ),
                    contentPadding = PaddingValues(DigaUiDimensions.PaddingZero),
                )

                // Circular OK Button in the center
                RemoteKey(
                    label = DigaUiStrings.BTN_OK,
                    onClick = { onSend(DigaUiStrings.CMD_LABEL_OK, OBC.ENTER_OK, false) },
                    modifier = Modifier.size(DigaUiDimensions.SizeDpadOkWheel),
                    style = KeyStyle.Primary,
                    fontSize = DigaUiDimensions.TextSizeDpadOk,
                    shape = CircleShape,
                    contentPadding = PaddingValues(DigaUiDimensions.PaddingZero),
                )

                // RIGHT Arc
                RemoteKey(
                    label = DigaUiStrings.BTN_RIGHT,
                    onClick = { onSend(DigaUiStrings.CMD_LABEL_RIGHT, OBC.RIGHT, false) },
                    modifier =
                        Modifier.size(
                            width = DigaUiDimensions.WidthDpadLeftRightWheel,
                            height = DigaUiDimensions.HeightDpadLeftRightWheel,
                        ),
                    icon = Icons.Filled.KeyboardArrowRight,
                    iconOnly = true,
                    shape =
                        RoundedCornerShape(
                            topStart = DigaUiDimensions.RadiusDpadInnerArc,
                            topEnd = DigaUiDimensions.RadiusDpadOuterArc,
                            bottomStart = DigaUiDimensions.RadiusDpadInnerArc,
                            bottomEnd = DigaUiDimensions.RadiusDpadOuterArc,
                        ),
                    contentPadding = PaddingValues(DigaUiDimensions.PaddingZero),
                )
            }

            // DOWN Arc
            RemoteKey(
                label = DigaUiStrings.BTN_DOWN,
                onClick = { onSend(DigaUiStrings.CMD_LABEL_DOWN, OBC.DOWN, false) },
                modifier =
                    Modifier.size(
                        width = DigaUiDimensions.WidthDpadUpDownWheel,
                        height = DigaUiDimensions.HeightDpadUpDownWheel,
                    ),
                icon = Icons.Filled.KeyboardArrowDown,
                iconOnly = true,
                shape =
                    RoundedCornerShape(
                        topStart = DigaUiDimensions.RadiusDpadInnerArc,
                        topEnd = DigaUiDimensions.RadiusDpadInnerArc,
                        bottomStart = DigaUiDimensions.RadiusDpadOuterArc,
                        bottomEnd = DigaUiDimensions.RadiusDpadOuterArc,
                    ),
                contentPadding = PaddingValues(DigaUiDimensions.PaddingZero),
            )
        }

        // Right Flank: Channel controls (CH + / CH -)
        Column(
            verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingDpadFlankGap),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RemoteKey(
                label = DigaUiStrings.BTN_CH_UP,
                onClick = { onSend(DigaUiStrings.CMD_LABEL_CH_UP, OBC.CH_UP, false) },
                modifier =
                    Modifier.size(
                        width = DigaUiDimensions.WidthDpadFlankButton,
                        height = DigaUiDimensions.HeightDpadFlankButton,
                    ),
                icon = Icons.Filled.KeyboardArrowUp,
                fontSize = DigaUiDimensions.TextSizeDpadFlank,
                shape =
                    RoundedCornerShape(
                        topStart = DigaUiDimensions.RadiusDpadFlankOuter,
                        topEnd = DigaUiDimensions.RadiusDpadFlankOuter,
                        bottomStart = DigaUiDimensions.RadiusDpadFlankInner,
                        bottomEnd = DigaUiDimensions.RadiusDpadFlankInner,
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
                    Modifier.size(
                        width = DigaUiDimensions.WidthDpadFlankButton,
                        height = DigaUiDimensions.HeightDpadFlankButton,
                    ),
                icon = Icons.Filled.KeyboardArrowDown,
                fontSize = DigaUiDimensions.TextSizeDpadFlank,
                shape =
                    RoundedCornerShape(
                        topStart = DigaUiDimensions.RadiusDpadFlankInner,
                        topEnd = DigaUiDimensions.RadiusDpadFlankInner,
                        bottomStart = DigaUiDimensions.RadiusDpadFlankOuter,
                        bottomEnd = DigaUiDimensions.RadiusDpadFlankOuter,
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
