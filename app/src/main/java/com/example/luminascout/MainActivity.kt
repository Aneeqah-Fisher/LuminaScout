package com.example.luminascout

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.luminascout.Data.ScoutLocation
import com.example.luminascout.ui.AppBottomNavigationBar
import com.example.luminascout.ui.HomeScreen
import com.example.luminascout.ui.LocationDetailScreen
import com.example.luminascout.ui.LoginScreen
import com.example.luminascout.ui.NotificationsScreen
import com.example.luminascout.ui.PlanScreen
import com.example.luminascout.ui.RegisterScreen
import com.example.luminascout.ui.ScoutScreen
import com.example.luminascout.ui.SettingsScreen
import com.example.luminascout.ui.WeatherScreen
import com.example.luminascout.ui.theme.LuminaScoutTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val sharedPrefs =
            getSharedPreferences("LuminaScoutPrefs", Context.MODE_PRIVATE)

        setContent {
            LuminaScoutTheme {

                val savedUser =
                    sharedPrefs.getString("logged_in_user_name", "") ?: ""

                var currentScreen by remember {
                    mutableStateOf(
                        if (savedUser.isNotEmpty()) "home" else "register"
                    )
                }

                var activeTab by remember {
                    mutableStateOf("home")
                }

                var loggedInUserName by remember {
                    mutableStateOf(savedUser)
                }

                // Stores the location selected from the Scout screen
                var selectedLocation by remember {
                    mutableStateOf<ScoutLocation?>(null)
                }

                // Show bottom bar only on the main dashboard
                val showBottomBar = currentScreen == "home"

                Scaffold(
                    modifier = Modifier.statusBarsPadding(),
                    bottomBar = {
                        if (showBottomBar) {
                            AppBottomNavigationBar(
                                activeTab = activeTab,
                                onNavigate = { destination ->
                                    activeTab = destination
                                }
                            )
                        }
                    },
                    containerColor = Color(0xFF0F141C)
                ) { innerPadding ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {

                        when (currentScreen) {

                            // -------------------------------------------------
                            // REGISTER
                            // -------------------------------------------------
                            "register" -> {

                                RegisterScreen(
                                    onRegisterClick = { name, email, password ->

                                        val cleanEmail =
                                            email.trim().lowercase()

                                        val cleanName =
                                            name.trim()

                                        if (
                                            cleanEmail.isEmpty() ||
                                            password.isEmpty() ||
                                            cleanName.isEmpty()
                                        ) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Please fill in all fields",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            return@RegisterScreen
                                        }

                                        val salt =
                                            SecurityUtils.createSalt()

                                        val hashedPassword =
                                            SecurityUtils.hashPassword(
                                                password,
                                                salt
                                            )

                                        sharedPrefs.edit().apply {
                                            putString(
                                                "user_pwd_$cleanEmail",
                                                hashedPassword
                                            )

                                            putString(
                                                "user_salt_$cleanEmail",
                                                salt
                                            )

                                            putString(
                                                "user_name_$cleanEmail",
                                                cleanName
                                            )

                                            putString(
                                                "logged_in_user_name",
                                                cleanName
                                            )

                                            apply()
                                        }

                                        loggedInUserName = cleanName

                                        Toast.makeText(
                                            this@MainActivity,
                                            "Account created for $cleanName!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        currentScreen = "home"
                                        activeTab = "home"
                                    },

                                    onNavigateToLogin = {
                                        currentScreen = "login"
                                    }
                                )
                            }

                            // -------------------------------------------------
                            // LOGIN
                            // -------------------------------------------------
                            "login" -> {

                                LoginScreen(
                                    onLoginClick = { email, password ->

                                        val cleanEmail =
                                            email.trim().lowercase()

                                        val storedHash =
                                            sharedPrefs.getString(
                                                "user_pwd_$cleanEmail",
                                                null
                                            )

                                        val storedSalt =
                                            sharedPrefs.getString(
                                                "user_salt_$cleanEmail",
                                                null
                                            )

                                        if (
                                            storedHash != null &&
                                            storedSalt != null &&
                                            SecurityUtils.verifyPassword(
                                                password,
                                                storedHash,
                                                storedSalt
                                            )
                                        ) {

                                            val registeredName =
                                                sharedPrefs.getString(
                                                    "user_name_$cleanEmail",
                                                    "Scout"
                                                ) ?: "Scout"

                                            loggedInUserName =
                                                registeredName

                                            sharedPrefs.edit()
                                                .putString(
                                                    "logged_in_user_name",
                                                    registeredName
                                                )
                                                .apply()

                                            Toast.makeText(
                                                this@MainActivity,
                                                "Welcome back, $registeredName!",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            currentScreen = "home"
                                            activeTab = "home"

                                        } else {

                                            Toast.makeText(
                                                this@MainActivity,
                                                "Invalid credentials or user not registered",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    },

                                    onNavigateToRegister = {
                                        currentScreen = "register"
                                    },

                                    onForgotPasswordClick = {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Password recovery requested",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }

                            // -------------------------------------------------
                            // MAIN HOME
                            // -------------------------------------------------
                            "home" -> {

                                when (activeTab) {

                                    // HOME TAB
                                    "home" -> {

                                        HomeScreen(
                                            userName = loggedInUserName,
                                            onNavigate = { destination -> activeTab = destination },
                                            onNextShootClick = { currentScreen = "detail" },
                                            onNotificationClick = { currentScreen = "notifications" },
                                            onLocationClick = { location ->
                                                selectedLocation = location
                                                currentScreen = "detail"
                                            }
                                        )
                                    }

                                    // SCOUT TAB
                                    "scout" -> {

                                        ScoutScreen(

                                            // This now receives the actual
                                            // location that was tapped.
                                            onLocationClick = { location ->

                                                selectedLocation = location

                                                currentScreen = "detail"
                                            },

                                            onNavigate = { destination ->
                                                activeTab = destination
                                            }
                                        )
                                    }

                                    // PLAN TAB
                                    "plan" -> {

                                        PlanScreen(
                                            onNavigate = { destination ->
                                                activeTab = destination
                                            }
                                        )
                                    }

                                    // WEATHER TAB
                                    "weather" -> {

                                        WeatherScreen()
                                    }

                                    // SETTINGS TAB
                                    "settings" -> {

                                        SettingsScreen(
                                            userName = loggedInUserName,

                                            onNavigate = { destination ->
                                                activeTab = destination
                                            },

                                            onLogOut = {

                                                sharedPrefs.edit()
                                                    .remove(
                                                        "logged_in_user_name"
                                                    )
                                                    .apply()

                                                activeTab = "home"
                                                currentScreen = "login"

                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Logged out successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                    }

                                    // FALLBACK
                                    else -> {

                                        HomeScreen(
                                            userName = loggedInUserName,

                                            onNavigate = { destination ->
                                                activeTab = destination
                                            },

                                            onNextShootClick = {
                                                currentScreen = "detail"
                                            },

                                            onNotificationClick = {
                                                currentScreen = "notifications"
                                            }
                                        )
                                    }
                                }
                            }

                            // -------------------------------------------------
                            // LOCATION DETAIL
                            // -------------------------------------------------
                            "detail" -> {

                                selectedLocation?.let { location ->

                                    LocationDetailScreen(
                                        location = location,

                                        onBackClick = {
                                            currentScreen = "home"
                                            activeTab = "scout"
                                        }
                                    )
                                }
                            }

                            // -------------------------------------------------
                            // NOTIFICATIONS
                            // -------------------------------------------------
                            "notifications" -> {

                                NotificationsScreen(
                                    onBackClick = {
                                        currentScreen = "home"
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}