package com.example.playgroundestudos.ui.components.paginationDotsRow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DotsGroup(
    modifier: Modifier = Modifier,
    count: Int,
    selectedIndex: Int = 0,
    dotSpacing: Dp = 4.dp,
    selectedDotWidth: Dp = 20.dp,
    idleDotWidth: Dp = 6.dp,
) {
    val dotsGroupWidth = remember {
        (idleDotWidth * 4) + selectedDotWidth + (dotSpacing * 4)
    }

    var listOfDotDataObjects by remember { mutableStateOf(emptyList<DotData>()) }


    LaunchedEffect(count, selectedIndex, selectedDotWidth, idleDotWidth) {
        val newList = mutableListOf<DotData>()
        repeat(count) { index ->
            val isSelected = (index == selectedIndex)

            newList.add(
                DotData(
                    id = index,
                    isSelected = isSelected,
                    selectedWidth = selectedDotWidth,
                    idleWidth = idleDotWidth,
                )
            )
        }
        listOfDotDataObjects = newList
    }

    Row(
        modifier = modifier
            .width(dotsGroupWidth)
            .clipToBounds(),
        horizontalArrangement = Arrangement.spacedBy(dotSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (dotData in listOfDotDataObjects) {
            Dot(
                isSelected = dotData.isSelected,
                selectedWidth = dotData.selectedWidth,
                idleWidth = dotData.idleWidth,
                dopPadding = dotSpacing
            )
        }
    }
}
