package com.example.playgroundestudos

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.playgroundestudos.ui.components.paginationDotsRow.DotsGroup
import com.example.playgroundestudos.ui.theme.PlaygroundEstudosTheme

@Composable
fun OnboardingScreen(modifier: Modifier = Modifier) {
    val totalPages = 5 // Example: 5 pages for onboarding
    var currentPage by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Onboarding Page: ${currentPage + 1}", modifier = Modifier.padding(16.dp))

        Spacer(modifier = Modifier.height(32.dp))

        DotsGroup(
            count = totalPages,
            selectedIndex = currentPage,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row {
            Button(
                onClick = { if (currentPage > 0) currentPage-- },
                enabled = currentPage > 0
            ) {
                Text("Previous")
            }
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Button(
                onClick = { if (currentPage < totalPages - 1) currentPage++ },
                enabled = currentPage < totalPages - 1
            ) {
                Text("Next")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (currentPage == totalPages - 1) {
            Button(onClick = { /* TODO: Handle onboarding completion */ }) {
                Text("Get Started")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    PlaygroundEstudosTheme {
        OnboardingScreen()
    }
}
