package com.shelfsag.spacer.core.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.shelfsag.spacer.R
import com.shelfsag.spacer.core.theme.Accent
import com.shelfsag.spacer.core.theme.BackgroundDeep
import com.shelfsag.spacer.core.theme.Outline
import com.shelfsag.spacer.core.theme.Primary
import com.shelfsag.spacer.core.theme.PrimaryDark
import com.shelfsag.spacer.core.theme.PrimaryLight
import com.shelfsag.spacer.core.theme.RatingInvisible
import com.shelfsag.spacer.core.theme.RatingNoticeable
import com.shelfsag.spacer.core.theme.TextSecondary
import kotlin.math.atan2

private val SpineColors = listOf(Primary, Accent, RatingNoticeable, RatingInvisible, TextSecondary)
private val SpineHeights = listOf(44f, 52f, 48f, 58f, 46f, 54f, 42f)

private fun sagAt(mid: Float, t: Float): Float = 4f * mid * t * (1f - t)

@Composable
fun SagCanvas(
    sagFraction: Float,
    description: String,
    valueLabel: String,
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    tools: Boolean = false
) {
    val target = sagFraction.coerceIn(0f, 1f)
    val settled by animateFloatAsState(
        targetValue = target,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "sagSettle"
    )
    val value = if (animated) settled else target

    BoxWithConstraints(modifier = modifier.semantics { contentDescription = description }) {
        val areaHeight = maxHeight
        val areaWidth = maxWidth
        val baseLine = areaHeight * 0.42f
        val midSag = areaHeight * 0.30f * value
        val boardLeft = areaWidth * 0.12f
        val boardSpan = areaWidth * 0.84f

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val left = width * 0.12f
            val right = width * 0.96f
            val span = right - left
            val base = height * 0.42f
            val mid = height * 0.30f * value
            val control = mid * 4f / 3f
            val thickness = height * 0.055f

            drawRect(
                color = BackgroundDeep,
                topLeft = Offset(0f, 0f),
                size = Size(width * 0.085f, height)
            )
            for (i in 0..3) {
                val y = height * (0.12f + i * 0.24f)
                drawLine(
                    color = Outline,
                    start = Offset(0f, y),
                    end = Offset(width * 0.085f, y + height * 0.05f),
                    strokeWidth = 2f
                )
            }

            drawLine(
                color = Accent.copy(alpha = 0.55f),
                start = Offset(left, base),
                end = Offset(right, base),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
            )

            val board = Path().apply {
                moveTo(left, base)
                cubicTo(
                    left + span / 3f, base + control,
                    left + span * 2f / 3f, base + control,
                    right, base
                )
                lineTo(right, base + thickness)
                cubicTo(
                    left + span * 2f / 3f, base + control + thickness,
                    left + span / 3f, base + control + thickness,
                    left, base + thickness
                )
                close()
            }
            drawPath(path = board, color = Primary)

            val highlight = Path().apply {
                moveTo(left, base)
                cubicTo(
                    left + span / 3f, base + control,
                    left + span * 2f / 3f, base + control,
                    right, base
                )
            }
            drawPath(path = highlight, color = PrimaryLight, style = Stroke(width = 3f))

            val shadow = Path().apply {
                moveTo(left, base + thickness)
                cubicTo(
                    left + span / 3f, base + control + thickness,
                    left + span * 2f / 3f, base + control + thickness,
                    right, base + thickness
                )
            }
            drawPath(path = shadow, color = PrimaryDark, style = Stroke(width = 3f))

            if (!tools) {
                val spineWidth = width * 0.035f
                SpineHeights.forEachIndexed { index, factor ->
                    val t = 0.12f + index * 0.127f
                    val x = left + span * t
                    val y = base + sagAt(mid, t)
                    val spineHeight = height * factor / 180f
                    val angle = atan2(4f * mid * (1f - 2f * t), span) * 180f / Math.PI.toFloat()
                    rotate(degrees = angle, pivot = Offset(x, y)) {
                        drawRoundRect(
                            color = SpineColors[index % SpineColors.size],
                            topLeft = Offset(x - spineWidth / 2f, y - spineHeight),
                            size = Size(spineWidth, spineHeight),
                            cornerRadius = CornerRadius(3f, 3f)
                        )
                    }
                }
            }

            val midX = left + span / 2f
            drawLine(
                color = Accent,
                start = Offset(midX, base),
                end = Offset(midX, base + mid),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )
            drawCircle(color = Accent, radius = 5f, center = Offset(midX, base + mid))
            drawCircle(color = Accent, radius = 4f, center = Offset(midX, base))
        }

        if (tools) {
            listOf(0.26f, 0.5f, 0.74f).forEach { t ->
                val x = boardLeft + boardSpan * t
                val y = baseLine + midSag * (4f * t * (1f - t))
                Image(
                    painter = painterResource(R.drawable.sprite_5),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .offset(x = x - 22.dp, y = y - 44.dp)
                        .size(44.dp)
                )
            }
        }

        Text(
            text = valueLabel,
            style = MaterialTheme.typography.bodySmall,
            color = Accent,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp)
        )
    }
}
