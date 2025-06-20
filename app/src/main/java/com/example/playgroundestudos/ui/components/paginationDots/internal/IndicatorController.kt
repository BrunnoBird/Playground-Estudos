package com.example.playgroundestudos.ui.components.paginationDots.internal

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import com.example.playgroundestudos.ui.components.paginationDots.internal.dot.DotStyle
import kotlin.times

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

    internal val sizeTargets = SnapshotStateList<Size>()
    internal val sizes = mutableListOf<State<Size>>()

    internal val offsetTargets = SnapshotStateList<Offset>()
    internal val offSets = mutableListOf<State<Offset>>()

    private var offsetEach = dotStyle.unselectedDotSize + dotStyle.dotMargin

    private var visibleRange = startRange

    init {
        Log.e("indicatorController", "init")
        for (i in 0 until count) {
            colorTargets.add(colorFinder(i))
            sizeTargets.add(sizeFinder(i))

            offsetTargets.add(
                Offset(
                    x = calculateStartOffset() + i * offsetEach - (startRange.first * offsetEach),
                    y = size.center.y.toFloat() / 2
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
            selectedIndex.intValue -> dotStyle.currentDotColor
            else -> dotStyle.regularDotColor
        }
    }

    private fun sizeFinder(index: Int): Size {
        return when (index) {
            selectedIndex.intValue -> Size(width = dotStyle.selectedDotWidth, height = dotStyle.unselectedDotSize)
            in visibleRange -> Size(dotStyle.unselectedDotSize, dotStyle.unselectedDotSize)
            else -> Size.Zero
        }
    }

    private fun calculateStartOffset(): Float {
        val unselectedDotWidth = dotStyle.unselectedDotSize
        var totalVisibleWidth = 0f
        val visibleCount = if (count > dotStyle.visibleDotCount) dotStyle.visibleDotCount else count

        for (i in 0 until visibleCount) {
            totalVisibleWidth += unselectedDotWidth
            if (i < visibleCount - 1) {
                totalVisibleWidth += dotStyle.dotMargin
            }
        }

        return size.width / 2f - totalVisibleWidth / 2f + unselectedDotWidth / 2f
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
        val oldIndex = selectedIndex.intValue

        selectedIndex.intValue++

        sizeTargets[oldIndex] = sizeFinder(oldIndex)
        colorTargets[oldIndex] = colorFinder(oldIndex)

        sizeTargets[selectedIndex.intValue] = sizeFinder(selectedIndex.intValue)
        colorTargets[selectedIndex.intValue] = colorFinder(selectedIndex.intValue)
    }

    override fun processMovementBackward() {
        val oldIndex = selectedIndex.intValue
        selectedIndex.intValue--

        sizeTargets[oldIndex] = sizeFinder(oldIndex)
        colorTargets[oldIndex] = colorFinder(oldIndex)

        sizeTargets[selectedIndex.intValue] = sizeFinder(selectedIndex.intValue)
        colorTargets[selectedIndex.intValue] = colorFinder(selectedIndex.intValue)
    }

    fun getCurrentIndex(): Int = selectedIndex.intValue

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