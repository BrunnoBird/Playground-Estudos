package com.example.playgroundestudos.ui.components.paginationDots.utils

import kotlin.math.max
import kotlin.math.min

/**
 * Calcula a "janela" de itens visíveis e a quantidade de itens restantes
 * em uma lista, com base em um índice de início e um tamanho de janela.
 *
 * @param allItems A lista completa de itens.
 * @param startIndex O índice do primeiro item a ser incluído na janela.
 * @param windowSize O número de itens a serem exibidos na janela.
 * @return Um objeto [WindowCalculationResult] contendo a sublista da janela
 * e a contagem de itens restantes.
 */
fun <T> calculateSlidingWindow(
    allItems: List<T>,
    startIndex: Int,
    windowSize: Int
): WindowCalculationResult<T> {
    require(startIndex >= 0) { "startIndex não pode ser negativo." }
    require(windowSize > 0) { "windowSize deve ser maior que zero." }

    val actualStartIndex = max(0, startIndex) // Garante que startIndex não seja negativo
    val actualEndIndex = min(allItems.size, actualStartIndex + windowSize)

    val currentWindowItems = if (actualStartIndex < allItems.size) {
        allItems.subList(actualStartIndex, actualEndIndex)
    } else {
        emptyList() // Se startIndex já está além do tamanho da lista, a janela está vazia
    }

    val remainingItemsCount = max(0, allItems.size - actualEndIndex)

    return WindowCalculationResult(
        currentWindowItems = currentWindowItems,
        remainingItemsCount = remainingItemsCount
    )
}

/**
 * Classe de dados para armazenar o resultado do cálculo da janela.
 */
data class WindowCalculationResult<T>(
    val currentWindowItems: List<T>,
    val remainingItemsCount: Int
)

