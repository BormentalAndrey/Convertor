// app/src/main/java/com/example/russianpath/presentation/screens/lesson/TopicLessonScreen.kt

package com.example.russianpath.presentation.screens.lesson

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.russianpath.domain.model.Lesson
import com.example.russianpath.presentation.components.Emoji
import com.example.russianpath.presentation.components.EmojiText
import com.example.russianpath.presentation.theme.GemCrystal
import com.example.russianpath.presentation.theme.SuccessGreen
import com.example.russianpath.presentation.theme.VasilisaBlue
import com.example.russianpath.presentation.theme.XpGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicLessonScreen(
    topicId: String,
    onBackClick: () -> Unit = {},
    onLessonClick: (String) -> Unit = {},
    onRulesClick: (String) -> Unit = {},
    viewModel: TopicLessonViewModel = hiltViewModel()
) {
    val lessons by viewModel.lessons.collectAsStateWithLifecycle()
    val topicTitle by viewModel.topicTitle.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(topicId) {
        viewModel.loadLessons(topicId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topicTitle.ifBlank { "Уроки" },
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        EmojiText(Emoji.BACK, fontSize = 24)
                    }
                },
                actions = {
                    IconButton(onClick = { onRulesClick(topicId) }) {
                        Text("📖", fontSize = 24.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = VasilisaBlue)
                        Spacer(Modifier.height(16.dp))
                        Text("Загружаем уроки...", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "⚠️", fontSize = 48.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(text = errorMessage ?: "", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { viewModel.loadLessons(topicId) }) {
                            Text("Повторить загрузку")
                        }
                    }
                }
            }
            lessons.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📭", fontSize = 48.sp)
                        Spacer(Modifier.height(16.dp))
                        Text("Уроки пока отсутствуют", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        Spacer(Modifier.height(8.dp))
                        Text("ID темы: $topicId", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = lessons, key = { it.id }) { lesson ->
                        LessonCard(lesson = lesson, onClick = { onLessonClick(lesson.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonCard(lesson: Lesson, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (lesson.isCompleted) SuccessGreen.copy(alpha = 0.05f) else Color.White
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(VasilisaBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                EmojiText(
                    emoji = when (lesson.lessonType) {
                        com.example.russianpath.domain.model.LessonType.THEORY -> Emoji.BOOK
                        com.example.russianpath.domain.model.LessonType.TEST -> "📝"
                        com.example.russianpath.domain.model.LessonType.DIAGNOSTIC -> "🔍"
                        com.example.russianpath.domain.model.LessonType.BONUS -> Emoji.GEM
                        else -> "✏️"
                    },
                    fontSize = 22
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (lesson.description.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = lesson.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐ ${lesson.bestStars}/${lesson.maxStars}",
                        style = MaterialTheme.typography.labelSmall,
                        color = XpGold,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "❓ ${lesson.questionsCount} вопросов",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    if (lesson.isCompleted) {
                        Text(
                            text = "✅ Пройден",
                            style = MaterialTheme.typography.labelSmall,
                            color = SuccessGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))
            EmojiText(if (lesson.isCompleted) Emoji.CHECK else Emoji.FORWARD, fontSize = 24)
        }

        if (lesson.bestScorePercent > 0) {
            LinearProgressIndicator(
                progress = lesson.bestScorePercent / 100f,
                modifier = Modifier.fillMaxWidth().height(3.dp),
                color = if (lesson.isCompleted) SuccessGreen else VasilisaBlue,
                trackColor = Color.Gray.copy(alpha = 0.1f)
            )
        }
    }
}
