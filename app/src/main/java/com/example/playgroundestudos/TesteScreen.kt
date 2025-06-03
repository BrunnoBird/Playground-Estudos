package com.example.playgroundestudos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun MyLazyRowScreen(
    modifier: Modifier
) {
    // Lista de itens de exemplo
    val items = (1..20).map { "Item $it" }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Largura de cada item para calcular a visibilidade de 5 itens
    // Ajuste este valor conforme necessário, dependendo do padding, etc.
    // Para simplificar, vamos assumir que os itens têm o mesmo tamanho.
    // Se você tiver itens com tamanhos diferentes, essa lógica precisará ser mais robusta.
    val itemWidthDp = 8.dp // Largura estimada de cada item
    val visibleItemsCount = 5

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Item Selecionado: ${items.getOrNull(selectedIndex) ?: "Nenhum"}", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            // Define a largura do Box para acomodar 5 itens.
            // Isso ajuda a LazyRow a entender seu "viewport" para os 5 itens.
            // Se os itens tiverem larguras variáveis ou espaçamentos complexos,
            // você pode precisar de uma abordagem mais dinâmica para definir essa largura.
            // Uma forma simples é (larguraDoItem + espacamentoEntreItens) * numeroDeItensVisiveis.
            // Aqui, vamos definir uma largura fixa para a LazyRow como exemplo.
            // Você pode ajustar isso com base na largura real dos seus itens.
            modifier = Modifier.width(itemWidthDp * visibleItemsCount) // Largura para 5 itens
        ) {
            LazyRow(
                state = lazyListState,
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Espaçamento entre os itens
            ) {
                itemsIndexed(items) { index, item ->
                    MyLazyRowItem(
                        text = item,
                        isSelected = index == selectedIndex,
                        onClick = {
                            val oldSelectedIndex = selectedIndex
                            selectedIndex = index

                            // Lógica de Rolagem
                            // Se o item anteriormente selecionado era o 3º (índice 2)
                            // e o novo item selecionado é o 4º (índice 3)
                            if (oldSelectedIndex == 2 && selectedIndex == 3) {
                                // Queremos que o item de índice 3 (o quarto item)
                                // apareça na segunda posição visível.
                                // Isso significa que o item de índice 2 deve ser o primeiro visível.
                                coroutineScope.launch {
                                    lazyListState.animateScrollToItem(index = 2)
                                }
                            } else if (selectedIndex > oldSelectedIndex && selectedIndex >= visibleItemsCount - 1) {
                                // Lógica para rolar e manter o item selecionado visível
                                // (preferencialmente não no final da lista visível se possível)
                                // Se o item selecionado está no final ou além da janela visível atual
                                // e estamos avançando.
                                // Queremos que o item selecionado fique na segunda posição visível.
                                val targetFirstVisible = selectedIndex - 1
                                if (targetFirstVisible >= 0) {
                                    coroutineScope.launch {
                                        lazyListState.animateScrollToItem(index = targetFirstVisible)
                                    }
                                }
                            } else if (selectedIndex < oldSelectedIndex && selectedIndex > 0) {
                                // Se estamos voltando e o item selecionado não é o primeiro.
                                // Tenta manter o selecionado na segunda posição.
                                val targetFirstVisible = selectedIndex - 1
                                coroutineScope.launch {
                                    lazyListState.animateScrollToItem(index = targetFirstVisible)
                                }
                            } else if (selectedIndex == 0) {
                                // Se o primeiro item for selecionado, role para o início.
                                coroutineScope.launch {
                                    lazyListState.animateScrollToItem(index = 0)
                                }
                            }
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text("Primeiro item visível: ${lazyListState.firstVisibleItemIndex}")
        Text("Offset do primeiro item visível: ${lazyListState.firstVisibleItemScrollOffset}")
    }
}

@Composable
fun MyLazyRowItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp) // Tamanho fixo para cada item
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}