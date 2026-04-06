package com.example.flightsearch.data.repository

import com.example.flightsearch.data.local.AirportDao
import com.example.flightsearch.data.local.FavoriteDao
import com.example.flightsearch.data.local.entities.Airport
import com.example.flightsearch.data.local.entities.Favorite
import kotlinx.coroutines.flow.Flow
import com.example.flightsearch.data.local.entities.FavoriteRoute

class OfflineFlightRepository(
    private val airportDao: AirportDao,
    private val favoriteDao: FavoriteDao
) : FlightRepository {
    override fun searchAirports(query: String): Flow<List<Airport>> = airportDao.getAirportsByQuery(query)
    override fun getDestinations(departureIata: String): Flow<List<Airport>> = airportDao.getAllDestinations(departureIata)
    override suspend fun getAirportByIata(iataCode: String): Airport? = airportDao.getAirportByIata(iataCode)

    override fun getAllFavorites(): Flow<List<Favorite>> = favoriteDao.getAllFavorites()
    override suspend fun addFavorite(favorite: Favorite) = favoriteDao.insertFavorite(favorite)
    override suspend fun removeFavorite(favorite: Favorite) = favoriteDao.deleteFavorite(favorite)
    override suspend fun getFavorite(departureCode: String, destinationCode: String): Favorite? = favoriteDao.getFavorite(departureCode, destinationCode)
    override fun getFavoriteRoutesWithNames(): Flow<List<FavoriteRoute>> =
        favoriteDao.getFavoriteRoutesWithNames()
}