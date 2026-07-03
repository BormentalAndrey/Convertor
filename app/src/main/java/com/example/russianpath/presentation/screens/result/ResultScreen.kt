package com.example.russianpath.presentation.screens.result

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.russianpath.presentation.components.Emoji
import com.example.russianpath.presentation.components.EmojiText
import com.example.russianpath.presentation.components.KnopaImage
import com.example.russianpath.presentation.components.KnopaMood
import com.example.russianpath.presentation.screens.lesson.LessonResult
import com.example.russianpath.presentation.theme.ErrorRed
import com.example.russianpath.presentation.theme.SuccessGreen
import com.example.russianpath.presentation.theme.VasilisaBlue
import com.example.russianpath.presentation.theme.XpGold
import kotlinx.coroutines.delay

/**
 * Экран результатов после завершения урока.
 *
 * Отображает:
 * - Маскота с реакцией на результат
 * - Количество заработанных звёзд
 * - Количество полученного XP
 * - Детальную статистику попытки
 * - Кнопки «Продолжить» и «Повторить»
 *
 * @param result Результат урока (звёзды, XP, статистика).
 * @param onContinue Действие при нажатии «Продолжить» (возврат к списку тем).
 * @param onRepeat Действие при нажатии «Повторить» (перезапуск урока).
 */
@Composable
fun ResultScreen(
    result: LessonResult,
    onContinue: () -> Unit = {},
    onRepeat: () -> Unit = {}
) {
    // Анимация появления звёзд
    var displayedStars by remember { mutableIntStateOf(0) }
    LaunchedEffect(result.stars) {
        for (i in 1..result.stars) {
            displayedStars = i
            delay(400)
        }
    }

    // Анимация масштаба для XP
    val xpScale by animateFloatAsState(
        targetValue = 1.2f,
        animationSpec = tween(800),
        label = "xpScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        when {
                            result.stars >= 3 -> XpGold.copy(alpha = 0.15f)
                            result.stars >= 2 -> VasilisaBlue.copy(alpha = 0.1f)
                            else -> ErrorRed.copy(alpha = 0.05f)
                        },
                        Color.White,
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
            // Маскот
            KnopaImage(
                modifier = Modifier.size(100.dp),
                mood = when {
                    result.stars >= 3 -> KnopaMood.EXCITED
                    result.stars >= 2 -> KnopaMood.HAPPY
                    else -> KnopaMood.IDLE
                }
            )

            Spacer(Modifier.height(20.dp))

            // Заголовок
            Text(
                text = when {
                    result.stars >= 3 -> "Великолепно!"
                    result.stars >= 2 -> "Хорошая работа!"
                    result.scorePercent > 0 -> "Продолжай стараться!"
                    else -> "Не сдавайся!"
                },
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    result.stars >= 3 -> XpGold
                    result.stars >= 2 -> SuccessGreen
                    else -> VasilisaBlue
                }
            )

            Spacer(Modifier.height(16.dp))

            // Звёзды (анимированные)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(displayedStars) {
                    EmojiText(
                        Emoji.STAR_GOLD,
                        fontSize = 44,
                        modifier = Modifier.scale(
                            animateFloatAsState(
                                targetValue = 1f,
                                animationSpec = tween(300),
                                label = "starScale"
                            ).value
                        )
                    )
                }
                repeat(result.stars - displayedStars) {
                    EmojiText(Emoji.STAR_EMPTY, fontSize = 44)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Карточка с XP
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = XpGold.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EmojiText(Emoji.XP, fontSize = 32)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "+${result.xpEarned}",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = XpGold,
                            modifier = Modifier.scale(xpScale)
                        )
                    }
                    Text(
                        text = "опыта получено",
                        color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Детальная статистика
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ResultStatItem(
                        emoji = Emoji.CHECK,
                        value = "${result.correctAnswers}/${result.totalQuestions}",
                        label = "Правильно"
                    )
                    ResultStatItem(
                        emoji = Emoji.CLOCK,
                        value = formatTime(result.timeSpentSeconds),
                        label = "Время"
                    )
                    ResultStatItem(
                        emoji = Emoji.CHART,
                        value = "${result.scorePercent}%",
                        label = "Точность"
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Кнопка «Продолжить»
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VasilisaBlue
                )
            ) {
                Text(
                    text = "Продолжить ${Emoji.FORWARD}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))

            // Кнопка «Повторить»
            OutlinedButton(
                onClick = onRepeat,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "${Emoji.REPEAT} Повторить урок",
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ResultStatItem(
    emoji: String,
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        EmojiText(emoji, fontSize = 24)
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

/**
 * Форматирует время в секундах в строку "MM:SS".
 */
private fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
