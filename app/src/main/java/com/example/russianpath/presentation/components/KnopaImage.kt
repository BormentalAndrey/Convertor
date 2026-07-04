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
 * Composable для отображения маскота Кнопы.
 *
 * Кнопа — маленький анимированный помощник, который:
 * - Приветствует пользователя на главном экране
 * - Реагирует на результаты урока
 * - Даёт подсказки и мотивационные сообщения
 *
 * Изображения находятся в res/drawable/:
 * - ic_knopa_idle.png — нейтральное выражение
 * - ic_knopa_happy.png — радостное выражение
 * - ic_knopa_excited.png — восторженное выражение
 * - ic_knopa_sad.png — грустное выражение
 * - ic_knopa_jump.png — подпрыгивание (особая радость)
 *
 * @param modifier Модификатор для настройки размера и позиции.
 * @param mood Настроение маскота. По умолчанию IDLE.
 * @param sizeDp Размер изображения в dp. По умолчанию 80.
 */
@Composable
fun KnopaImage(
    modifier: Modifier = Modifier,
    mood: KnopaMood = KnopaMood.IDLE,
    sizeDp: Int = 80
) {
    val resourceId = when (mood) {
        KnopaMood.IDLE -> R.drawable.ic_knopa_idle
        KnopaMood.HAPPY -> R.drawable.ic_knopa_happy
        KnopaMood.EXCITED -> R.drawable.ic_knopa_excited
        KnopaMood.SAD -> R.drawable.ic_knopa_sad
        KnopaMood.SURPRISED -> R.drawable.ic_knopa_jump
    }

    Image(
        painter = painterResource(id = resourceId),
        contentDescription = "Кнопа — ${getMoodDescription(mood)}",
        modifier = modifier.size(sizeDp.dp),
        contentScale = ContentScale.Fit
    )
}

/**
 * Возвращает текстовое описание настроения для accessibility (contentDescription).
 */
private fun getMoodDescription(mood: KnopaMood): String {
    return when (mood) {
        KnopaMood.IDLE -> "спокойный"
        KnopaMood.HAPPY -> "радостный"
        KnopaMood.EXCITED -> "восторженный"
        KnopaMood.SAD -> "грустный"
        KnopaMood.SURPRISED -> "удивлённый"
    }
}
