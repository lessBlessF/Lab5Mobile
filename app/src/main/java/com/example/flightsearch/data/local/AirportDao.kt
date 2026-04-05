package com.example.flightsearch.data.local

import androidx.room.Dao
import androidx.room.Query
import com.example.flightsearch.data.local.entities.Airport
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query(
        """
        SELECT * FROM airport 
        WHERE iata_code LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%' 
        ORDER BY passengers DESC
        """
    )
    fun getAirportsByQuery(query: String): Flow<List<Airport>>
    @Query("SELECT * FROM airport WHERE iata_code != :departureIata ORDER BY passengers DESC")
    fun getAllDestinations(departureIata: String): Flow<List<Airport>>
    @Query("SELECT * FROM airport WHERE iata_code = :iataCode")
    suspend fun getAirportByIata(iataCode: String): Airport?
}