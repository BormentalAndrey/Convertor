package com.example.russianpath.presentation.screens.result

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.russianpath.R
import com.example.russianpath.presentation.components.AnimatedStar
import com.example.russianpath.presentation.components.ConfettiAnimation
import com.example.russianpath.presentation.components.MascotAnimationStatic
import com.example.russianpath.presentation.components.MascotMood
import com.example.russianpath.presentation.components.MascotType
import com.example.russianpath.presentation.theme.*

@Composable
fun ResultScreen(
    lessonId: String,
    stars: Int,
    xpEarned: Int,
    onContinue: () -> Unit,
    onRepeat: () -> Unit
) {
    // Анимация пульсации для главной звезды
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Состояние для конфетти
    var showConfetti by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        if (stars >= 2) {
            showConfetti = true
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Фон
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            if (stars >= 3) XpGold.copy(alpha = 0.2f)
                            else if (stars >= 2) VasilisaBlue.copy(alpha = 0.2f)
                            else Color.White,
                            Color.White
                        )
                    )
                )
        )
        
        // Конфетти
        ConfettiAnimation(
            modifier = Modifier.fillMaxSize(),
            isVisible = showConfetti
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Маскот в зависимости от результата
            MascotAnimationStatic(
                modifier = Modifier.size(120.dp),
                mascotType = MascotType.KNOPA,
                mood = when (stars) {
                    3 -> MascotMood.EXCITED
                    2 -> MascotMood.HAPPY
                    else -> MascotMood.SAD
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Заголовок
            Text(
                text = when (stars) {
                    3 -> "Отлично!"
                    2 -> "Хорошо!"
                    else -> "Продолжай учиться!"
                },
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = when (stars) {
                    3 -> XpGold
                    2 -> VasilisaBlue
                    else -> MaterialTheme.colorScheme.onBackground
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Звездочки с анимацией
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { index ->
                    AnimatedStar(
                        isEarned = index < stars,
                        delay = index * 300
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // XP начислено
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = XpGold.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_xp),
                            contentDescription = "XP",
                            modifier = Modifier.size(32.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+$xpEarned XP",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = XpGold
                        )
                    }
                    Text(
                        text = "опыта получено",
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Кнопки действий
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VasilisaBlue)
            ) {
                Text(
                    "Продолжить →",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = onRepeat,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Повторить урок",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
