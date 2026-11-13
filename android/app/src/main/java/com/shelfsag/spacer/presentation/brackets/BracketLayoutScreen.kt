package com.shelfsag.spacer.presentation.brackets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shelfsag.spacer.R
import com.shelfsag.spacer.core.config.GameConfig
import com.shelfsag.spacer.core.di.AppViewModelFactory
import com.shelfsag.spacer.core.theme.Accent
import com.shelfsag.spacer.core.theme.AccentSoft
import com.shelfsag.spacer.core.theme.Background
import com.shelfsag.spacer.core.theme.BackgroundDeep
import com.shelfsag.spacer.core.theme.Outline
import com.shelfsag.spacer.core.theme.Primary
import com.shelfsag.spacer.core.theme.PrimaryDark
import com.shelfsag.spacer.core.theme.RatingSagging
import com.shelfsag.spacer.core.theme.SurfaceRaised
import com.shelfsag.spacer.core.theme.SurfaceSunken
import com.shelfsag.spacer.core.theme.TextMuted
import com.shelfsag.spacer.core.theme.TextPrimary
import com.shelfsag.spacer.core.theme.ratingColor
import com.shelfsag.spacer.core.ui.DimensionStepper
import com.shelfsag.spacer.core.ui.PrimaryButton
import com.shelfsag.spacer.core.ui.ScreenBackground
import com.shelfsag.spacer.core.ui.ScreenHeader
import com.shelfsag.spacer.core.ui.SecondaryButton
import com.shelfsag.spacer.core.ui.StatCard
import com.shelfsag.spacer.core.ui.lengthUnitRes

@Composable
fun BracketLayoutScreen(
    planId: String,
    onBack: () -> Unit,
    onCutList: () -> Unit
) {
    val viewModel: BracketLayoutViewModel = viewModel(
        key = "brackets-" + planId,
        factory = AppViewModelFactory(planId)
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                BracketEvent.Snapped -> haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }

    BracketLayoutContent(
        state = state,
        onBack = onBack,
        onCutList = onCutList,
        onDrag = viewModel::dragBracket,
        onDragEnd = viewModel::commit,
        onCount = viewModel::changeBracketCount,
        onEven = viewModel::evenSpacing,
        onStudSpacing = viewModel::setStudSpacing
    )
}

