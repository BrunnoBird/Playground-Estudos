package com.example.playgroundestudos.ui.components.paginationDots.data

data class PaginationScrollConfig(
    val positionOfCurrentInWindow: Int = 3,
    val scrollChunkSize: Int = 3,
    val scrollBlockSize: Int = 3,
    val visibleWindowSizeEstimate: Int = 5
) {
    fun getMaxPageStartIndex(count: Int): Int {
        return (count - visibleWindowSizeEstimate).coerceAtLeast(0)
    }
}