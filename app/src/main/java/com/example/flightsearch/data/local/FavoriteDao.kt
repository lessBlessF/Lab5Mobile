package com.example.flightsearch.data.local

import com.example.flightsearch.data.local.entities.FavoriteRoute
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flightsearch.data.local.entities.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    fun getAllFavorites(): Flow<List<Favorite>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favorite: Favorite)

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)

    @Query("SELECT * FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    suspend fun getFavorite(departureCode: String, destinationCode: String): Favorite?

    @Query("""
        SELECT f.id, 
               f.departure_code AS departureCode, 
               dep.name AS departureName, 
               f.destination_code AS destinationCode, 
               dest.name AS destinationName
        FROM favorite f
        INNER JOIN airport dep ON f.departure_code = dep.iata_code
        INNER JOIN airport dest ON f.destination_code = dest.iata_code
    """)
    fun getFavoriteRoutesWithNames(): Flow<List<FavoriteRoute>>

}