package com.vhs.digacommander.presentation.ui.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.vhs.digacommander.data.catalog.PanasonicDigaCatalog
import com.vhs.digacommander.data.model.DigaModelProfile
import com.vhs.digacommander.data.model.RemoteMode
import com.vhs.digacommander.presentation.constant.DigaUiDimensions
import com.vhs.digacommander.presentation.constant.DigaUiStrings
import com.vhs.digacommander.presentation.theme.*

/**
 * Main Remote Control tab mirroring the physical Panasonic EUR7729KG0 controller.
 */
@Composable
fun RemoteTab(
    activeProfile: DigaModelProfile,
    activeMode: RemoteMode,
    onProfileSelected: (DigaModelProfile) -> Unit,
    onModeSelected: (RemoteMode) -> Unit,
    onSendCommand: (label: String, command: Int, isVcr: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(DigaUiDimensions.PaddingScreen),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingSections),
    ) {
        // --- SECTION 0: RECORDER PROFILE & IR CHANNEL MODE SELECTOR ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface),
        ) {
            Column(modifier = Modifier.padding(DigaUiDimensions.PaddingCardCompact)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = DigaUiStrings.SECTION_PROFILE_MODE,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                    )
                    Text(
                        text = "${DigaUiStrings.LABEL_ACTIVE_PREFIX}${activeMode.label}",
                        fontWeight = FontWeight.Bold,
                        fontSize = DigaUiDimensions.TextSizeVfdSub,
                        color =
                            when (activeMode) {
                                RemoteMode.MODE_1 -> DigaBlueLight
                                RemoteMode.MODE_2 -> DigaGreen
                                RemoteMode.MODE_3 -> DigaAmber
                            },
                    )
                }

                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))

                // Profile Selector Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingTight),
                ) {
                    DigaModelProfile.entries.forEach { profile ->
                        val isSelected = activeProfile == profile
                        Button(
                            onClick = { onProfileSelected(profile) },
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .height(DigaUiDimensions.HeightProfileButton),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) DigaBlue else DigaSurfaceVariant,
                                    contentColor = if (isSelected) Color.White else Color.LightGray,
                                ),
                            shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                            contentPadding = PaddingValues(horizontal = DigaUiDimensions.PaddingProfileButtonH),
                        ) {
                            Text(
                                text = profile.id,
                                fontSize = DigaUiDimensions.TextSizeProfileTab,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))

                // Channel Selector Row (SET 1, SET 2, SET 3)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
                ) {
                    RemoteMode.entries.forEach { mode ->
                        val isSelected = activeMode == mode
                        val accentColor =
                            when (mode) {
                                RemoteMode.MODE_1 -> DigaBlue
                                RemoteMode.MODE_2 -> DigaGreen
                                RemoteMode.MODE_3 -> DigaAmber
                            }
                        Button(
                            onClick = { onModeSelected(mode) },
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .height(DigaUiDimensions.HeightModeButton),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) accentColor else DigaSurfaceVariant,
                                    contentColor = if (isSelected) Color.White else Color.LightGray,
                                ),
                            shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                            contentPadding = PaddingValues(horizontal = DigaUiDimensions.PaddingModeButtonH),
                        ) {
                            Text(
                                text = mode.label,
                                fontSize = DigaUiDimensions.TextSizeButtonCompact,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            )
                        }
                    }
                }
            }
        }

        // --- SECTION 1: SYSTEM & DRIVE SELECTION ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface),
        ) {
            Column(modifier = Modifier.padding(DigaUiDimensions.PaddingCardCompact)) {
                Text(
                    text = DigaUiStrings.SECTION_SYSTEM_DRIVE,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                )
                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
                ) {
                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_POWER, PanasonicDigaCatalog.OBC.POWER, false) },
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaRed),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_POWER,
                            fontWeight = FontWeight.Bold,
                            fontSize = DigaUiDimensions.TextSizeButtonStandard,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_EJECT, PanasonicDigaCatalog.OBC.EJECT, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_EJECT,
                            fontSize = DigaUiDimensions.TextSizeButtonCompact,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_STATUS, PanasonicDigaCatalog.OBC.STATUS, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_STATUS,
                            fontSize = DigaUiDimensions.TextSizeButtonCompact,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_DISPLAY, PanasonicDigaCatalog.OBC.DISPLAY, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_DISPLAY,
                            fontSize = DigaUiDimensions.TextSizeButtonCompact,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingRows),
                ) {
                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_DRIVE_SELECT, PanasonicDigaCatalog.OBC.DRIVE_SELECT, false) },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaBlue),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_DRIVE_SELECT_ALL,
                            fontWeight = FontWeight.Bold,
                            fontSize = DigaUiDimensions.TextSizeButtonCompact,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_INPUT_SELECT, PanasonicDigaCatalog.OBC.INPUT_SELECT, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_INPUT_SELECT,
                            fontSize = DigaUiDimensions.TextSizeButtonCompact,
                        )
                    }
                }
            }
        }

        // --- SECTION 2: NAVIGATION & MENUS (D-PAD) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface),
        ) {
            Column(
                modifier = Modifier.padding(DigaUiDimensions.PaddingCardCompact),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = DigaUiStrings.SECTION_NAVIGATION_MENUS,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start),
                )

                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
                ) {
                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_DIRECT_NAV, PanasonicDigaCatalog.OBC.DIRECT_NAVIGATOR, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaBlueLight),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_DIRECT_NAV,
                            fontSize = DigaUiDimensions.TextSizeButtonMini,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_FUNCTIONS, PanasonicDigaCatalog.OBC.FUNCTIONS, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_FUNCTIONS,
                            fontSize = DigaUiDimensions.TextSizeButtonMini,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_TOP_MENU, PanasonicDigaCatalog.OBC.TOP_MENU, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_TOP_MENU,
                            fontSize = DigaUiDimensions.TextSizeButtonMini,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_OPTION, PanasonicDigaCatalog.OBC.OPTION_SUB_MENU, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_OPTION,
                            fontSize = DigaUiDimensions.TextSizeButtonMini,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(DigaUiDimensions.PaddingCardCompact))

                // D-Pad Directional Controls
                Button(
                    onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_UP, PanasonicDigaCatalog.OBC.UP, false) },
                    modifier =
                        Modifier.size(
                            width = DigaUiDimensions.WidthDpadUpDown,
                            height = DigaUiDimensions.HeightDpadUpDown,
                        ),
                    colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                    shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                ) {
                    Text(
                        text = DigaUiStrings.BTN_UP,
                        fontSize = DigaUiDimensions.TextSizeDpadIcon,
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingRows),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_LEFT, PanasonicDigaCatalog.OBC.LEFT, false) },
                        modifier =
                            Modifier.size(
                                width = DigaUiDimensions.WidthDpadLeftRight,
                                height = DigaUiDimensions.HeightDpadLeftRight,
                            ),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_LEFT,
                            fontSize = DigaUiDimensions.TextSizeDpadIcon,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_OK, PanasonicDigaCatalog.OBC.ENTER_OK, false) },
                        modifier = Modifier.size(DigaUiDimensions.SizeDpadOkButton),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaBlue),
                        shape = CircleShape,
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_OK,
                            fontWeight = FontWeight.Bold,
                            fontSize = DigaUiDimensions.TextSizeDpadOk,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_RIGHT, PanasonicDigaCatalog.OBC.RIGHT, false) },
                        modifier =
                            Modifier.size(
                                width = DigaUiDimensions.WidthDpadLeftRight,
                                height = DigaUiDimensions.HeightDpadLeftRight,
                            ),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_RIGHT,
                            fontSize = DigaUiDimensions.TextSizeDpadIcon,
                        )
                    }
                }

                Button(
                    onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_DOWN, PanasonicDigaCatalog.OBC.DOWN, false) },
                    modifier =
                        Modifier.size(
                            width = DigaUiDimensions.WidthDpadUpDown,
                            height = DigaUiDimensions.HeightDpadUpDown,
                        ),
                    colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                    shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                ) {
                    Text(
                        text = DigaUiStrings.BTN_DOWN,
                        fontSize = DigaUiDimensions.TextSizeDpadIcon,
                    )
                }

                Spacer(modifier = Modifier.height(DigaUiDimensions.PaddingCardCompact))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
                ) {
                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_RETURN, PanasonicDigaCatalog.OBC.RETURN, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_RETURN,
                            fontSize = DigaUiDimensions.TextSizeButtonMini,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_EXIT, PanasonicDigaCatalog.OBC.EXIT, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_EXIT,
                            fontSize = DigaUiDimensions.TextSizeButtonMini,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_GUIDE, PanasonicDigaCatalog.OBC.GUIDE, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_GUIDE,
                            fontSize = DigaUiDimensions.TextSizeButtonMini,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_PROG_CHECK, PanasonicDigaCatalog.OBC.PROG_CHECK, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_PROG_CHECK,
                            fontSize = DigaUiDimensions.TextSizeButtonMini,
                        )
                    }
                }
            }
        }

        // --- SECTION 3: TRANSPORT DECK ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface),
        ) {
            Column(modifier = Modifier.padding(DigaUiDimensions.PaddingCardCompact)) {
                Text(
                    text = DigaUiStrings.SECTION_TRANSPORT,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                )
                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingRows),
                ) {
                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_PLAY, PanasonicDigaCatalog.OBC.PLAY, false) },
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaGreen),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_PLAY,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_PAUSE, PanasonicDigaCatalog.OBC.PAUSE, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_PAUSE,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_STOP, PanasonicDigaCatalog.OBC.STOP, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaRedDark),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_STOP,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
                ) {
                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_SKIP_PREV, PanasonicDigaCatalog.OBC.SKIP_PREV, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_SKIP_PREV,
                            fontWeight = FontWeight.Bold,
                            fontSize = DigaUiDimensions.TextSizeTransportSymbol,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_REWIND, PanasonicDigaCatalog.OBC.REWIND, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_REWIND,
                            fontWeight = FontWeight.Bold,
                            fontSize = DigaUiDimensions.TextSizeTransportSymbol,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_FAST_FORWARD, PanasonicDigaCatalog.OBC.FAST_FORWARD, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_FAST_FORWARD,
                            fontWeight = FontWeight.Bold,
                            fontSize = DigaUiDimensions.TextSizeTransportSymbol,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_SKIP_NEXT, PanasonicDigaCatalog.OBC.SKIP_NEXT, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_SKIP_NEXT,
                            fontWeight = FontWeight.Bold,
                            fontSize = DigaUiDimensions.TextSizeTransportSymbol,
                        )
                    }
                }
            }
        }

        // --- SECTION EXTRA: VCR DECK (COMBO VHS / S-VHS - DEVICE 0x90) ---
        if (activeProfile == DigaModelProfile.COMBO_VHS_DVD || activeProfile == DigaModelProfile.UNIVERSAL) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DigaSurface),
            ) {
                Column(modifier = Modifier.padding(DigaUiDimensions.PaddingCardCompact)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = DigaUiStrings.SECTION_VCR_DECK,
                            style = MaterialTheme.typography.labelSmall,
                            color = DigaAmber,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = activeMode.label,
                            fontSize = DigaUiDimensions.TextSizeVfdStatus,
                            color = Color.Gray,
                        )
                    }
                    Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
                    ) {
                        Button(
                            onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_VCR_DRIVE, PanasonicDigaCatalog.OBC.DRIVE_SELECT, true) },
                            modifier = Modifier.weight(1.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                            shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                        ) {
                            Text(
                                text = DigaUiStrings.BTN_VCR_DRIVE,
                                fontWeight = FontWeight.Bold,
                                fontSize = DigaUiDimensions.TextSizeButtonCompact,
                                color = Color.Black,
                            )
                        }

                        Button(
                            onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_VCR_EJECT, PanasonicDigaCatalog.OBC.EJECT, true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                            shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                        ) {
                            Text(
                                text = DigaUiStrings.BTN_VCR_EJECT,
                                fontSize = DigaUiDimensions.TextSizeButtonCompact,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
                    ) {
                        Button(
                            onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_VCR_PLAY, PanasonicDigaCatalog.OBC.PLAY, true) },
                            modifier = Modifier.weight(1.2f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaGreen),
                            shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                        ) {
                            Text(
                                text = DigaUiStrings.BTN_VCR_PLAY,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        Button(
                            onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_VCR_PAUSE, PanasonicDigaCatalog.OBC.PAUSE, true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                            shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                        ) {
                            Text(
                                text = DigaUiStrings.BTN_VCR_PAUSE,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        Button(
                            onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_VCR_STOP, PanasonicDigaCatalog.OBC.STOP, true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaRedDark),
                            shape = RoundedCornerShape(DigaUiDimensions.RadiusButton),
                        ) {
                            Text(
                                text = DigaUiStrings.BTN_VCR_STOP,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
                    ) {
                        Button(
                            onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_VCR_REWIND, PanasonicDigaCatalog.OBC.REWIND, true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                            shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                        ) {
                            Text(
                                text = DigaUiStrings.BTN_VCR_REWIND,
                                fontWeight = FontWeight.Bold,
                                fontSize = DigaUiDimensions.TextSizeButtonCompact,
                            )
                        }

                        Button(
                            onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_VCR_FF, PanasonicDigaCatalog.OBC.FAST_FORWARD, true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                            shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                        ) {
                            Text(
                                text = DigaUiStrings.BTN_VCR_FF,
                                fontWeight = FontWeight.Bold,
                                fontSize = DigaUiDimensions.TextSizeButtonCompact,
                            )
                        }

                        Button(
                            onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_VCR_REC, PanasonicDigaCatalog.OBC.REC, true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaRed),
                            shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                        ) {
                            Text(
                                text = DigaUiStrings.BTN_VCR_REC,
                                fontWeight = FontWeight.Bold,
                                fontSize = DigaUiDimensions.TextSizeButtonCompact,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
                    ) {
                        Button(
                            onClick = {
                                onSendCommand(
                                    DigaUiStrings.CMD_LABEL_VCR_TRACKING_UP,
                                    PanasonicDigaCatalog.OBC.VCR_TRACKING_PLUS,
                                    true,
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaBlue),
                            shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                        ) {
                            Text(
                                text = DigaUiStrings.BTN_VCR_TRACKING_UP,
                                fontWeight = FontWeight.Bold,
                                fontSize = DigaUiDimensions.TextSizeButtonCompact,
                            )
                        }

                        Button(
                            onClick = {
                                onSendCommand(
                                    DigaUiStrings.CMD_LABEL_VCR_TRACKING_DOWN,
                                    PanasonicDigaCatalog.OBC.VCR_TRACKING_MINUS,
                                    true,
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DigaBlue),
                            shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                        ) {
                            Text(
                                text = DigaUiStrings.BTN_VCR_TRACKING_DOWN,
                                fontWeight = FontWeight.Bold,
                                fontSize = DigaUiDimensions.TextSizeButtonCompact,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
                    ) {
                        Button(
                            onClick = {
                                onSendCommand(
                                    DigaUiStrings.CMD_LABEL_VCR_DUB_VHS_TO_DVD,
                                    PanasonicDigaCatalog.OBC.VCR_DUB_VHS_TO_DVD,
                                    false,
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                            shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                        ) {
                            Text(
                                text = DigaUiStrings.BTN_DUB_VHS_TO_DVD,
                                fontWeight = FontWeight.Bold,
                                fontSize = DigaUiDimensions.TextSizeButtonMini,
                            )
                        }

                        Button(
                            onClick = {
                                onSendCommand(
                                    DigaUiStrings.CMD_LABEL_VCR_DUB_DVD_TO_VHS,
                                    PanasonicDigaCatalog.OBC.VCR_DUB_DVD_TO_VHS,
                                    false,
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                            shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                        ) {
                            Text(
                                text = DigaUiStrings.BTN_DUB_DVD_TO_VHS,
                                fontWeight = FontWeight.Bold,
                                fontSize = DigaUiDimensions.TextSizeButtonMini,
                            )
                        }
                    }
                }
            }
        }

        // --- SECTION 4: DIGA EDITING & ADVANCED PLAYBACK ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface),
        ) {
            Column(modifier = Modifier.padding(DigaUiDimensions.PaddingCardCompact)) {
                Text(
                    text = DigaUiStrings.SECTION_EDITING_FEATURES,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                )
                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
                ) {
                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_TIME_SLIP, PanasonicDigaCatalog.OBC.TIME_SLIP, false) },
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaAmber),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_TIME_SLIP,
                            fontWeight = FontWeight.Bold,
                            fontSize = DigaUiDimensions.TextSizeButtonMini,
                            color = Color.Black,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_REPLAY_10S, PanasonicDigaCatalog.OBC.MANUAL_REPLAY_10S, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_REPLAY_10S,
                            fontSize = DigaUiDimensions.TextSizeButtonCompact,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_SKIP_30S, PanasonicDigaCatalog.OBC.MANUAL_SKIP_30S, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_SKIP_30S,
                            fontSize = DigaUiDimensions.TextSizeButtonCompact,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
                ) {
                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_CREATE_CHAPTER, PanasonicDigaCatalog.OBC.CREATE_CHAPTER, false) },
                        modifier = Modifier.weight(1.1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_CREATE_CHAPTER,
                            fontSize = DigaUiDimensions.TextSizeButtonMini,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_DELETE_TITLE, PanasonicDigaCatalog.OBC.DELETE_TITLE, false) },
                        modifier = Modifier.weight(1.1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaRedDark),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_DELETE_TITLE,
                            fontSize = DigaUiDimensions.TextSizeButtonMini,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_AUDIO, PanasonicDigaCatalog.OBC.AUDIO, false) },
                        modifier = Modifier.weight(0.9f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_AUDIO,
                            fontSize = DigaUiDimensions.TextSizeButtonMini,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_SUBTITLE, PanasonicDigaCatalog.OBC.SUBTITLE, false) },
                        modifier = Modifier.weight(0.9f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_SUBTITLE,
                            fontSize = DigaUiDimensions.TextSizeButtonMini,
                        )
                    }
                }
            }
        }

        // --- SECTION 5: RECORDING & BROADCAST ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface),
        ) {
            Column(modifier = Modifier.padding(DigaUiDimensions.PaddingCardCompact)) {
                Text(
                    text = DigaUiStrings.SECTION_RECORDING_BROADCAST,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                )
                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
                ) {
                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_REC, PanasonicDigaCatalog.OBC.REC, false) },
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaRed),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_REC,
                            fontWeight = FontWeight.Bold,
                            fontSize = DigaUiDimensions.TextSizeButtonStandard,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_REC_MODE, PanasonicDigaCatalog.OBC.REC_MODE, false) },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_REC_MODE,
                            fontSize = DigaUiDimensions.TextSizeButtonMini,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_EXT_LINK, PanasonicDigaCatalog.OBC.EXT_LINK, false) },
                        modifier = Modifier.weight(1.1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_EXT_LINK,
                            fontSize = DigaUiDimensions.TextSizeButtonMini,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingRows),
                ) {
                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_CH_UP, PanasonicDigaCatalog.OBC.CH_UP, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_CH_UP,
                            fontWeight = FontWeight.Bold,
                            fontSize = DigaUiDimensions.TextSizeTransportSymbol,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_CH_DOWN, PanasonicDigaCatalog.OBC.CH_DOWN, false) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_CH_DOWN,
                            fontWeight = FontWeight.Bold,
                            fontSize = DigaUiDimensions.TextSizeTransportSymbol,
                        )
                    }
                }
            }
        }

        // --- SECTION 6: NUMERIC KEYPAD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DigaSurface),
        ) {
            Column(
                modifier = Modifier.padding(DigaUiDimensions.PaddingCardCompact),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = DigaUiStrings.SECTION_NUMERIC_KEYPAD,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start),
                )

                Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))

                val digits =
                    listOf(
                        listOf(
                            "1" to PanasonicDigaCatalog.OBC.DIGIT_1,
                            "2" to PanasonicDigaCatalog.OBC.DIGIT_2,
                            "3" to PanasonicDigaCatalog.OBC.DIGIT_3,
                        ),
                        listOf(
                            "4" to PanasonicDigaCatalog.OBC.DIGIT_4,
                            "5" to PanasonicDigaCatalog.OBC.DIGIT_5,
                            "6" to PanasonicDigaCatalog.OBC.DIGIT_6,
                        ),
                        listOf(
                            "7" to PanasonicDigaCatalog.OBC.DIGIT_7,
                            "8" to PanasonicDigaCatalog.OBC.DIGIT_8,
                            "9" to PanasonicDigaCatalog.OBC.DIGIT_9,
                        ),
                    )

                for (row in digits) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingRows),
                    ) {
                        for ((label, cmd) in row) {
                            Button(
                                onClick = { onSendCommand(label, cmd, false) },
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .height(DigaUiDimensions.HeightKeypadButton),
                                colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                                shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                            ) {
                                Text(
                                    text = label,
                                    fontSize = DigaUiDimensions.TextSizeKeypadDigit,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(DigaUiDimensions.SpacingRows))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingRows),
                ) {
                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_MEDIA_SERVER, PanasonicDigaCatalog.OBC.MEDIA_SERVER, false) },
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(DigaUiDimensions.HeightKeypadButton),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_MEDIA,
                            fontSize = DigaUiDimensions.TextSizeButtonMini,
                        )
                    }

                    Button(
                        onClick = { onSendCommand("0", PanasonicDigaCatalog.OBC.DIGIT_0, false) },
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(DigaUiDimensions.HeightKeypadButton),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = "0",
                            fontSize = DigaUiDimensions.TextSizeKeypadDigit,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Button(
                        onClick = { onSendCommand(DigaUiStrings.CMD_LABEL_CANCEL, PanasonicDigaCatalog.OBC.CANCEL, false) },
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(DigaUiDimensions.HeightKeypadButton),
                        colors = ButtonDefaults.buttonColors(containerColor = DigaSurfaceVariant),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusButtonCompact),
                    ) {
                        Text(
                            text = DigaUiStrings.BTN_CANCEL,
                            fontSize = DigaUiDimensions.TextSizeButtonMini,
                        )
                    }
                }
            }
        }
    }
}
