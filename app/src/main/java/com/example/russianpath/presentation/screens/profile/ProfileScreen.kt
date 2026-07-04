// app/src/main/java/com/example/russianpath/presentation/screens/profile/ProfileScreen.kt

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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.russianpath.domain.model.UserStats
import com.example.russianpath.presentation.components.Emoji
import com.example.russianpath.presentation.components.EmojiText
import com.example.russianpath.presentation.components.VasilisaImage
import com.example.russianpath.presentation.theme.ErrorRed
import com.example.russianpath.presentation.theme.GemCrystal
import com.example.russianpath.presentation.theme.KnopaOrange
import com.example.russianpath.presentation.theme.SuccessGreen
import com.example.russianpath.presentation.theme.VasilisaBlue
import com.example.russianpath.presentation.theme.XpGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Профиль", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { EmojiText(Emoji.BACK, fontSize = 24) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = VasilisaBlue)
                        Spacer(Modifier.height(16.dp))
                        Text(text = "Загружаем профиль...", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProfileHeader(userStats = userStats)
                    StatsCard(userStats = userStats)
                    DetailedStatsCard(userStats = userStats)
                    LevelCard(userStats = userStats)
                    Spacer(Modifier.height(32.dp))
                }
            }

            errorMessage?.let { message ->
                Snackbar(modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter)) {
                    Text(text = message)
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(userStats: UserStats) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        VasilisaImage(modifier = Modifier.size(120.dp), isHappy = userStats.currentStreak > 0)
        Spacer(Modifier.height(12.dp))
        Text(text = userStats.getLevelTitle(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = "Уровень ${userStats.level}", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
        if (userStats.currentStreak > 0) {
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                EmojiText(Emoji.STREAK, fontSize = 20)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Серия: ${userStats.currentStreak} ${getStreakDaysWord(userStats.currentStreak)}",
                    color = KnopaOrange,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (userStats.longestStreak > 0) {
            Text(
                text = "Рекорд: ${userStats.longestStreak} ${getStreakDaysWord(userStats.longestStreak)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun StatsCard(userStats: UserStats) {
    Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Основные показатели", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ProfileStatItem(emoji = Emoji.XP, value = userStats.totalXp.toString(), label = "XP", color = XpGold)
                ProfileStatItem(emoji = Emoji.GEM, value = userStats.gemsBalance.toString(), label = "Самоцветов", color = GemCrystal)
                ProfileStatItem(emoji = Emoji.HEART, value = "${userStats.livesCount}/${userStats.maxLives}", label = "Жизней", color = ErrorRed)
                ProfileStatItem(emoji = Emoji.STREAK, value = userStats.longestStreak.toString(), label = "Рекорд", color = KnopaOrange)
            }
        }
    }
}

@Composable
private fun DetailedStatsCard(userStats: UserStats) {
    Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Детальная статистика", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            DetailedStatRow(label = "Завершено уроков", value = userStats.totalLessonsCompleted.toString())
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            DetailedStatRow(label = "Идеальных уроков", value = userStats.totalPerfectLessons.toString())
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            DetailedStatRow(label = "Всего ошибок", value = userStats.totalMistakesCount.toString())
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            DetailedStatRow(label = "Точность", value = "${userStats.accuracy.toInt()}%",
                valueColor = when {
                    userStats.accuracy >= 90f -> SuccessGreen
                    userStats.accuracy >= 70f -> XpGold
                    else -> ErrorRed
                })
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            DetailedStatRow(label = "Время обучения", value = userStats.getFormattedTotalTime())
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            DetailedStatRow(label = "Дней активности", value = userStats.totalDaysActive.toString())
        }
    }
}

@Composable
private fun LevelCard(userStats: UserStats) {
    Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Прогресс уровня", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Уровень ${userStats.level}", fontWeight = FontWeight.SemiBold)
                Text(text = "Уровень ${userStats.level + 1}", color = Color.Gray)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = userStats.getLevelProgressPercent() / 100f,
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                color = XpGold,
                trackColor = XpGold.copy(alpha = 0.2f)
            )
            Spacer(Modifier.height(8.dp))
            Text(text = "Осталось ${userStats.xpToNextLevel} XP до следующего уровня", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(text = "Всего: ${userStats.totalXp} XP", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
private fun ProfileStatItem(emoji: String, value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        EmojiText(emoji, fontSize = 32)
        Spacer(Modifier.height(4.dp))
        Text(text = value, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
private fun DetailedStatRow(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold,
            color = if (valueColor != Color.Unspecified) valueColor else Color.Black)
    }
}

private fun getStreakDaysWord(days: Int): String {
    val lastDigit = days % 10
    val lastTwoDigits = days % 100
    return when {
        lastTwoDigits in 11..14 -> "дней"
        lastDigit == 1 -> "день"
        lastDigit in 2..4 -> "дня"
        else -> "дней"
    }
}
