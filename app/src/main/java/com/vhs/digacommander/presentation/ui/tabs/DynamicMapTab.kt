package com.vhs.digacommander.presentation.ui.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vhs.digacommander.data.model.DynamicKeymapProfile
import com.vhs.digacommander.data.model.DynamicRemoteKey
import com.vhs.digacommander.data.model.KeyProtocol
import com.vhs.digacommander.data.model.KeySection
import com.vhs.digacommander.data.model.KeyTone
import com.vhs.digacommander.data.model.PanasonicCatalogCodeset
import com.vhs.digacommander.data.model.PanasonicCategory
import com.vhs.digacommander.data.model.RemoteMode
import com.vhs.digacommander.presentation.constant.DigaUiDimensions
import com.vhs.digacommander.presentation.constant.DigaUiStrings
import com.vhs.digacommander.presentation.theme.DigaAmber
import com.vhs.digacommander.presentation.theme.DigaBlue
import com.vhs.digacommander.presentation.theme.DigaBlueLight
import com.vhs.digacommander.presentation.theme.DigaGreen
import com.vhs.digacommander.presentation.theme.DigaOutline
import com.vhs.digacommander.presentation.theme.DigaStatusWarn
import com.vhs.digacommander.presentation.theme.DigaSurfaceVariant
import com.vhs.digacommander.presentation.theme.DigaTextMuted
import com.vhs.digacommander.presentation.theme.DigaTextPrimary
import com.vhs.digacommander.presentation.theme.DigaTextSecondary
import com.vhs.digacommander.presentation.ui.components.ChoiceRow
import com.vhs.digacommander.presentation.ui.components.DigaBodyText
import com.vhs.digacommander.presentation.ui.components.DigaCard
import com.vhs.digacommander.presentation.ui.components.KeyStyle
import com.vhs.digacommander.presentation.ui.components.RemoteKey
import com.vhs.digacommander.presentation.ui.components.keyStyle
import com.vhs.digacommander.presentation.viewmodel.DigaUiState

/**
 * Self-assembling dynamic keymap & all-Panasonic offline database tab.
 *
 * Allows searching across all bundled Panasonic models (~14.7k models / 1.7k+ codesets),
 * auto-assembling an interactive remote grid, and live-editing any button's label, protocol,
 * Kaseikyo Device/Subdevice/OBC bytes, section, color tone, order, or Pronto Hex at runtime.
 */
