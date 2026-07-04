// app/src/main/java/com/example/russianpath/presentation/components/EmojiText.kt

package com.example.russianpath.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Emoji {
    const val XP = "\u26A1"
    const val GEM = "\uD83D\uDC8E"
    const val HEART = "\u2764\uFE0F"
    const val HEART_EMPTY = "\uD83E\uDD0D"
    const val STREAK = "\uD83D\uDD25"
    const val STAR_GOLD = "\u2B50"
    const val STAR_EMPTY = "\u2606"
    const val TROPHY = "\uD83C\uDFC6"
    const val PERFECT = "\uD83D\uDCAF"
    const val BACK = "\u2B05\uFE0F"
    const val FORWARD = "\u27A1\uFE0F"
    const val PROFILE = "\uD83D\uDC64"
    const val REPEAT = "\uD83D\uDD04"
    const val BOOK = "\uD83D\uDCDA"
    const val LOCK = "\uD83D\uDD12"
    const val UNLOCK = "\uD83D\uDD13"
    const val CHECK = "\u2705"
    const val CROSS = "\u274C"
    const val HINT = "\uD83D\uDCA1"
    const val CHART = "\uD83D\uDCCA"
    const val CLOCK = "\u23F1\uFE0F"
    const val HAPPY = "\uD83D\uDE0A"
    const val EXCITED = "\uD83C\uDF89"
    const val CONFETTI = "\uD83C\uDF8A"
    const val GOOD = "\uD83D\uDC4D"
    const val TRY_AGAIN = "\uD83D\uDCAA"
}

@Composable
fun EmojiText(
    emoji: String,
    fontSize: Int = 24,
    modifier: Modifier = Modifier,
    pulse: Boolean = false,
    bounce: Boolean = false
) {
    val scale by if (pulse) {
        animateFloatAsState(
            targetValue = 1f,
            animationSpec = tween(600),
            label = "emojiPulse"
        )
    } else {
        remember { mutableFloatStateOf(1f) }
    }

    val offsetY by if (bounce) {
        animateFloatAsState(
            targetValue = 0f,
            animationSpec = tween(400),
            label = "emojiBounce"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    Text(
        text = emoji,
        fontSize = fontSize.sp,
        modifier = modifier
            .scale(scale)
            .offset(y = offsetY.dp)
    )
}

@Composable
fun PulseEmojiText(
    emoji: String,
    fontSize: Int = 24,
    modifier: Modifier = Modifier
) {
    EmojiText(emoji = emoji, fontSize = fontSize, modifier = modifier, pulse = true)
}

@Composable
fun BounceEmojiText(
    emoji: String,
    fontSize: Int = 24,
    modifier: Modifier = Modifier
) {
    EmojiText(emoji = emoji, fontSize = fontSize, modifier = modifier, bounce = true)
}
