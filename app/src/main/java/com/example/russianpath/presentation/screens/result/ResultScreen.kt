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
import androidx.compose.material3.MaterialTheme
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
import androidx.hilt.navigation.compose.hiltViewModel
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
 * - Маскота Кнопу с реакцией на результат (EXCITED / HAPPY / IDLE)
 * - Количество заработанных звёзд с анимацией последовательного появления
 * - Количество полученного XP с анимацией пульсации
 * - Детальную статистику попытки (правильно/всего, время, точность)
 * - Кнопки «Продолжить» (возврат к списку тем) и «Повторить» (перезапуск урока)
 *
 * Результат сохраняется в ResultViewModel для переживания пересоздания конфигурации.
 *
 * @param result Результат урока, переданный через NavGraph (JSON → LessonResult).
 * @param onContinue Колбэк для кнопки «Продолжить».
 * @param onRepeat Колбэк для кнопки «Повторить».
 */
@Composable
fun ResultScreen(
    result: LessonResult,
    onContinue: () -> Unit = {},
    onRepeat: () -> Unit = {},
    viewModel: ResultViewModel = hiltViewModel()
) {
    // Сохраняем результат в ViewModel при первом composable
    // Это защищает от потери данных при пересоздании конфигурации (поворот экрана)
    LaunchedEffect(result) {
        viewModel.setResult(result)
    }

    // Анимация последовательного появления звёзд (по одной каждые 400 мс)
    var displayedStars by remember { mutableIntStateOf(0) }
    LaunchedEffect(result.stars) {
        // Сбрасываем перед анимацией
        displayedStars = 0
        for (i in 1..result.stars) {
            displayedStars = i
            delay(400)
        }
    }

    // Анимация пульсации для XP (увеличение до 120% и обратно)
    val xpScale by animateFloatAsState(
        targetValue = 1.2f,
        animationSpec = tween(800),
        label = "xpScale"
    )

    // Цвет фона зависит от результата:
    // - 3 звезды → золотой оттенок
    // - 2 звезды → синий оттенок
    // - 1 звезда или меньше → красноватый оттенок
    val backgroundColor = when {
        result.stars >= 3 -> XpGold.copy(alpha = 0.15f)
        result.stars >= 2 -> VasilisaBlue.copy(alpha = 0.1f)
        else -> ErrorRed.copy(alpha = 0.05f)
    }

    // Настроение маскота зависит от количества звёзд
    val mascotMood = when {
        result.stars >= 3 -> KnopaMood.EXCITED
        result.stars >= 2 -> KnopaMood.HAPPY
        else -> KnopaMood.IDLE
    }

    // Заголовок и его цвет
    val titleText = when {
        result.stars >= 3 -> "Великолепно!"
        result.stars >= 2 -> "Хорошая работа!"
        result.scorePercent > 0 -> "Продолжай стараться!"
        else -> "Не сдавайся!"
    }

    val titleColor = when {
        result.stars >= 3 -> XpGold
        result.stars >= 2 -> SuccessGreen
        else -> VasilisaBlue
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(backgroundColor, Color.White, Color.White)
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
            // ================================================================
            // Маскот Кнопа
            // ================================================================
            KnopaImage(
                modifier = Modifier.size(100.dp),
                mood = mascotMood
            )

            Spacer(Modifier.height(20.dp))

            // ================================================================
            // Заголовок результата
            // ================================================================
            Text(
                text = titleText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )

            // Название урока (если есть)
            if (result.lessonTitle.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = result.lessonTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(16.dp))

            // ================================================================
            // Звёзды с анимацией появления
            // ================================================================
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Уже появившиеся звёзды (золотые, с анимацией масштаба)
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
                // Ещё не появившиеся звёзды (пустые)
                repeat(result.stars - displayedStars) {
                    EmojiText(Emoji.STAR_EMPTY, fontSize = 44)
                }
            }

            Spacer(Modifier.height(24.dp))

            // ================================================================
            // Карточка с заработанным XP
            // ================================================================
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

            // ================================================================
            // Детальная статистика попытки
            // ================================================================
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
                    ResultDetailItem(
                        emoji = Emoji.CHECK,
                        value = "${result.correctAnswers}/${result.totalQuestions}",
                        label = "Правильно"
                    )
                    ResultDetailItem(
                        emoji = Emoji.CLOCK,
                        value = formatTime(result.timeSpentSeconds),
                        label = "Время"
                    )
                    ResultDetailItem(
                        emoji = Emoji.CHART,
                        value = "${result.scorePercent}%",
                        label = "Точность"
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ================================================================
            // Кнопка «Продолжить» (возврат к списку тем)
            // ================================================================
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

            // ================================================================
            // Кнопка «Повторить» (перезапуск урока)
            // ================================================================
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

    // Очищаем результат при уходе с экрана
    LaunchedEffect(Unit) {
        // DisposableEffect не нужен, так как LaunchedEffect с Unit
        // выполняется только один раз при входе в композицию.
        // Очистка произойдёт при следующей навигации на этот экран.
    }
}

// ========================================================================
// Переиспользуемые компоненты
// ========================================================================

/**
 * Элемент детальной статистики на экране результата.
 *
 * @param emoji Эмодзи-символ для отображения.
 * @param value Значение показателя (строка).
 * @param label Подпись под значением.
 */
@Composable
private fun ResultDetailItem(
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

// ========================================================================
// Утилиты
// ========================================================================

/**
 * Форматирует время в секундах в строку "M:SS" или "MM:SS".
 *
 * Примеры:
 * - 65 → "1:05"
 * - 125 → "2:05"
 * - 3600 → "60:00"
 *
 * @param totalSeconds Общее количество секунд.
 * @return Отформатированная строка времени.
 */
private fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