@Composable
fun DynamicMapTab(
    uiState: DigaUiState,
    onModeSelected: (RemoteMode) -> Unit,
    onSendDynamicKey: (DynamicRemoteKey) -> Unit,
    onSelectProfile: (String) -> Unit,
    onDeleteProfile: (String) -> Unit,
    onToggleEditMode: () -> Unit,
    onUpsertKey: (DynamicRemoteKey) -> Unit,
    onDuplicateKey: (DynamicRemoteKey) -> Unit,
    onDeleteKey: (String) -> Unit,
    onMoveKey: (String, Int) -> Unit,
    onResetProfile: () -> Unit,
    onCatalogSearchChanged: (String, PanasonicCategory) -> Unit,
    onAssembleCodeset: (PanasonicCatalogCodeset, String?) -> Unit,
    onImportText: (String, String) -> Boolean,
    onExportIrplus: () -> String,
    modifier: Modifier = Modifier,
) {
    var showCatalogDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var exportXmlPreview by remember { mutableStateOf<String?>(null) }
    var editingKey by remember { mutableStateOf<Pair<DynamicRemoteKey, Boolean>?>(null) }
    var pendingConfirmKey by remember { mutableStateOf<DynamicRemoteKey?>(null) }

    val activeProfile = uiState.activeDynamicProfile
    val groupedSections =
        remember(activeProfile.keys) {
            KeySection.entries.mapNotNull { sec ->
                val list = activeProfile.keys.filter { it.section == sec }
                if (list.isEmpty()) null else sec to list
            }
        }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        val columns = if (maxWidth >= DigaUiDimensions.BreakpointTabletCards) 3 else 2

        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .width(maxWidth.coerceAtMost(DigaUiDimensions.MaxContentWidth)),
            contentPadding = PaddingValues(DigaUiDimensions.PaddingScreen),
            verticalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingCards),
        ) {
            item(key = "header_controls") {
                DynamicMapToolbarCard(
                    uiState = uiState,
                    activeProfile = activeProfile,
                    onModeSelected = onModeSelected,
                    onSelectProfile = onSelectProfile,
                    onDeleteProfile = onDeleteProfile,
                    onOpenCatalog = { showCatalogDialog = true },
                    onToggleEditMode = onToggleEditMode,
                    onAddNewKey = {
                        editingKey =
                            DynamicRemoteKey(
                                id = "custom_${System.currentTimeMillis()}",
                                label = "NEW KEY",
                                section = KeySection.CUSTOM,
                                protocol = KeyProtocol.KASEIKYO,
                                device = 0xB0,
                                subdevice = 0x00,
                                obc = 0x00,
                                tone = KeyTone.ACCENT,
                            ) to true
                    },
                    onOpenImport = { showImportDialog = true },
                    onOpenExport = { exportXmlPreview = onExportIrplus() },
                    onResetProfile = onResetProfile,
                )
            }

            if (uiState.isMapEditMode) {
                item(key = "edit_banner") {
                    Surface(
                        color = DigaAmber.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(DigaUiDimensions.RadiusCard),
                        border = BorderStroke(DigaUiDimensions.BorderWidthHairline, DigaAmber),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null, tint = DigaAmber, modifier = Modifier.size(18.dp))
                            Text(
                                text = DigaUiStrings.KEYMAP_EDIT_HINT,
                                color = DigaTextPrimary,
                                fontSize = DigaUiDimensions.TextSizeBody,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            items(groupedSections, key = { it.first.name }) { (section, keysInSection) ->
                DynamicSectionCard(
                    section = section,
                    keys = keysInSection,
                    columns = columns,
                    isEditMode = uiState.isMapEditMode,
                    activeMode = uiState.activeMode,
                    onKeyClick = { key ->
                        if (uiState.isMapEditMode) {
                            editingKey = key to false
                        } else if (key.confirmRequired) {
                            pendingConfirmKey = key
                        } else {
                            onSendDynamicKey(key)
                        }
                    },
                )
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showCatalogDialog) {
        PanasonicCatalogBrowserDialog(
            query = uiState.catalogSearchQuery,
            category = uiState.catalogCategory,
            results = uiState.catalogResults,
            codesetCount = uiState.catalogCodesetCount,
            modelCount = uiState.catalogModelCount,
            loading = uiState.catalogLoading,
            onSearchChanged = onCatalogSearchChanged,
            onSelectCodeset = { codeset, preferredModel ->
                onAssembleCodeset(codeset, preferredModel)
                showCatalogDialog = false
            },
            onDismiss = { showCatalogDialog = false },
        )
    }

    editingKey?.let { (targetKey, isNew) ->
        DynamicKeyEditorDialog(
            initialKey = targetKey,
            isNew = isNew,
            activeMode = uiState.activeMode,
            onTestTransmit = onSendDynamicKey,
            onSave = { savedKey ->
                onUpsertKey(savedKey)
                editingKey = null
            },
            onDuplicate = {
                onDuplicateKey(targetKey)
                editingKey = null
            },
            onMove = { delta ->
                onMoveKey(targetKey.id, delta)
            },
            onDelete = {
                onDeleteKey(targetKey.id)
                editingKey = null
            },
            onDismiss = { editingKey = null },
        )
    }

    if (showImportDialog) {
        ImportKeymapDialog(
            onImport = { text, name ->
                val ok = onImportText(text, name)
                if (ok) showImportDialog = false
                ok
            },
            onDismiss = { showImportDialog = false },
        )
    }

    exportXmlPreview?.let { xml ->
        ExportKeymapDialog(
            xml = xml,
            onDismiss = { exportXmlPreview = null },
        )
    }

    pendingConfirmKey?.let { dangerKey ->
        AlertDialog(
            onDismissRequest = { pendingConfirmKey = null },
            title = { Text(DigaUiStrings.KEYMAP_CONFIRM_SEND_TITLE) },
            text = {
                Text(
                    "Transmit '${dangerKey.label}' (${dangerKey.formatWirePreview(uiState.activeMode)})?",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onSendDynamicKey(dangerKey)
                        pendingConfirmKey = null
                    },
                ) {
                    Text(DigaUiStrings.DIALOG_CONFIRM, color = DigaAmber)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingConfirmKey = null }) {
                    Text(DigaUiStrings.DIALOG_CANCEL)
                }
            },
        )
    }
}

@Composable
private fun DynamicMapToolbarCard(
    uiState: DigaUiState,
    activeProfile: DynamicKeymapProfile,
    onModeSelected: (RemoteMode) -> Unit,
    onSelectProfile: (String) -> Unit,
    onDeleteProfile: (String) -> Unit,
    onOpenCatalog: () -> Unit,
    onToggleEditMode: () -> Unit,
    onAddNewKey: () -> Unit,
    onOpenImport: () -> Unit,
    onOpenExport: () -> Unit,
    onResetProfile: () -> Unit,
) {
    val statsBadge =
        if (uiState.catalogCodesetCount > 0) {
            "${uiState.catalogCodesetCount} DB | ${uiState.catalogModelCount} MODELS"
        } else {
            "${activeProfile.keys.size} KEYS"
        }

    DigaCard(
        title = DigaUiStrings.KEYMAP_CARD_ACTIVE_TITLE,
        icon = Icons.Filled.Tune,
        accent = DigaBlueLight,
        trailing = {
            Text(
                text = statsBadge,
                color = DigaTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        },
    ) {
        Text(
            text = activeProfile.name + if (activeProfile.isUserModified) " *" else "",
            color = DigaTextPrimary,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        if (uiState.dynamicProfiles.size > 1) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.dynamicProfiles, key = { it.id }) { profile ->
                    val selected = profile.id == activeProfile.id
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectProfile(profile.id) },
                        label = { Text(profile.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        trailingIcon =
                            if (uiState.dynamicProfiles.size > 1 && selected) {
                                {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription = "Remove profile",
                                        modifier =
                                            Modifier
                                                .size(16.dp)
                                                .clickable { onDeleteProfile(profile.id) },
                                    )
                                }
                            } else {
                                null
                            },
                        colors =
                            FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DigaBlue,
                                selectedLabelColor = DigaTextPrimary,
                            ),
                    )
                }
            }
        }

        ChoiceRow(
            options = RemoteMode.entries,
            selected = uiState.activeMode,
            labelOf = { it.label },
            onSelect = onModeSelected,
            accentOf = { it.keyStyle() },
            height = DigaUiDimensions.HeightMiniButton,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
        ) {
            RemoteKey(
                label = DigaUiStrings.KEYMAP_BTN_CATALOG,
                onClick = onOpenCatalog,
                modifier = Modifier.weight(1.2f),
                style = KeyStyle.Primary,
                icon = Icons.Filled.Search,
                height = DigaUiDimensions.HeightMiniButton,
            )
            RemoteKey(
                label = if (uiState.isMapEditMode) DigaUiStrings.KEYMAP_BTN_EDIT_ON else DigaUiStrings.KEYMAP_BTN_EDIT_OFF,
                onClick = onToggleEditMode,
                modifier = Modifier.weight(1f),
                style = if (uiState.isMapEditMode) KeyStyle.Warning else KeyStyle.Neutral,
                icon = Icons.Filled.Edit,
                height = DigaUiDimensions.HeightMiniButton,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingTight),
        ) {
            OutlinedButton(onClick = onAddNewKey, modifier = Modifier.heightIn(min = 40.dp)) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(DigaUiStrings.KEYMAP_BTN_ADD_KEY, fontSize = 12.sp)
            }
            OutlinedButton(onClick = onOpenImport, modifier = Modifier.heightIn(min = 40.dp)) {
                Icon(Icons.Filled.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(DigaUiStrings.KEYMAP_BTN_IMPORT, fontSize = 12.sp)
            }
            OutlinedButton(onClick = onOpenExport, modifier = Modifier.heightIn(min = 40.dp)) {
                Icon(Icons.Filled.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(DigaUiStrings.KEYMAP_BTN_EXPORT, fontSize = 12.sp)
            }
            OutlinedButton(onClick = onResetProfile, modifier = Modifier.heightIn(min = 40.dp)) {
                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(DigaUiStrings.KEYMAP_BTN_RESET, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun DynamicSectionCard(
    section: KeySection,
    keys: List<DynamicRemoteKey>,
    columns: Int,
    isEditMode: Boolean,
    activeMode: RemoteMode,
    onKeyClick: (DynamicRemoteKey) -> Unit,
) {
    val rows = remember(keys, columns) { keys.chunked(columns) }
    DigaCard(
        title = "${section.title.uppercase()} (${keys.size})",
        accent = if (isEditMode) DigaAmber else DigaTextMuted,
    ) {
        rows.forEach { rowKeys ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DigaUiDimensions.SpacingButtons),
            ) {
                rowKeys.forEach { key ->
                    val labelText =
                        if (isEditMode && key.protocol == KeyProtocol.KASEIKYO) {
                            val sub = key.effectiveSubdevice(activeMode)
                            "${key.label} [${hex2(key.device)}:${hex2(sub)}:${hex2(key.obc)}]"
                        } else {
                            key.label
                        }
                    RemoteKey(
                        label = labelText,
                        onClick = { onKeyClick(key) },
                        modifier = Modifier.weight(1f),
                        style = key.tone.toKeyStyle(),
                        icon = if (isEditMode) Icons.Filled.Edit else null,
                        height = DigaUiDimensions.HeightKey,
                        fontSize = if (isEditMode) 12.sp else DigaUiDimensions.TextSizeKey,
                    )
                }
                repeat(columns - rowKeys.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PanasonicCatalogBrowserDialog(
    query: String,
    category: PanasonicCategory,
    results: List<PanasonicCatalogCodeset>,
    codesetCount: Int,
    modelCount: Int,
    loading: Boolean,
    onSearchChanged: (String, PanasonicCategory) -> Unit,
    onSelectCodeset: (PanasonicCatalogCodeset, String?) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Panasonic Offline Catalog ($modelCount models / $codesetCount maps)",
                style = MaterialTheme.typography.titleSmall,
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 460.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { onSearchChanged(it, category) },
                    label = { Text(DigaUiStrings.KEYMAP_SEARCH_PLACEHOLDER, maxLines = 1) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(PanasonicCategory.entries) { cat ->
                        FilterChip(
                            selected = cat == category,
                            onClick = { onSearchChanged(query, cat) },
                            label = { Text(cat.displayLabel, fontSize = 12.sp) },
                        )
                    }
                }
                when {
                    loading -> DigaBodyText("Loading offline Panasonic IR database...")
                    results.isEmpty() -> DigaBodyText(DigaUiStrings.KEYMAP_EMPTY_SEARCH, color = DigaStatusWarn)
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            items(results, key = { it.id }) { codeset ->
                                Surface(
                                    color = DigaSurfaceVariant,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, DigaOutline),
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val matchedModel =
                                                    if (query.isNotBlank()) {
                                                        codeset.models.firstOrNull {
                                                            it.contains(query.trim(), ignoreCase = true)
                                                        } ?: codeset.primaryDisplayName
                                                    } else {
                                                        codeset.primaryDisplayName
                                                    }
                                                onSelectCodeset(codeset, matchedModel)
                                            },
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = codeset.primaryDisplayName,
                                            color = DigaTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Text(
                                            text =
                                                "${codeset.category.displayLabel} | ${codeset.commands.size} cmds | " +
                                                    "${codeset.models.size} model(s)",
                                            color = DigaBlueLight,
                                            fontSize = 12.sp,
                                        )
                                        if (codeset.models.size > 1) {
                                            Text(
                                                text = codeset.models.drop(1).take(6).joinToString(", "),
                                                color = DigaTextSecondary,
                                                fontSize = 11.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(DigaUiStrings.DIALOG_CANCEL)
            }
        },
    )
}

@Composable
private fun DynamicKeyEditorDialog(
    initialKey: DynamicRemoteKey,
    isNew: Boolean,
    activeMode: RemoteMode,
    onTestTransmit: (DynamicRemoteKey) -> Unit,
    onSave: (DynamicRemoteKey) -> Unit,
    onDuplicate: () -> Unit,
    onMove: (Int) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    var label by remember(initialKey.id) { mutableStateOf(initialKey.label) }
    var section by remember(initialKey.id) { mutableStateOf(initialKey.section) }
    var tone by remember(initialKey.id) { mutableStateOf(initialKey.tone) }
    var protocol by remember(initialKey.id) { mutableStateOf(initialKey.protocol) }
    var devHex by remember(initialKey.id) { mutableStateOf(hex2(initialKey.device)) }
    var subHex by remember(initialKey.id) { mutableStateOf(hex2(initialKey.subdevice)) }
    var obcHex by remember(initialKey.id) { mutableStateOf(hex2(initialKey.obc)) }
    var respectMode by remember(initialKey.id) { mutableStateOf(initialKey.respectRemoteMode) }
    var prontoHex by remember(initialKey.id) { mutableStateOf(initialKey.prontoHex.orEmpty()) }
    var confirmRequired by remember(initialKey.id) { mutableStateOf(initialKey.confirmRequired) }

    val draft =
        DynamicRemoteKey(
            id = initialKey.id,
            label = label.trim().ifEmpty { initialKey.label },
            section = section,
            protocol = protocol,
            device = devHex.toIntOrNull(16)?.and(0xFF) ?: initialKey.device,
            subdevice = subHex.toIntOrNull(16)?.and(0xFF) ?: initialKey.subdevice,
            obc = obcHex.toIntOrNull(16)?.and(0xFF) ?: initialKey.obc,
            respectRemoteMode = respectMode,
            prontoHex = prontoHex.trim().ifEmpty { null },
            tone = tone,
            confirmRequired = confirmRequired,
        )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isNew) DigaUiStrings.KEYMAP_NEW_KEY_TITLE else DigaUiStrings.KEYMAP_EDIT_KEY_TITLE,
                style = MaterialTheme.typography.titleSmall,
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 460.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Button Label") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                ChoiceRow(
                    options = KeyProtocol.entries,
                    selected = protocol,
                    labelOf = { if (it == KeyProtocol.KASEIKYO) "Kaseikyo 48b" else "Pronto Hex" },
                    onSelect = { protocol = it },
                    height = 42.dp,
                )

                if (protocol == KeyProtocol.KASEIKYO) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = devHex,
                            onValueChange = { devHex = it.uppercase().take(2) },
                            label = { Text("Dev Hex") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedTextField(
                            value = subHex,
                            onValueChange = { subHex = it.uppercase().take(2) },
                            label = { Text("Sub Hex") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedTextField(
                            value = obcHex,
                            onValueChange = { obcHex = it.uppercase().take(2) },
                            label = { Text("OBC Hex") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Apply SET 1/2/3 channel", color = DigaTextSecondary, fontSize = 13.sp)
                        Switch(checked = respectMode, onCheckedChange = { respectMode = it })
                    }
                    Text(
                        text = "Wire: ${draft.formatWirePreview(activeMode)}",
                        color = DigaGreen,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                } else {
                    OutlinedTextField(
                        value = prontoHex,
                        onValueChange = { prontoHex = it },
                        label = { Text("Pronto Hex (0000 0070 ...)") },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Text("Section", color = DigaTextMuted, fontSize = 12.sp)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    KeySection.entries.forEach { sec ->
                        FilterChip(
                            selected = sec == section,
                            onClick = { section = sec },
                            label = { Text(sec.title, fontSize = 11.sp) },
                        )
                    }
                }

                Text("Color Tone", color = DigaTextMuted, fontSize = 12.sp)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    KeyTone.entries.forEach { candidate ->
                        FilterChip(
                            selected = candidate == tone,
                            onClick = { tone = candidate },
                            label = { Text(candidate.name, fontSize = 11.sp) },
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Ask confirmation before IR TX", color = DigaTextSecondary, fontSize = 13.sp)
                    Switch(checked = confirmRequired, onCheckedChange = { confirmRequired = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    OutlinedButton(onClick = { onTestTransmit(draft) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(DigaUiStrings.KEYMAP_TEST_IR, fontSize = 12.sp)
                    }
                    if (!isNew) {
                        IconButton(onClick = { onMove(-1) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Move Left")
                        }
                        IconButton(onClick = { onMove(1) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Move Right")
                        }
                        IconButton(onClick = onDuplicate) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = "Duplicate Key")
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Filled.Delete, contentDescription = DigaUiStrings.KEYMAP_DELETE, tint = DigaStatusWarn)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(draft) }) {
                Text(DigaUiStrings.KEYMAP_SAVE, color = DigaBlueLight, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(DigaUiStrings.DIALOG_CANCEL)
            }
        },
    )
}

@Composable
private fun ImportKeymapDialog(
    onImport: (String, String) -> Boolean,
    onDismiss: () -> Unit,
) {
    var profileName by remember { mutableStateOf("") }
    var rawText by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(DigaUiStrings.KEYMAP_IMPORT_TITLE, style = MaterialTheme.typography.titleSmall) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = profileName,
                    onValueChange = { profileName = it },
                    label = { Text("Custom Profile Name (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = rawText,
                    onValueChange = {
                        rawText = it
                        errorMsg = null
                    },
                    label = { Text("Paste .irplus XML or Flipper .ir content") },
                    minLines = 5,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth(),
                )
                errorMsg?.let { Text(it, color = DigaStatusWarn, fontSize = 12.sp) }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val ok = onImport(rawText, profileName)
                    if (!ok) {
                        errorMsg = "Could not parse any valid IR commands from input."
                    }
                },
            ) {
                Text(DigaUiStrings.KEYMAP_BTN_IMPORT)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(DigaUiStrings.DIALOG_CANCEL)
            }
        },
    )
}

@Composable
private fun ExportKeymapDialog(
    xml: String,
    onDismiss: () -> Unit,
) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(DigaUiStrings.KEYMAP_EXPORT_TITLE, style = MaterialTheme.typography.titleSmall) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = xml,
                    onValueChange = {},
                    readOnly = true,
                    minLines = 6,
                    maxLines = 10,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (copied) {
                    Text("Copied .irplus XML to clipboard.", color = DigaGreen, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    clipboard.setText(AnnotatedString(xml))
                    copied = true
                },
            ) {
                Text("COPY XML")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(DigaUiStrings.DIALOG_CANCEL)
            }
        },
    )
}

private fun KeyTone.toKeyStyle(): KeyStyle =
    when (this) {
        KeyTone.NEUTRAL -> KeyStyle.Neutral
        KeyTone.PRIMARY -> KeyStyle.Primary
        KeyTone.INFO -> KeyStyle.Info
        KeyTone.SUCCESS -> KeyStyle.Success
        KeyTone.WARNING -> KeyStyle.Warning
        KeyTone.DANGER -> KeyStyle.Danger
        KeyTone.DANGER_DARK -> KeyStyle.DangerDark
        KeyTone.ACCENT -> KeyStyle.Accent
    }

private fun hex2(value: Int): String = (value and 0xFF).toString(16).uppercase().padStart(2, '0')
