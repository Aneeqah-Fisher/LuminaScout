package com.example.luminascout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminascout.Data.ScoutLocation

@Composable
fun LocationDetailScreen(
    location: ScoutLocation,
    onBackClick: () -> Unit = {},
    onLocationSaved: (ScoutLocation) -> Unit = {}
) {
    val context = LocalContext.current
    var current by remember(location.id) { mutableStateOf(location) }
    var editing by remember(location.id) { mutableStateOf(false) }
    var name by remember(location.id) { mutableStateOf(location.name) }
    var description by remember(location.id) { mutableStateOf(location.description) }
    var notes by remember(location.id) { mutableStateOf(location.notes) }
    var parking by remember(location.id) { mutableStateOf(location.parkingDetails) }
    var angle by remember(location.id) { mutableStateOf(location.shootingAngle) }
    var error by remember { mutableStateOf(false) }
    val background = Color(0xFF0F141C)
    val card = Color(0xFF1B222D)
    val orange = Color(0xFFFF9800)
    val muted = Color(0xFF9BA3AF)

    Column(
        modifier = Modifier.fillMaxSize().background(background)
            .verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick,
                modifier = Modifier.clip(CircleShape).background(card)) {
                Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
            }
            Text("Saved location", color = Color.White,
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            IconButton(onClick = {
                if (editing) {
                    editing = false
                    name = current.name
                    description = current.description
                    notes = current.notes
                    parking = current.parkingDetails
                    angle = current.shootingAngle
                    error = false
                } else editing = true
            }, modifier = Modifier.clip(CircleShape).background(card)) {
                Icon(Icons.Default.Edit, "Edit location", tint = orange)
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = card),
            shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(current.name, color = Color.White, fontSize = 24.sp,
                    fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = orange)
                    Spacer(Modifier.width(8.dp))
                    Text("${current.latitude}, ${current.longitude}", color = muted,
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (editing) {
            Text("Edit saved location", color = Color.White,
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(name, { name = it; error = false },
                label = { Text("Location name") }, isError = error,
                singleLine = true, modifier = Modifier.fillMaxWidth())
            if (error) Text("Please enter a location name", color = MaterialTheme.colorScheme.error)
            OutlinedTextField(description, { description = it },
                label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(notes, { notes = it },
                label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            OutlinedTextField(parking, { parking = it },
                label = { Text("Parking details") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(angle, { angle = it },
                label = { Text("Shooting angle") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                if (name.isBlank()) { error = true }
                else {
                    val updated = current.copy(
                        name = name.trim(), description = description.trim(),
                        notes = notes.trim(), parkingDetails = parking.trim(),
                        shootingAngle = angle.trim()
                    )
                    replaceSavedLocation(context, updated)
                    syncLocationToFirestore(updated)
                    current = updated
                    onLocationSaved(updated)
                    editing = false
                }
            }, modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = orange)) {
                Text("Save changes", color = Color.Black)
            }
            TextButton(onClick = {
                editing = false
                name = current.name; description = current.description
                notes = current.notes; parking = current.parkingDetails
                angle = current.shootingAngle
                error = false
            }, modifier = Modifier.fillMaxWidth()) { Text("Cancel") }
        } else {
            LocationInfoCard("DESCRIPTION", current.description, card, muted)
            LocationInfoCard("NOTES", current.notes, card, muted)
            LocationInfoCard("PARKING DETAILS", current.parkingDetails, card, muted)
            LocationInfoCard("SHOOTING ANGLE", current.shootingAngle, card, muted)
        }
    }
}

@Composable
private fun LocationInfoCard(title: String, value: String, card: Color, muted: Color) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = card)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, color = muted, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(value.ifBlank { "Not added yet. Tap the pencil to add details." },
                color = Color.White, fontSize = 14.sp)
        }
    }
}
