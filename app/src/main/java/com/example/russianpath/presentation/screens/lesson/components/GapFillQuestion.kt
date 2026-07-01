package com.example.russianpath.presentation.screens.lesson.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.russianpath.presentation.theme.VasilisaBlue

@Composable
fun GapFillQuestion(
    correctAnswer: String,
    isAnswered: Boolean,
    onSubmit: (String) -> Unit
) {
    var userInput by remember { mutableStateOf("") }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = userInput,
            onValueChange = { 
                if (!isAnswered) userInput = it 
            },
            modifier = Modifier.width(200.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (userInput.isNotEmpty()) {
                        onSubmit(userInput)
                    }
                }
            ),
            enabled = !isAnswered
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Button(
            onClick = { 
                if (userInput.isNotEmpty()) {
                    onSubmit(userInput)
                }
            },
            enabled = userInput.isNotEmpty() && !isAnswered,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VasilisaBlue)
        ) {
            Text("Проверить ✓")
        }
    }
}
