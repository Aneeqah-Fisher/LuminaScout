package com.example.luminascout.ui

import com.example.luminascout.R
import androidx.compose.ui.platform.LocalContext

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.example.luminascout.Data.LocationSearchApi
import com.example.luminascout.Data.SearchedLocation
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.luminascout.AppSettings
import com.example.luminascout.GoldenHourReminderScheduler
import com.example.luminascout.Data.RetrofitClient
import androidx.compose.ui.res.stringResource


// -------------------------------------------------------------
// FORECAST DATA
// -------------------------------------------------------------

private data class ForecastData(
    val day: String,
    val maxTemperature: Double,
    val minTemperature: Double?,
    val weatherCode: Int
)

// -------------------------------------------------------------
// WEATHER SCREEN
// -------------------------------------------------------------

@Composable
fun WeatherScreen() {

    // ---------------------------------------------------------
    // LIVE WEATHER DATA
    // ---------------------------------------------------------

    var temperature by remember { mutableStateOf<Double?>(null) }
    var windSpeed by remember { mutableStateOf<Double?>(null) }
    var weatherCode by remember { mutableStateOf<Int?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    var cloudCover by remember { mutableStateOf<Double?>(null) }
    var rainChance by remember { mutableStateOf<Double?>(null) }

    // Location is selected independently of the weather data.
    var selectedLat by remember { mutableStateOf(-33.9249) }
    var selectedLon by remember { mutableStateOf(18.4241) }
    var locationLabel by remember { mutableStateOf("Cape Town (default)") }
    var usingPhoneLocation by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<SearchedLocation>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val fusedLocation = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }

    fun usePhoneLocation() {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationError = "Location permission is needed to use your position."
            return
        }
        locationError = null
        try {
            fusedLocation.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        selectedLat = location.latitude
                        selectedLon = location.longitude
                        locationLabel = "My current location"
                        usingPhoneLocation = true
                        searchText = ""
                        searchResults = emptyList()
                    } else {
                        locationError = "Couldn't get GPS location. Enable Location on your phone and try again."
                    }
                }
                .addOnFailureListener {
                    locationError = "Couldn't get GPS location. Please try again."
                }
        } catch (_: SecurityException) {
            locationError = "Location permission is required."
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) usePhoneLocation()
        else locationError = "Location permission denied. You can still search for an area."
    }

    fun requestPhoneLocation() {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) usePhoneLocation()
        else permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    // Automatically try GPS when the Weather tab first opens.
    LaunchedEffect(Unit) { requestPhoneLocation() }

    // Sunrise and sunset reminders always use Cape Town, regardless of
    // the location being viewed or searched on this Weather screen.
    // The scheduler fetches Cape Town sun times separately.
    LaunchedEffect(Unit) {
        GoldenHourReminderScheduler.scheduleCapeTown(context)
    }

    LaunchedEffect(searchText) {
        searchResults = emptyList()
        if (searchText.trim().length < 2) {
            searching = false
            return@LaunchedEffect
        }
        delay(350) // Avoid making a network request for every keystroke.
        searching = true
        try {
            searchResults = LocationSearchApi.service
                .searchLocations(searchText.trim()).results.orEmpty()
            locationError = null
        } catch (_: Exception) {
            locationError = "Couldn't search locations. Check your internet connection."
        } finally {
            searching = false
        }
    }


    // ---------------------------------------------------------
    // SOLAR DATA
    // ---------------------------------------------------------

    var sunriseTime by remember { mutableStateOf("Loading...") }
    var goldenHourTime by remember { mutableStateOf("Loading...") }
    var sunsetTime by remember { mutableStateOf("Loading...") }
    var blueHourTime by remember { mutableStateOf("Loading...") }

    // ---------------------------------------------------------
    // 5-DAY FORECAST
    // ---------------------------------------------------------

    var forecastDays by remember {
        mutableStateOf<List<ForecastData>>(emptyList())
    }

    var apiError by remember { mutableStateOf(false) }

    val temperatureUnit = AppSettings.temperatureUnit

    // ---------------------------------------------------------
    // GET LIVE API DATA
    // ---------------------------------------------------------

    LaunchedEffect(selectedLat, selectedLon) {

        // Clear old location's values while the new forecast loads.
        temperature = null
        windSpeed = null
        cloudCover = null
        rainChance = null
        sunriseTime = "Loading..."
        goldenHourTime = "Loading..."
        sunsetTime = "Loading..."
        blueHourTime = "Loading..."
        forecastDays = emptyList()
        apiError = false

        try {

            val response =
                RetrofitClient.apiService.getCurrentWeather(
                    lat = selectedLat, lon = selectedLon
                )

            // -------------------------------------------------
            // CURRENT WEATHER
            // -------------------------------------------------

            temperature =
                response.current_weather?.temperature

            windSpeed =
                response.current_weather?.windspeed

            weatherCode =
                response.current_weather?.weathercode

            // -------------------------------------------------
            // HOURLY WEATHER
            // -------------------------------------------------

            cloudCover =
                response.hourly
                    ?.cloud_cover
                    ?.firstOrNull()

            rainChance =
                response.hourly
                    ?.precipitation_probability
                    ?.firstOrNull()

            // -------------------------------------------------
            // SOLAR TIMES
            // -------------------------------------------------

            val sunriseRaw =
                response.daily
                    ?.sunrise
                    ?.firstOrNull()

            val sunsetRaw =
                response.daily
                    ?.sunset
                    ?.firstOrNull()

            if (
                sunriseRaw != null &&
                sunsetRaw != null
            ) {
                sunriseTime =
                    formatSolarTime(sunriseRaw)

                sunsetTime =
                    formatSolarTime(sunsetRaw)

                // Golden hour:
                // approximately one hour before sunset
                goldenHourTime =
                    formatSolarTime(
                        adjustSolarTime(
                            sunsetRaw,
                            -60
                        )
                    )

                // Blue hour:
                // approximately 30 minutes after sunset
                blueHourTime =
                    formatSolarTime(
                        adjustSolarTime(
                            sunsetRaw,
                            30
                        )
                    )
            }

            // -------------------------------------------------
            // 5-DAY FORECAST
            // -------------------------------------------------

            val daily = response.daily

            val forecastDates =
                daily?.time

            val forecastWeatherCodes =
                daily?.weathercode

            val forecastMaxTemperatures =
                daily?.temperature_2m_max

            val forecastMinTemperatures =
                daily?.temperature_2m_min

            if (
                forecastDates != null &&
                forecastWeatherCodes != null &&
                forecastMaxTemperatures != null
            ) {

                forecastDays =
                    forecastDates.indices
                        .take(5)
                        .mapNotNull { index ->

                            val date =
                                forecastDates.getOrNull(index)

                            val code =
                                forecastWeatherCodes
                                    .getOrNull(index)

                            val maxTemperature =
                                forecastMaxTemperatures
                                    .getOrNull(index)

                            if (
                                date != null &&
                                code != null &&
                                maxTemperature != null
                            ) {

                                ForecastData(
                                    day = formatForecastDay(
                                        date,
                                        index
                                    ),
                                    maxTemperature =
                                        maxTemperature,
                                    minTemperature = forecastMinTemperatures?.getOrNull(index),
                                    weatherCode = code
                                )

                            } else {

                                null
                            }
                        }
            }

            apiError = false

        } catch (e: Exception) {

            apiError = true

            temperature = null
            windSpeed = null
            weatherCode = null
            cloudCover = null
            rainChance = null

            sunriseTime = "--"
            goldenHourTime = "--"
            sunsetTime = "--"
            blueHourTime = "--"

            forecastDays = emptyList()
        }
    }

    // ---------------------------------------------------------
    // PHOTOGRAPHY CONDITIONS SCORE
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
    // COLORS
    // ---------------------------------------------------------

    val darkBackground =
        Color(0xFF0F141C)

    val cardBackground =
        Color(0xFF1B222D)

    val accentOrange =
        Color(0xFFFF9800)

    val accentGreen =
        Color(0xFF2ECC71)

    val accentBlue =
        Color(0xFF4A90E2)

    val textMuted =
        Color(0xFF8C93A0)

    // ---------------------------------------------------------
    // MAIN SCREEN
    // ---------------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
    ) {

        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = stringResource(R.string.ls_weather_light),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Outlined.Place,
                        contentDescription = null,
                        tint = textMuted,
                        modifier =
                            Modifier.size(14.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Text(
                        text =
                            locationLabel,
                        color = textMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Column(
                horizontalAlignment =
                    Alignment.End
            ) {

                Text(
                    text =
                        if (apiError) {
                            stringResource(R.string.ls_api_error)
                        } else {
                            stringResource(R.string.ls_updated)
                        },
                    color = textMuted,
                    fontSize = 10.sp
                )

                Text(
                    text =
                        if (apiError) {
                            stringResource(R.string.ls_check_connection)
                        } else {
                            stringResource(R.string.ls_live_data)
                        },
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight =
                        FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Location selector: search or use the phone's GPS.
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text(stringResource(R.string.ls_search_city_or_area)) },
            placeholder = { Text("e.g. Stellenbosch, Durban") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = accentOrange,
                focusedLabelColor = accentOrange,
                unfocusedLabelColor = textMuted,
                unfocusedBorderColor = textMuted
            )
        )
        TextButton(onClick = { requestPhoneLocation() }) {
            Icon(Icons.Outlined.Place, contentDescription = null, tint = accentOrange)
            Spacer(Modifier.width(6.dp))
            Text(stringResource(R.string.ls_use_my_current_location), color = accentOrange)
        }
        if (searching) CircularProgressIndicator(
            modifier = Modifier.size(22.dp), color = accentOrange
        )
        if (searchResults.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    searchResults.forEach { place ->
                        Text(
                            text = listOfNotNull(place.name, place.admin1, place.country)
                                .distinct().joinToString(", "),
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                                .clickable {
                                    selectedLat = place.latitude
                                    selectedLon = place.longitude
                                    locationLabel = listOfNotNull(place.name, place.country)
                                        .joinToString(", ")
                                    usingPhoneLocation = false
                                    searchText = ""
                                    searchResults = emptyList()
                                }
                                .padding(12.dp)
                        )
                        HorizontalDivider(color = textMuted.copy(alpha = 0.2f))
                    }
                }
            }
        }
        locationError?.let { Text(it, color = Color(0xFFFFB74D), fontSize = 12.sp) }

        Spacer(modifier = Modifier.height(16.dp))

        // -----------------------------------------------------
        // PHOTOGRAPHY CONDITIONS SCORE
        // -----------------------------------------------------

        Card(
            shape =
                RoundedCornerShape(16.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        cardBackground
                ),
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Row(
                modifier =
                    Modifier.padding(16.dp),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier =
                        Modifier.size(80.dp),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Canvas(
                        modifier =
                            Modifier.fillMaxSize()
                    ) {

                        val strokeWidth =
                            8.dp.toPx()

                        drawArc(
                            color =
                                Color.White.copy(
                                    alpha = 0.1f
                                ),
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style =
                                Stroke(
                                    width =
                                        strokeWidth,
                                    cap =
                                        StrokeCap.Round
                                )
                        )

                        drawArc(
                            color =
                                accentOrange,
                            startAngle = 135f,
                            sweepAngle =
                                270f *
                                        (photographyScore /
                                                100f),
                            useCenter = false,
                            style =
                                Stroke(
                                    width =
                                        strokeWidth,
                                    cap =
                                        StrokeCap.Round
                                )
                        )
                    }

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text =
                                "$photographyScore",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text = "/ 100",
                            color = textMuted,
                            fontSize = 9.sp
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(16.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(
                            modifier =
                                Modifier
                                    .size(8.dp)
                                    .clip(
                                        CircleShape
                                    )
                                    .background(
                                        accentGreen
                                    )
                        )

                        Spacer(
                            modifier =
                                Modifier.width(6.dp)
                        )

                        Text(
                            text = when {

                                photographyScore >= 85 ->
                                    stringResource(R.string.ls_excellent_conditions)

                                photographyScore >= 70 ->
                                    stringResource(R.string.ls_good_conditions)

                                else ->
                                    stringResource(R.string.ls_fair_conditions)
                            },
                            color = accentGreen,
                            fontSize = 14.sp,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            if (apiError) {

                                "Weather information could not be loaded. Please check your internet connection."

                            } else {

                                "Photography score based on the current temperature and wind conditions."
                            },
                        color = textMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // -----------------------------------------------------
        // LIGHT TIMELINE
        // -----------------------------------------------------

        Card(
            shape =
                RoundedCornerShape(16.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        cardBackground
                ),
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Column(
                modifier =
                    Modifier.padding(16.dp)
            ) {

                Text(
                    text = stringResource(R.string.ls_light_timeline),
                    color = textMuted,
                    fontSize = 11.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Canvas(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                ) {

                    val width = size.width
                    val height = size.height

                    val arcPathColor =
                        Brush.horizontalGradient(
                            colors =
                                listOf(
                                    accentBlue,
                                    accentOrange,
                                    Color(0xFF6C5CE7)
                                )
                        )

                    drawArc(
                        brush = arcPathColor,
                        startAngle = 190f,
                        sweepAngle = 160f,
                        useCenter = false,
                        topLeft =
                            Offset(
                                width * 0.05f,
                                10.dp.toPx()
                            ),
                        size =
                            Size(
                                width * 0.9f,
                                height * 1.4f
                            ),
                        style =
                            Stroke(
                                width =
                                    3.dp.toPx(),
                                cap =
                                    StrokeCap.Round
                            )
                    )

                    val sunX =
                        width * 0.62f

                    val sunY =
                        height * 0.22f

                    drawCircle(
                        color =
                            accentOrange.copy(
                                alpha = 0.3f
                            ),
                        radius =
                            12.dp.toPx(),
                        center =
                            Offset(
                                sunX,
                                sunY
                            )
                    )

                    drawCircle(
                        color =
                            accentOrange,
                        radius =
                            7.dp.toPx(),
                        center =
                            Offset(
                                sunX,
                                sunY
                            )
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    LightTimelineItem(
                        label =stringResource(R.string.ls_sunrise),
                        time = sunriseTime,
                        highlightColor =
                            accentBlue
                    )

                    LightTimelineItem(
                        label = stringResource(R.string.ls_golden_hr),
                        time = goldenHourTime,
                        highlightColor =
                            accentOrange
                    )

                    LightTimelineItem(
                        label = stringResource(R.string.ls_sunset),
                        time = sunsetTime,
                        highlightColor =
                            Color(0xFFFF5722)
                    )

                    LightTimelineItem(
                        label = stringResource(R.string.ls_blue_hr),
                        time = blueHourTime,
                        highlightColor =
                            Color(0xFF7C4DFF)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // -----------------------------------------------------
        // WEATHER METRICS
        // -----------------------------------------------------

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            WeatherMetricCard(
                modifier =
                    Modifier.weight(1f),
                icon =
                    Icons.Outlined.Thermostat,
                iconTint =
                    accentOrange,
                title =
                    stringResource(R.string.ls_temperature),
                value =
                    if (temperature != null) {

                        val displayTemperature =
                            if (
                                temperatureUnit ==
                                "°F"
                            ) {

                                (temperature!! * 9 / 5) + 32

                            } else {

                                temperature!!
                            }

                        "${"%.1f".format(
                            displayTemperature
                        )}$temperatureUnit"

                    } else {

                        "--$temperatureUnit"
                    },
                subtext =
                    if (temperature != null) {

                        "Live API temperature"

                    } else {

                        "Loading..."
                    },
                cardBg =
                    cardBackground,
                textMuted =
                    textMuted
            )

            WeatherMetricCard(
                modifier =
                    Modifier.weight(1f),
                icon =
                    Icons.Outlined.Air,
                iconTint =
                    accentBlue,
                title =
                    "Wind",
                value =
                    if (windSpeed != null) {

                        "${"%.1f".format(
                            windSpeed
                        )} km/h"

                    } else {

                        "-- km/h"
                    },
                subtext =
                    if (windSpeed != null) {

                        "Live API wind speed"

                    } else {

                        "Loading..."
                    },
                cardBg =
                    cardBackground,
                textMuted =
                    textMuted
            )
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        // -----------------------------------------------------
        // CLOUD COVER
        // -----------------------------------------------------

        WeatherMetricCard(
            modifier =
                Modifier.fillMaxWidth(),
            icon =
                Icons.Outlined.Cloud,
            iconTint =
                textMuted,
            title =
                stringResource(R.string.ls_cloud_cover),
            value =
                if (cloudCover != null) {

                    "${cloudCover!!.toInt()}%"

                } else {

                    "--%"
                },
            subtext =
                if (cloudCover != null) {

                    "Live API cloud cover"

                } else {

                    "Loading..."
                },
            cardBg =
                cardBackground,
            textMuted =
                textMuted
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        // -----------------------------------------------------
        // RAIN CHANCE
        // -----------------------------------------------------

        WeatherMetricCard(
            modifier =
                Modifier.fillMaxWidth(),
            icon =
                Icons.Outlined.WaterDrop,
            iconTint =
                accentBlue,
            title =
                stringResource(R.string.ls_rain_chance),
            value =
                if (rainChance != null) {

                    "${rainChance!!.toInt()}%"

                } else {

                    "--%"
                },
            subtext =
                if (rainChance != null) {

                    "Live API rain probability"

                } else {

                    "Loading..."
                },
            cardBg =
                cardBackground,
            textMuted =
                textMuted
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        // -----------------------------------------------------
        // 5-DAY FORECAST — swipeable, individual cards
        // -----------------------------------------------------

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.ls_weather_outlook),
                            color = textMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.ls_next_5_days),
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(text = "Swipe →", color = accentOrange, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (forecastDays.isEmpty()) {
                    Text(
                        text = if (apiError) stringResource(R.string.ls_forecast_unavailable) else stringResource(R.string.ls_loading_forecast),
                        color = textMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        forecastDays.forEachIndexed { index, forecast ->
                            ForecastDayItem(
                                day = forecast.day,
                                icon = weatherIconForCode(forecast.weatherCode),
                                temp = formatForecastTemperature(
                                    forecast.maxTemperature, temperatureUnit
                                ),
                                lowTemp = forecast.minTemperature?.let {
                                    formatForecastTemperature(it, temperatureUnit)
                                },
                                description = forecastDescription(forecast.weatherCode),
                                isSunny = isSunnyWeatherCode(forecast.weatherCode),
                                isToday = index == 0,
                                accentOrange = accentOrange,
                                textMuted = textMuted
                            )
                        }
                    }
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )
    }
}

// -------------------------------------------------------------
// SOLAR TIME HELPERS
// -------------------------------------------------------------

private fun formatSolarTime(
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

// -------------------------------------------------------------
// ADJUST SOLAR TIME
// -------------------------------------------------------------

private fun adjustSolarTime(
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

// -------------------------------------------------------------
// FORECAST DAY
// -------------------------------------------------------------

private fun formatForecastDay(
    date: String,
    index: Int
): String {

    return try {

        if (index == 0) {
            "Today"
        } else {

            val month =
                date
                    .substring(5, 7)
                    .toInt()

            val day =
                date
                    .substring(8, 10)
                    .toInt()

            "$day/$month"
        }

    } catch (e: Exception) {

        "--"
    }
}

// -------------------------------------------------------------
// FORECAST TEMPERATURE
// -------------------------------------------------------------

private fun formatForecastTemperature(
    temperature: Double,
    temperatureUnit: String
): String {

    val displayTemperature =
        if (temperatureUnit == "°F") {

            (temperature * 9 / 5) + 32

        } else {

            temperature
        }

    return "${"%.0f".format(
        displayTemperature
    )}$temperatureUnit"
}

// -------------------------------------------------------------
// WEATHER ICON
// -------------------------------------------------------------

private fun weatherIconForCode(
    weatherCode: Int
): ImageVector {

    return when (weatherCode) {

        // Clear
        0, 1 ->
            Icons.Outlined.WbSunny

        // Partly cloudy / cloudy
        2, 3 ->
            Icons.Outlined.Cloud

        // Fog
        45, 48 ->
            Icons.Outlined.Cloud

        // Rain
        51, 53, 55,
        56, 57,
        61, 63, 65,
        66, 67,
        80, 81, 82 ->
            Icons.Outlined.Grain

        // Snow / thunderstorm
        71, 73, 75, 77,
        85, 86,
        95, 96, 99 ->
            Icons.Outlined.Grain

        else ->
            Icons.Outlined.Cloud
    }
}

// -------------------------------------------------------------
// SUNNY WEATHER
// -------------------------------------------------------------

private fun isSunnyWeatherCode(
    weatherCode: Int
): Boolean {

    return weatherCode == 0 ||
            weatherCode == 1
}

// -------------------------------------------------------------
// LIGHT TIMELINE ITEM
// -------------------------------------------------------------

@Composable
private fun LightTimelineItem(
    label: String,
    time: String,
    highlightColor: Color
) {

    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = label,
            color = highlightColor,
            fontSize = 11.sp,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(2.dp)
        )

        Text(
            text = time,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight =
                FontWeight.Medium
        )
    }
}

// -------------------------------------------------------------
// WEATHER METRIC CARD
// -------------------------------------------------------------

@Composable
private fun WeatherMetricCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    title: String,
    value: String,
    subtext: String,
    cardBg: Color,
    textMuted: Color
) {

    Card(
        shape =
            RoundedCornerShape(14.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    cardBg
            ),
        modifier =
            modifier
    ) {

        Column(
            modifier =
                Modifier.padding(14.dp)
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier =
                    Modifier.size(24.dp)
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text = title,
                color = textMuted,
                fontSize = 11.sp
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text = value,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = subtext,
                color = textMuted,
                fontSize = 10.sp
            )
        }
    }
}

// -------------------------------------------------------------
// FORECAST DAY ITEM
// -------------------------------------------------------------

private fun forecastDescription(code: Int): String = when (code) {
    0, 1 -> "Sunny"
    2 -> "Partly cloudy"
    3 -> "Cloudy"
    45, 48 -> "Foggy"
    51, 53, 55, 56, 57 -> "Drizzle"
    61, 63, 65, 66, 67, 80, 81, 82 -> "Rainy"
    71, 73, 75, 77, 85, 86 -> "Snowy"
    95, 96, 99 -> "Stormy"
    else -> "Variable"
}

@Composable
private fun ForecastDayItem(
    day: String,
    icon: ImageVector,
    temp: String,
    lowTemp: String?,
    description: String,
    isSunny: Boolean,
    isToday: Boolean,
    accentOrange: Color,
    textMuted: Color
) {
    val iconColor = if (isSunny) accentOrange else Color(0xFF79B8F8)

    Column(
        modifier = Modifier
            .width(132.dp)
            .background(
                if (isToday) Color(0xFF30303A) else Color(0xFF252D39),
                RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 12.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day,
            color = if (isToday) accentOrange else Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = iconColor,
            modifier = Modifier.size(38.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = temp,
            color = Color.White,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = lowTemp?.let { "Low $it" } ?: "Low --",
            color = textMuted,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = description,
            color = iconColor,
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}
