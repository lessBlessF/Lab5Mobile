package com.example.flightsearch.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightSearchApplication
import com.example.flightsearch.data.local.entities.Airport
import com.example.flightsearch.data.local.entities.Favorite
import com.example.flightsearch.data.preferences.UserPreferencesRepository
import com.example.flightsearch.data.repository.FlightRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import com.example.flightsearch.data.local.entities.FavoriteRoute

class FlightSearchViewModel(
    private val flightRepository: FlightRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedAirport = MutableStateFlow<Airport?>(null)
    val selectedAirport = _selectedAirport.asStateFlow()

    init {
        viewModelScope.launch {
            val savedQuery = userPreferencesRepository.searchQuery.first()
            _searchQuery.value = savedQuery
        }
    }

    fun updateQuery(query: String) {
        _searchQuery.value = query
        _selectedAirport.value = null
        viewModelScope.launch {
            userPreferencesRepository.saveSearchQuery(query)
        }
    }

    fun onAirportSelected(airport: Airport) {
        _selectedAirport.value = airport
        _searchQuery.value = airport.iataCode
        viewModelScope.launch {
            userPreferencesRepository.saveSearchQuery(airport.iataCode)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val autocompleteAirports: StateFlow<List<Airport>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isNotBlank() && _selectedAirport.value == null) {
                flightRepository.searchAirports(query)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val destinationFlights: StateFlow<List<Airport>> = _selectedAirport
        .flatMapLatest { airport ->
            if (airport != null) {
                flightRepository.getDestinations(airport.iataCode)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteFlightsWithNames: StateFlow<List<FavoriteRoute>> = flightRepository.getFavoriteRoutesWithNames()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(departureCode: String, destinationCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingFavorite = flightRepository.getFavorite(departureCode, destinationCode)
            if (existingFavorite == null) {
                flightRepository.addFavorite(Favorite(departureCode = departureCode, destinationCode = destinationCode))
            } else {
                flightRepository.removeFavorite(existingFavorite)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FlightSearchApplication)
                FlightSearchViewModel(
                    flightRepository = application.container.flightRepository,
                    userPreferencesRepository = application.container.userPreferencesRepository
                )
            }
        }
    }
}