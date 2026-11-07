package com.example.luminascout.ui

import android.content.Context
import android.content.res.Configuration
import java.util.Locale
import com.example.luminascout.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun SettingsScreen(
    userName: String,
    userEmail: String = "",
    membershipStatus: String = "Pro Member",
    onNavigate: (String) -> Unit = {},
    onLogOut: () -> Unit = {},
    onLanguageChanged: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
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

    // Keep compatibility with the language preference already stored by LuminaScout.
    var currentLanguage by remember {
        mutableStateOf(preferences.getString("language", "English") ?: "English")
    }
    var showLanguageDialog by remember { mutableStateOf(false) }

    val languageCode = when (currentLanguage) {
        "Afrikaans" -> "af"
        "isiXhosa" -> "xh"
        else -> "en"
    }
    val currentConfiguration = LocalConfiguration.current

    val localizedContext = remember(context, languageCode, currentConfiguration) {
        val configuration = Configuration(currentConfiguration)
        configuration.setLocale(Locale.forLanguageTag(languageCode))
        context.createConfigurationContext(configuration)
    }
    fun translated(id: Int): String = localizedContext.getString(id)

    val darkBackground = Color(0xFF0F141C)
    val cardBackground = Color(0xFF1B222D)
    val foreground = Color.White
    val accentOrange = Color(0xFFFF9800)
    val textMuted = foreground.copy(alpha = 0.62f)

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(translated(R.string.ls_choose_language)) },
            text = {
                Column {
                    listOf("English", "Afrikaans", "isiXhosa").forEach { language ->
                        TextButton(
                            onClick = {
                                currentLanguage = language
                                preferences.edit().putString("language", language).apply()
                                showLanguageDialog = false
                                onLanguageChanged()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(language)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(translated(R.string.ls_cancel))
                }
            }
        )
    }

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
                            translated(R.string.ls_user_account)
                        },
                        color = foreground,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    if (userEmail.isNotBlank()) {
                        Text(
                            text = userEmail,
                            color = textMuted,
                            fontSize = 12.sp
                        )
                    }

                }

            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------------------------------------------------------
        // PREFERENCES
        // ---------------------------------------------------------

        Text(
            text = translated(R.string.ls_preferences),
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
                    title = translated(R.string.ls_language),
                    textMuted = textMuted,
                    onClick = { showLanguageDialog = true }
                ) {

                    Text(
                        text = currentLanguage,
                        color = textMuted,
                        fontSize = 13.sp
                    )
                }

                Divider(
                    color = foreground.copy(alpha = 0.10f)
                )

                // NOTIFICATIONS
                SettingRow(
                    icon = Icons.Outlined.Notifications,
                    title = translated(R.string.ls_notifications),
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
                    color = foreground.copy(alpha = 0.10f)
                )

                // UNITS
                SettingRow(
                    icon = Icons.Outlined.Thermostat,
                    title = translated(R.string.ls_units),
                    textMuted = textMuted
                ) {

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                foreground.copy(alpha = 0.08f)
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
                }            }
        }

        Spacer(modifier = Modifier.height(20.dp))

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
                    contentDescription = translated(R.string.ls_log_out),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = translated(R.string.ls_log_out),
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