package com.shelfsag.spacer.presentation.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shelfsag.spacer.R
import com.shelfsag.spacer.core.theme.Accent
import com.shelfsag.spacer.core.theme.Background
import com.shelfsag.spacer.core.theme.Outline
import com.shelfsag.spacer.core.theme.Primary
import com.shelfsag.spacer.core.theme.SurfaceRaised
import com.shelfsag.spacer.core.theme.TextMuted
import com.shelfsag.spacer.core.theme.TextPrimary
import com.shelfsag.spacer.core.ui.loadNameRes
import com.shelfsag.spacer.core.ui.loadUnitRes
import com.shelfsag.spacer.domain.model.ShelfLoad
import com.shelfsag.spacer.domain.model.UnitSystem

@Composable
fun LoadPickerDialog(
    selected: ShelfLoad,
    loadValues: List<String>,
    units: UnitSystem,
    customInput: String,
    onLoad: (ShelfLoad) -> Unit,
    onCustomKg: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(12.dp),
        containerColor = SurfaceRaised,
        titleContentColor = TextPrimary,
        title = {
            Text(
                text = stringResource(R.string.dialog_load_title),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                ShelfLoad.entries.forEachIndexed { index, load ->
                    if (load != ShelfLoad.CUSTOM) {
                        LoadRow(
                            load = load,
                            valueText = stringResource(
                                loadUnitRes(units),
                                loadValues.getOrNull(index) ?: ""
                            ),
                            selected = load == selected,
                            onClick = { onLoad(load) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                Text(
                    text = stringResource(R.string.load_custom),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = customInput,
                    onValueChange = onCustomKg,
                    singleLine = true,
                    label = {
                        Text(
                            text = stringResource(R.string.load_custom_field),
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Outline,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cta_done),
                    style = MaterialTheme.typography.labelLarge,
                    color = Primary
                )
            }
        }
    )
}

@Composable
private fun LoadRow(
    load: ShelfLoad,
    valueText: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tools = load == ShelfLoad.TOOLS
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Background else SurfaceRaised
        ),
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) Primary else Outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(
                    if (tools) R.drawable.sprite_5 else R.drawable.sprite_2
                ),
                contentDescription = stringResource(
                    if (tools) R.string.load_tools_cd else R.string.load_books_cd
                ),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = stringResource(loadNameRes(load)),
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.bodyMedium,
                color = Accent,
                maxLines = 1
            )
        }
    }
}
