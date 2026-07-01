package com.example.russianpath.presentation.screens.result

import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.russianpath.presentation.theme.*

@Composable
fun ResultScreen(
    lessonId: String,
    stars: Int,
    xpEarned: Int,
    onContinue: () -> Unit,
    onRepeat: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val confettiComposition by rememberLottieComposition(
        LottieCompositionSpec.Asset("animations/confetti.json")
    )
    val confettiProgress by animateLottieCompositionAsState(
        composition = confettiComposition,
        iterations = 1,
        isPlaying = true
    )
    
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
                            else VasilisaBlue.copy(alpha = 0.2f),
                            Color.White
                        )
                    )
                )
        )
        
        // Конфетти
        if (stars >= 2) {
            LottieAnimation(
                composition = confettiComposition,
                progress = confettiProgress,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Анимация персонажа
            Text(
                text = when (stars) {
                    3 -> "🌟"
                    2 -> "⭐"
                    else -> "📚"
                },
                fontSize = 80.sp,
                modifier = Modifier.scale(scale)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Звезды
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Звездочки
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(stars) {
                    Text("⭐", fontSize = 40.sp)
                }
                repeat(3 - stars) {
                    Text("☆", fontSize = 40.sp)
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
                    Text(
                        text = "+$xpEarned XP",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = XpGold
                    )
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
                Text(
                    "Повторить урок 🔄",
                    fontSize = 18.sp
                )
            }
        }
    }
}
