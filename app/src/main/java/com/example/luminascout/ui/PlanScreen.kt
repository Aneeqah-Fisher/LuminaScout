package com.example.luminascout.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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

@Composable
fun PlanScreen(
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current

    var shootName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("Select Date") }
    var selectedTime by remember { mutableStateOf("Select Time") }
    var locationName by remember { mutableStateOf("Signal Hill Viewpoint, Cape Town") }
    var notes by remember { mutableStateOf("") }

    val gearList = remember {
        mutableStateListOf(
            Pair("Camera body", true),
            Pair("Primary lens", true),
            Pair("Tripod", false),
            Pair("Batteries", false)
        )
    }

    val shotList = remember {
        mutableStateListOf(
            Pair("Wide establishing shot", true),
            Pair("Golden hour reflection", false),
            Pair("Foreground wildflowers / Fynbos", false)
        )
    }

    val darkBackground = Color(0xFF0F141C)
    val inputBackground = Color(0xFF1B222D)
    val cardBackground = Color(0xFF1B222D)
    val accentOrange = Color(0xFFFF9800)
    val textMuted = Color(0xFF8C93A0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onNavigate("home") },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(inputBackground)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "New Shoot",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Shoot Name
        OutlinedTextField(
            value = shootName,
            onValueChange = { shootName = it },
            placeholder = { Text("Shoot name", color = textMuted, fontSize = 14.sp) },
            leadingIcon = {
                Icon(imageVector = Icons.Outlined.PhotoCamera, contentDescription = null, tint = textMuted)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = inputBackground,
                unfocusedContainerColor = inputBackground,
                focusedBorderColor = accentOrange,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Date & Time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(inputBackground)
                    .clickable { selectedDate = "2026-09-23" }
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Outlined.CalendarToday, contentDescription = null, tint = textMuted, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = selectedDate, color = if (selectedDate == "Select Date") textMuted else Color.White, fontSize = 13.sp)
                    }
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, tint = textMuted)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(inputBackground)
                    .clickable { selectedTime = "18:15 PM" }
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Outlined.Schedule, contentDescription = null, tint = textMuted, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = selectedTime, color = if (selectedTime == "Select Time") textMuted else Color.White, fontSize = 13.sp)
                    }
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, tint = textMuted)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Location Field
        OutlinedTextField(
            value = locationName,
            onValueChange = { locationName = it },
            leadingIcon = {
                Icon(imageVector = Icons.Outlined.Place, contentDescription = null, tint = accentOrange)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = inputBackground,
                unfocusedContainerColor = inputBackground,
                focusedBorderColor = accentOrange,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Notes Area
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            placeholder = { Text("Notes...", color = textMuted, fontSize = 14.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = inputBackground,
                unfocusedContainerColor = inputBackground,
                focusedBorderColor = accentOrange,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Gear Checklist Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "GEAR CHECKLIST", color = textMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "Edit",
                color = accentOrange,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    Toast.makeText(context, "Edit Gear List", Toast.LENGTH_SHORT).show()
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                gearList.forEachIndexed { index, item ->
                    ChecklistItemRow(
                        text = item.first,
                        isChecked = item.second,
                        accentOrange = accentOrange,
                        textMuted = textMuted,
                        onToggle = { gearList[index] = item.copy(second = !item.second) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Shot List Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "SHOT LIST", color = textMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "Edit",
                color = accentOrange,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    Toast.makeText(context, "Edit Shot List", Toast.LENGTH_SHORT).show()
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                shotList.forEachIndexed { index, item ->
                    ChecklistItemRow(
                        text = item.first,
                        isChecked = item.second,
                        accentOrange = accentOrange,
                        textMuted = textMuted,
                        onToggle = { shotList[index] = item.copy(second = !item.second) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save Shoot Button
        Button(
            onClick = {
                val name = if (shootName.isBlank()) "Untitled Shoot" else shootName
                Toast.makeText(context, "Saved shoot plan: $name", Toast.LENGTH_SHORT).show()
                onNavigate("home")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accentOrange)
        ) {
            Text(text = "Save Shoot Plan", color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ChecklistItemRow(
    text: String,
    isChecked: Boolean,
    accentOrange: Color,
    textMuted: Color,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isChecked) accentOrange else Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = if (isChecked) Color.White else textMuted,
            fontSize = 13.sp,
            fontWeight = if (isChecked) FontWeight.Medium else FontWeight.Normal
        )
    }
}