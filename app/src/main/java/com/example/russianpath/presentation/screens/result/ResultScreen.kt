package com.example.russianpath.presentation.screens.result

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.russianpath.presentation.components.*
import com.example.russianpath.presentation.theme.*

@Composable
// ИСПРАВЛЕНО: Добавлен lessonId
fun ResultScreen(
    lessonId: String = "",
    stars: Int = 3,
    xpEarned: Int = 50,
    onContinue: () -> Unit = {},
    onRepeat: () -> Unit = {}
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            KnopaImage(
                modifier = Modifier.size(120.dp),
                mood = when (stars) {
                    3 -> KnopaMood.EXCITED
                    2 -> KnopaMood.HAPPY
                    else -> KnopaMood.SAD
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (stars >= 3) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(5) { EmojiText(Emoji.CONFETTI, fontSize = 32) }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                when (stars) {
                    3 -> "Отлично!"
                    2 -> "Хорошо!"
                    else -> "Продолжай учиться!"
                },
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = when (stars) {
                    3 -> XpGold
                    2 -> VasilisaBlue
                    else -> Color.Gray
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(stars) { EmojiText(Emoji.STAR_GOLD, fontSize = 40) }
                repeat(3 - stars) { EmojiText(Emoji.STAR_EMPTY, fontSize = 40) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = XpGold.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EmojiText(Emoji.XP, fontSize = 32)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "+$xpEarned XP",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = XpGold
                        )
                    }
                    Text("опыта получено", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VasilisaBlue)
            ) {
                Text("Продолжить ${Emoji.FORWARD}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onRepeat,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("${Emoji.REPEAT} Повторить урок", fontSize = 18.sp)
            }
        }
    }
}
