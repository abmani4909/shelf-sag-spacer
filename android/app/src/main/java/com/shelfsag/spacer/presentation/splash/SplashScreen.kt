package com.shelfsag.spacer.presentation.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shelfsag.spacer.R
import com.shelfsag.spacer.core.di.AppViewModelFactory
import com.shelfsag.spacer.core.theme.Accent
import com.shelfsag.spacer.core.theme.OnDark
import com.shelfsag.spacer.core.theme.OnDarkMuted
import com.shelfsag.spacer.core.theme.Primary
import com.shelfsag.spacer.core.theme.RatingNoticeable
import com.shelfsag.spacer.core.theme.SplashMid
import com.shelfsag.spacer.core.theme.SplashTop
import com.shelfsag.spacer.core.theme.TextMuted
import com.shelfsag.spacer.core.ui.ScreenBackground

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val viewModel: SplashViewModel = viewModel(factory = AppViewModelFactory())
    val finished by viewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) onFinished()
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.pause() }
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    val entry = remember { MutableTransitionState(false).apply { targetState = true } }
    val transition = rememberInfiniteTransition(label = "splashDecor")
    val breath by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoBreath"
    )
    val settle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shelfSettle"
    )

    ScreenBackground(
        colors = listOf(SplashTop, SplashMid, SplashTop),
        imageAlpha = 0.20f
    ) {
        AnimatedVisibility(
            visibleState = entry,
            enter = fadeIn(tween(520)) + slideInVertically(tween(520)) { full -> full / 12 },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.splash_logo_cd),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(148.dp)
                        .graphicsLayer {
                            scaleX = breath
                            scaleY = breath
                        }
                )
                Spacer(modifier = Modifier.height(26.dp))
                Text(
                    text = stringResource(R.string.splash_title),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 34.sp,
                        lineHeight = 40.sp,
                        letterSpacing = 2.2.sp
                    ),
                    color = OnDark,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(14.dp))
                Spacer(
                    modifier = Modifier
                        .width(64.dp)
                        .height(2.dp)
                        .background(Primary, RoundedCornerShape(1.dp))
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.splash_tagline),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        letterSpacing = 3.sp
                    ),
                    color = OnDarkMuted.copy(alpha = 0.78f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(30.dp))
                SettlingShelf(progress = settle)
                Spacer(modifier = Modifier.height(24.dp))
                LinearProgressIndicator(
                    color = RatingNoticeable,
                    trackColor = SplashMid,
                    modifier = Modifier
                        .width(180.dp)
                        .height(4.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.splash_loading),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        letterSpacing = 4.sp
                    ),
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun SettlingShelf(progress: Float) {
    Box(
        modifier = Modifier
            .width(200.dp)
            .height(54.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val base = height * 0.74f
            val dip = height * 0.22f * progress
            val control = dip * 4f / 3f
            val board = Path().apply {
                moveTo(0f, base)
                cubicTo(
                    width / 3f, base + control,
                    width * 2f / 3f, base + control,
                    width, base
                )
            }
            drawPath(
                path = board,
                color = Primary,
                style = Stroke(width = height * 0.11f, cap = StrokeCap.Round)
            )
            val spineColors = listOf(Primary, Accent, RatingNoticeable)
            spineColors.forEachIndexed { index, color ->
                val t = 0.3f + index * 0.2f
                val x = width * t
                val y = base + 4f * dip * t * (1f - t)
                val spineHeight = height * (0.34f + index * 0.06f)
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x - width * 0.016f, y - spineHeight - height * 0.05f),
                    size = Size(width * 0.032f, spineHeight),
                    cornerRadius = CornerRadius(2f, 2f)
                )
            }
        }
    }
}
