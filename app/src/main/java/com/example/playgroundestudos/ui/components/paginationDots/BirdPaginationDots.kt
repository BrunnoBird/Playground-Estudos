package com.example.playgroundestudos.ui.components.paginationDots

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.playgroundestudos.ui.components.paginationDots.dot.BirdDot

private const val DEFAULT_MAX_VISIBLE_DOTS_INDEX = 4
private val DEFAULT_DOT_SPACING = 6.dp
private val DOT_WIDTH_IDLE = 8.dp
private val DOT_WIDTH_SELECTED = 8.dp


@Composable
fun BirdPaginationDots(
    count: Int,
    currentIndex: Int,
    modifier: Modifier = Modifier,
    maxVisibleDotsIndex: Int = DEFAULT_MAX_VISIBLE_DOTS_INDEX,
    dotSelectedWidth: Dp = DOT_WIDTH_SELECTED,
    dotIdleWidth: Dp = DOT_WIDTH_IDLE,
    dotSpacing: Dp = DEFAULT_DOT_SPACING
) {
    if (count <= 0 || maxVisibleDotsIndex <= 0) {
        return
    }

    var oldIndex = remember { mutableIntStateOf(0) }

    LaunchedEffect(currentIndex) {
        oldIndex = mutableIntStateOf(currentIndex)
    }

    Row(
        modifier = modifier
            .wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val hideDots = oldIndex.intValue > currentIndex

        for (i in 0 until count) {

            when {
                i <= DEFAULT_MAX_VISIBLE_DOTS_INDEX -> {
                    AnimatedVisibility(
                        visible = hideDots,
                    ){
                        BirdDot(
                            isSelected = i == currentIndex,
                            selectedWidth = dotSelectedWidth,
                            idleWidth = dotIdleWidth
                        )
                    }
                }

                i > DEFAULT_MAX_VISIBLE_DOTS_INDEX -> {
                    AnimatedVisibility(
                        visible = i == currentIndex,
                    ) {
                        BirdDot(
                            isSelected = i == currentIndex,
                            selectedWidth = dotSelectedWidth,
                            idleWidth = dotIdleWidth
                        )
                    }
                }
            }
        }
    }
}