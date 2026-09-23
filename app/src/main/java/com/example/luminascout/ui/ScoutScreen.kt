package com.example.luminascout.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.luminascout.Data.ScoutLocation
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import org.json.JSONObject

@Composable
fun ScoutScreen(
    onLocationClick: (ScoutLocation) -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current

    val darkBackground = Color(0xFF070A10)
    val cardBackground = Color(0xFF131924)
    val accentOrange = Color(0xFFFF9800)
    val accentBlue = Color(0xFF3B82F6)
    val accentGreen = Color(0xFF10B981)
    val textMuted = Color(0xFF8C93A0)

    var showAddDialog by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }

    var locationName by remember { mutableStateOf("") }
    var locationNotes by remember { mutableStateOf("") }
    var parkingDetails by remember { mutableStateOf("") }
    var shootingAngle by remember { mutableStateOf("") }

    val savedLocations = remember {
        mutableStateListOf<ScoutLocation>()
    }

    // Load locations saved on this device
    LaunchedEffect(Unit) {
        savedLocations.clear()
        savedLocations.addAll(loadSavedLocations(context))
    }

    fun getCurrentDeviceLocation() {
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)

        try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { location ->

                if (location != null) {
                    currentLocation = location
                    locationError = null
                    showAddDialog = true
                } else {
                    locationError =
                        "Could not get your location. Please try again."
                }

            }.addOnFailureListener {
                locationError =
                    "Could not get your location. Please try again."
            }

        } catch (e: SecurityException) {
            locationError = "Location permission is required."
        }
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val coarseGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            val fineGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            if (coarseGranted || fineGranted) {
                getCurrentDeviceLocation()
            } else {
                locationError = "Location permission was denied."
            }
        }

    fun addLocationButtonClicked() {

        val coarseGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val fineGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (coarseGranted || fineGranted) {
            getCurrentDeviceLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
    ) {

        // ---------------------------------------------------------
        // STYLIZED MAP BACKGROUND
        // ---------------------------------------------------------

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090D15))
        ) {

            Canvas(modifier = Modifier.fillMaxSize()) {

                val gridColor = Color.White.copy(alpha = 0.05f)
                val roadColor = Color(0xFF1E293B)

                drawLine(
                    color = Color.White.copy(alpha = 0.08f),
                    start = Offset(size.width * 0.51f, 0f),
                    end = Offset(size.width * 0.51f, size.height),
                    strokeWidth = 1.5.dp.toPx()
                )

                drawLine(
                    gridColor,
                    Offset(0f, size.height * 0.16f),
                    Offset(size.width, size.height * 0.16f),
                    1.dp.toPx()
                )

                drawLine(
                    gridColor,
                    Offset(0f, size.height * 0.35f),
                    Offset(size.width, size.height * 0.35f),
                    1.dp.toPx()
                )

                val roadPath = Path().apply {
                    moveTo(0f, size.height * 0.40f)
                    cubicTo(
                        size.width * 0.3f,
                        size.height * 0.39f,
                        size.width * 0.7f,
                        size.height * 0.41f,
                        size.width,
                        size.height * 0.40f
                    )
                }

                drawPath(
                    path = roadPath,
                    color = roadColor,
                    style = Stroke(width = 4.dp.toPx())
                )
            }

            Box(
                modifier = Modifier
                    .offset(x = (-50).dp, y = 180.dp)
                    .width(250.dp)
                    .height(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF143022).copy(alpha = 0.85f))
            )

            Box(
                modifier = Modifier
                    .offset(x = 240.dp, y = 140.dp)
                    .width(220.dp)
                    .height(115.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF132A42).copy(alpha = 0.85f))
            )

            MapPinMarker(
                modifier = Modifier.offset(x = 132.dp, y = 230.dp),
                pinColor = accentOrange
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            MapPinMarker(
                modifier = Modifier.offset(x = 250.dp, y = 150.dp),
                pinColor = accentBlue
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            MapPinMarker(
                modifier = Modifier.offset(x = 88.dp, y = 330.dp),
                pinColor = accentGreen
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Box(
                modifier = Modifier
                    .offset(x = 193.dp, y = 310.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(accentBlue.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(accentBlue)
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .align(Alignment.Center)
                    )
                }
            }

            // -----------------------------------------------------
            // ADD LOCATION BUTTON
            // -----------------------------------------------------

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-20).dp, y = (-20).dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(accentOrange.copy(alpha = 0.25f))
                        .blur(8.dp)
                )

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(accentOrange)
                        .clickable {
                            addLocationButtonClicked()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Spot",
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // ---------------------------------------------------------
        // SEARCH BAR
        // ---------------------------------------------------------

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = cardBackground,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = textMuted
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Search locations...",
                        color = textMuted,
                        fontSize = 14.sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = cardBackground,
                modifier = Modifier
                    .size(52.dp)
                    .clickable {
                        onNavigate("scout")
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }

        // ---------------------------------------------------------
        // SAVED LOCATIONS
        // ---------------------------------------------------------

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp
            ),
            color = cardBackground
        ) {

            Column(
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                )
            ) {

                Box(
                    modifier = Modifier
                        .width(38.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(textMuted.copy(alpha = 0.35f))
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Saved Locations",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${3 + savedLocations.size} pins",
                        color = textMuted,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Existing location 1
                LocationPinRow(
                    title = "Signal Hill Viewpoint",
                    distance = "2.4 mi",
                    score = "92",
                    dotColor = accentOrange,
                    onClick = {
                        onLocationClick(
                            ScoutLocation(
                                id = "1",
                                name = "Signal Hill Viewpoint",
                                description = "Panoramas & Sunset Views",
                                photoScore = 92,
                                goldenHourTime = "6:42 PM",
                                distance = "2.4 mi",
                                latitude = -33.9249,
                                longitude = 18.4241,
                                notes = "Panoramas and sunset views",
                                parkingDetails = "Street parking",
                                shootingAngle = "Wide angle toward the city"
                            )
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Existing location 2
                LocationPinRow(
                    title = "Kirstenbosch Canopy Walk",
                    distance = "5.1 mi",
                    score = "85",
                    dotColor = accentBlue,
                    onClick = {
                        onLocationClick(
                            ScoutLocation(
                                id = "2",
                                name = "Kirstenbosch Canopy Walk",
                                description = "Forest & Mountain Backdrop",
                                photoScore = 85,
                                goldenHourTime = "6:42 PM",
                                distance = "5.1 mi",
                                latitude = -33.9881,
                                longitude = 18.4324,
                                notes = "Forest and mountain backdrop",
                                parkingDetails = "Parking available",
                                shootingAngle = "Mountain-facing angle"
                            )
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Existing location 3
                LocationPinRow(
                    title = "Bloubergstrand Beach",
                    distance = "8.3 mi",
                    score = "96",
                    dotColor = accentGreen,
                    onClick = {
                        onLocationClick(
                            ScoutLocation(
                                id = "3",
                                name = "Bloubergstrand Beach",
                                description = "Table Mountain Ocean View",
                                photoScore = 96,
                                goldenHourTime = "6:42 PM",
                                distance = "8.3 mi",
                                latitude = -33.8206,
                                longitude = 18.4740,
                                notes = "Table Mountain ocean view",
                                parkingDetails = "Beach parking",
                                shootingAngle = "Face Table Mountain across the water"
                            )
                        )
                    }
                )

                // -------------------------------------------------
                // NEW USER-SAVED LOCATIONS
                // -------------------------------------------------

                savedLocations.forEach { location ->

                    Spacer(modifier = Modifier.height(12.dp))

                    LocationPinRow(
                        title = location.name,
                        distance = "GPS pin",
                        score = "—",
                        dotColor = accentOrange,
                        onClick = {
                            onLocationClick(location)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // ---------------------------------------------------------
        // LOCATION ERROR
        // ---------------------------------------------------------

        locationError?.let { errorMessage ->

            Snackbar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                action = {
                    TextButton(
                        onClick = {
                            locationError = null
                        }
                    ) {
                        Text("OK")
                    }
                }
            ) {
                Text(errorMessage)
            }
        }

        // ---------------------------------------------------------
        // ADD LOCATION DIALOG
        // ---------------------------------------------------------

        if (showAddDialog) {

            AlertDialog(
                onDismissRequest = {
                    showAddDialog = false
                },

                title = {
                    Text("Add Scouting Location")
                },

                text = {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 430.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Text(
                            text = "GPS location captured",
                            fontSize = 12.sp,
                            color = textMuted
                        )

                        OutlinedTextField(
                            value = locationName,
                            onValueChange = {
                                locationName = it
                            },
                            label = {
                                Text("Location name")
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = locationNotes,
                            onValueChange = {
                                locationNotes = it
                            },
                            label = {
                                Text("Notes")
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = parkingDetails,
                            onValueChange = {
                                parkingDetails = it
                            },
                            label = {
                                Text("Parking details")
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = shootingAngle,
                            onValueChange = {
                                shootingAngle = it
                            },
                            label = {
                                Text("Shooting angle")
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },

                confirmButton = {

                    TextButton(
                        onClick = {

                            if (locationName.isBlank()) {
                                locationError =
                                    "Please enter a location name."
                                return@TextButton
                            }

                            val location = currentLocation

                            if (location == null) {
                                locationError =
                                    "Location could not be found. Please try again."
                                return@TextButton
                            }

                            val newLocation = ScoutLocation(
                                id = System.currentTimeMillis().toString(),
                                name = locationName.trim(),
                                description = "User saved scouting location",
                                photoScore = 0,
                                goldenHourTime = "--",
                                distance = "GPS pin",
                                latitude = location.latitude,
                                longitude = location.longitude,
                                notes = locationNotes.trim(),
                                parkingDetails = parkingDetails.trim(),
                                shootingAngle = shootingAngle.trim()
                            )

                            saveLocation(context, newLocation)

                            savedLocations.add(newLocation)

                            locationName = ""
                            locationNotes = ""
                            parkingDetails = ""
                            shootingAngle = ""
                            currentLocation = null
                            showAddDialog = false
                        }
                    ) {
                        Text("Save")
                    }
                },

                dismissButton = {

                    TextButton(
                        onClick = {
                            showAddDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

// -------------------------------------------------------------
// SAVE LOCATION
// -------------------------------------------------------------

private fun saveLocation(
    context: Context,
    location: ScoutLocation
) {

    val preferences =
        context.getSharedPreferences(
            "scout_locations",
            Context.MODE_PRIVATE
        )

    val locationJson = JSONObject().apply {

        put("id", location.id)
        put("name", location.name)
        put("description", location.description)
        put("photoScore", location.photoScore)
        put("goldenHourTime", location.goldenHourTime)
        put("distance", location.distance)
        put("latitude", location.latitude)
        put("longitude", location.longitude)
        put("notes", location.notes)
        put("parkingDetails", location.parkingDetails)
        put("shootingAngle", location.shootingAngle)
    }

    val existing =
        preferences.getStringSet(
            "locations",
            emptySet()
        )?.toMutableSet()
            ?: mutableSetOf()

    existing.add(locationJson.toString())

    preferences.edit()
        .putStringSet("locations", existing)
        .apply()
}

// -------------------------------------------------------------
// LOAD SAVED LOCATIONS
// -------------------------------------------------------------

private fun loadSavedLocations(
    context: Context
): List<ScoutLocation> {

    val preferences =
        context.getSharedPreferences(
            "scout_locations",
            Context.MODE_PRIVATE
        )

    val saved =
        preferences.getStringSet(
            "locations",
            emptySet()
        ) ?: emptySet()

    return saved.mapNotNull { jsonString ->

        try {

            val json = JSONObject(jsonString)

            ScoutLocation(
                id = json.getString("id"),
                name = json.getString("name"),
                description = json.getString("description"),
                photoScore = json.getInt("photoScore"),
                goldenHourTime = json.getString("goldenHourTime"),
                distance = json.getString("distance"),
                latitude = json.getDouble("latitude"),
                longitude = json.getDouble("longitude"),
                notes = json.getString("notes"),
                parkingDetails = json.getString("parkingDetails"),
                shootingAngle = json.getString("shootingAngle")
            )

        } catch (e: Exception) {
            null
        }
    }
}

// -------------------------------------------------------------
// MAP PIN
// -------------------------------------------------------------

@Composable
private fun MapPinMarker(
    modifier: Modifier = Modifier,
    pinColor: Color,
    content: @Composable () -> Unit
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(pinColor),
            contentAlignment = Alignment.Center
        ) {
            content()
        }

        Box(
            modifier = Modifier
                .width(2.dp)
                .height(12.dp)
                .background(pinColor)
        )

        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(pinColor)
        )
    }
}

// -------------------------------------------------------------
// LOCATION ROW
// -------------------------------------------------------------

@Composable
private fun LocationPinRow(
    title: String,
    distance: String,
    score: String,
    dotColor: Color,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {

                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = distance,
                    color = Color(0xFF8C93A0),
                    fontSize = 12.sp
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFF9800),
                modifier = Modifier.size(14.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = score,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}