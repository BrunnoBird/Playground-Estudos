package com.example.playgroundestudos.ui.components.paginationDots

import com.example.playgroundestudos.ui.components.paginationDots.data.PaginationScrollConfig

class ScrollTargetCalculator(private val config: PaginationScrollConfig) {

    // Mantém o estado do início da página calculado anteriormente.
    // Isso é importante para a lógica de "paging" que compara com o estado anterior.
    private var previousCalculatedPageStartIndex: Int = 0

    fun calculateScrollTarget(
        currentIndex: Int,
        prevIndex: Int?, // O currentIndex da composição anterior
        count: Int
    ): Int { // Retorna o finalScrollTarget
        if (count <= 0) return 0

        var calculatedPageStartIndex = previousCalculatedPageStartIndex

        // 1. LÓGICA DE SALTO DE PÁGINA (PAGING)
        //    Usa o 'previousCalculatedPageStartIndex' para decidir se um salto de página deve ocorrer.
        val previousEffectiveIndex = prevIndex ?: currentIndex
        if (previousEffectiveIndex != currentIndex) {
            if (currentIndex > previousEffectiveIndex) { // Avançando
                if (previousEffectiveIndex == previousCalculatedPageStartIndex + config.positionOfCurrentInWindow &&
                    currentIndex == previousEffectiveIndex + 1
                ) {
                    val newPageStart = previousCalculatedPageStartIndex + config.scrollChunkSize
                    calculatedPageStartIndex =
                        if (newPageStart + config.positionOfCurrentInWindow < count && newPageStart >= 0) {
                            newPageStart
                        } else {
                            config.getMaxPageStartIndex(count)
                        }
                }
            } else { // Retornando
                if (previousEffectiveIndex == previousCalculatedPageStartIndex + 1 &&
                    currentIndex == previousEffectiveIndex - 1
                ) {
                    val newPageStart = previousCalculatedPageStartIndex - config.scrollChunkSize
                    calculatedPageStartIndex = if (newPageStart >= 0) {
                        newPageStart
                    } else {
                        0
                    }
                }
            }
        }

        // 2. AJUSTE PARA SALTOS DIRETOS (SE `currentIndex` ESTIVER FORA DA JANELA DA `calculatedPageStartIndex`)
        if (currentIndex > calculatedPageStartIndex + config.positionOfCurrentInWindow + 1 || // +1 como folga
            currentIndex < calculatedPageStartIndex
        ) {
            calculatedPageStartIndex = (currentIndex - config.positionOfCurrentInWindow)
                .coerceIn(0, config.getMaxPageStartIndex(count))
        }

        // 3. GARANTIA FINAL DE POSICIONAMENTO DE `currentIndex` (REGRA DOMINANTE)
        //    Assegura que `calculatedPageStartIndex` tenta centralizar `currentIndex`.
        calculatedPageStartIndex = (currentIndex - config.positionOfCurrentInWindow)
            .coerceIn(0, config.getMaxPageStartIndex(count))

        // Atualiza o estado interno para a próxima chamada
        this.previousCalculatedPageStartIndex = calculatedPageStartIndex


        // 4. LÓGICA DE ALINHAMENTO DE BLOCO (APLICADA À `calculatedPageStartIndex`)
        var blockAlignedPageStartIndex =
            (calculatedPageStartIndex / config.scrollBlockSize) * config.scrollBlockSize

        // Ajuste do bloco alinhado para manter `currentIndex` visível (se possível)
        // Se, ao alinhar para o bloco, o currentIndex ficou muito à frente
        if (currentIndex >= blockAlignedPageStartIndex + config.visibleWindowSizeEstimate) {
            val nextBlockStart = blockAlignedPageStartIndex + config.scrollBlockSize
            // Se o próximo bloco ainda permite a janela visível
            if (nextBlockStart <= config.getMaxPageStartIndex(count)) {
                blockAlignedPageStartIndex = nextBlockStart
            } else {
                // Não pode pular para o próximo bloco completo, então alinha ao máximo possível
                blockAlignedPageStartIndex = config.getMaxPageStartIndex(count)
                // E re-snap este máximo ao seu bloco
                blockAlignedPageStartIndex =
                    (blockAlignedPageStartIndex / config.scrollBlockSize) * config.scrollBlockSize
            }
        }
        // Se o currentIndex ficou antes do bloco alinhado (menos comum com a centralização forte anterior)
        else if (currentIndex < blockAlignedPageStartIndex && calculatedPageStartIndex > blockAlignedPageStartIndex) {
            // O calculatedPageStartIndex era mais à direita, mas o alinhamento de bloco o puxou para a esquerda,
            // fazendo currentIndex ficar "antes". Isso pode ser um sinal para não alinhar tão agressivamente à esquerda,
            // ou que a centralização dominante já cuidou disso.
            // No código original, não havia um ajuste forte para esse caso, então vamos manter simples.
        }


        // 5. CÁLCULO DO ALVO FINAL DA ROLAGEM E COERÇÃO
        //    Garante que o último bloco possível seja o alvo se estivermos perto do fim
        val maxPossibleStartForBlockAlignment = config.getMaxPageStartIndex(count)
        val finalScrollTarget = blockAlignedPageStartIndex
            .coerceIn(0, maxPossibleStartForBlockAlignment.let { maxStart ->
                if (maxStart % config.scrollBlockSize != 0 && count > config.scrollBlockSize) {
                    (maxStart / config.scrollBlockSize) * config.scrollBlockSize
                } else {
                    maxStart
                }
            })
            .coerceIn(0, (count - 1).coerceAtLeast(0)) // Coerção final para índice de item válido

        return finalScrollTarget
    }

    // Método para resetar o estado interno se o count mudar drasticamente, por exemplo.
    fun resetInternalState(currentIndex: Int, count: Int) {
        if (count <= 0) {
            previousCalculatedPageStartIndex = 0
            return
        }
        previousCalculatedPageStartIndex = (currentIndex - config.positionOfCurrentInWindow)
            .coerceIn(0, config.getMaxPageStartIndex(count))
    }
}