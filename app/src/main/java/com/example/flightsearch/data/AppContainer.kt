package com.example.flightsearch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.flightsearch.data.preferences.UserPreferencesRepository
import com.example.flightsearch.data.repository.FlightRepository
import com.example.flightsearch.data.repository.OfflineFlightRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "search_preferences"
)

interface AppContainer {
    val flightRepository: FlightRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val flightRepository: FlightRepository by lazy {
        OfflineFlightRepository(
            FlightDatabase.getDatabase(context).airportDao(),
            FlightDatabase.getDatabase(context).favoriteDao()
        )
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
}