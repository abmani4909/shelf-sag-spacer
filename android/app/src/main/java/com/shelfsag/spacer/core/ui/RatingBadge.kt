package com.shelfsag.spacer.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shelfsag.spacer.core.theme.ratingColor
import com.shelfsag.spacer.domain.model.SagRating

@Composable
fun RatingBadge(
    rating: SagRating,
    modifier: Modifier = Modifier,
    compact: Boolean = true
) {
    val label = stringResource(ratingLabelRes(rating))
    AnimatedContent(
        targetState = rating,
        transitionSpec = { fadeIn(tween(160)) togetherWith fadeOut(tween(160)) },
        label = "ratingBadge",
        modifier = modifier.semantics { stateDescription = label }
    ) { target ->
        val color = ratingColor(target)
        Box(
            modifier = Modifier
                .height(if (compact) 28.dp else 56.dp)
                .background(color.copy(alpha = 0.16f), RoundedCornerShape(12.dp))
                .border(if (compact) 1.dp else 1.5.dp, color, RoundedCornerShape(12.dp))
                .padding(horizontal = if (compact) 10.dp else 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(ratingLabelRes(target)),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = if (compact) 11.sp else 15.sp,
                    letterSpacing = 2.sp
                ),
                color = color
            )
        }
    }
}
