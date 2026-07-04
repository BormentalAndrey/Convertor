// app/src/main/java/com/example/russianpath/presentation/components/KnopaImage.kt

package com.example.russianpath.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.russianpath.R

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

private fun getMoodDescription(mood: KnopaMood): String {
    return when (mood) {
        KnopaMood.IDLE -> "спокойный"
        KnopaMood.HAPPY -> "радостный"
        KnopaMood.EXCITED -> "восторженный"
        KnopaMood.SAD -> "грустный"
        KnopaMood.SURPRISED -> "удивлённый"
    }
}
