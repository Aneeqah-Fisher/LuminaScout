package com.example.luminascout.ui

import com.example.luminascout.R

import android.widget.Toast
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.util.Calendar
import java.util.Locale
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
@Composable
fun PlanScreen(
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current

    var shootName by rememberSaveable { mutableStateOf("") }
    var selectedDate by rememberSaveable { mutableStateOf("") }
    var selectedTime by rememberSaveable { mutableStateOf("") }
    var locationName by rememberSaveable {
        mutableStateOf("")
    }
    var notes by rememberSaveable { mutableStateOf("") }

    val calendar = remember { Calendar.getInstance() }

    val openDatePicker = {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(context, { _, y, m, d ->
            selectedDate = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)
            calendar.set(Calendar.YEAR, y)
            calendar.set(Calendar.MONTH, m)
            calendar.set(Calendar.DAY_OF_MONTH, d)
        }, year, month, day).apply {
            datePicker.minDate = System.currentTimeMillis() - 86_400_000L
        }.show()
    }

    val openTimePicker = {
        TimePickerDialog(context, { _, hour, minute ->
            selectedTime = String.format(Locale.US, "%02d:%02d", hour, minute)
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

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

    var editingGear by remember { mutableStateOf(false) }
    var editingShots by remember { mutableStateOf(false) }

    if (editingGear) {
        ChecklistEditorDialog(
            title = "Edit gear checklist",
            addHint = "Add equipment (e.g. Flash)",
            entries = gearList,
            onAdd = { gearList.add(it to false) },
            onRemove = { gearList.removeAt(it) },
            onDone = { editingGear = false }
        )
    }
    if (editingShots) {
        ChecklistEditorDialog(
            title = "Edit shot list",
            addHint = "Add shot (e.g. Close-up portrait)",
            entries = shotList,
            onAdd = { shotList.add(it to false) },
            onRemove = { shotList.removeAt(it) },
            onDone = { editingShots = false }
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
                    contentDescription = stringResource(R.string.ls_back),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.ls_new_shoot),
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
            placeholder = { Text(stringResource(R.string.ls_shoot_name), color = textMuted, fontSize = 14.sp) },
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
                    .clickable { openDatePicker() }
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
                        Text(text = selectedDate.ifEmpty { "Select Date" }, color = if (selectedDate.isEmpty()) textMuted else Color.White, fontSize = 13.sp)
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
                    .clickable { openTimePicker() }
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
                        Text(text = selectedTime.ifEmpty { "Select Time" }, color = if (selectedTime.isEmpty()) textMuted else Color.White, fontSize = 13.sp)
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
            placeholder = {
                Text("Add location")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = null,
                    tint = accentOrange
                )
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
            placeholder = { Text(stringResource(R.string.ls_notes), color = textMuted, fontSize = 14.sp) },
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
            Text(text = stringResource(R.string.ls_gear_checklist), color = textMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(
                text = stringResource(R.string.ls_edit),
                color = accentOrange,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    editingGear = true
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
            Text(text = stringResource(R.string.ls_shot_list), color = textMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(
                text = stringResource(R.string.ls_edit),
                color = accentOrange,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    editingShots = true
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
                if (selectedDate.isBlank() || selectedTime.isBlank()) {
                    Toast.makeText(context, "Please choose a date and time", Toast.LENGTH_SHORT).show()
                } else if (locationName.isBlank()) {
                    Toast.makeText(context, "Please enter a location", Toast.LENGTH_SHORT).show()
                } else {
                    val name = shootName.trim().ifBlank { "Untitled Shoot" }
                    val plan = SavedShootPlan(
                        id = java.util.UUID.randomUUID().toString(),
                        name = name,
                        date = selectedDate,
                        time = selectedTime,
                        location = locationName.trim(),
                        notes = notes.trim(),
                        gear = gearList.map { ShootChecklistEntry(it.first, it.second) },
                        shots = shotList.map { ShootChecklistEntry(it.first, it.second) }
                    )
                    if (ShootPlanStorage.save(context, plan)) {
                        Toast.makeText(context, "Saved shoot plan: $name", Toast.LENGTH_SHORT).show()
                        onNavigate("home")
                    } else {
                        Toast.makeText(context, "Could not save shoot plan", Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accentOrange)
        ) {
            Text(text = stringResource(R.string.ls_save_shoot_plan), color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold)
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

@Composable
private fun ChecklistEditorDialog(
    title: String,
    addHint: String,
    entries: List<Pair<String, Boolean>>,
    onAdd: (String) -> Unit,
    onRemove: (Int) -> Unit,
    onDone: () -> Unit
) {
    var newItem by remember { mutableStateOf("") }
    val orange = Color(0xFFFF9800)
    AlertDialog(
        onDismissRequest = onDone,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Add or remove items. Your checklist is saved when you save the shoot.", style = MaterialTheme.typography.bodySmall)
                Column(
                    modifier = Modifier.heightIn(max = 240.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    entries.forEachIndexed { index, entry ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Text(entry.first, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                            IconButton(onClick = { onRemove(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove ${entry.first}")
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = newItem,
                    onValueChange = { newItem = it },
                    label = { Text(addHint) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        val name = newItem.trim()
                        if (name.isNotEmpty() && entries.none { it.first.equals(name, ignoreCase = true) }) {
                            onAdd(name)
                            newItem = ""
                        }
                    },
                    enabled = newItem.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = orange),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Add item", color = Color.Black) }
            }
        },
        confirmButton = { TextButton(onClick = onDone) { Text("Done") } }
    )
}
