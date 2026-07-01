package com.example.russianpath.presentation.screens.lesson

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.russianpath.presentation.components.*
import com.example.russianpath.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    onBackClick: () -> Unit = {},
    onComplete: () -> Unit = {}
) {
    var currentQuestion by remember { mutableStateOf(0) }
    val totalQuestions = 5
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    val lives = 5

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Правописание корней", fontWeight = FontWeight.Bold)
                        LinearProgressIndicator(
                            progress = (currentQuestion + 1).toFloat() / totalQuestions,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = VasilisaBlue,
                            trackColor = Color.Gray.copy(alpha = 0.2f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        EmojiText(Emoji.BACK, fontSize = 24)
                    }
                },
                actions = {
                    Row(modifier = Modifier.padding(end = 16.dp)) {
                        repeat(lives) { EmojiText(Emoji.HEART, fontSize = 20) }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Вопрос
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Выберите правильное написание:",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Варианты ответов
                    val options = listOf("Собирать", "Соберать", "Сабирать", "Собиреть")
                    options.forEach { option ->
                        Button(
                            onClick = {
                                isCorrect = option == "Собирать"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text(option, fontSize = 18.sp)
                        }
                    }
                }
            }

            // Обратная связь
            AnimatedVisibility(visible = isCorrect != null) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCorrect == true)
                            SuccessGreen.copy(alpha = 0.1f)
                        else
                            ErrorRed.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        EmojiText(
                            if (isCorrect == true) Emoji.CHECK else Emoji.CROSS,
                            fontSize = 28
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            if (isCorrect == true) "Отлично!" else "Неправильно. Подсказка: после корня есть суффикс -А-",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Кнопка "Далее"
            Button(
                onClick = {
                    if (currentQuestion < totalQuestions - 1) {
                        currentQuestion++
                        isCorrect = null
                    } else {
                        onComplete()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isCorrect != null,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCorrect == true) SuccessGreen else VasilisaBlue
                )
            ) {
                Text(
                    if (currentQuestion < totalQuestions - 1) "Далее ${Emoji.FORWARD}"
                    else "Завершить ${Emoji.CHECK}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
