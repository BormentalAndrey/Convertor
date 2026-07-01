package com.example.russianpath.presentation.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = VasilisaBlue,
    onPrimary = Color.White,
    primaryContainer = VasilisaLightBlue,
    secondary = KnopaOrange,
    onSecondary = Color.White,
    tertiary = GemCrystal,
    background = BackgroundGray,
    surface = Color.White,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun RussianPathTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
