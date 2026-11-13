package com.shelfsag.spacer.presentation.gameover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shelfsag.spacer.R
import com.shelfsag.spacer.core.di.AppViewModelFactory
import com.shelfsag.spacer.core.theme.Accent
import com.shelfsag.spacer.core.theme.Outline
import com.shelfsag.spacer.core.theme.Primary
import com.shelfsag.spacer.core.theme.SurfaceRaised
import com.shelfsag.spacer.core.theme.TextMuted
import com.shelfsag.spacer.core.theme.TextPrimary
import com.shelfsag.spacer.core.theme.ratingColor
import com.shelfsag.spacer.core.theme.ratingWash
import com.shelfsag.spacer.core.ui.PrimaryButton
import com.shelfsag.spacer.core.ui.QuietButton
import com.shelfsag.spacer.core.ui.RatingBadge
import com.shelfsag.spacer.core.ui.SagCanvas
import com.shelfsag.spacer.core.ui.ScreenBackground
import com.shelfsag.spacer.core.ui.SecondaryButton
import com.shelfsag.spacer.core.ui.SectionHeading
import com.shelfsag.spacer.core.ui.StatCard
import com.shelfsag.spacer.core.ui.lengthUnitRes
import com.shelfsag.spacer.core.ui.ratingVerdictRes
import com.shelfsag.spacer.domain.model.ShelfLoad

@Composable
fun GameOverScreen(
    planId: String,
    onBrackets: () -> Unit,
    onPlanAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val viewModel: GameOverViewModel = viewModel(
        key = "result-" + planId,
        factory = AppViewModelFactory(planId)
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    GameOverContent(
        state = state,
        onBrackets = onBrackets,
        onPlanAgain = onPlanAgain,
        onMenu = onMenu
    )
}

@Composable
private fun GameOverContent(
    state: GameOverUiState,
    onBrackets: () -> Unit,
    onPlanAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val accent = ratingColor(state.rating)
    val sagLabel = stringResource(lengthUnitRes(state.units), state.sagText)
    val badgeEntry = remember { MutableTransitionState(false).apply { targetState = true } }

    ScreenBackground(
        colors = ratingWash(state.rating),
        imageAlpha = 0.22f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeading(
                eyebrow = stringResource(R.string.result_eyebrow),
                title = state.planName,
                subtitle = state.room,
                titleStyle = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
            AnimatedVisibility(
                visibleState = badgeEntry,
                enter = scaleIn(spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow)) +
                    fadeIn(tween(160))
            ) {
                RatingBadge(rating = state.rating, compact = false)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = sagLabel,
                style = MaterialTheme.typography.displayMedium,
                color = accent,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
            Text(
                text = stringResource(R.string.result_of_sag),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(ratingVerdictRes(state.rating)),
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    label = stringResource(R.string.result_stat_ratio),
                    value = stringResource(R.string.value_ratio, state.ratioK),
                    accent = Primary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = stringResource(R.string.result_stat_max_span),
                    value = stringResource(lengthUnitRes(state.units), state.maxSpanText),
                    accent = Accent,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = stringResource(R.string.result_stat_brackets),
                    value = stringResource(R.string.value_count, state.bracketCount),
                    accent = accent,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceRaised),
                border = BorderStroke(1.dp, Outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                SagCanvas(
                    sagFraction = state.sagFraction,
                    description = stringResource(R.string.sag_canvas_cd, sagLabel),
                    valueLabel = sagLabel,
                    animated = false,
                    tools = state.load == ShelfLoad.TOOLS,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = stringResource(R.string.cta_brackets),
                onClick = onBrackets,
                enabled = !state.loading,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                text = stringResource(R.string.cta_play_again),
                onClick = onPlanAgain,
                modifier = Modifier.fillMaxWidth()
            )
            QuietButton(
                text = stringResource(R.string.cta_menu),
                onClick = onMenu,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
