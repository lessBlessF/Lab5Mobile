package com.example.flightsearch.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.flightsearch.ui.screens.FlightSearchScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchApp() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flight Search", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E5EAA)
                )
            )
        }
    ) { innerPadding ->
        Modifier.padding(innerPadding)
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
            FlightSearchScreen()
        }
    }
}