package com.example.russianpath.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.russianpath.R

@Composable
fun MascotAnimation(
    modifier: Modifier = Modifier,
    isHappy: Boolean = false,
    mascotType: MascotType = MascotType.KNOPA
) {
    // Анимация пульсации для PNG
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Прыгающая анимация для радостного состояния
    val jumpOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isHappy) -20f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            painter = painterResource(
                id = when {
                    isHappy && mascotType == MascotType.KNOPA -> R.drawable.ic_knopa_happy
                    isHappy && mascotType == MascotType.VASILISA -> R.drawable.ic_vasilisa_happy
                    mascotType == MascotType.KNOPA -> R.drawable.ic_knopa_idle
                    mascotType == MascotType.VASILISA -> R.drawable.ic_vasilisa
                }
            ),
            contentDescription = if (mascotType == MascotType.KNOPA) "Кнопа" else "Василиса",
            modifier = Modifier
                .size(80.dp)
                .scale(scale)
                .offset(y = jumpOffset.dp),
            contentScale = ContentScale.Fit
        )
    }
}

enum class MascotType {
    KNOPA,
    VASILISA
}

@Composable
fun MascotAnimationStatic(
    modifier: Modifier = Modifier,
    mascotType: MascotType = MascotType.KNOPA,
    mood: MascotMood = MascotMood.IDLE
) {
    val rotationAngle by animateFloatAsState(
        targetValue = when (mood) {
            MascotMood.HAPPY -> 0f
            MascotMood.SAD -> -10f
            MascotMood.IDLE -> 0f
            MascotMood.EXCITED -> 0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val scaleAnim by animateFloatAsState(
        targetValue = when (mood) {
            MascotMood.HAPPY -> 1.1f
            MascotMood.SAD -> 0.9f
            MascotMood.IDLE -> 1f
            MascotMood.EXCITED -> 1.15f
        },
        animationSpec = tween(300)
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                id = when {
                    mascotType == MascotType.KNOPA && mood == MascotMood.HAPPY -> R.drawable.ic_knopa_happy
                    mascotType == MascotType.KNOPA && mood == MascotMood.SAD -> R.drawable.ic_knopa_sad
                    mascotType == MascotType.KNOPA && mood == MascotMood.EXCITED -> R.drawable.ic_knopa_jump
                    mascotType == MascotType.VASILISA && mood == MascotMood.HAPPY -> R.drawable.ic_vasilisa_happy
                    mascotType == MascotType.VASILISA -> R.drawable.ic_vasilisa
                    else -> R.drawable.ic_knopa_idle
                }
            ),
            contentDescription = if (mascotType == MascotType.KNOPA) "Кнопа" else "Василиса",
            modifier = Modifier
                .size(80.dp)
                .scale(scaleAnim)
                .rotate(rotationAngle),
            contentScale = ContentScale.Fit
        )
    }
}

enum class MascotMood {
    IDLE,
    HAPPY,
    SAD,
    EXCITED
}

// Компонент для анимированного появления конфетти (используя PNG)
@Composable
fun ConfettiAnimation(
    modifier: Modifier = Modifier,
    isVisible: Boolean = false
) {
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(500)
    )
    
    val confettiScale by animateFloatAsState(
        targetValue = if (isVisible) 1.2f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    if (alpha > 0f) {
        Image(
            painter = painterResource(R.drawable.ic_confetti),
            contentDescription = "Конфетти",
            modifier = modifier
                .fillMaxSize()
                .scale(confettiScale)
                .alpha(alpha),
            contentScale = ContentScale.FillBounds
        )
    }
}

// Анимированная звезда
@Composable
fun AnimatedStar(
    modifier: Modifier = Modifier,
    isEarned: Boolean = false,
    delay: Int = 0
) {
    val scale by animateFloatAsState(
        targetValue = if (isEarned) 1f else 0.5f,
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = delay
        ),
        finishedListener = {}
    )
    
    Image(
        painter = painterResource(
            if (isEarned) R.drawable.ic_star_gold else R.drawable.ic_star_empty
        ),
        contentDescription = if (isEarned) "Звезда" else "Пустая звезда",
        modifier = modifier
            .size(40.dp)
            .scale(scale),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun LivesDisplay(
    modifier: Modifier = Modifier,
    currentLives: Int = 5,
    maxLives: Int = 5
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(maxLives) { index ->
            val isFilled = index < currentLives
            
            val alpha by animateFloatAsState(
                targetValue = if (isFilled) 1f else 0.3f,
                animationSpec = tween(300)
            )
            
            Image(
                painter = painterResource(
                    if (isFilled) R.drawable.ic_heart else R.drawable.ic_heart_empty
                ),
                contentDescription = if (isFilled) "Жизнь" else "Потерянная жизнь",
                modifier = Modifier
                    .size(24.dp)
                    .alpha(alpha),
                contentScale = ContentScale.Fit
            )
        }
    }
}
