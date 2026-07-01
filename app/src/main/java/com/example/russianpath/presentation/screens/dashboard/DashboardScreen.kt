package com.example.russianpath.presentation.screens.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.russianpath.R
import com.example.russianpath.domain.model.Topic
import com.example.russianpath.presentation.components.MascotAnimationStatic
import com.example.russianpath.presentation.components.MascotMood
import com.example.russianpath.presentation.components.MascotType
import com.example.russianpath.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLessonClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val userStats by viewModel.userStats.collectAsState()
    val topics by viewModel.topics.collectAsState()
    val mascotMessage by viewModel.mascotMessage.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Русский Путь",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        // XP с иконкой
                        StatusChip(
                            iconRes = R.drawable.ic_xp,
                            value = "${userStats.totalXp}",
                            color = XpGold
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Кристаллы с иконкой
                        StatusChip(
                            iconRes = R.drawable.ic_diamond,
                            value = "${userStats.gemsBalance}",
                            color = GemCrystal
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Жизни
                        StatusChip(
                            iconRes = R.drawable.ic_heart,
                            value = "${userStats.livesCount}",
                            color = ErrorRed
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Профиль
                        IconButton(onClick = onProfileClick) {
                            Image(
                                painter = painterResource(R.drawable.ic_profile),
                                contentDescription = "Профиль",
                                modifier = Modifier.size(32.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
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
                        colors = listOf(
                            VasilisaBlue.copy(alpha = 0.1f),
                            Color.White
                        )
                    )
                )
        ) {
            // Блок с маскотом и информацией
            MascotCard(
                mascotMessage = mascotMessage,
                streak = userStats.currentStreak
            )
            
            // Заголовок раздела
            Text(
                "Твой путь знаний",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // Список тем
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(topics) { topic ->
                    TopicCard(
                        topic = topic,
                        onClick = {
                            if (topic.isUnlocked) {
                                onLessonClick("lesson_${topic.id}_1")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(
    iconRes: Int,
    value: String,
    color: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                value,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun MascotCard(
    mascotMessage: String,
    streak: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Маскот Кнопа
            MascotAnimationStatic(
                modifier = Modifier.size(80.dp),
                mascotType = MascotType.KNOPA,
                mood = if (streak > 0) MascotMood.HAPPY else MascotMood.IDLE
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    mascotMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.ic_streak),
                        contentDescription = "Стрик",
                        modifier = Modifier.size(20.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Серия: $streak дней",
                        style = MaterialTheme.typography.bodyMedium,
                        color = KnopaOrange,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TopicCard(
    topic: Topic,
    onClick: () -> Unit
) {
    val scaleAnim = remember { Animatable(1f) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scaleAnim.value),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (topic.isUnlocked) 6.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (topic.isUnlocked) 
                MaterialTheme.colorScheme.surface 
            else 
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка темы
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        if (topic.isUnlocked) VasilisaBlue.copy(alpha = 0.1f)
                        else Color.Gray.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (topic.isUnlocked) {
                    // Загружаем PNG иконку темы
                    val iconRes = getTopicIcon(topic.iconName)
                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = topic.title,
                        modifier = Modifier.size(36.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = "Заблокировано",
                        modifier = Modifier.size(36.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (topic.isUnlocked) 
                        MaterialTheme.colorScheme.onSurface 
                    else 
                        Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    topic.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2
                )
                
                if (topic.isUnlocked && topic.completionPercentage > 0f) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = topic.completionPercentage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = SuccessGreen,
                        trackColor = Color.Gray.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(topic.stars) {
                            Image(
                                painter = painterResource(R.drawable.ic_star_gold),
                                contentDescription = "Звезда",
                                modifier = Modifier.size(16.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        repeat(3 - topic.stars) {
                            Image(
                                painter = painterResource(R.drawable.ic_star_empty),
                                contentDescription = "Пустая звезда",
                                modifier = Modifier.size(16.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }
        }
    }
}

// Вспомогательная функция для получения иконки темы
fun getTopicIcon(iconName: String): Int {
    return when (iconName) {
        "phonetics" -> R.drawable.ic_sound
        "spelling" -> R.drawable.ic_check
        "grammar" -> R.drawable.ic_profile
        "syntax" -> R.drawable.ic_hint
        else -> R.drawable.ic_profile
    }
}
