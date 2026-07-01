package com.example.russianpath.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.russianpath.R

@Composable
fun MascotAnimation(
    modifier: Modifier = Modifier,
    isHappy: Boolean = false,
    mascotType: MascotType = MascotType.KNOPA
) {
    val infiniteTransition = rememberInfiniteTransition(label = "MascotPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScaleAnimation"
    )
    
    val jumpOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isHappy) -20f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "JumpAnimation"
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
                    else -> R.drawable.ic_knopa_idle // ИСПРАВЛЕНО: Добавлен обязательный else
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
        ),
        label = "RotationAnimation"
    )
    
    val scaleAnim by animateFloatAsState(
        targetValue = when (mood) {
            MascotMood.HAPPY -> 1.1f
            MascotMood.SAD -> 0.9f
            MascotMood.IDLE -> 1f
            MascotMood.EXCITED -> 1.15f
        },
        animationSpec = tween(300),
        label = "ScaleStaticAnimation"
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
                    mascotType == MascotType.KNOPA && mood == MascotMood.EXCITED -> R.drawable.ic_knopa_happy // Заменено для безопасности
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

@Composable
fun ConfettiAnimation(
    modifier: Modifier = Modifier,
    isVisible: Boolean = false
) {
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(500),
        label = "ConfettiAlpha"
    )
    
    val confettiScale by animateFloatAsState(
        targetValue = if (isVisible) 1.2f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ConfettiScale"
    )
    
    if (alpha > 0f) {
        // ИСПРАВЛЕНО: Вместо отсутствующей картинки используем текстовый эмодзи конфетти
        Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()) {
            Text(
                text = "🎊",
                fontSize = 120.sp,
                modifier = Modifier
                    .scale(confettiScale)
                    .alpha(alpha)
            )
        }
    }
}

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
        label = "StarScale"
    )
    
    // ИСПРАВЛЕНО: Используем стандартные Material иконки
    Icon(
        imageVector = if (isEarned) Icons.Filled.Star else Icons.Outlined.StarOutline,
        contentDescription = if (isEarned) "Звезда" else "Пустая звезда",
        tint = if (isEarned) Color(0xFFFFD700) else Color.Gray,
        modifier = modifier
            .size(40.dp)
            .scale(scale)
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
                animationSpec = tween(300),
                label = "HeartAlpha"
            )
            
            // ИСПРАВЛЕНО: Используем стандартные Material иконки
            Icon(
                imageVector = if (isFilled) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFilled) "Жизнь" else "Потерянная жизнь",
                tint = if (isFilled) Color.Red else Color.Gray,
                modifier = Modifier
                    .size(24.dp)
                    .alpha(alpha)
            )
        }
    }
}
