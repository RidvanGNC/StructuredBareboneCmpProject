package org.structured_barebone.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import structuredbarebonecmpproject.shared.generated.resources.Res
import structuredbarebonecmpproject.shared.generated.resources.common_cancel
import structuredbarebonecmpproject.shared.generated.resources.common_done
import org.structured_barebone.shared.i18n.Iso639
import org.structured_barebone.shared.i18n.Iso639Language
import org.structured_barebone.shared.i18n.languageFlagEmoji

@Composable
fun LanguageLabel(
    code: String?,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyleOrDefault(),
) {
    val lang = Iso639.find(code)
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        val flag = languageFlagEmoji(lang?.country)
        if (flag != null) {
            Text(text = flag, style = style)
            Text(text = " ")
        }
        Text(text = lang?.name ?: code ?: "-", style = style)
    }
}

@Composable
private fun LocalTextStyleOrDefault(): TextStyle = MaterialTheme.typography.bodyMedium

@Composable
fun LanguageSelectField(
    label: String,
    selectedCode: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
    restrictToCodes: List<String>? = null,
    allowClear: Boolean = false,
    clearLabel: String = "-",
) {
    var showDialog by remember { mutableStateOf(false) }
    val selected = Iso639.find(selectedCode)

    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        OutlinedButton(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (selected != null) {
                    LanguageLabel(code = selected.code)
                } else {
                    Text(clearLabel)
                }
            }
        }
    }

    if (showDialog) {
        LanguagePickerDialog(
            restrictToCodes = restrictToCodes,
            allowClear = allowClear,
            clearLabel = clearLabel,
            selectedCodes = setOfNotNull(selectedCode),
            multiSelect = false,
            onDismiss = { showDialog = false },
            onToggle = { code ->
                onSelect(code)
                showDialog = false
            },
        )
    }
}

@Composable
fun MultiLanguageSelectField(
    label: String,
    selectedCodes: List<String>,
    onChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        OutlinedButton(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
            if (selectedCodes.isEmpty()) {
                Text("-")
            } else {
                Text(selectedCodes.joinToString(", ") { code -> Iso639.find(code)?.name ?: code })
            }
        }
    }

    if (showDialog) {
        LanguagePickerDialog(
            restrictToCodes = null,
            allowClear = false,
            clearLabel = "-",
            selectedCodes = selectedCodes.toSet(),
            multiSelect = true,
            onDismiss = { showDialog = false },
            onToggle = { code ->
                if (code == null) return@LanguagePickerDialog
                val updated = if (code in selectedCodes) selectedCodes - code else selectedCodes + code
                onChange(updated)
            },
        )
    }
}

@Composable
private fun LanguagePickerDialog(
    restrictToCodes: List<String>?,
    allowClear: Boolean,
    clearLabel: String,
    selectedCodes: Set<String>,
    multiSelect: Boolean,
    onDismiss: () -> Unit,
    onToggle: (String?) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val allLanguages = remember(restrictToCodes) {
        if (restrictToCodes != null) {
            Iso639.LANGUAGES.filter { it.code in restrictToCodes }
        } else {
            Iso639.LANGUAGES
        }
    }
    val filtered = remember(query, allLanguages) {
        if (query.isBlank()) {
            allLanguages
        } else {
            allLanguages.filter {
                it.name.contains(query, ignoreCase = true) || it.code.contains(query, ignoreCase = true)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (multiSelect) {
                TextButton(onClick = onDismiss) { Text(stringResource(Res.string.common_done)) }
            }
        },
        dismissButton = if (!multiSelect) {
            { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.common_cancel)) } }
        } else {
            null
        },
        text = {
            Column(modifier = Modifier.heightIn(max = 420.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    if (allowClear && !multiSelect) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                                TextButton(onClick = { onToggle(null) }) {
                                    Text(clearLabel)
                                }
                            }
                        }
                    }
                    items(filtered) { lang ->
                        LanguagePickerRow(
                            lang = lang,
                            selected = lang.code in selectedCodes,
                            multiSelect = multiSelect,
                            onClick = { onToggle(lang.code) },
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun LanguagePickerRow(
    lang: Iso639Language,
    selected: Boolean,
    multiSelect: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (multiSelect) {
            Checkbox(checked = selected, onCheckedChange = { onClick() })
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            TextButton(onClick = onClick) {
                LanguageLabel(code = lang.code)
            }
        }
    }
}
