package com.example.luminascout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luminascout.Data.ScoutLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    object Loading : HomeUiState

    data class Success(
        val locations: List<ScoutLocation>
    ) : HomeUiState

    data class Error(
        val message: String
    ) : HomeUiState
}

class HomeViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    val uiState: StateFlow<HomeUiState> =
        _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            try {
                val defaultLocations = listOf(

                    ScoutLocation(
                        id = "1",
                        name = "Signal Hill Viewpoint",
                        description = "Panoramas & Sunset Views",
                        photoScore = 94,
                        distance = "2.4 mi away",
                        goldenHourTime = "6:42 PM",
                        latitude = -33.9249,
                        longitude = 18.4241,
                        notes = "Panoramas and sunset views",
                        parkingDetails = "Street parking",
                        shootingAngle = "Wide angle toward the city"
                    ),

                    ScoutLocation(
                        id = "2",
                        name = "Kirstenbosch Canopy Walk",
                        description = "Forest & Mountain Backdrop",
                        photoScore = 86,
                        distance = "5.1 mi away",
                        goldenHourTime = "6:40 PM",
                        latitude = -33.9881,
                        longitude = 18.4324,
                        notes = "Forest and mountain backdrop",
                        parkingDetails = "Parking available",
                        shootingAngle = "Mountain-facing angle"
                    )
                )

                _uiState.value =
                    HomeUiState.Success(defaultLocations)

            } catch (e: Exception) {
                _uiState.value =
                    HomeUiState.Error(
                        e.localizedMessage
                            ?: "An unexpected error occurred"
                    )
            }
        }
    }
}