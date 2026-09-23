package com.example.luminascout.Data

data class ScoutLocation(
    val id: String,
    val name: String,
    val description: String,
    val photoScore: Int,
    val goldenHourTime: String,
    val distance: String,

    // GPS coordinates
    val latitude: Double,
    val longitude: Double,

    // Photographer scouting information
    val notes: String,
    val parkingDetails: String,
    val shootingAngle: String
)

class ScoutRepository {

    fun fetchLocations(): Result<List<ScoutLocation>> {
        return try {
            val capeTownLocations = listOf(
                ScoutLocation(
                    id = "1",
                    name = "Signal Hill Viewpoint",
                    description = "Panoramas & Sunset Views",
                    photoScore = 94,
                    goldenHourTime = "6:42 PM",
                    distance = "2.4 mi",
                    latitude = -33.9180,
                    longitude = 18.4035,
                    notes = "Great sunset viewpoint overlooking Cape Town.",
                    parkingDetails = "Parking available near the viewpoint.",
                    shootingAngle = "Face west toward the ocean and city."
                ),
                ScoutLocation(
                    id = "2",
                    name = "Bloubergstrand Beach",
                    description = "Iconic Table Mountain Reflections",
                    photoScore = 91,
                    goldenHourTime = "6:45 PM",
                    distance = "8.1 mi",
                    latitude = -33.7974,
                    longitude = 18.4586,
                    notes = "Good location for Table Mountain reflection shots.",
                    parkingDetails = "Street parking available near the beach.",
                    shootingAngle = "Face southeast toward Table Mountain."
                ),
                ScoutLocation(
                    id = "3",
                    name = "Kalk Bay Harbor",
                    description = "Coastal Waves & Lighthouse Shots",
                    photoScore = 88,
                    goldenHourTime = "6:38 PM",
                    distance = "14.2 mi",
                    latitude = -34.1299,
                    longitude = 18.4488,
                    notes = "Useful location for coastal and harbour photography.",
                    parkingDetails = "Public parking available around the harbour.",
                    shootingAngle = "Face toward the harbour and coastline."
                ),
                ScoutLocation(
                    id = "4",
                    name = "Kirstenbosch Canopy Walk",
                    description = "Forest & Mountain Backdrop",
                    photoScore = 86,
                    goldenHourTime = "6:40 PM",
                    distance = "5.7 mi",
                    latitude = -33.9881,
                    longitude = 18.4325,
                    notes = "Forest scenery with mountain backgrounds.",
                    parkingDetails = "Parking available at Kirstenbosch.",
                    shootingAngle = "Face toward the mountain backdrop."
                )
            )

            Result.success(capeTownLocations)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}