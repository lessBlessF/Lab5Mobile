package com.example.flightsearch.data.repository

import com.example.flightsearch.data.local.entities.Airport
import com.example.flightsearch.data.local.entities.Favorite
import kotlinx.coroutines.flow.Flow
import com.example.flightsearch.data.local.entities.FavoriteRoute

interface FlightRepository {
    fun searchAirports(query: String): Flow<List<Airport>>
    fun getDestinations(departureIata: String): Flow<List<Airport>>
    suspend fun getAirportByIata(iataCode: String): Airport?

    fun getAllFavorites(): Flow<List<Favorite>>
    suspend fun addFavorite(favorite: Favorite)
    suspend fun removeFavorite(favorite: Favorite)
    suspend fun getFavorite(departureCode: String, destinationCode: String): Favorite?
    fun getFavoriteRoutesWithNames(): Flow<List<FavoriteRoute>>
}