package com.example.playgroundestudos


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.playgroundestudos.ui.components.paginationDots.BirdPaginationDotsLazy

@Composable
fun MyCarouselScreen(
    modifier: Modifier
) {
    val totalPages = 8 // Exemplo: 10 páginas no carrossel
    var currentPage by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Seu conteúdo do carrossel aqui (ex: HorizontalPager)
        Text("Conteúdo da Página: ${currentPage + 1}", modifier = Modifier.padding(16.dp))

        Spacer(modifier = Modifier.height(20.dp))

        // Adicionando o BirdPaginationDots
        BirdPaginationDotsLazy(
            count = totalPages,
            currentIndex = currentPage,
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Botões para simular a navegação no carrossel
        Row {
            Button(onClick = { if (currentPage > 0) currentPage-- }, enabled = currentPage > 0) {
                Text("Anterior")
            }
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Button(
                onClick = { if (currentPage < totalPages - 1) currentPage++ },
                enabled = currentPage < totalPages - 1
            ) {
                Text("Próximo")
            }
        }
    }
}