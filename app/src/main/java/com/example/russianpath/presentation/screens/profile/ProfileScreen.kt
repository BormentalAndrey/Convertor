package com.example.russianpath.presentation.screens.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.russianpath.domain.model.UserStats
import com.example.russianpath.presentation.components.Emoji
import com.example.russianpath.presentation.components.EmojiText
import com.example.russianpath.presentation.components.VasilisaImage
import com.example.russianpath.presentation.screens.dashboard.DashboardViewModel
import com.example.russianpath.presentation.theme.ErrorRed
import com.example.russianpath.presentation.theme.GemCrystal
import com.example.russianpath.presentation.theme.KnopaOrange
import com.example.russianpath.presentation.theme.SuccessGreen
import com.example.russianpath.presentation.theme.VasilisaBlue
import com.example.russianpath.presentation.theme.XpGold

/**
 * Экран профиля пользователя.
 *
 * Отображает:
 * - Аватар и уровень
 * - Детальную статистику
 * - Достижения (заглушка)
 * - Время в приложении
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Профиль", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        EmojiText(Emoji.BACK, fontSize = 24)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = VasilisaBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Аватар и уровень
                ProfileHeader(userStats = userStats)

                // Основная статистика
                StatsCard(userStats = userStats)

                // Детальная статистика
                DetailedStatsCard(userStats = userStats)

                // Прогресс уровня
                LevelCard(userStats = userStats)

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ProfileHeader(userStats: UserStats) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VasilisaImage(
            modifier = Modifier.size(120.dp),
            isHappy = userStats.currentStreak > 0
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = userStats.getLevelTitle(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Уровень ${userStats.level}",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )
        if (userStats.currentStreak > 0) {
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                EmojiText(Emoji.STREAK, fontSize = 20)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Серия: ${userStats.currentStreak} дней",
                    color = KnopaOrange,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StatsCard(userStats: UserStats) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Основные показатели",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(Emoji.XP, userStats.totalXp.toString(), "XP", XpGold)
                StatItem(Emoji.GEM, userStats.gemsBalance.toString(), "Самоцветов", GemCrystal)
                StatItem(Emoji.HEART, "${userStats.livesCount}/${userStats.maxLives}", "Жизней", ErrorRed)
                StatItem(Emoji.STREAK, userStats.longestStreak.toString(), "Рекорд", KnopaOrange)
            }
        }
    }
}

@Composable
private fun DetailedStatsCard(userStats: UserStats) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Детальная статистика",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(12.dp))

            DetailedStatRow("Завершено уроков", userStats.totalLessonsCompleted.toString())
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            DetailedStatRow("Идеальных уроков", userStats.totalPerfectLessons.toString())
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            DetailedStatRow("Всего ошибок", userStats.totalMistakesCount.toString())
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            DetailedStatRow(
                "Точность",
                "${userStats.accuracy.toInt()}%"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            DetailedStatRow(
                "Время обучения",
                userStats.getFormattedTotalTime()
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            DetailedStatRow(
                "Дней активности",
                userStats.totalDaysActive.toString()
            )
        }
    }
}

@Composable
private fun LevelCard(userStats: UserStats) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Прогресс уровня",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Уровень ${userStats.level}",
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Уровень ${userStats.level + 1}",
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = userStats.getLevelProgressPercent() / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = XpGold,
                trackColor = XpGold.copy(alpha = 0.2f)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Осталось ${userStats.xpToNextLevel} XP до следующего уровня",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun StatItem(
    emoji: String,
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        EmojiText(emoji, fontSize = 32)
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun DetailedStatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
