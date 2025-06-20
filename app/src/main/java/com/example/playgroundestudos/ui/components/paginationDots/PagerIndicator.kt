package com.example.playgroundestudos.ui.components.paginationDots

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
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

    val indicatorController =
        rememberIndicatorController(
            count = pageCount,
            size = intSize,
            dotStyle = dotStyle,
            startIndex = page,
            startRange = range.startIndex..range.endIndex
        )

    // <-- ALTERADO: O gatilho agora é a MUDANÇA do `currentIndex`
    LaunchedEffect(currentIndex) {
        // Compara o novo currentIndex com o estado interno do controller
        if (currentIndex != indicatorController.getCurrentIndex()) {
            // Chama a lógica de animação do controller
            indicatorController.pageChanged(currentIndex)
            // Atualiza o estado salvo
            page = currentIndex
            updateRange(currentIndex)
        }
    }

    indicatorController.clearAll()

    for (i in 0 until pageCount) {
        indicatorController.sizes.add(
            animateFloatAsState(
                targetValue = indicatorController.sizeTargets[i],
                dotAnimation.sizeAnim, label = ""
            )
        )
        indicatorController.offSets.add(
            animateOffsetAsState(
                targetValue = indicatorController.offsetTargets[i],
                dotAnimation.offsetAnim, label = ""
            )
        )
        indicatorController.colors.add(
            animateColorAsState(
                targetValue = indicatorController.colorTargets[i],
                dotAnimation.colorAnim, label = ""
            )
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        for (i in 0 until pageCount) {
            drawCircle(
                indicatorController.colors[i].value,
                radius = indicatorController.sizes[i].value,
                center = indicatorController.offSets[i].value
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
    dotStyle: DotStyle = DotStyle.defaultDotStyle,
    dotAnimation: DotAnimation = DotAnimation.defaultDotAnimation,
) {
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val h = this.maxHeight
        val w = this.maxWidth

        SimplePagerIndicatorKernel(
            pageCount = pageCount,
            currentIndex = currentIndex,
            intSize = with(density) {
                IntSize(
                    w.toPx().toInt(),
                    h.toPx().toInt()
                )
            },
            dotStyle = dotStyle,
            dotAnimation = dotAnimation
        )
    }
}
