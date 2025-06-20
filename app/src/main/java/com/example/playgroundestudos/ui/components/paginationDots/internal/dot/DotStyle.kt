package com.example.playgroundestudos.ui.components.paginationDots.internal.dot

import androidx.compose.ui.graphics.Color

data class DotStyle(
    val selectedDotWidth: Float,
    val unselectedDotSize: Float,
    val dotMargin: Float,
    val visibleDotCount: Int,
    val currentDotColor: Color,
    val regularDotColor: Color
) {
    init {
        require(visibleDotCount > 2) { "Visible dot count must be greater than 2" }
        require(selectedDotWidth > 0f) { "Selected dot width must be greater than 0F" }
        require(unselectedDotSize > 0f) { "Unselected dot size must be greater than 0F" }
        require(dotMargin > 0f) { "Dot margin must be greater than 0F" }
    }

    companion object {
        private const val defaultVisibleDotCount = 5
        private const val defaultUnselectedDotDiameter = 8f
        private const val defaultSelectedDotWidth = 16f
        private const val defaultMargin = 4f
        private val defaultCurrentDotColor = Color(0xFF0d6efd)
        private val defaultRegularDotColor = Color(0xFF6c757d)

        val defaultDotStyle = DotStyle(
            // Use as constantes diretamente, sem multiplicar.
            selectedDotWidth = defaultSelectedDotWidth,
            unselectedDotSize = defaultUnselectedDotDiameter,
            dotMargin = defaultMargin,
            visibleDotCount = defaultVisibleDotCount,
            currentDotColor = defaultCurrentDotColor,
            regularDotColor = defaultRegularDotColor
        )
    }
}