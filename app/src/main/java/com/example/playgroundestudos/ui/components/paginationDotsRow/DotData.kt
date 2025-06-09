package com.example.playgroundestudos.ui.components.paginationDotsRow

import androidx.compose.ui.unit.Dp


data class DotData(
    val id: Int,
    val isSelected: Boolean,
    val selectedWidth: Dp,
    val idleWidth: Dp,
)