package com.shelfsag.spacer.presentation.menu

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shelfsag.spacer.R
import com.shelfsag.spacer.core.di.AppViewModelFactory
import com.shelfsag.spacer.core.theme.Background
import com.shelfsag.spacer.core.theme.BackgroundDeep
import com.shelfsag.spacer.core.theme.Accent
import com.shelfsag.spacer.core.theme.Primary
import com.shelfsag.spacer.core.theme.SurfaceSunken
import com.shelfsag.spacer.core.theme.TextMuted
import com.shelfsag.spacer.core.theme.TextPrimary
import com.shelfsag.spacer.core.theme.TextSecondary
import com.shelfsag.spacer.core.theme.ratingColor
import com.shelfsag.spacer.core.ui.PlanCard
import com.shelfsag.spacer.core.ui.PrimaryButton
import com.shelfsag.spacer.core.ui.ScreenBackground
import com.shelfsag.spacer.core.ui.SectionHeading
import com.shelfsag.spacer.core.ui.StatCard
import com.shelfsag.spacer.core.ui.lengthUnitRes
import com.shelfsag.spacer.core.ui.materialNameRes
import com.shelfsag.spacer.presentation.dialog.DeletePlanDialog

@Composable
fun MenuScreen(
    onStartPlanning: () -> Unit,
    onOpenPlan: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    val viewModel: MenuViewModel = viewModel(factory = AppViewModelFactory())
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    MenuContent(
        state = state,
        onStartPlanning = onStartPlanning,
        onOpenPlan = onOpenPlan,
        onOpenSettings = onOpenSettings,
        onAskDelete = viewModel::askDelete,
        onConfirmDelete = viewModel::confirmDelete,
        onDismissDelete = viewModel::dismissDelete
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onStartPlanning: () -> Unit,
    onOpenPlan: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onAskDelete: (PlanSummary) -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit
) {
    ScreenBackground(
        colors = listOf(Background, SurfaceSunken, BackgroundDeep),
        imageAlpha = 0.18f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 8.dp, top = 28.dp),
                verticalAlignment = Alignment.Top
            ) {
                SectionHeading(
                    eyebrow = stringResource(R.string.menu_eyebrow),
                    title = stringResource(R.string.menu_title),
                    subtitle = stringResource(
                        R.string.menu_subtitle,
                        state.planCount,
                        state.roomCount
                    ),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onOpenSettings, modifier = Modifier.size(48.dp)) {
                    Image(
                        painter = painterResource(R.drawable.icon_pause),
                        contentDescription = stringResource(R.string.menu_settings_cd),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 18.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Image(
                    painter = painterResource(R.drawable.sprite_1),
                    contentDescription = stringResource(R.string.menu_hero_cd),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(150.dp)
                        .height(86.dp)
                        .graphicsLayer {
                            rotationZ = -4f
                            alpha = 0.95f
                        }
                )
            }

            if (state.planCount == 0 && !state.loading) {
                EmptyPlans(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.groups.forEach { group ->
                        item(key = "room-" + group.room) {
                            Text(
                                text = group.room,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                        items(items = group.plans, key = { plan -> plan.id }) { plan ->
                            val color = ratingColor(plan.rating)
                            val sagLabel = stringResource(
                                lengthUnitRes(state.units),
                                plan.sagText
                            )
                            PlanCard(
                                name = plan.name,
                                details = stringResource(
                                    R.string.plan_dims,
                                    stringResource(lengthUnitRes(state.units), plan.lengthText),
                                    stringResource(lengthUnitRes(state.units), plan.depthText),
                                    stringResource(lengthUnitRes(state.units), plan.thicknessText),
                                    stringResource(materialNameRes(plan.material))
                                ),
                                sagLabel = sagLabel,
                                accent = color,
                                description = stringResource(
                                    R.string.menu_plan_cd,
                                    plan.name,
                                    sagLabel
                                ),
                                deleteDescription = stringResource(R.string.menu_delete_cd),
                                onClick = { onOpenPlan(plan.id) },
                                onDelete = { onAskDelete(plan) }
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp, bottom = 20.dp)
            ) {
                if (state.planCount > 0 && state.safeCount > 0) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(
                            label = stringResource(R.string.menu_stat_plans),
                            value = stringResource(R.string.value_count, state.planCount),
                            accent = Primary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = stringResource(R.string.menu_stat_safe),
                            value = stringResource(R.string.value_count, state.safeCount),
                            accent = Accent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
                Text(
                    text = stringResource(R.string.menu_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                PrimaryButton(
                    text = stringResource(R.string.cta_start),
                    onClick = onStartPlanning,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
        }

        if (state.pendingDeleteId != null) {
            DeletePlanDialog(
                planName = state.pendingDeleteName,
                onConfirm = onConfirmDelete,
                onDismiss = onDismissDelete
            )
        }
    }
}

@Composable
private fun EmptyPlans(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_4),
            contentDescription = stringResource(R.string.menu_empty_cd),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(96.dp)
        )
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = stringResource(R.string.menu_empty_title),
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.menu_empty_body),
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
