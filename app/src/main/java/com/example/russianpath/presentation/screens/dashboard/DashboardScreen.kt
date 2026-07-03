package com.example.russianpath.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.russianpath.presentation.components.*
import com.example.russianpath.presentation.theme.*

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EmojiText(Emoji.BOOK, fontSize = 28)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Русский Путь",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {

                        StatusChip(
                            Emoji.XP,
                            userStats.totalXp.toString(),
                            XpGold
                        )

                        Spacer(Modifier.width(6.dp))

                        StatusChip(
                            Emoji.GEM,
                            userStats.gemsBalance.toString(),
                            GemCrystal
                        )

                        Spacer(Modifier.width(6.dp))

                        StatusChip(
                            Emoji.HEART,
                            userStats.livesCount.toString(),
                            ErrorRed
                        )

                        Spacer(Modifier.width(6.dp))

                        IconButton(onClick = onProfileClick) {
                            EmojiText(
                                Emoji.PROFILE,
                                fontSize = 28
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VasilisaBlue,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            VasilisaBlue.copy(alpha = 0.08f),
                            Color.White
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Card(
                shape = RoundedCornerShape(24.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {

                        VasilisaImage(
                            modifier = Modifier.size(120.dp),
                            isHappy = userStats.currentStreak > 0
                        )

                        Spacer(Modifier.width(8.dp))

                        KnopaImage(
                            modifier = Modifier.size(80.dp),
                            mood =
                                if (userStats.currentStreak > 0)
                                    KnopaMood.HAPPY
                                else
                                    KnopaMood.IDLE
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        mascotMessage,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        EmojiText(
                            Emoji.STREAK,
                            fontSize = 20
                        )

                        Spacer(Modifier.width(4.dp))

                        Text(
                            "Серия: ${userStats.currentStreak} дней",
                            color = KnopaOrange,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                "Твой путь знаний",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (topics.isEmpty()) {

                Text(
                    "Темы пока отсутствуют"
                )

            } else {

                topics.forEach { topic ->

                    Card(
                        onClick = {
                            onLessonClick(topic.id)
                        },
                        shape = RoundedCornerShape(20.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                "📘",
                                fontSize = 36.sp
                            )

                            Spacer(Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    topic.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    topic.description,
                                    color = Color.Gray
                                )
                            }

                            EmojiText(
                                Emoji.FORWARD,
                                fontSize = 24
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(
    emoji: String,
    value: String,
    color: Color
) {

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.2f)
        )
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 4.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            EmojiText(
                emoji,
                fontSize = 18
            )

            Spacer(Modifier.width(4.dp))

            Text(
                value,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
