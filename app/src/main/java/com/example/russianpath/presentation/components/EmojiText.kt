package com.example.russianpath.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Набор эмодзи-констант, используемых в приложении «Русский Путь».
 *
 * Все эмодзи вынесены в единый объект для:
 * - Единообразия (один и тот же эмодзи на всех экранах)
 * - Лёгкой замены (при смене дизайна достаточно изменить константу)
 * - Отсутствия опечаток в строковых литералах
 * - Корректного отображения на всех версиях Android (Unicode-последовательности)
 *
 * Все константы используют полные Unicode-последовательности с вариативными селекторами
 * (U+FE0F) для гарантированного отображения в цвете, а не как чёрно-белые символы.
 */
object Emoji {
    // ========================================================================
    // Статистика и ресурсы
    // ========================================================================

    /** Опыт (XP) — молния. */
    const val XP = "\u26A1"          // ⚡

    /** Самоцветы — бриллиант. */
    const val GEM = "\uD83D\uDC8E"   // 💎

    /** Жизнь (полная) — красное сердце. */
    const val HEART = "\u2764\uFE0F" // ❤️

    /** Жизнь (пустая) — белое сердце. */
    const val HEART_EMPTY = "\uD83E\uDD0D" // 🤍

    /** Стрик (серия дней) — огонь. */
    const val STREAK = "\uD83D\uDD25" // 🔥

    // ========================================================================
    // Звёзды и достижения
    // ========================================================================

    /** Золотая звезда (заработанная). */
    const val STAR_GOLD = "\u2B50"    // ⭐

    /** Пустая звезда (незаработанная). */
    const val STAR_EMPTY = "\u2606"   // ☆

    /** Трофей — для особых достижений. */
    const val TROPHY = "\uD83C\uDFC6"  // 🏆

    /** Идеальный результат — 100 очков. */
    const val PERFECT = "\uD83D\uDCAF"  // 💯

    // ========================================================================
    // Навигация
    // ========================================================================

    /** Назад — стрелка влево. */
    const val BACK = "\u2B05\uFE0F"   // ⬅️

    /** Вперёд — стрелка вправо. */
    const val FORWARD = "\u27A1\uFE0F" // ➡️

    /** Профиль — силуэт человека. */
    const val PROFILE = "\uD83D\uDC64" // 👤

    /** Повторить — круговая стрелка. */
    const val REPEAT = "\uD83D\uDD04"  // 🔄

    // ========================================================================
    // Образовательные
    // ========================================================================

    /** Книга — для уроков и тем. */
    const val BOOK = "\uD83D\uDCDA"    // 📚

    /** Замок (закрытый) — для заблокированных тем. */
    const val LOCK = "\uD83D\uDD12"   // 🔒

    /** Замок (открытый) — для разблокированных тем. */
    const val UNLOCK = "\uD83D\uDD13" // 🔓

    /** Галочка — правильный ответ. */
    const val CHECK = "\u2705"        // ✅

    /** Крестик — неправильный ответ. */
    const val CROSS = "\u274C"        // ❌

    /** Лампочка — подсказка. */
    const val HINT = "\uD83D\uDCA1"   // 💡

    /** График — для статистики и точности. */
    const val CHART = "\uD83D\uDCCA"   // 📊

    /** Часы — для времени прохождения. */
    const val CLOCK = "\u23F1\uFE0F"   // ⏱️

    // ========================================================================
    // Эмоции и реакции
    // ========================================================================

    /** Улыбка — для хорошего настроения. */
    const val HAPPY = "\uD83D\uDE0A"   // 😊

    /** Праздник — для отличного результата. */
    const val EXCITED = "\uD83C\uDF89" // 🎉

    /** Конфетти — для анимации празднования. */
    const val CONFETTI = "\uD83C\uDF8A" // 🎊

    /** Палец вверх — для одобрения. */
    const val GOOD = "\uD83D\uDC4D"     // 👍

    /** Сжатый кулак — для мотивации. */
    const val TRY_AGAIN = "\uD83D\uDCAA" // 💪
}

// ========================================================================
// Базовый EmojiText
// ========================================================================

/**
 * Composable для отображения эмодзи-текста.
 *
 * Простой враппер над Material3 Text с предустановленным стилем для эмодзи.
 * Используется во всех экранах для отображения иконок-эмодзи.
 *
 * Поддерживает:
 * - Обычное отображение
 * - Анимацию пульсации (через параметр pulse)
 * - Анимацию подпрыгивания (через параметр bounce)
 *
 * @param emoji Строка с эмодзи из объекта Emoji.
 * @param fontSize Размер шрифта в sp (по умолчанию 24).
 * @param modifier Модификатор для настройки отображения.
 * @param pulse Если true — применяется анимация пульсации (масштаб 0.8 → 1.2 → 1.0).
 * @param bounce Если true — применяется анимация подпрыгивания (смещение по Y).
 */
@Composable
fun EmojiText(
    emoji: String,
    fontSize: Int = 24,
    modifier: Modifier = Modifier,
    pulse: Boolean = false,
    bounce: Boolean = false
) {
    // Анимация пульсации (масштаб)
    val scale by if (pulse) {
        animateFloatAsState(
            targetValue = 1f,
            animationSpec = tween(600),
            label = "emojiPulse"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(1f) }
    }

    // Анимация подпрыгивания (смещение по вертикали)
    val offsetY by if (bounce) {
        animateFloatAsState(
            targetValue = 0f,
            animationSpec = tween(400),
            label = "emojiBounce"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    Text(
        text = emoji,
        fontSize = fontSize.sp,
        modifier = modifier
            .scale(scale)
            .offset(y = offsetY.dp)
    )
}

// ========================================================================
// Специализированные EmojiText
// ========================================================================

/**
 * Эмодзи с анимацией пульсации.
 * Используется для акцентирования внимания (например, XP при получении).
 *
 * @param emoji Строка с эмодзи.
 * @param fontSize Размер шрифта в sp.
 * @param modifier Модификатор.
 */
@Composable
fun PulseEmojiText(
    emoji: String,
    fontSize: Int = 24,
    modifier: Modifier = Modifier
) {
    EmojiText(
        emoji = emoji,
        fontSize = fontSize,
        modifier = modifier,
        pulse = true
    )
}

/**
 * Эмодзи с анимацией подпрыгивания.
 * Используется для радостных моментов (например, конфетти при идеальном результате).
 *
 * @param emoji Строка с эмодзи.
 * @param fontSize Размер шрифта в sp.
 * @param modifier Модификатор.
 */
@Composable
fun BounceEmojiText(
    emoji: String,
    fontSize: Int = 24,
    modifier: Modifier = Modifier
) {
    EmojiText(
        emoji = emoji,
        fontSize = fontSize,
        modifier = modifier,
        bounce = true
    )
}
