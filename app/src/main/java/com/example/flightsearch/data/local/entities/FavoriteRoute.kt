package com.example.flightsearch.data.local.entities

data class FavoriteRoute(
    val id: Int,
    val departureCode: String,
    val departureName: String,
    val destinationCode: String,
    val destinationName: String
)