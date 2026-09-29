package com.example.myapplication.ui.screens.characterdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.CharacterDb
import com.example.myapplication.ui.components.DetailRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsScreen(characterId: Int, onBackClick: () -> Unit) {
    val character = remember(characterId) { CharacterDb().getCharacterById(characterId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Characters details") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text(
                            text = "<- Atrás",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = character.image,
                contentDescription = "Imagen de ${character.name}",
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .padding(16.dp)
            )

            Text(
                text = character.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            DetailRow(label = "Species:", value = character.species)
            DetailRow(label = "Status:", value = character.status)
            DetailRow(label = "Gender:", value = character.gender)
        }
    }
}