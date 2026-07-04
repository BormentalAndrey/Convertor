// app/src/main/java/com/example/russianpath/presentation/screens/dashboard/DashboardScreen.kt

package com.example.russianpath.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.russianpath.domain.model.Topic
import com.example.russianpath.presentation.components.Emoji
import com.example.russianpath.presentation.components.EmojiText
import com.example.russianpath.presentation.components.KnopaImage
import com.example.russianpath.presentation.components.KnopaMood
import com.example.russianpath.presentation.components.VasilisaImage
import com.example.russianpath.presentation.theme.ErrorRed
import com.example.russianpath.presentation.theme.GemCrystal
import com.example.russianpath.presentation.theme.KnopaOrange
import com.example.russianpath.presentation.theme.SuccessGreen
import com.example.russianpath.presentation.theme.VasilisaBlue
import com.example.russianpath.presentation.theme.XpGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLessonClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    val mascotMessage by viewModel.mascotMessage.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isEmpty by viewModel.isEmpty.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val currentGradeId by viewModel.currentGradeId.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DashboardTopBar(
                userStats = userStats,
                onProfileClick = onProfileClick
            )
        },
        containerColor = Color.White
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (isLoading) {
                LoadingContent()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    VasilisaBlue.copy(alpha = 0.05f),
                                    Color.White,
                                    Color.White
                                )
                            )
                        ),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { MascotCard(userStats = userStats, mascotMessage = mascotMessage) }
                    item { LevelProgressCard(userStats = userStats) }
                    item {
                        GradeSelector(
                            currentGradeId = currentGradeId,
                            onGradeSelected = { viewModel.switchGrade(it) }
                        )
                    }
                    item {
                        Text(
                            text = "Твой путь знаний",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isEmpty) {
                        item { EmptyTopicsPlaceholder() }
                    } else {
                        items(items = topics, key = { it.id }) { topic ->
                            TopicCard(
                                topic = topic,
                                onClick = {
                                    if (viewModel.onTopicClick(topic)) {
                                        onLessonClick(topic.id)
                                    }
                                }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }

            errorMessage?.let { message ->
                Snackbar(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Text(message)
                }
            }
        }
    }
}

// ========================================================================
// Top Bar
// ========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(
    userStats: com.example.russianpath.domain.model.UserStats,
    onProfileClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                EmojiText(Emoji.BOOK, fontSize = 28)
                Spacer(Modifier.width(8.dp))
                Text(text = "Русский Путь", fontWeight = FontWeight.Bold)
            }
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                StatusChip(emoji = Emoji.XP, value = userStats.totalXp.toString(), color = XpGold)
                Spacer(Modifier.width(4.dp))
                StatusChip(emoji = Emoji.GEM, value = userStats.gemsBalance.toString(), color = GemCrystal)
                Spacer(Modifier.width(4.dp))
                StatusChip(emoji = Emoji.HEART, value = userStats.livesCount.toString(), color = ErrorRed)
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = onProfileClick) {
                    EmojiText(Emoji.PROFILE, fontSize = 28)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = VasilisaBlue,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

// ========================================================================
// Mascot Card
// ========================================================================

@Composable
private fun MascotCard(
    userStats: com.example.russianpath.domain.model.UserStats,
    mascotMessage: String
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                VasilisaImage(modifier = Modifier.size(100.dp), isHappy = userStats.currentStreak > 0)
                Spacer(Modifier.width(8.dp))
                KnopaImage(
                    modifier = Modifier.size(70.dp),
                    mood = when {
                        userStats.currentStreak >= 7 -> KnopaMood.EXCITED
                        userStats.currentStreak > 0 -> KnopaMood.HAPPY
                        userStats.livesCount <= 2 -> KnopaMood.IDLE
                        else -> KnopaMood.HAPPY
                    }
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(text = mascotMessage, style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
            if (userStats.currentStreak > 0) {
                Spacer(Modifier.height(8.dp))
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
        }
    }
}

// ========================================================================
// Level Progress Card
// ========================================================================

@Composable
private fun LevelProgressCard(userStats: com.example.russianpath.domain.model.UserStats) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Уровень ${userStats.level}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = userStats.getLevelTitle(), style = MaterialTheme.typography.bodyMedium, color = XpGold, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = userStats.getLevelProgressPercent() / 100f,
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = XpGold,
                trackColor = XpGold.copy(alpha = 0.2f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${userStats.totalXp} / ${userStats.totalXp + userStats.xpToNextLevel} XP",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

// ========================================================================
// Grade Selector
// ========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GradeSelector(
    currentGradeId: String,
    onGradeSelected: (String) -> Unit
) {
    val grades = listOf(
        "5" to "5 класс", "6" to "6 класс", "7" to "7 класс",
        "8" to "8 класс", "9" to "9 класс", "10" to "10 класс",
        "11" to "11 класс", "oge" to "ОГЭ", "ege" to "ЕГЭ"
    )

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(grades) { (id, label) ->
            FilterChip(
                selected = id == currentGradeId,
                onClick = { onGradeSelected(id) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = VasilisaBlue,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

// ========================================================================
// Topic Card
// ========================================================================

@Composable
private fun TopicCard(topic: Topic, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (topic.isUnlocked) Color.White else Color.LightGray.copy(alpha = 0.3f)
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(
                    if (topic.isUnlocked) VasilisaBlue.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f)
                ),
                contentAlignment = Alignment.Center
            ) {
                EmojiText(emoji = if (topic.isUnlocked) Emoji.BOOK else Emoji.LOCK, fontSize = 24)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = topic.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (topic.description.isNotBlank()) {
                    Text(text = topic.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "⏱ ${topic.estimatedMinutes} мин", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(text = "📊 ${topic.getDifficultyLabel()}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    if (topic.completedLessons > 0) {
                        Text(text = "✅ ${topic.completedLessons}/${topic.totalLessons}", style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            if (topic.isUnlocked) {
                EmojiText(Emoji.FORWARD, fontSize = 24)
            }
        }
        if (topic.isUnlocked && topic.totalLessons > 0) {
            LinearProgressIndicator(
                progress = topic.calculateCompletionPercentage() / 100f,
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = SuccessGreen,
                trackColor = SuccessGreen.copy(alpha = 0.1f)
            )
        }
    }
}

// ========================================================================
// Status Chip
// ========================================================================

@Composable
fun StatusChip(emoji: String, value: String, color: Color) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            EmojiText(emoji, fontSize = 16)
            Spacer(Modifier.width(4.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
        }
    }
}

// ========================================================================
// Loading & Empty States
// ========================================================================

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = VasilisaBlue)
            Spacer(Modifier.height(16.dp))
            Text(text = "Загружаем приключение...", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        }
    }
}

@Composable
private fun EmptyTopicsPlaceholder() {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            EmojiText(Emoji.BOOK, fontSize = 48)
            Spacer(Modifier.height(16.dp))
            Text(text = "Темы пока отсутствуют", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Контент для этого класса находится в разработке. Попробуй выбрать другой класс!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

// ========================================================================
// Utilities
// ========================================================================

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
