package com.example.luminascout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminascout.Data.ScoutLocation

@Composable
fun LocationDetailScreen(
    location: ScoutLocation,
    onBackClick: () -> Unit = {}
) {
    // Tab State: "Info", "Weather", "Gear"
    var selectedTab by remember { mutableStateOf("Info") }

    // Interactive Gear State
    val gearList = remember {
        mutableStateListOf(
            Pair("Camera Body (e.g. Sony A7 IV)", true),
            Pair("Wide Angle Lens (16-35mm f/2.8)", true),
            Pair("Heavy Duty Tripod (Windy Ridge)", true),
            Pair("ND Filter Set (0.9 / 1.2)", false),
            Pair("Extra Batteries (x3)", false)
        )
    }

    val darkBackground = Color(0xFF0F141C)
    val cardBackground = Color(0xFF1B222D)
    val accentOrange = Color(0xFFFF9800)
    val textMuted = Color(0xFF8C93A0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // --- HEADER HERO SECTION WITH BACK & EDIT BUTTONS ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color(0xFF2C3545)) // Dark hero container placeholder
            ) {
                // Top Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.4f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = { /* Edit Action */ },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.4f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = accentOrange
                        )
                    }
                }

                // Title overlay inside Hero Header
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = accentOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "94 / 100",
                            color = accentOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = location.name,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = textMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${location.latitude}°, ${location.longitude}° • Cape Town",
                            color = textMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- TAB SELECTOR (Info | Weather | Gear) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Info", "Weather", "Gear").forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (isSelected) accentOrange else cardBackground)
                            .clickable { selectedTab = tab },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- DYNAMIC CONTENT BASED ON TAB ---
            when (selectedTab) {
                "Info" -> InfoTabContent(cardBackground, accentOrange, textMuted)
                "Weather" -> WeatherTabContent(cardBackground, accentOrange, textMuted)
                "Gear" -> GearTabContent(gearList, cardBackground, accentOrange, textMuted)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- 1. INFO TAB CONTENT
@Composable
private fun InfoTabContent(cardBg: Color, accent: Color, textMuted: Color) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp) // Reduced spacing between cards
    ) {
        // Best Shooting Time Card
        Card(
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) { // Reduced padding
                Text(
                    text = "BEST SHOOTING TIME",
                    color = textMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Golden Hour — 6:42 PM",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Duration ~33 min • West-facing ocean sunset view",
                            color = textMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Notes Card
        Card(
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "NOTES",
                    color = textMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Park near Signal Hill Road parking area. High vantage point facing Atlantic Seaboard. Strong winds common—use heavy tripod.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Photography Score Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "PHOTOGRAPHY SCORE",
                    color = textMuted,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circle Score Badge
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(accent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "94",
                            color = accent,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Compact Progress Bars
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ScoreBar("Composition", 0.95f, accent, textMuted)
                        ScoreBar("Light Quality", 0.92f, accent, textMuted)
                        ScoreBar("Accessibility", 0.88f, accent, textMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreBar(label: String, progress: Float, accent: Color, textMuted: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = textMuted, fontSize = 13.sp)
            Text(text = "${(progress * 100).toInt()}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = accent,
            trackColor = Color.White.copy(alpha = 0.1f)
        )
    }
}
// --- 2. WEATHER TAB CONTENT ---
@Composable
private fun WeatherTabContent(cardBg: Color, accent: Color, textMuted: Color) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        WeatherItemCard(Icons.Outlined.Thermostat, "Temperature", "20°C / 68°F", cardBg, textMuted)
        WeatherItemCard(Icons.Outlined.Air, "Wind", "18 km/h SE (South-Easter)", cardBg, textMuted)
        WeatherItemCard(Icons.Outlined.Cloud, "Cloud Cover", "15%", cardBg, textMuted)
        WeatherItemCard(Icons.Outlined.WaterDrop, "Rain Probability", "0%", cardBg, textMuted)
    }
}

@Composable
private fun WeatherItemCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    cardBg: Color,
    textMuted: Color
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = title, color = textMuted, fontSize = 14.sp)
            }
            Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

// --- 3. GEAR TAB CONTENT ---
@Composable
private fun GearTabContent(
    gearList: MutableList<Pair<String, Boolean>>,
    cardBg: Color,
    accent: Color,
    textMuted: Color
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        gearList.forEachIndexed { index, item ->
            val isChecked = item.second
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { gearList[index] = item.copy(second = !isChecked) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isChecked) accent else Color.White.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isChecked) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = item.first,
                        color = if (isChecked) Color.White else textMuted,
                        fontSize = 14.sp,
                        fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}