package com.example.russianpath.presentation.screens.lesson.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.russianpath.presentation.theme.ErrorRed
import com.example.russianpath.presentation.theme.SuccessGreen
import com.example.russianpath.presentation.theme.VasilisaBlue

@Composable
fun SingleChoiceQuestion(
    options: List<String>,
    correctAnswer: String,
    isAnswered: Boolean,
    onSelect: (String) -> Unit
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEach { option ->
            val isSelected = selectedOption == option
            val isCorrectOption = option == correctAnswer
            val buttonColor = when {
                !isAnswered && isSelected -> VasilisaBlue
                isAnswered && isCorrectOption -> SuccessGreen
                isAnswered && isSelected && !isCorrectOption -> ErrorRed
                else -> MaterialTheme.colorScheme.surface
            }
            
            Button(
                onClick = {
                    if (!isAnswered) {
                        selectedOption = option
                        onSelect(option)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = if (isAnswered && isCorrectOption || isSelected && !isAnswered) 
                        MaterialTheme.colorScheme.onPrimary 
                    else 
                        MaterialTheme.colorScheme.onSurface
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = if (isSelected) 8.dp else 2.dp
                )
            ) {
                Text(
                    text = option,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
