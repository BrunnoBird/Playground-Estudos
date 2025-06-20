package com.example.playgroundestudos.ui.components.paginationDots

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.playgroundestudos.ui.components.paginationDots.internal.RangeChanged
import com.example.playgroundestudos.ui.components.paginationDots.internal.dot.DotAnimation
import com.example.playgroundestudos.ui.components.paginationDots.internal.dot.DotStyle
import com.example.playgroundestudos.ui.components.paginationDots.internal.rememberIndicatorController

@Composable
internal fun SimplePagerIndicatorKernel(
    pageCount: Int,
    currentIndex: Int,
    intSize: IntSize,
    dotStyle: DotStyle = DotStyle.defaultDotStyle,
    dotAnimation: DotAnimation = DotAnimation.defaultDotAnimation,
) {
    var page by remember {
        mutableIntStateOf(currentIndex)
    }

    var range by remember {
        mutableStateOf(RangeChanged(0, dotStyle.visibleDotCount - 1))
    }

    fun updateRange(index: Int) {
        if (index == range.endIndex && index != pageCount - 1) {
            range = RangeChanged(
                startIndex = range.startIndex + 1,
                endIndex = range.endIndex + 1
            )
        } else if (index == range.startIndex && index != 0) {
            range = RangeChanged(
                startIndex = range.startIndex - 1,
                endIndex = range.endIndex - 1
            )
        }
    }

    val indicatorController = rememberIndicatorController(
        count = pageCount,
        size = intSize,
        dotStyle = dotStyle,
        startIndex = page,
        startRange = range.startIndex..range.endIndex
    )

    LaunchedEffect(currentIndex) {
        if (currentIndex != indicatorController.getCurrentIndex()) {
            indicatorController.pageChanged(currentIndex)
            page = currentIndex
            updateRange(currentIndex)
        }
    }

    indicatorController.clearAll()

    for (i in 0 until pageCount) {
        indicatorController.sizes.add(
            animateSizeAsState(
                targetValue = indicatorController.sizeTargets[i],
                animationSpec = dotAnimation.sizeAnim,
                label = "sizeAnimation"
            )
        )
        indicatorController.offSets.add(
            animateOffsetAsState(
                targetValue = indicatorController.offsetTargets[i],
                animationSpec = dotAnimation.offsetAnim,
                label = "offsetAnimation"
            )
        )
        indicatorController.colors.add(
            animateColorAsState(
                targetValue = indicatorController.colorTargets[i],
                animationSpec = dotAnimation.colorAnim,
                label = "colorAnimation"
            )
        )
    }

    Canvas(modifier = Modifier.wrapContentSize()) {
        val unselectedWidth = dotStyle.unselectedDotSize

        for (i in 0 until pageCount) {
            val center = indicatorController.offSets[i].value
            val currentSize = indicatorController.sizes[i].value
            val topLeftY = center.y - currentSize.height / 2
            val topLeftX = center.x - unselectedWidth / 2
            val topLeft = Offset(topLeftX, topLeftY)
            val cornerRadius = CornerRadius(x = currentSize.height / 2, y = currentSize.height / 2)

            drawRoundRect(
                color = indicatorController.colors[i].value,
                topLeft = topLeft,
                size = currentSize,
                cornerRadius = cornerRadius
            )
        }
    }
}

/**
 * Um indicador de paginação que é controlado por um [currentIndex] inteiro,
 * sem a necessidade de um PagerState.
 *
 * @param modifier O modificador a ser aplicado ao componente.
 * @param pageCount O número total de páginas (dots).
 * @param currentIndex O índice da página atualmente selecionada (começando em 0).
 * @param dotStyle O estilo customizado para os dots.
 * @param dotAnimation As animações customizadas para os dots.
 */
@Composable
fun SimplePagerIndicator(
    modifier: Modifier,
    pageCount: Int,
    currentIndex: Int,
    unselectedDotSize: Dp = 8.dp,
    selectedDotWidth: Dp = 16.dp,
    dotMargin: Dp = 4.dp,
    visibleDotCount: Int = 5,
    selectedDotColor: Color = Color(0xFF0d6efd),
    unselectedDotColor: Color = Color(0xFF6c757d),
    dotAnimation: DotAnimation = DotAnimation.defaultDotAnimation,
) {
    var intSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .wrapContentSize()
            .onGloballyPositioned {
                intSize = it.size
            }) {

        val density = LocalDensity.current

        val dotStyleInPx = remember(
            unselectedDotSize,
            selectedDotWidth,
            dotMargin,
            visibleDotCount,
            selectedDotColor,
            unselectedDotColor
        ) {
            with(density) {
                DotStyle(
                    unselectedDotSize = unselectedDotSize.toPx(),
                    selectedDotWidth = selectedDotWidth.toPx(),
                    dotMargin = dotMargin.toPx(),
                    visibleDotCount = visibleDotCount,
                    currentDotColor = selectedDotColor,
                    regularDotColor = unselectedDotColor
                )
            }
        }

        SimplePagerIndicatorKernel(
            pageCount = pageCount,
            currentIndex = currentIndex,
            intSize = intSize,
            dotStyle = dotStyleInPx,
            dotAnimation = dotAnimation
        )
    }
}
