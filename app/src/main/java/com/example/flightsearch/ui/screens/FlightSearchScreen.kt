package com.example.flightsearch.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.data.local.entities.Airport
import com.example.flightsearch.data.local.entities.Favorite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchScreen(
    viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.Factory)
) {
    val query by viewModel.searchQuery.collectAsState()
    val autocompleteAirports by viewModel.autocompleteAirports.collectAsState()
    val selectedAirport by viewModel.selectedAirport.collectAsState()
    val destinationFlights by viewModel.destinationFlights.collectAsState()
    val favorites by viewModel.favoriteFlightsWithNames.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.updateQuery(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter departure airport") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            singleLine = true,
            shape = RoundedCornerShape(24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (query.isEmpty()) {
            Text("Favorite routes", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            if (favorites.isEmpty()) {
                Text("No favorite routes saved.", color = Color.Gray)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(favorites, key = { it.id }) { favorite ->
                        FlightCard(
                            departureCode = favorite.departureCode,
                            departureName = favorite.departureName,
                            destinationCode = favorite.destinationCode,
                            destinationName = favorite.destinationName,
                            isFavorite = true,
                            onFavoriteClick = { viewModel.toggleFavorite(favorite.departureCode, favorite.destinationCode) }
                        )
                    }
                }
            }
        } else if (selectedAirport == null) {
            LazyColumn {
                items(autocompleteAirports, key = { it.id }) { airport ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onAirportSelected(airport) }
                            .padding(vertical = 12.dp, horizontal = 8.dp)
                    ) {
                        Text(text = airport.iataCode, fontWeight = FontWeight.Bold, modifier = Modifier.width(50.dp))
                        Text(text = airport.name, maxLines = 1)
                    }
                }
            }
        } else {
            Text("Flights from ${selectedAirport!!.iataCode}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(destinationFlights, key = { it.id }) { destination ->
                    val isFav = favorites.any { it.departureCode == selectedAirport!!.iataCode && it.destinationCode == destination.iataCode }
                    FlightCard(
                        departureCode = selectedAirport!!.iataCode,
                        departureName = selectedAirport!!.name,
                        destinationCode = destination.iataCode,
                        destinationName = destination.name,
                        isFavorite = isFav,
                        onFavoriteClick = { viewModel.toggleFavorite(selectedAirport!!.iataCode, destination.iataCode) }
                    )
                }
            }
        }
    }
}

@Composable
fun FlightCard(
    departureCode: String,
    departureName: String,
    destinationCode: String,
    destinationName: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "DEPART", fontSize = 10.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = departureCode, fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp))
                    Text(text = departureName, fontSize = 14.sp, maxLines = 1)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "ARRIVE", fontSize = 10.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = destinationCode, fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp))
                    Text(text = destinationName, fontSize = 14.sp, maxLines = 1)
                }
            }
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFA66D00) else Color.Gray
                )
            }
        }
    }
}