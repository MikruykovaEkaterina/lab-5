package com.example.lab_5.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab_5.data.Airport
import com.example.lab_5.data.Favorite

@Composable
fun FlightSearchScreen(viewModel: FlightSearchViewModel, modifier: Modifier = Modifier) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            placeholder = { Text("Enter departure airport") },
            singleLine = true,
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
        when (uiState) {
            is UiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Enter airport name or IATA code")
                }
            }
            is UiState.Suggestions -> {
                val airports = (uiState as UiState.Suggestions).airports
                LazyColumn {
                    items(airports) { airport ->
                        SuggestionItem(airport) {
                            viewModel.selectAirport(airport)
                            focusManager.clearFocus()
                        }
                    }
                }
            }
            is UiState.Flights -> {
                val state = uiState as UiState.Flights
                Text(
                    "Flights from ${state.departure.iata_code}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(8.dp)
                )
                LazyColumn {
                    items(state.destinations) { dest ->
                        val isFavorite = state.favorites.any { it.destination_code == dest.iata_code }
                        FlightItem(
                            departure = state.departure,
                            destination = dest,
                            isFavorite = isFavorite,
                            onFavoriteClick = {
                                if (isFavorite) {
                                    val fav = state.favorites.first { it.destination_code == dest.iata_code }
                                    viewModel.removeFavorite(fav, state.departure)
                                } else {
                                    viewModel.addFavorite(state.departure, dest)
                                }
                            }
                        )
                    }
                }
            }
            is UiState.Favorites -> {
                val favs = (uiState as UiState.Favorites).favorites
                Text(
                    "Favorite routes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(8.dp)
                )
                if (favs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No favorites yet")
                    }
                } else {
                    LazyColumn {
                        items(favs) { triple ->
                            val fav = triple.first
                            val dep = triple.second
                            val dest = triple.third
                            FavoriteItem(fav, dep, dest)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestionItem(airport: Airport, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(airport.iata_code, fontWeight = FontWeight.Bold, modifier = Modifier.width(48.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(airport.name)
    }
}

@Composable
fun FlightItem(
    departure: Airport,
    destination: Airport,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("DEPART", fontSize = 12.sp, color = Color.Gray)
                Text("${departure.iata_code}  ${departure.name}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("ARRIVE", fontSize = 12.sp, color = Color.Gray)
                Text("${destination.iata_code}  ${destination.name}", fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onFavoriteClick) {
                if (isFavorite) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFA000))
                } else {
                    Icon(Icons.Outlined.Star, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun FavoriteItem(fav: Favorite, dep: Airport?, dest: Airport?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("DEPART", fontSize = 12.sp, color = Color.Gray)
                Text("${fav.departure_code}  ${dep?.name ?: ""}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("ARRIVE", fontSize = 12.sp, color = Color.Gray)
                Text("${fav.destination_code}  ${dest?.name ?: ""}", fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFA000))
        }
    }
} 