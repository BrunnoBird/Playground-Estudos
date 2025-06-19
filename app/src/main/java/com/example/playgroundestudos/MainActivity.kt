package com.example.playgroundestudos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.playgroundestudos.ui.components.paginationDots.SimplePagerIndicator
import com.example.playgroundestudos.ui.components.paginationDots.internal.dot.DotAnimation
import com.example.playgroundestudos.ui.theme.PlaygroundEstudosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlaygroundEstudosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        // 1. (Opcional) Crie uma curva de animação totalmente customizada
                        val curvaSuperSuave = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)

                        // 2. Crie sua própria instância de `DotAnimation`
                        val animacaoCustomizada = DotAnimation(
                            // Animação para o TAMANHO (size): Rápida e com a curva padrão
                            sizeAnim = tween(
                                durationMillis = 1000, // 300ms de duração
                                easing = FastOutSlowInEasing // Curva padrão
                            ),

                            // Animação para a POSIÇÃO (offset): Mais lenta e com uma curva customizada "elástica"
                            offsetAnim = tween(
                                durationMillis = 3000, // 700ms de duração (mais lenta)
                                easing = curvaSuperSuave // Usando nossa curva especial
                            ),

                            // Animação para a COR (color): Bem rápida
                            colorAnim = tween(durationMillis = 1000) // Easing padrão é FastOutSlowInEasing
                        )

                        val totalPages = 7

                        // 1. O estado agora é um simples Int! Use rememberSaveable para manter o estado.
                        var currentIndex by rememberSaveable { mutableIntStateOf(0) }

                        // Conteúdo de exemplo que muda com o índice
                        val conteudos = List(totalPages) { index ->
                            "Conteúdo da Etapa ${index + 1}"
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // 2. Use o seu novo componente SimplePagerIndicator
                            SimplePagerIndicator(
                                modifier = Modifier.height(50.dp),
                                pageCount = totalPages,
                                currentIndex = currentIndex, // <-- Passe o estado aqui
                                orientation = Orientation.Vertical,
                                dotAnimation = animacaoCustomizada
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // 3. Botões que simplesmente manipulam o estado do currentIndex
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Button(
                                    onClick = { if (currentIndex > 0) currentIndex-- },
                                    enabled = currentIndex > 0 // Desabilita na primeira página
                                ) {
                                    Text("Anterior")
                                }
                                Button(
                                    onClick = { if (currentIndex < totalPages - 1) currentIndex++ },
                                    enabled = currentIndex < totalPages - 1 // Desabilita na última página
                                ) {
                                    Text("Próximo")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PlaygroundEstudosTheme {
        Greeting("Android")
    }
}