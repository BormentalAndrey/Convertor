package com.example.russianpath.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*

@Composable
fun MascotAnimation(
    modifier: Modifier = Modifier,
    isHappy: Boolean = false
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(
            if (isHappy) "animations/knopa_jump.json"
            else "animations/knopa_idle.json"
        )
    )
    
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier.size(80.dp)
        )
    }
}