@Composable
private fun BracketLayoutContent(
    state: BracketLayoutUiState,
    onBack: () -> Unit,
    onCutList: () -> Unit,
    onDrag: (Int, Double) -> Unit,
    onDragEnd: () -> Unit,
    onCount: (Int) -> Unit,
    onEven: () -> Unit,
    onStudSpacing: (Double) -> Unit
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
                title = stringResource(R.string.brackets_title),
                subtitle = state.planName,
                readout = stringResource(R.string.brackets_readout, state.bracketCount),
                readoutColor = Primary,
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
                    WallElevation(
                        state = state,
                        onDrag = onDrag,
                        onDragEnd = onDragEnd,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    )
                }
            }

            if (state.overLimit) {
                Text(
                    text = stringResource(
                        R.string.brackets_warning,
                        stringResource(lengthUnitRes(state.units), state.overByText)
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = RatingSagging,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    label = stringResource(R.string.brackets_stat_spacing),
                    value = stringResource(lengthUnitRes(state.units), state.spacingText),
                    accent = Primary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = stringResource(R.string.brackets_stat_gap),
                    value = stringResource(lengthUnitRes(state.units), state.widestGapText),
                    accent = accent,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = stringResource(R.string.brackets_stat_studs),
                    value = stringResource(
                        R.string.value_of_total,
                        state.onStudCount,
                        state.bracketCount
                    ),
                    accent = Accent,
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
                        DimensionStepper(
                            label = stringResource(R.string.brackets_label_count),
                            value = stringResource(R.string.value_count, state.bracketCount),
                            decreaseDescription = stringResource(
                                R.string.stepper_less_cd,
                                stringResource(R.string.brackets_label_count)
                            ),
                            increaseDescription = stringResource(
                                R.string.stepper_more_cd,
                                stringResource(R.string.brackets_label_count)
                            ),
                            onDecrease = { onCount(-1) },
                            onIncrease = { onCount(1) },
                            canDecrease = state.bracketCount > GameConfig.MIN_BRACKETS,
                            canIncrease = state.bracketCount < GameConfig.MAX_BRACKETS,
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryButton(
                            text = stringResource(R.string.cta_even),
                            onClick = onEven,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.brackets_label_studs),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SegmentChip(
                            label = stringResource(R.string.stud_16),
                            selected = state.studSpacingMm == GameConfig.STUD_SPACING_MM,
                            onClick = { onStudSpacing(GameConfig.STUD_SPACING_MM) },
                            modifier = Modifier.weight(1f)
                        )
                        SegmentChip(
                            label = stringResource(R.string.stud_24),
                            selected = state.studSpacingMm == GameConfig.STUD_SPACING_WIDE_MM,
                            onClick = { onStudSpacing(GameConfig.STUD_SPACING_WIDE_MM) },
                            modifier = Modifier.weight(1f)
                        )
                        SegmentChip(
                            label = stringResource(R.string.stud_off),
                            selected = state.studSpacingMm <= 0.0,
                            onClick = { onStudSpacing(GameConfig.STUD_SPACING_OFF_MM) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    PrimaryButton(
                        text = stringResource(R.string.cta_cut_list),
                        onClick = onCutList,
                        enabled = !state.loading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun SegmentChip(
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
        modifier = modifier.height(48.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
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
private fun WallElevation(
    state: BracketLayoutUiState,
    onDrag: (Int, Double) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val handleDescription = stringResource(R.string.bracket_handle_cd, state.bracketCount)
    val studDescription = stringResource(R.string.stud_tag_cd)

    BoxWithConstraints(modifier = modifier) {
        val sidePadding = 28.dp
        val trackWidth = maxWidth - sidePadding * 2
        val trackPx = with(density) { trackWidth.toPx() }
        val boardTop = maxHeight * 0.30f
        val length = if (state.lengthMm <= 0.0) 1.0 else state.lengthMm

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val left = with(density) { sidePadding.toPx() }
            val track = width - left * 2f
            val board = height * 0.30f

            drawRect(
                color = BackgroundDeep.copy(alpha = 0.45f),
                topLeft = Offset(0f, 0f),
                size = Size(width, height)
            )

            state.studMarks.forEach { mark ->
                val x = left + track * (mark / length).toFloat()
                drawLine(
                    color = AccentSoft,
                    start = Offset(x, height * 0.08f),
                    end = Offset(x, height),
                    strokeWidth = 2.5f,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }

            drawRoundRect(
                color = Primary,
                topLeft = Offset(left, board),
                size = Size(track, height * 0.045f),
                cornerRadius = CornerRadius(4f, 4f)
            )

            val sorted = state.positions
            for (i in 1 until sorted.size) {
                val gap = sorted[i] - sorted[i - 1]
                val startX = left + track * (sorted[i - 1] / length).toFloat()
                val endX = left + track * (sorted[i] / length).toFloat()
                val widest = state.positions.size >= 2 && gap >= state.widestGapValue()
                drawLine(
                    color = if (state.overLimit && widest) RatingSagging else Accent,
                    start = Offset(startX, board - height * 0.06f),
                    end = Offset(endX, board - height * 0.06f),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }

            sorted.forEach { position ->
                val x = left + track * (position / length).toFloat()
                drawRoundRect(
                    color = PrimaryDark,
                    topLeft = Offset(x - 4f, board + height * 0.045f),
                    size = Size(8f, height * 0.34f),
                    cornerRadius = CornerRadius(3f, 3f)
                )
                drawRoundRect(
                    color = PrimaryDark,
                    topLeft = Offset(x - 4f, board + height * 0.045f),
                    size = Size(height * 0.22f, 8f),
                    cornerRadius = CornerRadius(3f, 3f)
                )
            }
        }

        state.studMarks.forEachIndexed { index, mark ->
            val x = sidePadding + trackWidth * (mark / length).toFloat()
            Image(
                painter = painterResource(R.drawable.sprite_3),
                contentDescription = if (index == 0) studDescription else null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .offset(x = x - 11.dp, y = 2.dp)
                    .size(22.dp)
            )
        }

        state.positions.forEachIndexed { index, position ->
            val x = sidePadding + trackWidth * (position / length).toFloat()
            Box(
                modifier = Modifier
                    .offset(x = x - 28.dp, y = boardTop - 10.dp)
                    .size(56.dp)
                    .semantics { contentDescription = handleDescription }
                    .pointerInput(index, trackPx, length) {
                        detectDragGestures(
                            onDragEnd = onDragEnd,
                            onDrag = { change, amount ->
                                change.consume()
                                if (trackPx > 0f) {
                                    onDrag(index, amount.x / trackPx * length)
                                }
                            }
                        )
                    }
            ) {
                Spacer(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(26.dp)
                        .background(Primary.copy(alpha = 0.18f), RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

private fun BracketLayoutUiState.widestGapValue(): Double {
    val sorted = positions
    if (sorted.size < 2) return lengthMm
    var widest = 0.0
    for (i in 1 until sorted.size) {
        val gap = sorted[i] - sorted[i - 1]
        if (gap > widest) widest = gap
    }
    return widest
}
