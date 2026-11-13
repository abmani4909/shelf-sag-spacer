package com.shelfsag.spacer.presentation.cutlist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shelfsag.spacer.R
import com.shelfsag.spacer.core.di.AppViewModelFactory
import com.shelfsag.spacer.core.theme.Accent
import com.shelfsag.spacer.core.theme.Background
import com.shelfsag.spacer.core.theme.BackgroundDeep
import com.shelfsag.spacer.core.theme.Outline
import com.shelfsag.spacer.core.theme.Primary
import com.shelfsag.spacer.core.theme.SurfaceRaised
import com.shelfsag.spacer.core.theme.SurfaceSunken
import com.shelfsag.spacer.core.theme.TextMuted
import com.shelfsag.spacer.core.theme.TextPrimary
import com.shelfsag.spacer.core.theme.ratingColor
import com.shelfsag.spacer.core.ui.CutListRow
import com.shelfsag.spacer.core.ui.PrimaryButton
import com.shelfsag.spacer.core.ui.QuietButton
import com.shelfsag.spacer.core.ui.ScreenBackground
import com.shelfsag.spacer.core.ui.ScreenHeader
import com.shelfsag.spacer.core.ui.StatCard
import com.shelfsag.spacer.core.ui.lengthUnitRes
import com.shelfsag.spacer.core.ui.materialNameRes
import com.shelfsag.spacer.core.ui.ratingLabelRes
import com.shelfsag.spacer.domain.model.CutListKind
import com.shelfsag.spacer.domain.model.CutListSection
import com.shelfsag.spacer.domain.model.UnitSystem

@Composable
fun CutListScreen(
    planId: String,
    onBack: () -> Unit,
    onMenu: () -> Unit
) {
    val viewModel: CutListViewModel = viewModel(
        key = "cutlist-" + planId,
        factory = AppViewModelFactory(planId)
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                CutListEvent.Saved -> onMenu()
            }
        }
    }

    CutListContent(
        state = state,
        onBack = onBack,
        onMenu = onMenu,
        onToggle = viewModel::toggleBought,
        onSave = viewModel::savePlan
    )
}

@Composable
private fun CutListContent(
    state: CutListUiState,
    onBack: () -> Unit,
    onMenu: () -> Unit,
    onToggle: (String) -> Unit,
    onSave: () -> Unit
) {
    val accent = ratingColor(state.rating)

    ScreenBackground(
        colors = listOf(Background, SurfaceSunken, BackgroundDeep),
        imageAlpha = 0.14f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.cutlist_title),
                subtitle = state.planName,
                readout = stringResource(R.string.value_count, state.bracketCount),
                readoutColor = Primary,
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                item(key = "masthead") {
                    Text(
                        text = stringResource(R.string.cutlist_eyebrow),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.room,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                CutListSection.entries.forEach { section ->
                    val rows = state.rows.filter { it.section == section }
                    if (rows.isNotEmpty()) {
                        item(key = "section-" + section.name) {
                            SectionBar(section = section)
                        }
                        rows.forEach { row ->
                            item(key = "row-" + row.id) {
                                CutListRow(
                                    name = rowName(row, state.units),
                                    detail = rowDetail(row, state.units),
                                    quantity = stringResource(
                                        R.string.value_quantity,
                                        row.quantity
                                    ),
                                    bought = row.bought,
                                    toggleDescription = stringResource(
                                        if (row.bought) {
                                            R.string.cutlist_bought_cd
                                        } else {
                                            R.string.cutlist_not_bought_cd
                                        }
                                    ),
                                    onToggle = { onToggle(row.id) }
                                )
                            }
                        }
                    }
                }

                item(key = "summary") {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                        border = BorderStroke(1.dp, Outline),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                StatCard(
                                    label = stringResource(R.string.cutlist_summary_board),
                                    value = stringResource(
                                        R.string.value_metres,
                                        state.boardMetresText
                                    ),
                                    accent = Primary,
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    label = stringResource(R.string.cutlist_summary_brackets),
                                    value = stringResource(
                                        R.string.value_count,
                                        state.bracketCount
                                    ),
                                    accent = Accent,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(
                                    R.string.cutlist_summary_sag,
                                    stringResource(lengthUnitRes(state.units), state.sagText),
                                    stringResource(ratingLabelRes(state.rating))
                                ),
                                style = MaterialTheme.typography.labelLarge,
                                color = accent
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 16.dp)
            ) {
                PrimaryButton(
                    text = stringResource(R.string.cta_save),
                    onClick = onSave,
                    enabled = !state.loading,
                    modifier = Modifier.fillMaxWidth()
                )
                QuietButton(
                    text = stringResource(R.string.cta_menu),
                    onClick = onMenu,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SectionBar(section: CutListSection) {
    val titleRes = when (section) {
        CutListSection.BOARDS -> R.string.cutlist_section_boards
        CutListSection.HARDWARE -> R.string.cutlist_section_hardware
        CutListSection.FASTENERS -> R.string.cutlist_section_fasteners
    }
    val glyph = when (section) {
        CutListSection.BOARDS -> R.drawable.sprite_2
        CutListSection.HARDWARE -> R.drawable.sprite_3
        CutListSection.FASTENERS -> R.drawable.sprite_4
    }
    val glyphCd = when (section) {
        CutListSection.BOARDS -> R.string.load_books_cd
        CutListSection.HARDWARE -> R.string.cutlist_section_hardware
        CutListSection.FASTENERS -> R.string.menu_empty_cd
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(glyph),
            contentDescription = stringResource(glyphCd),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary
        )
    }
}

@Composable
private fun rowName(row: CutListRowState, units: UnitSystem): String {
    val unitRes = lengthUnitRes(units)
    return when (row.kind) {
        CutListKind.BOARD -> stringResource(
            R.string.cutlist_board_name,
            stringResource(materialNameRes(row.material)),
            stringResource(unitRes, row.lengthText),
            stringResource(unitRes, row.depthText),
            stringResource(unitRes, row.thicknessText)
        )

        CutListKind.BRACKET -> stringResource(
            R.string.cutlist_bracket_name,
            stringResource(unitRes, row.armText)
        )

        CutListKind.WALL_SCREW -> stringResource(R.string.cutlist_wall_screw)
        CutListKind.SHELF_SCREW -> stringResource(R.string.cutlist_shelf_screw)
        CutListKind.WALL_PLUG -> stringResource(R.string.cutlist_wall_plug)
    }
}

@Composable
private fun rowDetail(
    row: CutListRowState,
    units: UnitSystem
): String? = when (row.kind) {
    CutListKind.BOARD -> stringResource(
        R.string.cutlist_board_detail,
        stringResource(lengthUnitRes(units), row.buyLengthText)
    )

    CutListKind.BRACKET -> stringResource(R.string.cutlist_bracket_detail)
    else -> null
}
