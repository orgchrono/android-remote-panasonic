package com.vhs.digacommander.presentation.ui.components

import androidx.compose.ui.graphics.Color
import com.vhs.digacommander.data.model.RemoteMode
import com.vhs.digacommander.presentation.theme.DigaAmber
import com.vhs.digacommander.presentation.theme.DigaBlueLight
import com.vhs.digacommander.presentation.theme.DigaStatusOk

/**
 * Single mapping from a remote channel to its filled-key accent role, shared by every
 * channel picker (SSOT).
 */
fun RemoteMode.keyStyle(): KeyStyle =
    when (this) {
        RemoteMode.MODE_1 -> KeyStyle.Primary
        RemoteMode.MODE_2 -> KeyStyle.Success
        RemoteMode.MODE_3 -> KeyStyle.Warning
    }

/**
 * Readable text colour (>= 4.5:1 on card surfaces) matching [keyStyle], for inline labels.
 */
fun RemoteMode.textColor(): Color =
    when (this) {
        RemoteMode.MODE_1 -> DigaBlueLight
        RemoteMode.MODE_2 -> DigaStatusOk
        RemoteMode.MODE_3 -> DigaAmber
    }
