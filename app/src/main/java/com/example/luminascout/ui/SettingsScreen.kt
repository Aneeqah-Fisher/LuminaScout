package com.example.luminascout.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminascout.AppSettings

@Composable
fun SettingsScreen(
    userName: String,
    userEmail: String = "${userName.lowercase().replace(" ", "")}@luminascout.app",
    membershipStatus: String = "Pro Member",
    onNavigate: (String) -> Unit = {},
    onLogOut: () -> Unit = {}
) {

    val context = LocalContext.current

    // SharedPreferences allows settings to remain saved
    // after leaving and reopening the Settings screen.
    val preferences = remember {
        context.getSharedPreferences(
            "LuminaScoutSettings",
            Context.MODE_PRIVATE
        )
    }

    var notificationsEnabled by remember {
        mutableStateOf(
            preferences.getBoolean(
                "notifications",
                true
            )
        )
    }

    val selectedUnit = AppSettings.temperatureUnit

    var isDarkMode by remember {
        mutableStateOf(
            preferences.getBoolean(
                "dark_mode",
                true
            )
        )
    }

    var currentLanguage by remember {
        mutableStateOf(
            preferences.getString(
                "language",
                "English"
            ) ?: "English"
        )
    }

    val darkBackground = Color(0xFF0F141C)
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

        Spacer(modifier = Modifier.height(8.dp))

        // ---------------------------------------------------------
        // PROFILE HEADER
        // ---------------------------------------------------------

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardBackground
            ),
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
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(accentOrange),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = userName.ifBlank {
                            "User Account"
                        },
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = userEmail,
                        color = textMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = membershipStatus,
                        color = accentOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = {
                        Toast.makeText(
                            context,
                            "Edit Profile",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Color.White.copy(alpha = 0.08f)
                        )
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit Profile",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------------------------------------------------------
        // PREFERENCES
        // ---------------------------------------------------------

        Text(
            text = "PREFERENCES",
            color = textMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                start = 4.dp,
                bottom = 8.dp
            )
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardBackground
            ),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 4.dp
                )
            ) {

                // LANGUAGE
                SettingRow(
                    icon = Icons.Outlined.Language,
                    title = "Language",
                    textMuted = textMuted,
                    onClick = {

                        currentLanguage = when (currentLanguage) {
                            "English" -> "Afrikaans"
                            "Afrikaans" -> "isiXhosa"
                            else -> "English"
                        }

                        preferences.edit()
                            .putString(
                                "language",
                                currentLanguage
                            )
                            .apply()

                        Toast.makeText(
                            context,
                            "Language: $currentLanguage",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {

                    Text(
                        text = currentLanguage,
                        color = textMuted,
                        fontSize = 13.sp
                    )
                }

                Divider(
                    color = Color.White.copy(alpha = 0.06f)
                )

                // NOTIFICATIONS
                SettingRow(
                    icon = Icons.Outlined.Notifications,
                    title = "Notifications",
                    textMuted = textMuted
                ) {

                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { enabled ->

                            notificationsEnabled = enabled

                            preferences.edit()
                                .putBoolean(
                                    "notifications",
                                    enabled
                                )
                                .apply()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = accentOrange,
                            uncheckedThumbColor = textMuted,
                            uncheckedTrackColor =
                                Color.White.copy(alpha = 0.1f)
                        )
                    )
                }

                Divider(
                    color = Color.White.copy(alpha = 0.06f)
                )

                // UNITS
                SettingRow(
                    icon = Icons.Outlined.Thermostat,
                    title = "Units",
                    textMuted = textMuted
                ) {

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Color.White.copy(alpha = 0.08f)
                            )
                            .padding(2.dp)
                    ) {

                        UnitSegmentButton(
                            label = "°F",
                            isSelected = selectedUnit == "°F",
                            accentOrange = accentOrange
                        ) {
                            AppSettings.temperatureUnit = "°F"
                        }

                        UnitSegmentButton(
                            label = "°C",
                            isSelected = selectedUnit == "°C",
                            accentOrange = accentOrange
                        ) {
                            AppSettings.temperatureUnit = "°C"
                        }
                    }
                }

                Divider(
                    color = Color.White.copy(alpha = 0.06f)
                )

                // DARK MODE
                SettingRow(
                    icon = Icons.Outlined.DarkMode,
                    title = "Dark Mode",
                    textMuted = textMuted
                ) {

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { enabled ->

                            isDarkMode = enabled

                            preferences.edit()
                                .putBoolean(
                                    "dark_mode",
                                    enabled
                                )
                                .apply()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = accentOrange,
                            uncheckedThumbColor = textMuted,
                            uncheckedTrackColor =
                                Color.White.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------------------------------------------------------
        // ACCOUNT
        // ---------------------------------------------------------

        Text(
            text = "ACCOUNT",
            color = textMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                start = 4.dp,
                bottom = 8.dp
            )
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardBackground
            ),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 4.dp
                )
            ) {

                // SUBSCRIPTION
                SettingRow(
                    icon = Icons.Outlined.Tune,
                    title = "Subscription & Billing",
                    textMuted = textMuted,
                    onClick = {

                        Toast.makeText(
                            context,
                            "Navigating to Billing",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = textMuted
                    )
                }

                Divider(
                    color = Color.White.copy(alpha = 0.06f)
                )

                // SAVED LOCATIONS
                SettingRow(
                    icon = Icons.Outlined.Star,
                    title = "Saved Locations",
                    textMuted = textMuted,
                    onClick = {
                        onNavigate("scout")
                    }
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "3",
                            color = textMuted,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = textMuted
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---------------------------------------------------------
        // LOG OUT
        // ---------------------------------------------------------

        Button(
            onClick = {
                onLogOut()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2A1618),
                contentColor = Color(0xFFFF5252)
            )
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Log Out",
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Log Out",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// -------------------------------------------------------------
// SETTING ROW
// -------------------------------------------------------------

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    textMuted: Color,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable {
                        onClick()
                    }
                } else {
                    Modifier
                }
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF4A90E2),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        trailingContent()
    }
}

// -------------------------------------------------------------
// UNIT BUTTON
// -------------------------------------------------------------

@Composable
private fun UnitSegmentButton(
    label: String,
    isSelected: Boolean,
    accentOrange: Color,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isSelected) {
                    accentOrange
                } else {
                    Color.Transparent
                }
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 10.dp,
                vertical = 4.dp
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = label,
            color = if (isSelected) {
                Color.Black
            } else {
                Color(0xFF8C93A0)
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}