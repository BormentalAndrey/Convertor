package com.example.russianpath.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.russianpath.R

/**
 * Composable для отображения персонажа Василисы.
 *
 * Василиса — главный персонаж-наставник, который сопровождает пользователя
 * в приложении «Русский Путь».
 *
 * Изображения находятся в res/drawable/:
 * - ic_vasilisa.png — основное изображение (нейтральное/базовое)
 * - ic_vasilisa_happy.png — радостное выражение
 * - ic_vasilisa_idle.png — спокойное выражение
 *
 * @param modifier Модификатор для настройки размера и позиции.
 * @param isHappy true — радостное выражение, false — спокойное.
 * @param sizeDp Размер изображения в dp. По умолчанию 120.
 */
@Composable
fun VasilisaImage(
    modifier: Modifier = Modifier,
    isHappy: Boolean = true,
    sizeDp: Int = 120
) {
    val resourceId = when {
        isHappy -> R.drawable.ic_vasilisa_happy
        else -> R.drawable.ic_vasilisa_idle
    }

    Image(
        painter = painterResource(id = resourceId),
        contentDescription = "Василиса — ${if (isHappy) "радостная" else "спокойная"}",
        modifier = modifier.size(sizeDp.dp),
        contentScale = ContentScale.Fit
    )
}
