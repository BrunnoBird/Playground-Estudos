package com.example.playgroundestudos.ui.components.paginationDots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.playgroundestudos.ui.components.paginationDots.dot.BirdDot

// --- Parâmetros Padrão para BirdPaginationDots ---
private val DEFAULT_DOT_SPACING_PAGINATION = 4.dp

// --- Parâmetros Padrão para BirdDot (usados como default em BirdPaginationDots) ---
private val DEFAULT_SELECTED_DOT_WIDTH_PAGINATION = 20.dp
private val DEFAULT_IDLE_DOT_WIDTH_PAGINATION = 6.dp
private val DEFAULT_DOT_ANIMATION_DURATION_PAGINATION = 500

@Composable
fun BirdPaginationDotsLazy(
    count: Int,
    currentIndex: Int,
    modifier: Modifier = Modifier,
    dotSpacing: Dp = DEFAULT_DOT_SPACING_PAGINATION,
    selectedDotWidth: Dp = DEFAULT_SELECTED_DOT_WIDTH_PAGINATION,
    idleDotWidth: Dp = DEFAULT_IDLE_DOT_WIDTH_PAGINATION,
    dotAnimationDurationMillis: Int = DEFAULT_DOT_ANIMATION_DURATION_PAGINATION,
) {
    if (count <= 0) {
        return
    }

    // Largura fixa para o LazyRow, tentando acomodar ~5 dots.
    // (1 selecionado + 4 normais + 4 espaços entre eles)
    val dotsGroupWidth = remember(selectedDotWidth, idleDotWidth, dotSpacing) {
        (idleDotWidth * 4) + selectedDotWidth + (dotSpacing * 4)
    }

    val lazyListState = rememberLazyListState()
    val prevIndex = rememberPrevious(current = currentIndex)

    var effectiveDirection by remember { mutableStateOf(true) }
    LaunchedEffect(currentIndex, prevIndex) {
        if (prevIndex != null && currentIndex != prevIndex) {
            effectiveDirection = currentIndex > prevIndex
        }
    }

    // Posição desejada do currentIndex dentro da janela visível (0-indexado).
    // Se 3, significa que o currentIndex é o 4º item. Ex: [S, S+1, S+2, *S+3*(currentIndex), S+4]
    val positionOfCurrentInWindow = 3
    val scrollChunkSize = 3 // Quantos itens "pular" ao mudar de página
    // Tamanho aproximado da janela visível para cálculos de coerção.
    // Se positionOfCurrentInWindow é 3, e ele é o 4º item, a janela tem pelo menos 4 itens.
    // Para uma janela de 5, seria positionOfCurrentInWindow + 1 (para o item atual) + (5 - 1 - positionOfCurrentInWindow) itens depois.
    // Simplificando: assumimos que a janela tem pelo menos `positionOfCurrentInWindow + 2` itens para ter algum espaço de manobra.
    val approximateWindowSizeForBounds = positionOfCurrentInWindow + 1


    // `pageStartIndex` é o índice do primeiro item da "página" atual que o LazyRow deve mostrar.
    var pageStartIndex by remember(count) { // Resetar se `count` mudar
        mutableStateOf(
            (currentIndex - positionOfCurrentInWindow)
                .coerceIn(0, (count - approximateWindowSizeForBounds).coerceAtLeast(0))
        )
    }

    LaunchedEffect(currentIndex, count, prevIndex) {
        if (count <= 0) return@LaunchedEffect

        val previousEffectiveIndex = prevIndex ?: currentIndex

        if (previousEffectiveIndex != currentIndex) { // Somente se o índice realmente mudou
            if (currentIndex > previousEffectiveIndex) { // Avançando
                // Se o índice anterior era o 4º (posição 3) da página atual,
                // e o índice atual é o "5º" (ou seja, `previousEffectiveIndex + 1`),
                // então devemos pular a página para a direita.
                if (previousEffectiveIndex == pageStartIndex + positionOfCurrentInWindow &&
                    currentIndex == previousEffectiveIndex + 1
                ) {
                    val newPageStart = pageStartIndex + scrollChunkSize
                    // Verifica se a nova página é válida
                    pageStartIndex =
                        if (newPageStart + positionOfCurrentInWindow < count && newPageStart >= 0) {
                            newPageStart
                        } else {
                            // Não pode pular, apenas ajusta para o final possível
                            (count - approximateWindowSizeForBounds).coerceAtLeast(0)
                        }
                }
            } else { // Retornando (currentIndex < previousEffectiveIndex)
                // Se o índice anterior era o 2º (posição 1) da página atual,
                // e o índice atual é o "1º" (ou seja, `previousEffectiveIndex - 1`),
                // então devemos pular a página para a esquerda.
                if (previousEffectiveIndex == pageStartIndex + 1 && // 2º item da página
                    currentIndex == previousEffectiveIndex - 1
                ) {
                    val newPageStart = pageStartIndex - scrollChunkSize
                    // Verifica se a nova página é válida
                    if (newPageStart >= 0) { // newPageStart + positionOfCurrentInWindow < count já é coberto pela coerção depois
                        pageStartIndex = newPageStart
                    } else {
                        // Não pode pular, ajusta para o início possível
                        pageStartIndex = 0
                    }
                }
            }
        }

        // Após determinar saltos de página, garantir que `pageStartIndex`
        // seja ajustado se `currentIndex` estiver muito fora da janela atual.
        // Isso cobre casos onde o usuário salta vários índices de uma vez (ex: clicando num dot distante).
        // Se currentIndex está "à direita" demais da janela atual.
        if (currentIndex > pageStartIndex + positionOfCurrentInWindow + 1) { // +1 como uma pequena folga
            pageStartIndex = (currentIndex - positionOfCurrentInWindow)
                .coerceIn(0, (count - approximateWindowSizeForBounds).coerceAtLeast(0))
        }
        // Se currentIndex está "à esquerda" demais da janela atual.
        // (currentIndex < pageStartIndex) é um bom indicador,
        // ou mais precisamente currentIndex < pageStartIndex + (algum offset menor que positionOfCurrentInWindow)
        else if (currentIndex < pageStartIndex) { // Se o currentIndex ficou antes do início da página
            pageStartIndex = (currentIndex - positionOfCurrentInWindow)
                .coerceIn(0, (count - approximateWindowSizeForBounds).coerceAtLeast(0))
        }


        // Garante que o pageStartIndex final seja coerente para posicionar o currentIndex.
        // Se, após um salto, o currentIndex não cairia naturalmente na `positionOfCurrentInWindow`
        // da *nova* `pageStartIndex`, precisamos recalcular `pageStartIndex` para que isso ocorra.
        // Essencialmente, `pageStartIndex` deve ser `currentIndex - positionOfCurrentInWindow`
        // a menos que isso viole os limites do `count`.
        pageStartIndex = (currentIndex - positionOfCurrentInWindow)
            .coerceIn(0, (count - approximateWindowSizeForBounds).coerceAtLeast(0))


// ... (toda a sua lógica existente para calcular pageStartIndex) ...

// Tamanho do bloco de rolagem
        val scrollBlockSize = 3

// Após calcular e ajustar 'pageStartIndex' como você já faz:
// Agora, ajuste o 'pageStartIndex' para ser o início do bloco de 'scrollBlockSize'
// ao qual ele pertence.
        var blockAlignedPageStartIndex = (pageStartIndex / scrollBlockSize) * scrollBlockSize

// Garante que o blockAlignedPageStartIndex ainda permite que o currentIndex
// seja visível dentro da janela definida por positionOfCurrentInWindow, se possível.
// Esta parte pode precisar de ajuste fino dependendo do comportamento exato desejado.
// Se, ao alinhar para o bloco, o currentIndex ficar muito fora,
// podemos precisar escolher o bloco adjacente.

// Exemplo de ajuste (pode precisar de refinamento):
// Se o currentIndex está muito à frente do início do bloco alinhado
        if (currentIndex >= blockAlignedPageStartIndex + approximateWindowSizeForBounds) {
            // Tenta o próximo bloco
            if (blockAlignedPageStartIndex + scrollBlockSize < count) {
                blockAlignedPageStartIndex += scrollBlockSize
            }
        }
// Se o currentIndex está antes do bloco alinhado (improvável com sua lógica de pageStartIndex, mas para segurança)
        else if (currentIndex < blockAlignedPageStartIndex && blockAlignedPageStartIndex > 0) {
            // Tenta o bloco anterior, mas não faz sentido se pageStartIndex já tentou colocar currentIndex em positionOfCurrentInWindow
        }


// O alvo final do scroll é o blockAlignedPageStartIndex calculado e ajustado.
        val finalScrollTarget = blockAlignedPageStartIndex
            .coerceIn(0, (count - approximateWindowSizeForBounds).coerceAtLeast(0).let { maxStart ->
                // Garante que o último bloco possível seja o alvo se estivermos perto do fim
                if (maxStart % scrollBlockSize != 0 && count > scrollBlockSize) {
                    (maxStart / scrollBlockSize) * scrollBlockSize
                } else {
                    maxStart
                }
            })
            .coerceIn(0, (count - 1).coerceAtLeast(0)) // Coerção final para índice válido


        if (finalScrollTarget < count && finalScrollTarget >= 0) {
            lazyListState.animateScrollToItem(finalScrollTarget)
        }
    }

    LazyRow(
        state = lazyListState,
        modifier = modifier.width(dotsGroupWidth), // Largura fixa
        horizontalArrangement = Arrangement.spacedBy(dotSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(
            count = count,
            key = { index -> index }
        ) { index ->
            val isSelected = (index == currentIndex)
            BirdDot(
                isSelected = isSelected,
                isCurrentlyVisible = true, // Com LazyRow, o item é visível quando composto
                isForwardNavigation = effectiveDirection,
                selectedWidth = selectedDotWidth,
                idleWidth = idleDotWidth,
                animationDurationMillis = dotAnimationDurationMillis
            )
        }
    }
}

@Composable
private fun <T> rememberPrevious(current: T): T? {
    val ref = remember { PreviousValueHolder<T>() }
    val previous = ref.previousValue
    SideEffect {
        ref.previousValue = current
    }
    return previous
}

private class PreviousValueHolder<T> {
    var previousValue: T? = null
}