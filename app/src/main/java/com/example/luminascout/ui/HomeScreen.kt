package com.example.luminascout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminascout.AppSettings
import com.example.luminascout.Data.RetrofitClient
import com.example.luminascout.Data.ScoutLocation

@Composable
fun HomeScreen(
    userName: String,
    onNavigate: (String) -> Unit,
    onNextShootClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onLocationClick: (ScoutLocation) -> Unit = {}
) {

    // ---------------------------------------------------------
    // LIVE WEATHER DATA
    // ---------------------------------------------------------

    var temperature by remember { mutableStateOf<Double?>(null) }
    var windSpeed by remember { mutableStateOf<Double?>(null) }
    var cloudCover by remember { mutableStateOf<Double?>(null) }
    var rainChance by remember { mutableStateOf<Double?>(null) }

    // ---------------------------------------------------------
    // LIVE SOLAR DATA
    // ---------------------------------------------------------

    var goldenHourTime by remember { mutableStateOf("Loading...") }
    var sunsetTime by remember { mutableStateOf("Loading...") }

    var apiError by remember { mutableStateOf(false) }

    val temperatureUnit = AppSettings.temperatureUnit

    // ---------------------------------------------------------
    // GET LIVE API DATA
    // ---------------------------------------------------------

    LaunchedEffect(Unit) {

        try {

            val response =
                RetrofitClient.apiService.getCurrentWeather()

            // Current weather
            temperature =
                response.current_weather?.temperature

            windSpeed =
                response.current_weather?.windspeed

            // Hourly weather
            cloudCover =
                response.hourly?.cloud_cover?.firstOrNull()

            rainChance =
                response.hourly
                    ?.precipitation_probability
                    ?.firstOrNull()

            // -------------------------------------------------
            // SOLAR DATA
            // Same calculation used by WeatherScreen
            // -------------------------------------------------

            val sunsetRaw =
                response.daily?.sunset?.firstOrNull()

            if (sunsetRaw != null) {

                sunsetTime =
                    formatHomeSolarTime(sunsetRaw)

                // Golden hour = approximately
                // one hour before sunset
                goldenHourTime =
                    formatHomeSolarTime(
                        adjustHomeSolarTime(
                            sunsetRaw,
                            -60
                        )
                    )
            }

            apiError = false

        } catch (e: Exception) {

            apiError = true

            goldenHourTime = "--"
            sunsetTime = "--"
        }
    }

    // ---------------------------------------------------------
    // PHOTOGRAPHY CONDITIONS SCORE
    // Same logic as WeatherScreen
    // ---------------------------------------------------------

    val photographyScore = when {

        temperature == null ||
                windSpeed == null ->
            87

        windSpeed!! < 10 &&
                temperature!! in 15.0..30.0 ->
            90

        windSpeed!! < 20 ->
            75

        else ->
            55
    }

    // ---------------------------------------------------------
    // TEMPERATURE
    // Keep existing C/F behaviour
    // ---------------------------------------------------------

    val displayTemperature =
        temperature?.let {

            if (temperatureUnit == "°F") {

                (it * 9 / 5) + 32

            } else {

                it
            }
        }

    // ---------------------------------------------------------
    // MAIN SCREEN
    // ---------------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp)
    ) {

        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = "Good evening",
                    style =
                        MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme.colorScheme
                            .onBackground
                            .copy(alpha = 0.6f)
                )

                Text(
                    text = userName.ifBlank {
                        "Scout"
                    },
                    style =
                        MaterialTheme.typography
                            .headlineMedium,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        MaterialTheme.colorScheme
                            .onBackground
                )
            }

            IconButton(
                onClick = {
                    onNotificationClick()
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.surface
                    )
            ) {

                BadgedBox(
                    badge = {
                        Badge(
                            containerColor =
                                MaterialTheme.colorScheme
                                    .primary
                        )
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Notifications,
                        contentDescription =
                            "Notifications",
                        tint =
                            MaterialTheme.colorScheme
                                .onSurface
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // -----------------------------------------------------
        // NEXT SHOOT
        // -----------------------------------------------------

        Text(
            text = "NEXT SHOOT",
            style =
                MaterialTheme.typography.labelMedium,
            fontWeight =
                FontWeight.Bold,
            color =
                MaterialTheme.colorScheme
                    .onBackground
                    .copy(alpha = 0.5f),
            letterSpacing = 1.sp
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Card(
            shape =
                RoundedCornerShape(16.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme
                            .surface
                ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onNextShootClick()
                }
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint =
                                MaterialTheme.colorScheme
                                    .primary,
                            modifier =
                                Modifier.size(16.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(6.dp)
                        )

                        Text(
                            text = "GOLDEN HOUR",
                            fontSize = 12.sp,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                MaterialTheme.colorScheme
                                    .primary
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            "Signal Hill Viewpoint",
                        style =
                            MaterialTheme.typography
                                .titleMedium,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    // REAL-TIME GOLDEN HOUR
                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Schedule,
                            contentDescription = null,
                            modifier =
                                Modifier.size(14.dp),
                            tint =
                                MaterialTheme.colorScheme
                                    .onSurface
                                    .copy(alpha = 0.6f)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(4.dp)
                        )

                        Text(
                            text =
                                if (apiError) {
                                    "Weather unavailable"
                                } else {
                                    goldenHourTime
                                },
                            fontSize = 12.sp,
                            fontWeight =
                                FontWeight.Medium,
                            color =
                                MaterialTheme.colorScheme
                                    .onSurface
                                    .copy(alpha = 0.6f)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(12.dp)
                        )

                        Icon(
                            imageVector =
                                Icons.Default.Thermostat,
                            contentDescription = null,
                            modifier =
                                Modifier.size(14.dp),
                            tint =
                                MaterialTheme.colorScheme
                                    .onSurface
                                    .copy(alpha = 0.6f)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(4.dp)
                        )

                        Text(
                            text =
                                displayTemperature?.let {
                                    "${"%.1f".format(it)}$temperatureUnit"
                                } ?: "--$temperatureUnit",
                            fontSize = 12.sp,
                            color =
                                MaterialTheme.colorScheme
                                    .onSurface
                                    .copy(alpha = 0.6f)
                        )
                    }
                }

                IconButton(
                    onClick = {
                        onNextShootClick()
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme
                                .primary
                                .copy(alpha = 0.2f)
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.ChevronRight,
                        contentDescription =
                            "Details",
                        tint =
                            MaterialTheme.colorScheme
                                .primary
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // -----------------------------------------------------
        // PHOTO SCORE + WEATHER
        // -----------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            Card(
                shape =
                    RoundedCornerShape(16.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme
                                .surface
                    ),
                modifier =
                    Modifier.weight(1f)
            ) {

                Column(
                    modifier =
                        Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Photo Score",
                        fontSize = 12.sp,
                        color =
                            MaterialTheme.colorScheme
                                .onSurface
                                .copy(alpha = 0.6f)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text =
                                photographyScore.toString(),
                            fontSize = 28.sp,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                MaterialTheme.colorScheme
                                    .primary
                        )

                        Spacer(
                            modifier =
                                Modifier.width(12.dp)
                        )

                        Column {

                            Text(
                                text = when {
                                    photographyScore >= 85 ->
                                        "Excellent"

                                    photographyScore >= 70 ->
                                        "Good"

                                    else ->
                                        "Fair"
                                },
                                fontSize = 13.sp,
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    Color(0xFF4CAF50)
                            )

                            Text(
                                text =
                                    if (windSpeed != null) {
                                        "Live wind conditions"
                                    } else {
                                        "Loading weather..."
                                    },
                                fontSize = 10.sp,
                                color =
                                    MaterialTheme.colorScheme
                                        .onSurface
                                        .copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            Card(
                shape =
                    RoundedCornerShape(16.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme
                                .surface
                    ),
                modifier =
                    Modifier.weight(1f)
            ) {

                Column(
                    modifier =
                        Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Weather",
                        fontSize = 12.sp,
                        color =
                            MaterialTheme.colorScheme
                                .onSurface
                                .copy(alpha = 0.6f)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.WbSunny,
                            contentDescription = null,
                            tint =
                                MaterialTheme.colorScheme
                                    .primary,
                            modifier =
                                Modifier.size(20.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(6.dp)
                        )

                        Text(
                            text =
                                displayTemperature?.let {
                                    "${"%.1f".format(it)}$temperatureUnit"
                                } ?: "--",
                            fontSize = 18.sp,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            if (windSpeed != null) {
                                "Wind ${windSpeed!!.toInt()} km/h"
                            } else {
                                "Loading..."
                            },
                        fontSize = 10.sp,
                        color =
                            MaterialTheme.colorScheme
                                .onSurface
                                .copy(alpha = 0.5f)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // -----------------------------------------------------
        // LIVE CONDITIONS
        // -----------------------------------------------------

        Text(
            text = "LIVE CONDITIONS",
            style =
                MaterialTheme.typography.labelMedium,
            fontWeight =
                FontWeight.Bold,
            color =
                MaterialTheme.colorScheme
                    .onBackground
                    .copy(alpha = 0.5f),
            letterSpacing = 1.sp
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            HomeWeatherCard(
                title = "Cloud",
                value =
                    cloudCover?.let {
                        "${it.toInt()}%"
                    } ?: "--",
                modifier =
                    Modifier.weight(1f)
            )

            HomeWeatherCard(
                title = "Rain",
                value =
                    rainChance?.let {
                        "${it.toInt()}%"
                    } ?: "--",
                modifier =
                    Modifier.weight(1f)
            )

            HomeWeatherCard(
                title = "Wind",
                value =
                    windSpeed?.let {
                        "${it.toInt()} km/h"
                    } ?: "--",
                modifier =
                    Modifier.weight(1f)
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // -----------------------------------------------------
        // QUICK ACTIONS
        // -----------------------------------------------------

        Text(
            text = "QUICK ACTIONS",
            style =
                MaterialTheme.typography.labelMedium,
            fontWeight =
                FontWeight.Bold,
            color =
                MaterialTheme.colorScheme
                    .onBackground
                    .copy(alpha = 0.5f),
            letterSpacing = 1.sp
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            QuickActionButton(
                title = "Plan Shoot",
                icon =
                    Icons.Default.CalendarMonth,
                onClick = {
                    onNavigate("plan")
                },
                modifier =
                    Modifier.weight(1f)
            )

            QuickActionButton(
                title = "Scout",
                icon = Icons.Default.Map,
                onClick = {
                    onNavigate("scout")
                },
                modifier =
                    Modifier.weight(1f)
            )

            QuickActionButton(
                title = "Weather",
                icon = Icons.Default.Cloud,
                onClick = {
                    onNavigate("weather")
                },
                modifier =
                    Modifier.weight(1f)
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // -----------------------------------------------------
        // SAVED LOCATIONS
        // -----------------------------------------------------

        Text(
            text = "SAVED LOCATIONS",
            style =
                MaterialTheme.typography.labelMedium,
            fontWeight =
                FontWeight.Bold,
            color =
                MaterialTheme.colorScheme
                    .onBackground
                    .copy(alpha = 0.5f),
            letterSpacing = 1.sp
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        val locations = listOf(

            ScoutLocation(
                id = "1",
                name = "Signal Hill Viewpoint",
                description =
                    "Panoramas & Sunset Views",
                photoScore = 94,
                distance = "2.4 mi away",
                goldenHourTime =
                    goldenHourTime,
                latitude = -33.9249,
                longitude = 18.4241,
                notes =
                    "Panoramas and sunset views",
                parkingDetails =
                    "Street parking",
                shootingAngle =
                    "Wide angle toward the city"
            ),

            ScoutLocation(
                id = "2",
                name = "Kirstenbosch Canopy Walk",
                description =
                    "Forest & Mountain Backdrop",
                photoScore = 86,
                distance = "5.1 mi away",
                goldenHourTime =
                    goldenHourTime,
                latitude = -33.9881,
                longitude = 18.4324,
                notes =
                    "Forest and mountain backdrop",
                parkingDetails =
                    "Parking available",
                shootingAngle =
                    "Mountain-facing angle"
            )
        )

        locations.forEach { location ->

            SavedLocationCard(
                location = location,
                onClick = {
                    onLocationClick(location)
                }
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}

// =============================================================
// HOME WEATHER CARD
// =============================================================

@Composable
private fun HomeWeatherCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        shape =
            RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            )
    ) {

        Column(
            modifier =
                Modifier.padding(12.dp)
        ) {

            Text(
                text = title,
                fontSize = 11.sp,
                color =
                    MaterialTheme.colorScheme
                        .onSurface
                        .copy(alpha = 0.6f)
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}

// =============================================================
// SAVED LOCATION CARD
// =============================================================

@Composable
fun SavedLocationCard(
    location: ScoutLocation,
    onClick: () -> Unit = {}
) {

    Card(
        shape =
            RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = location.name,
                    fontWeight =
                        FontWeight.Bold,
                    fontSize = 15.sp
                )

                Text(
                    text = location.description,
                    fontSize = 12.sp,
                    color =
                        MaterialTheme.colorScheme
                            .onSurface
                            .copy(alpha = 0.5f)
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                // REAL-TIME GOLDEN HOUR
                Text(
                    text =
                        "Golden Hour: ${location.goldenHourTime}",
                    fontSize = 11.sp,
                    color =
                        MaterialTheme.colorScheme
                            .primary
                )
            }

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Star,
                    contentDescription = null,
                    tint =
                        MaterialTheme.colorScheme
                            .primary,
                    modifier =
                        Modifier.size(16.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(4.dp)
                )

                Text(
                    text =
                        location.photoScore.toString(),
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        MaterialTheme.colorScheme
                            .primary
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Icon(
                    imageVector =
                        Icons.Default.ChevronRight,
                    contentDescription =
                        "View location",
                    tint =
                        MaterialTheme.colorScheme
                            .onSurface
                            .copy(alpha = 0.5f),
                    modifier =
                        Modifier.size(18.dp)
                )
            }
        }
    }
}

// =============================================================
// QUICK ACTION BUTTON
// =============================================================

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        shape =
            RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            ),
        modifier =
            modifier.clickable {
                onClick()
            }
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = icon,
                contentDescription = title,
                tint =
                    MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight =
                    FontWeight.Medium
            )
        }
    }
}

// =============================================================
// SOLAR TIME HELPERS
// These mirror the WeatherScreen calculation.
// =============================================================

private fun formatHomeSolarTime(
    rawTime: String
): String {

    return try {

        val timePart =
            rawTime.substringAfter("T")

        val hour =
            timePart
                .substringBefore(":")
                .toInt()

        val minute =
            timePart
                .substringAfter(":")
                .take(2)
                .toInt()

        val period =
            if (hour >= 12) {
                "PM"
            } else {
                "AM"
            }

        val displayHour =
            when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }

        String.format(
            "%d:%02d %s",
            displayHour,
            minute,
            period
        )

    } catch (e: Exception) {

        "--"
    }
}

private fun adjustHomeSolarTime(
    rawTime: String,
    minutesToAdd: Int
): String {

    return try {

        val datePart =
            rawTime.substringBefore("T")

        val timePart =
            rawTime.substringAfter("T")

        val hour =
            timePart
                .substringBefore(":")
                .toInt()

        val minute =
            timePart
                .substringAfter(":")
                .take(2)
                .toInt()

        var totalMinutes =
            hour * 60 +
                    minute +
                    minutesToAdd

        totalMinutes %= 1440

        if (totalMinutes < 0) {
            totalMinutes += 1440
        }

        val newHour =
            totalMinutes / 60

        val newMinute =
            totalMinutes % 60

        String.format(
            "%sT%02d:%02d",
            datePart,
            newHour,
            newMinute
        )

    } catch (e: Exception) {

        rawTime
    }
}