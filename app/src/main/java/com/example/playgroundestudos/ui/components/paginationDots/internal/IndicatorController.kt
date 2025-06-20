package com.example.playgroundestudos.ui.components.paginationDots.internal

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import com.example.playgroundestudos.ui.components.paginationDots.internal.dot.DotStyle

const val MAX_SCROLLABLE_DOT = 3

internal class IndicatorController(
    private val count: Int,
    private val size: IntSize,
    private val dotStyle: DotStyle,
    private val startIndex: Int = 0,
    startRange: IntRange = startIndex..dotStyle.visibleDotCount.minus(1)

) : IndicatorRangeProcessor, IndicatorMovementProcessor {
    private var selectedIndex = mutableIntStateOf(startIndex)

    internal val colorTargets = SnapshotStateList<Color>()
    internal val colors = mutableListOf<State<Color>>()

    internal val sizeTargets = SnapshotStateList<Float>()
    internal val sizes = mutableListOf<State<Float>>()

    internal val offsetTargets = SnapshotStateList<Offset>()
    internal val offSets = mutableListOf<State<Offset>>()

    private var offsetEach = dotStyle.dotMargin + dotStyle.regularDotRadius.times(2)

    private var visibleRange = startRange

    init {
        Log.e("indicatorController", "init")
        for (i in 0 until count) {
            colorTargets.add(colorFinder(i))
            sizeTargets.add(sizeFinder(i))

            offsetTargets.add(
                Offset(
                    x = calculateStartOffset() + i.times(dotStyle.dotMargin) + i.times(
                        dotStyle.regularDotRadius.times(2)
                    ) - ((startRange.first) * offsetEach),
                    y = size.center.y.toFloat()
                )
            )
        }
    }

    fun clearAll() {
        sizes.clear()
        offSets.clear()
        colors.clear()
    }

    private fun next() {
        // A condição para rolar continua a mesma: quando a seleção atinge a borda da área visível.
        if (selectedIndex.intValue + 1 == visibleRange.last && selectedIndex.intValue + 1 != count - 1) {

            // ALTERADO: Em vez de mover o equivalente a 1 dot, movemos por `scrollStep` (3) dots.
            val totalOffsetShift = offsetEach * MAX_SCROLLABLE_DOT

            // Aplica o novo deslocamento, que é maior
            for (i in 0 until count)
                offsetTargets[i] = Offset(
                    x = offsetTargets[i].x - totalOffsetShift,
                    y = offsetTargets[i].y
                )

            // ALTERADO: Atualiza o intervalo de dots visíveis avançando `scrollStep` posições.
            processRangeNext(MAX_SCROLLABLE_DOT)

            // A lógica de seleção do próximo item e atualização dos estilos não muda.
            selectedIndex.intValue++
            for (i in 0 until count) {
                sizeTargets[i] = sizeFinder(i)
                colorTargets[i] = colorFinder(i)
            }

        } else {
            // Se não estivermos na borda, apenas avançamos a seleção (comportamento normal).
            processMovementForward()
        }
    }

    private fun prev() {
        // A lógica para voltar é simétrica à do `next()`.
        if (selectedIndex.intValue - 1 == visibleRange.first && selectedIndex.intValue - 1 != 0) {

            // ALTERADO: Calcula o deslocamento total para a direção oposta.
            val totalOffsetShift = offsetEach * MAX_SCROLLABLE_DOT

            // Aplica o deslocamento.
            for (i in 0 until count)
                offsetTargets[i] =
                    Offset(x = offsetTargets[i].x + totalOffsetShift, y = offsetTargets[i].y)

            // ALTERADO: Atualiza o intervalo de dots visíveis recuando `scrollStep` posições.
            processRangePrev(MAX_SCROLLABLE_DOT)

            // A lógica de seleção e atualização de estilo não muda.
            selectedIndex.intValue--
            for (i in 0 until count) {
                sizeTargets[i] = sizeFinder(i)
                colorTargets[i] = colorFinder(i)
            }

        } else {
            // Se não estivermos na borda, apenas retrocedemos a seleção.
            processMovementBackward()
        }
    }

    private fun colorFinder(index: Int): Color {
        return when (index) {
            selectedIndex.value -> dotStyle.currentDotColor
            else -> dotStyle.regularDotColor

        }
    }

    private fun sizeFinder(index: Int): Float {
        return when (index) {
            selectedIndex.value -> dotStyle.currentDotRadius
            visibleRange.first -> {
                if (visibleRange.first != 0)
                    dotStyle.notLastDotRadius
                else
                    dotStyle.regularDotRadius
            }

            visibleRange.last -> {
                if (visibleRange.last != count - 1)
                    dotStyle.notLastDotRadius
                else
                    dotStyle.regularDotRadius
            }

            in visibleRange -> dotStyle.regularDotRadius

            else -> 0f
        }
    }

    private fun calculateStartOffset(): Float {
        var totalDotSize = dotStyle.regularDotRadius.times(2f)

        val till = if (count > dotStyle.visibleDotCount) dotStyle.visibleDotCount else count
        for (i in 1 until till)
            totalDotSize += dotStyle.regularDotRadius.times(2f) + dotStyle.dotMargin

        return size.width.div(2f) - totalDotSize.div(2f) + dotStyle.regularDotRadius
    }

    override fun processRangeNext(step: Int) {
        val newFirst = (visibleRange.first + step).coerceAtMost(count - dotStyle.visibleDotCount)
        val newLast = (newFirst + dotStyle.visibleDotCount - 1).coerceAtMost(count - 1)
        visibleRange = newFirst..newLast
    }

    override fun processRangePrev(step: Int) {
        val newFirst = (visibleRange.first - step).coerceAtLeast(0)
        val newLast = (newFirst + dotStyle.visibleDotCount - 1).coerceAtMost(count - 1)
        visibleRange = newFirst..newLast
    }

    override fun processMovementForward() {
        sizeTargets[selectedIndex.intValue] = dotStyle.regularDotRadius
        colorTargets[selectedIndex.intValue] = dotStyle.regularDotColor
        selectedIndex.intValue++
        sizeTargets[selectedIndex.intValue] = dotStyle.currentDotRadius
        colorTargets[selectedIndex.intValue] = dotStyle.currentDotColor
    }

    override fun processMovementBackward() {
        sizeTargets[selectedIndex.intValue] = dotStyle.regularDotRadius
        colorTargets[selectedIndex.intValue] = dotStyle.regularDotColor
        selectedIndex.intValue--
        sizeTargets[selectedIndex.intValue] = dotStyle.currentDotRadius
        colorTargets[selectedIndex.intValue] = dotStyle.currentDotColor

    }

    fun getCurrentIndex(): Int {
        return selectedIndex.intValue
    }

    fun pageChanged(index: Int) {
        val diff = index - selectedIndex.intValue
        if (diff > 0) {
            repeat(diff) { next() }
        } else {
            repeat(-diff) { prev() }
        }
    }

}

@Composable
internal fun rememberIndicatorController(
    count: Int,
    size: IntSize,
    dotStyle: DotStyle,
    startIndex: Int,
    startRange: IntRange
): IndicatorController {
    return remember {
        IndicatorController(count, size, dotStyle, startIndex, startRange)
    }
}