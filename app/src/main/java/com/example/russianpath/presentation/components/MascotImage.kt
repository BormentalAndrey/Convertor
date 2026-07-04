// app/src/main/java/com/example/russianpath/presentation/components/MascotImage.kt

package com.example.russianpath.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.russianpath.R

@Composable
fun VasilisaImage(
    modifier: Modifier = Modifier,
    isHappy: Boolean = false
) {
    val scale by animateFloatAsState(
        targetValue = if (isHappy) 1.05f else 1f,
        animationSpec = tween(300),
        label = "VasilisaScale"
    )

    Image(
        painter = painterResource(
            if (isHappy) R.drawable.ic_vasilisa_happy
            else R.drawable.ic_vasilisa_idle
        ),
        contentDescription = "Василиса",
        modifier = modifier
            .size(400.dp)
            .scale(scale),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun KnopaImage(
    modifier: Modifier = Modifier,
    mood: KnopaMood = KnopaMood.IDLE
) {
    val infiniteTransition = rememberInfiniteTransition(label = "KnopaInfinite")

    val idleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "KnopaIdleScale"
    )

    val jumpOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (mood == KnopaMood.HAPPY || mood == KnopaMood.EXCITED) -20f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "KnopaJumpOffset"
    )

    val targetScale = when (mood) {
        KnopaMood.HAPPY -> 1.08f
        KnopaMood.EXCITED -> 1.15f
        KnopaMood.SAD -> 0.9f
        KnopaMood.SURPRISED -> 1.1f
        KnopaMood.IDLE -> idleScale
    }

    val rotationAngle = when (mood) {
        KnopaMood.SAD -> -8f
        KnopaMood.SURPRISED -> 5f
        else -> 0f
    }

    val imageRes = when (mood) {
        KnopaMood.IDLE -> R.drawable.ic_knopa_idle
        KnopaMood.HAPPY -> R.drawable.ic_knopa_happy
        KnopaMood.SAD -> R.drawable.ic_knopa_sad
        KnopaMood.EXCITED -> R.drawable.ic_knopa_excited
        KnopaMood.SURPRISED -> R.drawable.ic_knopa_jump
    }

    Image(
        painter = painterResource(imageRes),
        contentDescription = "Кнопа",
        modifier = modifier
            .size(200.dp)
            .scale(targetScale)
            .offset(y = jumpOffset.dp)
            .rotate(rotationAngle),
        contentScale = ContentScale.Fit
    )
}
