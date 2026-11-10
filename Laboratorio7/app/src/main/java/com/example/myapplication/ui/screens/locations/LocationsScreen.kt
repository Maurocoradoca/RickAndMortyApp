package com.example.myapplication.ui.screens.locations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.Location
import com.example.myapplication.ui.components.EmptyLayout
import com.example.myapplication.ui.components.ErrorLayout
import com.example.myapplication.ui.components.LoadingLayout
import com.example.myapplication.viewmodel.LocationFilter
import com.example.myapplication.viewmodel.LocationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    onLocationClick: (Int) -> Unit,
    viewModel: LocationsViewModel = viewModel(factory = LocationsViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Locations") },
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
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LocationFilter.entries.forEach { option ->
                    FilterChip(
                        selected = uiState.filter == option,
                        onClick = { viewModel.setFilter(option) },
                        label = { Text(option.label) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when {
                    uiState.hasError -> ErrorLayout(
                        message = "Error al obtener listado de ubicaciones.\nIntenta de nuevo",
                        onRetryClick = { viewModel.loadLocations() }
                    )

                    uiState.isLoading -> LoadingLayout(
                        onClick = { viewModel.onLoadingClick() }
                    )

                    uiState.data.isEmpty() -> EmptyLayout(
                        message = when (uiState.filter) {
                            LocationFilter.ALL -> "No hay elementos para mostrar."
                            LocationFilter.FAVORITES -> "No tienes ubicaciones favoritas.\nToca el corazón de una ubicación para agregarla."
                            LocationFilter.NOT_FAVORITES -> "Todas las ubicaciones son favoritas."
                        }
                    )

                    else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.data, key = { it.id }) { location ->
                            LocationListItem(
                                location = location,
                                onClick = { onLocationClick(location.id) },
                                onFavoriteClick = { viewModel.toggleFavorite(location) }
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocationListItem(
    location: Location,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 16.dp, top = 6.dp, bottom = 6.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = location.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = location.type,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (location.isFavorite) {
                    Icons.Filled.Favorite
                } else {
                    Icons.Filled.FavoriteBorder
                },
                contentDescription = if (location.isFavorite) {
                    "Quitar de favoritas"
                } else {
                    "Marcar como favorita"
                },
                tint = if (location.isFavorite) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}