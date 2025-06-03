package com.example.playgroundestudos.ui.components.paginationDots.dot

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BirdDot(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    selectedWidth: Dp = 20.dp,
    idleWidth: Dp = 6.dp
) {
    val selectedColor = Color.Blue
    val unselectedColor = Color.LightGray
    val animationDuration = 1000

    // Animação para a cor do ponto
    val color by animateColorAsState(
        targetValue = if (isSelected) selectedColor else unselectedColor,
        animationSpec = tween(durationMillis = animationDuration),
        label = "dot_color_animation"
    )

    // Animação para o tamanho do ponto
    val animatedWidth by animateDpAsState(
        targetValue = if (isSelected) selectedWidth else idleWidth,
        animationSpec = tween(durationMillis = animationDuration),
        label = "dot_size_animation"
    )

    Box(
        modifier = modifier
            .width(animatedWidth)
            .height(idleWidth)
            .background(color = color, shape = CircleShape)
    )
}
