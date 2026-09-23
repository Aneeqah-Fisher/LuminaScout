package com.example.luminascout.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppBottomNavigationBar(
    activeTab: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        color = Color(0xFF131822),
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars) // <--- FIX FOR ALL SCREENS
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(Icons.Outlined.Home, "Home", activeTab == "home") { onNavigate("home") }
            NavItem(Icons.Outlined.Map, "Scout", activeTab == "scout") { onNavigate("scout") }
            NavItem(Icons.Outlined.CalendarToday, "Plan", activeTab == "plan") { onNavigate("plan") }
            NavItem(Icons.Outlined.Cloud, "Weather", activeTab == "weather") { onNavigate("weather") }
            NavItem(Icons.Outlined.Settings, "Settings", activeTab == "settings") { onNavigate("settings") }
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeColor = Color(0xFFFF9800)
    val inactiveColor = Color(0xFF8C93A0)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isSelected) activeColor else inactiveColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}