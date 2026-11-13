package com.shelfsag.spacer.presentation.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.shelfsag.spacer.core.theme.ratingColor
import com.shelfsag.spacer.core.ui.DimensionStepper
import com.shelfsag.spacer.core.ui.PickerField
import com.shelfsag.spacer.core.ui.PrimaryButton
import com.shelfsag.spacer.core.ui.RatingBadge
import com.shelfsag.spacer.core.ui.SagCanvas
import com.shelfsag.spacer.core.ui.ScreenBackground
import com.shelfsag.spacer.core.ui.ScreenHeader
import com.shelfsag.spacer.core.ui.StatCard
import com.shelfsag.spacer.core.ui.lengthUnitRes
import com.shelfsag.spacer.core.ui.loadUnitRes
import com.shelfsag.spacer.core.ui.materialNameRes
import com.shelfsag.spacer.domain.model.ShelfLoad
import com.shelfsag.spacer.domain.model.ShelfMaterial
import com.shelfsag.spacer.presentation.dialog.LoadPickerDialog
import com.shelfsag.spacer.presentation.dialog.MaterialPickerDialog

@Composable
fun GameScreen(
    planId: String?,
    onBack: () -> Unit,
    onResult: (String) -> Unit
) {
    val viewModel: GameViewModel = viewModel(
        key = "game-" + planId,
        factory = AppViewModelFactory(planId)
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is GameEvent.Estimated -> onResult(event.planId)
            }
        }
    }

    GameContent(
        state = state,
        onBack = onBack,
        onEstimate = viewModel::estimate,
        onOpenMaterial = viewModel::openMaterialDialog,
        onOpenLoad = viewModel::openLoadDialog,
        onCloseDialogs = viewModel::closeDialogs,
        onMaterial = viewModel::selectMaterial,
        onThickness = viewModel::selectThickness,
        onLoad = viewModel::selectLoad,
        onCustomKg = viewModel::setCustomKg,
        onLength = viewModel::changeLength,
        onDepth = viewModel::changeDepth
    )
}

@Composable
private fun GameContent(
    state: GameUiState,
    onBack: () -> Unit,
    onEstimate: () -> Unit,
    onOpenMaterial: () -> Unit,
    onOpenLoad: () -> Unit,
    onCloseDialogs: () -> Unit,
    onMaterial: (ShelfMaterial) -> Unit,
    onThickness: (Int) -> Unit,
    onLoad: (ShelfLoad) -> Unit,
    onCustomKg: (String) -> Unit,
    onLength: (Int) -> Unit,
    onDepth: (Int) -> Unit
) {
    val accent = ratingColor(state.rating)
    val sagLabel = stringResource(lengthUnitRes(state.units), state.sagText)

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
                title = stringResource(R.string.game_title),
                subtitle = state.room,
                readout = sagLabel,
                readoutColor = accent,
                onBack = onBack
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                    border = BorderStroke(1.dp, Outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        SagCanvas(
                            sagFraction = state.sagFraction,
                            description = stringResource(R.string.sag_canvas_cd, sagLabel),
                            valueLabel = sagLabel,
                            animated = true,
                            tools = state.load == ShelfLoad.TOOLS,
                            modifier = Modifier.fillMaxSize()
                        )
                        RatingBadge(
                            rating = state.rating,
                            compact = true,
                            modifier = Modifier.align(Alignment.TopEnd)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    label = stringResource(R.string.game_stat_span),
                    value = stringResource(lengthUnitRes(state.units), state.spanText),
                    accent = Primary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = stringResource(R.string.game_stat_limit),
                    value = stringResource(R.string.value_ratio, state.limitK),
                    accent = Accent,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = stringResource(R.string.game_stat_max_span),
                    value = stringResource(lengthUnitRes(state.units), state.maxSpanText),
                    accent = accent,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                color = SurfaceRaised,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        PickerField(
                            label = stringResource(R.string.game_label_material),
                            value = stringResource(
                                R.string.material_with_thickness,
                                stringResource(
                                    lengthUnitRes(state.units),
                                    state.thicknessText
                                ),
                                stringResource(materialNameRes(state.material))
                            ),
                            description = stringResource(
                                R.string.picker_open_cd,
                                stringResource(R.string.game_label_material)
                            ),
                            onClick = onOpenMaterial,
                            modifier = Modifier.weight(1f)
                        )
                        PickerField(
                            label = stringResource(R.string.game_label_load),
                            value = stringResource(
                                loadUnitRes(state.units),
                                state.loadValueText
                            ),
                            description = stringResource(
                                R.string.picker_open_cd,
                                stringResource(R.string.game_label_load)
                            ),
                            onClick = onOpenLoad,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DimensionStepper(
                            label = stringResource(R.string.game_label_length),
                            value = stringResource(lengthUnitRes(state.units), state.lengthText),
                            decreaseDescription = stringResource(
                                R.string.stepper_less_cd,
                                stringResource(R.string.game_label_length)
                            ),
                            increaseDescription = stringResource(
                                R.string.stepper_more_cd,
                                stringResource(R.string.game_label_length)
                            ),
                            onDecrease = { onLength(-1) },
                            onIncrease = { onLength(1) },
                            canDecrease = state.canShorten,
                            canIncrease = state.canLengthen,
                            modifier = Modifier.weight(1f)
                        )
                        DimensionStepper(
                            label = stringResource(R.string.game_label_depth),
                            value = stringResource(lengthUnitRes(state.units), state.depthText),
                            decreaseDescription = stringResource(
                                R.string.stepper_less_cd,
                                stringResource(R.string.game_label_depth)
                            ),
                            increaseDescription = stringResource(
                                R.string.stepper_more_cd,
                                stringResource(R.string.game_label_depth)
                            ),
                            onDecrease = { onDepth(-1) },
                            onIncrease = { onDepth(1) },
                            canDecrease = state.canNarrow,
                            canIncrease = state.canWiden,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    PrimaryButton(
                        text = stringResource(R.string.cta_estimate),
                        onClick = onEstimate,
                        enabled = !state.loading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (state.showMaterialDialog) {
            MaterialPickerDialog(
                selected = state.material,
                thicknessMm = state.thicknessMm,
                thicknessLabels = state.thicknessLabels,
                units = state.units,
                onMaterial = onMaterial,
                onThickness = onThickness,
                onDismiss = onCloseDialogs
            )
        }

        if (state.showLoadDialog) {
            LoadPickerDialog(
                selected = state.load,
                loadValues = state.loadValues,
                units = state.units,
                customInput = state.customKgInput,
                onLoad = onLoad,
                onCustomKg = onCustomKg,
                onDismiss = onCloseDialogs
            )
        }
    }
}
