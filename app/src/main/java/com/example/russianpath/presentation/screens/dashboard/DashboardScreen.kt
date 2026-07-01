package com.example.russianpath.presentation.screens.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController // ИСПРАВЛЕНО: Добавлен импорт
import com.example.russianpath.presentation.components.*
import com.example.russianpath.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// ИСПРАВЛЕНО: Добавлен параметр navController
fun DashboardScreen(navController: NavController) {
    // Временные заглушки данных для демонстрации
    val userXp = 350
    val userGems = 120
    val userLives = 5
    val userStreak = 3

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EmojiText(Emoji.BOOK, fontSize = 28)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Русский Путь", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        StatusChip(Emoji.XP, "$userXp", XpGold)
                        Spacer(modifier = Modifier.width(6.dp))
                        StatusChip(Emoji.GEM, "$userGems", GemCrystal)
                        Spacer(modifier = Modifier.width(6.dp))
                        StatusChip(Emoji.HEART, "$userLives", ErrorRed)
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(onClick = { }) {
                            EmojiText(Emoji.PROFILE, fontSize = 28)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VasilisaBlue,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(VasilisaBlue.copy(alpha = 0.08f), Color.White)
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Карточка с персонажами
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Василиса и Кнопа рядом
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        VasilisaImage(
                            modifier = Modifier.size(120.dp),
                            isHappy = userStreak > 0
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        KnopaImage(
                            modifier = Modifier.size(80.dp),
                            mood = if (userStreak > 0) KnopaMood.HAPPY else KnopaMood.IDLE
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Привет! Я Кнопа, а это Василиса. Давай учить русский язык!",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EmojiText(Emoji.STREAK, fontSize = 20)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Серия: $userStreak дней",
                            color = KnopaOrange,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Заголовок
            Text(
                "Твой путь знаний",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Примеры тем (заглушки)
            repeat(5) { index ->
                TopicCardPlaceholder(index)
            }
        }
    }
}

@Composable
fun StatusChip(emoji: String, value: String, color: Color) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmojiText(emoji, fontSize = 18)
            Spacer(modifier = Modifier.width(4.dp))
            Text(value, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun TopicCardPlaceholder(index: Int) {
    val emojis = listOf("🔤", "✍️", "📖", "🗣️", "✏️")
    val titles = listOf(
        "Фонетика и звуки",
        "Правописание корней",
        "Части речи",
        "Орфоэпия",
        "Синтаксис"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmojiText(emojis[index % emojis.size], fontSize = 40)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    titles[index % titles.size],
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "5 класс • Прогресс: ${(index + 1) * 20}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            EmojiText(Emoji.FORWARD, fontSize = 24)
        }
    }
}
