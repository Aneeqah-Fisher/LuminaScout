package com.example.luminascout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

// Keep this model and composable for compatibility with any existing navigation code.
data class NotificationItem(
    val title: String,
    val subtitle: String,
    val time: String,
    val icon: ImageVector,
    val isHighlighted: Boolean = false
)

@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    reminders: List<NotificationItem> = emptyList()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var savedShoots by remember { mutableStateOf(ShootPlanStorage.load(context)) }

    // Refresh when the user returns to this screen after saving a new shoot.
    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                savedShoots = ShootPlanStorage.load(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        savedShoots = ShootPlanStorage.load(context)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val orderedShoots = remember(savedShoots) {
        savedShoots.sortedWith(compareBy<SavedShootPlan> { it.date }.thenBy { it.time })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.width(16.dp))
            Text("Reminders", style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground)
        }

        Text("Saved shoot plans",color = Color.White, style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))

        if (orderedShoots.isEmpty() && reminders.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.NotificationsNone, contentDescription = null,
                        tint = Color(0xFFFF9800), modifier = Modifier.size(38.dp))
                    Text("No saved shoots yet", fontWeight = FontWeight.Bold)
                    Text("Create a shoot in Plan and tap Save Shoot Plan. It will appear here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            orderedShoots.forEach { shoot ->
                key(shoot.id) { SavedShootCard(shoot) }
                Spacer(Modifier.height(10.dp))
            }
            // Preserve support for real reminders supplied by other parts of the app.
            reminders.forEach { reminder ->
                NotificationCard(reminder)
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun SavedShootCard(shoot: SavedShootPlan) {
    var expanded by remember(shoot.id) { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, contentDescription = null,
                    tint = Color(0xFFFF9800), modifier = Modifier.size(26.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(shoot.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${shoot.date}  •  ${shoot.time}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(if (expanded) "Hide" else "View",
                    color = Color(0xFFFF9800), fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))
            Text("Location: ${shoot.location}", style = MaterialTheme.typography.bodyMedium)
            if (expanded) {
                Spacer(Modifier.height(14.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))
                if (shoot.notes.isNotBlank()) {
                    Text("Notes", fontWeight = FontWeight.Bold)
                    Text(shoot.notes)
                    Spacer(Modifier.height(12.dp))
                }
                Text("Gear checklist", fontWeight = FontWeight.Bold)
                if (shoot.gear.isEmpty()) Text("No equipment added")
                shoot.gear.forEach { item ->
                    Text("${if (item.checked) "☑" else "☐"}  ${item.name}")
                }
                Spacer(Modifier.height(12.dp))
                Text("Shot list", fontWeight = FontWeight.Bold)
                if (shoot.shots.isEmpty()) Text("No shots added")
                shoot.shots.forEach { item ->
                    Text("${if (item.checked) "☑" else "☐"}  ${item.name}")
                }
            }
        }
    }
}

@Composable
fun NotificationCard(item: NotificationItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = if (item.isHighlighted) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB020)) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(item.icon, contentDescription = null,
                tint = Color(0xFFFF9800), modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold)
                Text(item.subtitle, style = MaterialTheme.typography.bodySmall)
                Text(item.time, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
