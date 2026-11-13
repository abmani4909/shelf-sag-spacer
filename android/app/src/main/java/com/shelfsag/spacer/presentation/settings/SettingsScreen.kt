package com.shelfsag.spacer.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shelfsag.spacer.R
import com.shelfsag.spacer.core.config.GameConfig
import com.shelfsag.spacer.core.di.AppViewModelFactory
import com.shelfsag.spacer.core.theme.Background
import com.shelfsag.spacer.core.theme.BackgroundDeep
import com.shelfsag.spacer.core.theme.Outline
import com.shelfsag.spacer.core.theme.Primary
import com.shelfsag.spacer.core.theme.SurfaceRaised
import com.shelfsag.spacer.core.theme.SurfaceSunken
import com.shelfsag.spacer.core.theme.TextMuted
import com.shelfsag.spacer.core.theme.TextPrimary
import com.shelfsag.spacer.core.theme.TextSecondary
import com.shelfsag.spacer.core.ui.DimensionStepper
import com.shelfsag.spacer.core.ui.ScreenBackground
import com.shelfsag.spacer.core.ui.ScreenHeader
import com.shelfsag.spacer.core.ui.SecondaryButton
import com.shelfsag.spacer.core.ui.SectionHeading
import com.shelfsag.spacer.core.ui.lengthUnitRes
import com.shelfsag.spacer.domain.model.UnitSystem

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onMenu: () -> Unit
) {
    val viewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory())
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsContent(
        state = state,
        onBack = onBack,
        onMenu = onMenu,
        onUnits = viewModel::selectUnits,
        onSagLimit = viewModel::selectSagLimit,
        onHardLimit = viewModel::changeHardLimit,
        onStudSpacing = viewModel::selectStudSpacing
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onBack: () -> Unit,
    onMenu: () -> Unit,
    onUnits: (UnitSystem) -> Unit,
    onSagLimit: (Int) -> Unit,
    onHardLimit: (Int) -> Unit,
    onStudSpacing: (Double) -> Unit
) {
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
                title = stringResource(R.string.settings_header),
                subtitle = stringResource(R.string.settings_eyebrow),
                readout = stringResource(R.string.value_ratio, state.sagLimitK),
                readoutColor = Primary,
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(key = "masthead") {
                    SectionHeading(
                        eyebrow = stringResource(R.string.settings_eyebrow),
                        title = stringResource(R.string.settings_title),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item(key = "units") {
                    SettingCard(title = stringResource(R.string.settings_units)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.sprite_4),
                                contentDescription = stringResource(R.string.settings_units_cd),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OptionChip(
                                    label = stringResource(R.string.settings_units_metric),
                                    selected = state.units == UnitSystem.METRIC,
                                    onClick = { onUnits(UnitSystem.METRIC) },
                                    modifier = Modifier.weight(1f)
                                )
                                OptionChip(
                                    label = stringResource(R.string.settings_units_imperial),
                                    selected = state.units == UnitSystem.IMPERIAL,
                                    onClick = { onUnits(UnitSystem.IMPERIAL) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                item(key = "sag-limit") {
                    SettingCard(title = stringResource(R.string.settings_sag_limit)) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            LimitRow(
                                label = stringResource(R.string.settings_limit_strict),
                                note = stringResource(R.string.settings_limit_note_strict),
                                selected = state.sagLimitK == 360,
                                onClick = { onSagLimit(360) }
                            )
                            LimitRow(
                                label = stringResource(R.string.settings_limit_normal),
                                note = stringResource(R.string.settings_limit_note_normal),
                                selected = state.sagLimitK == 240,
                                onClick = { onSagLimit(240) }
                            )
                            LimitRow(
                                label = stringResource(R.string.settings_limit_relaxed),
                                note = stringResource(R.string.settings_limit_note_relaxed),
                                selected = state.sagLimitK == 180,
                                onClick = { onSagLimit(180) }
                            )
                        }
                    }
                }

                item(key = "hard-limit") {
                    SettingCard(title = stringResource(R.string.settings_hard_limit)) {
                        Column {
                            DimensionStepper(
                                label = stringResource(R.string.settings_hard_limit),
                                value = stringResource(
                                    lengthUnitRes(UnitSystem.METRIC),
                                    state.hardLimitText
                                ),
                                decreaseDescription = stringResource(
                                    R.string.stepper_less_cd,
                                    stringResource(R.string.settings_hard_limit)
                                ),
                                increaseDescription = stringResource(
                                    R.string.stepper_more_cd,
                                    stringResource(R.string.settings_hard_limit)
                                ),
                                onDecrease = { onHardLimit(-1) },
                                onIncrease = { onHardLimit(1) },
                                canDecrease = state.canLowerHardLimit,
                                canIncrease = state.canRaiseHardLimit,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.settings_hard_limit_note),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                item(key = "stud-spacing") {
                    SettingCard(title = stringResource(R.string.settings_stud_spacing)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OptionChip(
                                label = stringResource(R.string.stud_16),
                                selected = state.studSpacingMm == GameConfig.STUD_SPACING_MM,
                                onClick = { onStudSpacing(GameConfig.STUD_SPACING_MM) },
                                modifier = Modifier.weight(1f)
                            )
                            OptionChip(
                                label = stringResource(R.string.stud_24),
                                selected = state.studSpacingMm == GameConfig.STUD_SPACING_WIDE_MM,
                                onClick = { onStudSpacing(GameConfig.STUD_SPACING_WIDE_MM) },
                                modifier = Modifier.weight(1f)
                            )
                            OptionChip(
                                label = stringResource(R.string.stud_off),
                                selected = state.studSpacingMm <= 0.0,
                                onClick = { onStudSpacing(GameConfig.STUD_SPACING_OFF_MM) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                item(key = "about") {
                    SettingCard(title = stringResource(R.string.settings_about)) {
                        Text(
                            text = stringResource(R.string.settings_about_body),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                    }
                }
            }

            SecondaryButton(
                text = stringResource(R.string.cta_menu),
                onClick = onMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 18.dp)
            )
        }
    }
}

@Composable
private fun SettingCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
        border = BorderStroke(1.dp, Outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun OptionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Primary else SurfaceRaised
        ),
        border = BorderStroke(1.dp, if (selected) Primary else Outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .height(48.dp)
            .semantics { stateDescription = label }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected) SurfaceRaised else TextPrimary
            )
        }
    }
}

@Composable
private fun LimitRow(
    label: String,
    note: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Background else SurfaceRaised
        ),
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) Primary else Outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .semantics { stateDescription = label }
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary
            )
            Text(
                text = note,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}
