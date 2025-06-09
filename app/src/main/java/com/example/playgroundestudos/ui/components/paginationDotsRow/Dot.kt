package com.example.playgroundestudos.ui.components.paginationDotsRow

import androidx.compose.animation.AnimatedVisibility
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
fun Dot(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    selectedWidth: Dp = 20.dp,
    idleWidth: Dp = 6.dp,
    animationDurationMillis: Int = 1000,
    dopPadding: Dp = 4.dp
) {
    val selectedColor = Color.Blue
    val unselectedColor = Color.LightGray

    val color by animateColorAsState(
        targetValue = if (isSelected) selectedColor else unselectedColor,
        animationSpec = tween(durationMillis = animationDurationMillis),
        label = "dot_color_animation"
    )

    val animatedWidth by animateDpAsState(
        targetValue = if (isSelected) selectedWidth else idleWidth,
        animationSpec = tween(durationMillis = animationDurationMillis),
        label = "dot_size_animation"
    )

//    val enterTransition = remember(isCurrentlyVisible, isSelected) {
//        slideInHorizontally(
//            initialOffsetX = { fullWidth -> (fullWidth + dopPadding.value.toInt()) * 3 },
//            animationSpec = tween(durationMillis = animationDurationMillis)
//        )
//    }
//
//    val exitTransition = remember(isCurrentlyVisible, isSelected) {
//        slideOutHorizontally(
//            targetOffsetX = { fullWidth -> (-fullWidth - (dopPadding.value.toInt() * 2)) * 3 },
//            animationSpec = tween(durationMillis = animationDurationMillis)
//        )
//    }

    AnimatedVisibility(
        visible = true,
//        exit = true,
        label = "animated_dot_visibility"
    ) {
        Box(
            modifier = modifier
                .width(animatedWidth)
                .height(idleWidth)
                .background(color = color, shape = CircleShape)
        )
    }

}