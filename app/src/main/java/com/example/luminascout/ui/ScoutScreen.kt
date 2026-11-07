

package com.example.luminascout.ui

import com.example.luminascout.R

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import kotlinx.coroutines.launch
import com.google.android.gms.maps.CameraUpdateFactory
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.DocumentSnapshot
import androidx.compose.ui.res.stringResource

@Composable
fun ScoutScreen(
    onLocationClick: (ScoutLocation) -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(-33.9249, 18.4241),
            11f
        )
    }

    var mapType by remember {
        mutableStateOf(MapType.NORMAL)
    }

    var savedPanelExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val panelHeight by animateDpAsState(
        targetValue = if (savedPanelExpanded) 320.dp else 76.dp,
        label = "Saved locations panel"
    )
    val darkBackground = Color(0xFF070A10)
    val cardBackground = Color(0xFF131924)
    val accentOrange = Color(0xFFFF9800)
    val accentBlue = Color(0xFF3B82F6)
    val accentGreen = Color(0xFF10B981)
    val textMuted = Color(0xFF8C93A0)

    var showAddDialog by remember { mutableStateOf(false) }
    var showLocationChoice by remember { mutableStateOf(false) }
    var selectedMapPoint by remember { mutableStateOf<LatLng?>(null) }
    var locationToMove by remember { mutableStateOf<ScoutLocation?>(null) }
    var isUsingMapPoint by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("") }
    var locationNotes by remember { mutableStateOf("") }
    var parkingDetails by remember { mutableStateOf("") }
    var shootingAngle by remember { mutableStateOf("") }

    val savedLocations = remember {
        mutableStateListOf<ScoutLocation>()
    }

    // Start with saved device data, then listen for cloud locations belonging to
    // the signed-in Firebase user. Only missing IDs are imported: an older cloud
    // snapshot must never replace a pin corrected on this phone while offline.
    val firebaseUid = FirebaseAuth.getInstance().currentUser?.uid
    DisposableEffect(context, firebaseUid) {
        savedLocations.clear()
        savedLocations.addAll(loadSavedLocations(context).distinctBy { it.id })

        val registration = if (firebaseUid != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(firebaseUid)
                .collection("scoutingLocations")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        android.util.Log.e("LuminaScoutSync", "Cloud download failed", error)
                    } else {
                        snapshot?.documents?.forEach { document ->
                            val cloudLocation = document.toScoutLocationOrNull()
                            if (cloudLocation != null &&
                                savedLocations.none { it.id == cloudLocation.id }
                            ) {
                                replaceSavedLocation(context, cloudLocation)
                                savedLocations.add(cloudLocation)
                            }
                        }
                    }
                }
        } else null

        onDispose { registration?.remove() }
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

    val matchingLocations = savedLocations.filter { location ->
        location.name.contains(searchQuery, ignoreCase = true) ||
                location.notes.contains(searchQuery, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
    ) {
        // The map stays visible behind the floating controls and bottom panel.
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            onMapClick = { point ->
                selectedMapPoint = point
                // The temporary marker shows where a new or moved pin will go.
            },
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = mapType),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = true,
                zoomGesturesEnabled = true,
                scrollGesturesEnabled = true,
                rotationGesturesEnabled = true
            )
        ) {
            selectedMapPoint?.let { point ->
                Marker(
                    state = rememberMarkerState(key = "selected-${point.latitude}-${point.longitude}", position = point),
                    title = if (locationToMove != null) "New position for saved pin" else "Selected location",
                    snippet = "Tap + to save this location"
                )
            }
            savedLocations.forEach { location ->
                Marker(
                    state = rememberMarkerState(
                        key = "${location.id}-${location.latitude}-${location.longitude}",
                        position = LatLng(location.latitude, location.longitude)
                    ),
                    title = location.name,
                    snippet = location.notes,
                    onInfoWindowClick = { onLocationClick(location) }
                )
            }
        }

        // Search only saved scouting locations.
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (it.isNotBlank()) savedPanelExpanded = true
                },
                placeholder = {
                    Text(stringResource(R.string.ls_search_saved_locations), color = textMuted, fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = stringResource(R.string.ls_search), tint = textMuted)
                },
                singleLine = true,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = cardBackground,
                    unfocusedContainerColor = cardBackground
                )
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = cardBackground,
                modifier = Modifier.size(52.dp).clickable {
                    mapType = when (mapType) {
                        MapType.NORMAL -> MapType.SATELLITE
                        MapType.SATELLITE -> MapType.TERRAIN
                        else -> MapType.NORMAL
                    }
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Layers,
                        contentDescription = stringResource(R.string.ls_change_map_type),
                        tint = Color.White
                    )
                }
            }
        }

        // Saved Locations: swipe UP on the header to expand, DOWN to collapse.
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(panelHeight),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = cardBackground
        ) {
            Column {
                var totalDrag by remember { mutableFloatStateOf(0f) }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onDragStart = { totalDrag = 0f },
                                onVerticalDrag = { change, dragAmount ->
                                    change.consume()
                                    totalDrag += dragAmount
                                },
                                onDragEnd = {
                                    if (totalDrag < -30f) savedPanelExpanded = true
                                    if (totalDrag > 30f) savedPanelExpanded = false
                                }
                            )
                        }
                        .clickable { savedPanelExpanded = !savedPanelExpanded }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(38.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(textMuted.copy(alpha = 0.55f))
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.ls_saved_locations),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${savedLocations.size} pins", color = textMuted, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                if (savedPanelExpanded) Icons.Default.KeyboardArrowDown
                                else Icons.Default.KeyboardArrowUp,
                                contentDescription = if (savedPanelExpanded) "Collapse saved locations"
                                else "Expand saved locations",
                                tint = Color.White
                            )
                        }
                    }
                }

                if (savedPanelExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp)
                    ) {
                        if (matchingLocations.isEmpty()) {
                            Text(
                                if (searchQuery.isBlank()) stringResource(R.string.ls_no_saved_locations_yet_tap_to_add_one)
                                else stringResource(R.string.ls_no_saved_locations_match_your_search),
                                color = textMuted,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 20.dp)
                            )
                        }
                        matchingLocations.forEach { location ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    LocationPinRow(
                                        title = location.name,
                                        distance = stringResource(R.string.ls_tap_to_show_on_map),
                                        score = "—",
                                        dotColor = accentOrange,
                                        onClick = {
                                            savedPanelExpanded = false
                                            scope.launch {
                                                cameraPositionState.animate(
                                                    CameraUpdateFactory.newLatLngZoom(
                                                        LatLng(location.latitude, location.longitude),
                                                        15f
                                                    )
                                                )
                                            }
                                        }
                                    )
                                }
                                TextButton(onClick = {
                                    locationToMove = location
                                    selectedMapPoint = null
                                    savedPanelExpanded = false
                                    scope.launch {
                                        cameraPositionState.animate(
                                            CameraUpdateFactory.newLatLngZoom(
                                                LatLng(location.latitude, location.longitude), 15f
                                            )
                                        )
                                    }
                                }) {
                                    Text(stringResource(R.string.ls_move_pin), color = accentOrange)
                                }
                                TextButton(onClick = { onLocationClick(location) }) {
                                    Text("Details", color = accentOrange)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        // Add button floats immediately above the Saved Locations panel.
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = panelHeight + 16.dp)
                .size(60.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(accentOrange)
                .clickable {
                    if (locationToMove != null && selectedMapPoint != null) {
                        // Handled by the confirmation dialog below.
                        showLocationChoice = true
                    } else if (locationToMove != null) {
                        locationError = "Tap the correct spot on the map, then tap + to move the pin."
                    } else {
                        showLocationChoice = true
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add scouting location",
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )
        }

        if (locationToMove != null) {
            Surface(
                modifier = Modifier.align(Alignment.TopCenter)
                    .padding(top = 84.dp, start = 16.dp, end = 16.dp),
                color = cardBackground,
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tap the correct map position for ${locationToMove?.name}",
                        color = Color.White, modifier = Modifier.weight(1f), fontSize = 12.sp)
                    TextButton(onClick = {
                        locationToMove = null
                        selectedMapPoint = null
                    }) { Text("Cancel", color = accentOrange) }
                }
            }
        }

        if (showLocationChoice) {
            AlertDialog(
                onDismissRequest = { showLocationChoice = false },
                title = { Text(if (locationToMove != null) "Correct saved pin" else "Choose location source") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (locationToMove != null) {
                            Text("Move ${locationToMove?.name} to the selected point? Your notes and other details will stay the same.")
                            TextButton(onClick = {
                                val old = locationToMove
                                val point = selectedMapPoint
                                if (old != null && point != null) {
                                    val corrected = old.copy(latitude = point.latitude, longitude = point.longitude)
                                    replaceSavedLocation(context, corrected)
                                    val index = savedLocations.indexOfFirst { it.id == old.id }
                                    if (index >= 0) savedLocations[index] = corrected
                                    syncLocationToFirestore(corrected)
                                    locationToMove = null
                                    selectedMapPoint = null
                                    showLocationChoice = false
                                }
                            }) { Text("Save corrected position") }
                        } else {
                            TextButton(onClick = {
                                if (selectedMapPoint == null) {
                                    showLocationChoice = false
                                    locationError = "Tap the desired place on the map first, then tap +."
                                } else {
                                    isUsingMapPoint = true
                                    currentLocation = null
                                    showLocationChoice = false
                                    showAddDialog = true
                                }
                            }) { Text("Use selected map point") }
                            TextButton(onClick = {
                                isUsingMapPoint = false
                                selectedMapPoint = null
                                showLocationChoice = false
                                addLocationButtonClicked()
                            }) { Text("Use my current GPS location") }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = { TextButton(onClick = { showLocationChoice = false }) { Text("Cancel") } }
            )
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
                            text = if (isUsingMapPoint) "Map location selected" else "GPS location captured",
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

                            val latitude = if (isUsingMapPoint) selectedMapPoint?.latitude else currentLocation?.latitude
                            val longitude = if (isUsingMapPoint) selectedMapPoint?.longitude else currentLocation?.longitude

                            if (latitude == null || longitude == null) {
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
                                latitude = latitude,
                                longitude = longitude,
                                notes = locationNotes.trim(),
                                parkingDetails = parkingDetails.trim(),
                                shootingAngle = shootingAngle.trim()
                            )

                            saveLocation(context, newLocation)

                            savedLocations.add(newLocation)

                            // Synchronise this location with Firestore when signed in with Google
                            val firebaseUser = FirebaseAuth.getInstance().currentUser

                            if (firebaseUser != null) {
                                val locationData = hashMapOf(
                                    "id" to newLocation.id,
                                    "name" to newLocation.name,
                                    "description" to newLocation.description,
                                    "photoScore" to newLocation.photoScore,
                                    "goldenHourTime" to newLocation.goldenHourTime,
                                    "distance" to newLocation.distance,
                                    "latitude" to newLocation.latitude,
                                    "longitude" to newLocation.longitude,
                                    "notes" to newLocation.notes,
                                    "parkingDetails" to newLocation.parkingDetails,
                                    "shootingAngle" to newLocation.shootingAngle,
                                    "updatedAt" to FieldValue.serverTimestamp()
                                )

                                FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(firebaseUser.uid)
                                    .collection("scoutingLocations")
                                    .document(newLocation.id)
                                    .set(locationData)
                                    .addOnFailureListener { error ->
                                        android.util.Log.e(
                                            "LuminaScoutSync",
                                            "Firestore sync failed",
                                            error
                                        )
                                    }
                            }

                            locationName = ""
                            locationNotes = ""
                            parkingDetails = ""
                            shootingAngle = ""
                            currentLocation = null
                            selectedMapPoint = null
                            isUsingMapPoint = false
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

    existing.removeAll { raw ->
        try { JSONObject(raw).optString("id") == location.id }
        catch (_: Exception) { false }
    }
    existing.add(locationJson.toString())

    preferences.edit()
        .putStringSet("locations", existing)
        .apply()
}

// -------------------------------------------------------------
// LOAD SAVED LOCATIONS
// -------------------------------------------------------------

fun loadSavedLocations(
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
// Replace an existing local pin without duplicating it or losing its notes.
fun replaceSavedLocation(context: Context, location: ScoutLocation) {
    val preferences = context.getSharedPreferences("scout_locations", Context.MODE_PRIVATE)
    val current = preferences.getStringSet("locations", emptySet()).orEmpty()
    val updated = current.filterNot { raw ->
        try { JSONObject(raw).optString("id") == location.id } catch (_: Exception) { false }
    }.toMutableSet()
    updated.add(JSONObject().apply {
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
    }.toString())
    preferences.edit().putStringSet("locations", updated).apply()
}

fun syncLocationToFirestore(location: ScoutLocation) {
    val user = FirebaseAuth.getInstance().currentUser ?: return
    val data = hashMapOf(
        "id" to location.id,
        "name" to location.name,
        "description" to location.description,
        "photoScore" to location.photoScore,
        "goldenHourTime" to location.goldenHourTime,
        "distance" to location.distance,
        "latitude" to location.latitude,
        "longitude" to location.longitude,
        "notes" to location.notes,
        "parkingDetails" to location.parkingDetails,
        "shootingAngle" to location.shootingAngle,
        "updatedAt" to FieldValue.serverTimestamp()
    )
    FirebaseFirestore.getInstance().collection("users").document(user.uid)
        .collection("scoutingLocations").document(location.id).set(data)
        .addOnFailureListener { error ->
            android.util.Log.e("LuminaScoutSync", "Pin correction sync failed", error)
        }
}

// Convert a Firestore document into the same model used by local saved pins.
// Missing optional fields get safe defaults, while invalid coordinates are skipped.
private fun DocumentSnapshot.toScoutLocationOrNull(): ScoutLocation? {
    return try {
        val lat = getDouble("latitude") ?: return null
        val lon = getDouble("longitude") ?: return null
        if (!lat.isFinite() || !lon.isFinite() || lat !in -90.0..90.0 || lon !in -180.0..180.0) {
            return null
        }
        ScoutLocation(
            id = id,
            name = getString("name") ?: "Unnamed location",
            description = getString("description") ?: "User saved scouting location",
            photoScore = getLong("photoScore")?.toInt() ?: 0,
            goldenHourTime = getString("goldenHourTime") ?: "--",
            distance = getString("distance") ?: "GPS pin",
            latitude = lat,
            longitude = lon,
            notes = getString("notes") ?: "",
            parkingDetails = getString("parkingDetails") ?: "",
            shootingAngle = getString("shootingAngle") ?: ""
        )
    } catch (error: Exception) {
        android.util.Log.e("LuminaScoutSync", "Could not read cloud location $id", error)
        null
    }
}
