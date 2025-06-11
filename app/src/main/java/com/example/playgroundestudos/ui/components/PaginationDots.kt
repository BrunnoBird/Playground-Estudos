package com.example.playgroundestudos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

@Composable
fun PaginationDots(
    totalItems: Int,
    currentPage: Int, // This is the actual selected page index (0 to totalItems - 1)
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (totalItems <= 0) {
        return // Nothing to display
    }

    val maxVisibleDots = 5
    val shiftSize = 3

    // windowStart is the index of the first dot currently visible on screen
    var windowStart by remember { mutableStateOf(0) }

    // This effect ensures that if currentPage changes (e.g., from parent or after onPageSelected),
    // the windowStart adjusts to keep currentPage visible and centered if possible,
    // respecting the boundaries. This is also key for initialization.
    LaunchedEffect(currentPage, totalItems, maxVisibleDots) {
        if (totalItems <= maxVisibleDots) {
            windowStart = 0
        } else {
            // If currentPage is outside the current effective window [windowStart, windowStart + maxVisibleDots - 1],
            // or to simply re-evaluate based on currentPage (e.g. for initialization/external changes).
            // Try to center currentPage (as the 3rd dot, which is index 2 in a 0-indexed 5-dot view).
            val idealStart = currentPage - 2
            // Clamp it: cannot be less than 0, cannot be more than (totalItems - maxVisibleDots)
            // This calculation ensures currentPage is visible. If the pagination rules below
            // also adjust windowStart, this LaunchedEffect will re-center/correct if needed.
            // The problem is if this fights with the click handler's immediate shift.

            // More robust: This LaunchedEffect primarily ensures currentPage is *visible*.
            // The click handler applies specific shift rules.
            // If currentPage lands outside window [windowStart, windowStart + maxVisibleDots -1], then re-center.
            if (currentPage < windowStart || currentPage >= windowStart + maxVisibleDots) {
                 val idealNewStart = currentPage - 2 // try to make currentPage the 3rd item
                 windowStart = idealNewStart.coerceIn(0, max(0, totalItems - maxVisibleDots))
            } else if (windowStart > max(0, totalItems - maxVisibleDots)) {
                 // Correct if windowStart somehow got too large (e.g. totalItems decreased)
                 windowStart = max(0, totalItems - maxVisibleDots)
            }
        }
    }

    val visibleDotIndices = remember(windowStart, totalItems, maxVisibleDots) {
        if (totalItems == 0) emptyList()
        // Ensure windowStart itself isn't out of valid range if totalItems shrinks significantly
        val correctedWindowStart = windowStart.coerceIn(0, max(0, totalItems - 1))
        (correctedWindowStart until min(correctedWindowStart + maxVisibleDots, totalItems)).toList()
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        visibleDotIndices.forEach { dotIndexToDisplay ->
            val isSelected = (dotIndexToDisplay == currentPage)
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(width = if (isSelected) 16.dp else 8.dp, height = 8.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.Blue else Color.Gray) // Using Blue for selected
                    .clickable {
                        // `currentPage` is the state *before* this click is processed by the parent.
                        // `dotIndexToDisplay` is the page the user wants to navigate to.

                        var newWindowStart = windowStart

                        if (dotIndexToDisplay > currentPage) { // User is trying to move FORWARD
                            // Rule: If current selection is the 3rd dot (relative index 2) of the current window, then shift.
                            if ((currentPage - windowStart) == 2 && totalItems > maxVisibleDots) {
                                newWindowStart = min(windowStart + shiftSize, max(0, totalItems - maxVisibleDots))
                            }
                        } else if (dotIndexToDisplay < currentPage) { // User is trying to move BACKWARD
                            // Rule: If current selection is the 1st dot (relative index 0) of the current window, then shift.
                            if ((currentPage - windowStart) == 0 && totalItems > maxVisibleDots) {
                                newWindowStart = max(0, windowStart - shiftSize)
                            }
                        }

                        // Only update windowStart if it actually changes, to avoid redundant recompositions if not needed.
                        if (newWindowStart != windowStart) {
                           windowStart = newWindowStart
                        }

                        // Notify parent about the selection.
                        // The LaunchedEffect above will then run with the new `currentPage`
                        // and can make further adjustments to windowStart if needed (e.g., if the new
                        // currentPage, after the shift, requires the window to be centered differently,
                        // though the click handler's shift should ideally be compatible).
                        if (currentPage != dotIndexToDisplay) { // Avoid redundant calls if already selected
                           onPageSelected(dotIndexToDisplay)
                        }
                    }
            )
        }
    }
}

import androidx.compose.material.Surface
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true, name = "PaginationDots Preview - 10 items, page 0")
@Composable
fun PaginationDotsPreview() {
    var currentPage by remember { mutableStateOf(0) }
    Surface {
        PaginationDots(
            totalItems = 10,
            currentPage = currentPage,
            onPageSelected = { selectedPage ->
                currentPage = selectedPage
            }
        )
    }
}

@Preview(showBackground = true, name = "PaginationDots Preview - 10 items, page 4")
@Composable
fun PaginationDotsPreviewPage4() {
    var currentPage by remember { mutableStateOf(4) }
    Surface {
        PaginationDots(
            totalItems = 10,
            currentPage = currentPage,
            onPageSelected = { selectedPage ->
                currentPage = selectedPage
            }
        )
    }
}

@Preview(showBackground = true, name = "PaginationDots Preview - 3 items, page 1")
@Composable
fun PaginationDotsPreviewSmall() {
    var currentPage by remember { mutableStateOf(1) }
    Surface {
        PaginationDots(
            totalItems = 3,
            currentPage = currentPage,
            onPageSelected = { selectedPage ->
                currentPage = selectedPage
            }
        )
    }
}

@Preview(showBackground = true, name = "PaginationDots Preview - 7 items, page 2 (test shift)")
@Composable
fun PaginationDotsPreviewShiftTest() {
    var currentPage by remember { mutableStateOf(2) } // Starts at page 2 (0,1,*2*,3,4)
                                                     // Click 3 (4th dot): should shift to (3,4,*5*,6,7) if logic is right
                                                     // Click 2 from there (now page 5 is selected): should be fine.
                                                     // Click 4 (dot before 5): should shift back to (0,1,2,3,4) if logic is right
    Surface {
        PaginationDots(
            totalItems = 7, // 0,1,2,3,4,5,6
            currentPage = currentPage,
            onPageSelected = { selectedPage ->
                currentPage = selectedPage
            }
        )
    }
}
