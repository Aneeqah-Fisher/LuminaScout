package com.example.luminascout

import android.content.Context
import android.content.res.Configuration
import java.util.Locale
import android.os.Bundle
import android.os.Build
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.delay
import com.example.luminascout.ui.SplashScreen
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.luminascout.Data.ScoutLocation
import com.example.luminascout.ui.AppBottomNavigationBar
import com.example.luminascout.ui.AIAssistantScreen
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
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("LuminaScoutSettings", Context.MODE_PRIVATE)
        val language = when (prefs.getString("language", "English")) {
            "Afrikaans" -> "af"
            "isiXhosa" -> "xh"
            else -> "en"
        }
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(Locale.forLanguageTag(language))
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }


    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(this, "Notifications are disabled. You can enable them in Settings.", Toast.LENGTH_LONG).show()
        }
    }


    private fun signInWithGoogle(
        onSuccess: (String) -> Unit
    ) {
        val credentialManager = CredentialManager.create(this)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = this@MainActivity,
                    request = request
                )

                val credential = result.credential

                if (
                    credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)

                    val firebaseCredential =
                        GoogleAuthProvider.getCredential(
                            googleCredential.idToken,
                            null
                        )

                    FirebaseAuth.getInstance()
                        .signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(this@MainActivity) { task ->
                            if (task.isSuccessful) {
                                val user = FirebaseAuth.getInstance().currentUser

                                val name = user?.displayName
                                    ?: user?.email
                                    ?: "Scout"

                                onSuccess(name)
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    task.exception?.localizedMessage
                                        ?: "Google sign-in failed",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Google sign-in was not completed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Google sign-in cancelled or failed: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    android.util.Log.d(
                        "LuminaScoutFCM",
                        "FCM TOKEN: ${task.result}"
                    )
                } else {
                    android.util.Log.e(
                        "LuminaScoutFCM",
                        "Failed to get FCM token",
                        task.exception
                    )
                }
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val sharedPrefs =
            getSharedPreferences("LuminaScoutPrefs", Context.MODE_PRIVATE)

        setContent {
            LuminaScoutTheme(darkTheme = true) {

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
                var loggedInUserEmail by remember {
                    mutableStateOf(sharedPrefs.getString("logged_in_user_email", "") ?: "")
                }

                // Stores the location selected from the Scout screen
                var selectedLocation by remember {
                    mutableStateOf<ScoutLocation?>(null)
                }

                // Display the branded splash screen once per app launch.
                var showSplash by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    delay(2000L)
                    showSplash = false
                }

                // Show bottom bar only on the main dashboard
                val showBottomBar = currentScreen == "home"

                if (showSplash) {
                    SplashScreen()
                } else {
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
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background
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
                                            loggedInUserEmail = cleanEmail
                                            sharedPrefs.edit().putString("logged_in_user_email", cleanEmail).apply()

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
                                                loggedInUserEmail = cleanEmail

                                                sharedPrefs.edit()
                                                    .putString(
                                                        "logged_in_user_name",
                                                        registeredName
                                                    )
                                                    .putString("logged_in_user_email", cleanEmail)
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
                                        onGoogleSignInClick = {
                                            signInWithGoogle { name ->

                                                loggedInUserName = name
                                                loggedInUserEmail =
                                                    FirebaseAuth.getInstance().currentUser?.email.orEmpty()

                                                sharedPrefs.edit()
                                                    .putString("logged_in_user_name", name)
                                                    .putString("logged_in_user_email", loggedInUserEmail)
                                                    .apply()

                                                currentScreen = "home"
                                                activeTab = "home"

                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Welcome, $name!",
                                                    Toast.LENGTH_SHORT
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
                                                onNavigate = { destination ->
                                                    if (destination == "ai_assistant") currentScreen = destination
                                                    else activeTab = destination
                                                },
                                                onNextShootClick = { activeTab = "scout" },
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
                                                userEmail = loggedInUserEmail,

                                                onNavigate = { destination ->
                                                    activeTab = destination
                                                },

                                                onLanguageChanged = {
                                                    // Recreate the Activity to refresh all stringResource() labels.
                                                    recreate()
                                                },
                                                onLogOut = {
                                                    FirebaseAuth.getInstance().signOut()
                                                    sharedPrefs.edit()
                                                        .remove(
                                                            "logged_in_user_name"
                                                        )
                                                        .remove("logged_in_user_email")
                                                        .apply()
                                                    loggedInUserName = ""
                                                    loggedInUserEmail = ""

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
                                                    if (destination == "ai_assistant") currentScreen = destination
                                                    else activeTab = destination
                                                },

                                                onNextShootClick = {
                                                    activeTab = "scout"
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
                                            },
                                            onLocationSaved = { updated ->
                                                selectedLocation = updated
                                            }
                                        )
                                    }
                                }

                                // -------------------------------------------------
                                // AI PHOTOGRAPHY ASSISTANT
                                // -------------------------------------------------
                                "ai_assistant" -> {
                                    AIAssistantScreen(
                                        onBackClick = {
                                            currentScreen = "home"
                                            activeTab = "home"
                                        }
                                    )
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
                } // End of splash screen conditional
            }
        }
    }
}