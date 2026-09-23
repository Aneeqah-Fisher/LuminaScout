package com.example.luminascout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NotificationItem(
    val title: String,
    val subtitle: String,
    val time: String,
    val icon: ImageVector,
    val isHighlighted: Boolean = false
)

@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit
) {
    val todayNotifications = listOf(
        NotificationItem(
            title = "Shoot in 3 hours",
            subtitle = "Signal Hill Viewpoint — Golden Hour at 6:42 PM",
            time = "3:41 PM",
            icon = Icons.Default.PhotoCamera,
            isHighlighted = true
        ),
        NotificationItem(
            title = "Excellent conditions",
            subtitle = "Photography score: 87/100 for this evening. Clear skies expected.",
            time = "12:00 PM",
            icon = Icons.Default.WbSunny
        ),
        NotificationItem(
            title = "Blue hour window",
            subtitle = "Optimal blue hour 7:54–8:18 PM. Set a reminder?",
            time = "9:00 AM",
            icon = Icons.Default.Info
        )
    )

    val yesterdayNotifications = listOf(
        NotificationItem(
            title = "Shoot reminder",
            subtitle = "Lion's Head sunrise shoot — 5:00 AM tomorrow.",
            time = "8:00 PM",
            icon = Icons.Default.PhotoCamera
        ),
        NotificationItem(
            title = "Weather update",
            subtitle = "Rain expected Wednesday. Consider rescheduling Camps Bay shoot.",
            time = "2:15 PM",
            icon = Icons.Default.Cloud
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // TODAY Section
        Text(
            text = "TODAY",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        todayNotifications.forEach { item ->
            NotificationCard(item)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // YESTERDAY Section
        Text(
            text = "YESTERDAY",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        yesterdayNotifications.forEach { item ->
            NotificationCard(item)
            Spacer(modifier = Modifier.height(8.dp))
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (item.isHighlighted) Color(0xFFFFB020).copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = if (item.isHighlighted) Color(0xFFFFB020) else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.time,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}