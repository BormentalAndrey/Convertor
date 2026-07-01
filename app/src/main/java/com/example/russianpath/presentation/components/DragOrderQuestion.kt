package com.example.russianpath.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.russianpath.presentation.theme.VasilisaBlue
import com.example.russianpath.presentation.theme.BackgroundGray

@Composable
fun DragOrderQuestion(
    questionText: String,
    shuffledParts: List<String>,
    onAnswerReady: (List<String>) -> Unit
) {
    // Состояние слотов (куда ребенок ставит слова/слоги)
    var selectedParts by remember { mutableStateOf(listOf<String>()) }
    
    // Оставшиеся доступные для выбора части
    val availableParts = shuffledParts.filter { !selectedParts.contains(it) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = questionText,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Зона со слотами (выбранные элементы)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundGray),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedParts.isEmpty()) {
                    Text(
                        text = "Нажимай на карточки внизу...",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                } else {
                    // Используем FlowRow в реальном проекте, но для простоты здесь Row + Scroll или просто Row
                    selectedParts.forEach { part ->
                        WordChip(
                            text = part,
                            isSelected = true,
                            onClick = {
                                // Возвращаем карточку обратно вниз
                                selectedParts = selectedParts - part
                                onAnswerReady(selectedParts)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Зона доступных элементов (откуда выбираем)
        // Для переноса на новую строку можно использовать FlowRow из Accompanist/Compose Foundation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            availableParts.forEach { part ->
                WordChip(
                    text = part,
                    isSelected = false,
                    onClick = {
                        // Перемещаем карточку наверх
                        selectedParts = selectedParts + part
                        onAnswerReady(selectedParts)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
private fun WordChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) VasilisaBlue else Color.White)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}
