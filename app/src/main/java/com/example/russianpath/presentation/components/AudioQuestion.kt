package com.example.russianpath.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.russianpath.presentation.theme.VasilisaBlue

@Composable
fun AudioQuestion(
    questionText: String,
    options: List<String>,
    onPlayAudio: () -> Unit,
    onAnswerSelected: (String) -> Unit
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = questionText,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Большая кнопка воспроизведения звука
        Button(
            onClick = { onPlayAudio() },
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = VasilisaBlue),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Text(
                text = "🔊", 
                fontSize = 48.sp // Крупная иконка для привлечения внимания
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Нажми, чтобы послушать",
            color = Color.Gray,
            fontSize = 16.sp
        )
        
        Spacer(modifier = Modifier.height(48.dp))

        // Варианты ответов
        options.forEach { option ->
            val isSelected = option == selectedOption
            
            Button(
                onClick = {
                    selectedOption = option
                    onAnswerSelected(option)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .height(64.dp), // Увеличенная высота для удобного нажатия
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) VasilisaBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                    contentColor = if (isSelected) VasilisaBlue else MaterialTheme.colorScheme.onSurface
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 2.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (isSelected) VasilisaBlue else Color.LightGray
                    )
                )
            ) {
                Text(
                    text = option,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
