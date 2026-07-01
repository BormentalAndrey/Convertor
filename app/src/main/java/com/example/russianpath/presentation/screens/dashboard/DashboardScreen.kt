package com.example.russianpath.presentation.screens.dashboard

import androidx.compose.animation.core.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.*
import com.example.russianpath.R
import com.example.russianpath.presentation.components.MascotAnimation
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
                    // Статистика пользователя
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        // XP
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = XpGold.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⭐", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "${userStats.totalXp}",
                                    fontWeight = FontWeight.Bold,
                                    color = XpGold
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Кристаллы
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = GemCrystal.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💎", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "${userStats.gemsBalance}",
                                    fontWeight = FontWeight.Bold,
                                    color = GemCrystal
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Жизни
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("❤️", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "${userStats.livesCount}",
                                    fontWeight = FontWeight.Bold,
                                    color = ErrorRed
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Профиль
                        IconButton(onClick = onProfileClick) {
                            Text("👤", fontSize = 24.sp)
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
            // Блок с маскотом и стриком
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
                    // Маскот Кнопа (котёнок-помощник)
                    MascotAnimation(
                        modifier = Modifier.size(80.dp),
                        isHappy = userStats.currentStreak > 0
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
                            Text("🔥", fontSize = 20.sp)
                            Text(
                                "Серия: ${userStats.currentStreak} дней",
                                style = MaterialTheme.typography.bodyMedium,
                                color = KnopaOrange,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            // Заголовок раздела
            Text(
                "Твой путь знаний",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // Список тем (Древо навыков)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(topics) { topic ->
                    TopicCard(
                        topic = topic,
                        onClick = {
                            // Переход к первому уроку темы
                            onLessonClick("lesson_${topic.id}_1")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TopicCard(
    topic: com.example.russianpath.domain.model.Topic,
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
        onClick = {
            if (topic.isUnlocked) {
                onClick()
            }
        }
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
                Text(
                    if (topic.isUnlocked) "📚" else "🔒",
                    fontSize = 28.sp
                )
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
                
                if (topic.isUnlocked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    // Прогресс-бар
                    LinearProgressIndicator(
                        progress = topic.completionPercentage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = SuccessGreen,
                        trackColor = Color.Gray.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "⭐ ${topic.stars}/3",
                        style = MaterialTheme.typography.bodySmall,
                        color = XpGold
                    )
                }
            }
        }
    }
}
