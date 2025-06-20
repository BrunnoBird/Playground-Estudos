package com.example.playgroundestudos.ui.components.paginationDots.internal.dot

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

data class DotAnimation(
    val sizeAnim: AnimationSpec<Size>,
    val offsetAnim: AnimationSpec<Offset>,
    val colorAnim: AnimationSpec<Color>
) {
    companion object {
        val defaultDotAnimation = DotAnimation(
            offsetAnim = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            sizeAnim = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            colorAnim = tween(durationMillis = 1000)
        )
    }
}
