package com.example.playgroundestudos.ui.components.paginationDots.dot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BirdDot(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    isCurrentlyVisible: Boolean,
    isForwardNavigation: Boolean,
    selectedWidth: Dp = 20.dp,
    idleWidth: Dp = 6.dp,
    animationDurationMillis: Int = 500
) {
    val selectedColor = Color.Blue
    val unselectedColor = Color.LightGray

    // Animação para a cor do ponto
    val color by animateColorAsState(
        targetValue = if (isSelected) selectedColor else unselectedColor,
        animationSpec = tween(durationMillis = animationDurationMillis),
        label = "dot_color_animation"
    )

    // Animação para o tamanho do ponto (largura)
    val animatedWidth by animateDpAsState(
        targetValue = if (isSelected) selectedWidth else idleWidth,
        animationSpec = tween(durationMillis = animationDurationMillis),
        label = "dot_size_animation"
    )

    // Define as transições de entrada e saída com base na direção da navegação
    val enterTransition = fadeIn(animationSpec = tween(animationDurationMillis)) +
            scaleIn(
                animationSpec = tween(animationDurationMillis),
                transformOrigin = TransformOrigin.Center // Efeito de escala a partir do centro
            ) +
            slideInHorizontally(animationSpec = tween(animationDurationMillis)) { fullWidth ->
                // Se for navegação para "próximo", entra da direita.
                // Se for navegação para "anterior", entra da esquerda.
                if (isForwardNavigation) fullWidth else -fullWidth
            }

    val exitTransition = fadeOut(animationSpec = tween(animationDurationMillis)) +
            scaleOut(
                animationSpec = tween(animationDurationMillis),
                transformOrigin = TransformOrigin.Center // Efeito de escala para o centro
            ) +
            slideOutHorizontally(animationSpec = tween(animationDurationMillis)) { fullWidth ->
                // Se for navegação para "próximo", sai para a esquerda.
                // Se for navegação para "anterior", sai para a direita.
                if (isForwardNavigation) -fullWidth else fullWidth
            }

    AnimatedVisibility(
        visible = isCurrentlyVisible,
        enter = enterTransition,
        exit = exitTransition,
        label = "animated_dot_visibility"
    ) {
        Box(
            modifier = modifier
                .width(animatedWidth) // Largura animada baseada na seleção
                .height(idleWidth)    // Altura geralmente é a mesma do idleWidth para um círculo
                .background(color = color, shape = CircleShape)
        )
    }
}
