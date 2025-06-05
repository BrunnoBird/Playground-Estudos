package com.example.playgroundestudos.ui.components.paginationDots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.playgroundestudos.ui.components.paginationDots.dot.BirdDot

// --- Parâmetros Padrão para BirdPaginationDots ---
private val DEFAULT_DOT_SPACING_PAGINATION = 4.dp

// --- Parâmetros Padrão para BirdDot (usados como default em BirdPaginationDots) ---
private val DEFAULT_SELECTED_DOT_WIDTH_PAGINATION = 20.dp
private val DEFAULT_IDLE_DOT_WIDTH_PAGINATION = 6.dp
private val DEFAULT_DOT_ANIMATION_DURATION_PAGINATION = 500

@Composable
fun BirdPaginationDotsLazy(
    count: Int,
    currentIndex: Int,
    modifier: Modifier = Modifier,
    dotSpacing: Dp = DEFAULT_DOT_SPACING_PAGINATION,
    selectedDotWidth: Dp = DEFAULT_SELECTED_DOT_WIDTH_PAGINATION,
    idleDotWidth: Dp = DEFAULT_IDLE_DOT_WIDTH_PAGINATION,
    dotAnimationDurationMillis: Int = DEFAULT_DOT_ANIMATION_DURATION_PAGINATION,
) {
    if (count <= 0) {
        return
    }

    val dotsGroupWidth = remember(selectedDotWidth, idleDotWidth, dotSpacing) {
        (idleDotWidth * 4) + selectedDotWidth + (dotSpacing * 4)
    }

    val lazyListState = rememberLazyListState()
    val prevIndex = rememberPrevious(current = currentIndex)

    var effectiveDirection by remember { mutableStateOf(true) }
    LaunchedEffect(currentIndex, prevIndex) {
        if (prevIndex != null && currentIndex != prevIndex) {
            effectiveDirection = currentIndex > prevIndex
        }
    }

    val chunkedPosition = remember { 3 }
    var positionOfCurrentInWindow = remember { mutableIntStateOf(3) }
    var approximateWindowSizeForBounds = remember(positionOfCurrentInWindow) {
        derivedStateOf { positionOfCurrentInWindow.intValue + 1 }
    }

    LaunchedEffect(currentIndex, count, prevIndex) {
        if (count <= 0) return@LaunchedEffect
//        val previousEffectiveIndex = prevIndex ?: currentIndex


        if (effectiveDirection) {
            //PAGINACAO PARA A PROXIMA PAGINA
            if (currentIndex == approximateWindowSizeForBounds.value) {
                lazyListState.animateScrollToItem(
                    positionOfCurrentInWindow.intValue,
                )
                positionOfCurrentInWindow.intValue = positionOfCurrentInWindow.intValue + chunkedPosition
            }
        } else {
//            //PAGINACAO PARA A PROXIMA ANTERIOR
//            val newPosition = positionOfCurrentInWindow.intValue / 2
//            positionOfCurrentInWindow.intValue = newPosition.coerceAtLeast(0)
//
//            if (currentIndex == approximateWindowSizeForBounds.value) {
//                lazyListState.animateScrollToItem(positionOfCurrentInWindow.intValue)
//                positionOfCurrentInWindow.intValue = positionOfCurrentInWindow.intValue * 2
//            }
        }
    }

    LazyRow(
        state = lazyListState,
        modifier = modifier
            .width(dotsGroupWidth)
            .height(48.dp),
        userScrollEnabled = false,
        horizontalArrangement = Arrangement.spacedBy(dotSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(
            count = count,
            key = { index -> index }
        ) { index ->
            val isSelected = (index == currentIndex)
            BirdDot(
                isSelected = isSelected,
                isCurrentlyVisible = true,
                isForwardNavigation = effectiveDirection,
                selectedWidth = selectedDotWidth,
                idleWidth = idleDotWidth,
                animationDurationMillis = dotAnimationDurationMillis
            )
        }
    }
}

@Composable
private fun <T> rememberPrevious(current: T): T? {
    val ref = remember { PreviousValueHolder<T>() }
    val previous = ref.previousValue
    SideEffect {
        ref.previousValue = current
    }
    return previous
}

private class PreviousValueHolder<T> {
    var previousValue: T? = null
}