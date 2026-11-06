📸 LuminaScout
SCOUT • PLAN • SHOOT
LuminaScout is an Android application designed to help outdoor photographers scout photography locations, monitor weather and lighting conditions, and plan photoshoots.
The application brings together weather forecasts, sunrise and sunset information, golden-hour and blue-hour tracking, interactive location scouting, shoot planning and reminders.
LuminaScout aims to reduce the need for photographers to switch between multiple applications when preparing for an outdoor photography session.
Module: OPSC6312
Assessment: Final Portfolio of Evidence (2026)
Developer: Aneeqah Fisher
Platform: Android
Programming Language: Kotlin
User Interface: Jetpack Compose
________________________________________
🎯 Application Purpose
Outdoor photography requires planning around location, weather, lighting conditions and available equipment.
LuminaScout supports photographers through three stages:
SCOUT → PLAN → SHOOT
•	Scout: Explore and save photography locations.
•	Plan: Check weather and lighting conditions and organise upcoming shoots.
•	Shoot: Prepare equipment and shot lists and review scheduled plans.
________________________________________
✨ Application Features
1. User Authentication
LuminaScout supports user registration and login.
Features include:
•	Email and password registration and login
•	Password hashing for locally stored credentials
•	Google Sign-In using Firebase Authentication
•	User account information in Settings
•	Logout functionality
Google Sign-In uses Firebase Authentication, while email/password authentication is implemented locally.
2. Weather Information
LuminaScout integrates the Open-Meteo REST API to provide weather information useful for photography.
The Weather screen displays:
•	Temperature
•	Wind speed
•	Cloud cover
•	Precipitation probability
•	Sunrise and sunset
•	Golden-hour and blue-hour information
•	Multi-day forecasts
Weather information helps users choose suitable conditions for outdoor photography.
3. Golden Hour and Blue Hour
The application provides sunrise, sunset, golden-hour and blue-hour information.
These lighting periods are particularly useful for outdoor photographers because natural light changes significantly throughout the day.
This information is displayed within the Weather screen.
4. Interactive Scouting Map
LuminaScout uses Google Maps to support location scouting.
Users can:
•	Explore locations on an interactive map
•	Save photography locations as map pins
•	View previously saved locations
•	Search saved locations
•	Move saved pins
•	Open and edit location details
5. Offline Location Storage and Firestore Synchronisation
Saved scouting locations are stored locally to support offline access.
Firebase Cloud Firestore is used to synchronise scouting location information for supported authenticated sessions.
This allows photographers to continue accessing saved locations when network connectivity is limited.
6. Shoot Planner
The Shoot Planner allows users to organise photography sessions.
Users can:
•	Create and save shoot plans
•	Enter a shoot name
•	Select a date and time
•	Add a location
•	Add notes
•	Manage a gear checklist
•	Manage a shot list
Shoot plans are stored locally on the device.
7. Reminders and Push Notifications
LuminaScout includes a Reminders screen and push notification functionality to help photographers stay informed about upcoming shoots and important photography events.
The application supports:
•	Viewing saved photography plans in the Reminders screen
•	Reviewing scheduled shoot dates, times and locations
•	Receiving push notifications through Firebase Cloud Messaging (FCM)
•	Scheduled golden-hour reminders using Android WorkManager
Firebase Cloud Messaging was integrated and tested on a physical Android device.
8. Multilingual Support
LuminaScout supports three languages:
•	English
•	Afrikaans
•	isiXhosa
Users can change their language preference through Settings.
9. Photography Advice
LuminaScout includes rule-based photography advice that provides suggestions based on weather-related conditions.
The advice feature is intended to assist photographers in making practical planning decisions.
9. LuminaScout AI Photography Assistant
LuminaScout includes an AI Photography Assistant designed to help photographers make better decisions when planning outdoor photoshoots.
The assistant uses weather-related information to provide practical photography recommendations based on current conditions.
Features include:
•	Photography advice based on weather conditions
•	Recommendations related to rain, wind and outdoor shooting
•	Suggestions for protecting photography equipment
•	Golden-hour estimates to assist with planning
•	An interactive Ask LuminaScout AI feature
•	Guidance to help users prepare for changing weather conditions
For example, when the probability of rain is high, the assistant may recommend planning an indoor backup location and protecting camera equipment.
The current implementation uses rule-based decision logic rather than a live generative AI service. This provides immediate photography guidance without requiring a separate AI API.
The assistant supports LuminaScout's goal of combining scouting, planning and photography guidance into one application.
10. Settings
The Settings screen provides access to available application preferences and account options, including language selection and logout.
________________________________________
💡 Two Innovative Features
The Final PoE highlights two user-defined features.
Innovative Feature 1: Interactive Photography Scouting Map
The Scouting Map allows photographers to identify and save locations, manage pins, edit saved location information and access location data offline.
Cloud synchronisation extends this functionality by supporting the storage of scouting locations in Firebase Cloud Firestore.
Innovative Feature 2: Photography Shoot Planner
The Shoot Planner helps users organise photography sessions by recording the shoot date, time, location and notes.
Editable gear checklists and shot lists help photographers prepare for their sessions.
Together, these features support LuminaScout's Scout → Plan → Shoot workflow.
________________________________________
🎨 User Interface Design
LuminaScout uses a dark interface designed to remain readable in outdoor and low-light environments.
The design includes:
•	Deep navy and charcoal backgrounds
•	Amber and golden-orange accents
•	Slate blue-grey cards
•	White primary text
•	Muted grey secondary text
•	Clear headings and bottom navigation
•	Photography-inspired branding
The Final PoE version includes a custom launcher icon and splash screen.
The interface was designed using Jetpack Compose.
________________________________________
🛠️ Technologies Used
Technology	Purpose
Kotlin	Android development
Jetpack Compose	User interface
Android Studio	Development environment
Retrofit	REST API communication
Gson	JSON processing
Kotlin Coroutines	Asynchronous tasks
Open-Meteo API	Weather forecasts
Google Maps SDK	Interactive scouting map
Firebase Authentication	Google Sign-In
Cloud Firestore	Cloud synchronisation of scouting locations
Firebase Cloud Messaging	Push notifications
WorkManager	Scheduled reminder tasks
SharedPreferences	Local application data
Gradle	Build automation
Git and GitHub	Version control
GitHub Actions	Automated tests and builds
________________________________________
🌐 REST API Integration
LuminaScout retrieves weather and forecast data from the Open-Meteo API.
Retrofit is used for API communication, while Gson processes JSON responses into Kotlin data models.
The application uses this information to display current weather, forecasts and solar information.
 <img width="940" height="369" alt="image" src="https://github.com/user-attachments/assets/f0c56e0c-b079-44cd-9d45-f1abc32e6f23" />

________________________________________
🔥 Firebase Integration
LuminaScout uses Firebase services for selected features.
Firebase Authentication: Supports Google Sign-In.
Cloud Firestore: Stores and synchronises saved scouting location information.
Firebase Cloud Messaging: Supports push notifications.
The project requires Firebase configuration to build and use these services.
The google-services.json file is not included in the public GitHub repository. The GitHub Actions workflow restores it using a repository secret.
________________________________________
🧪 Testing
Testing is used to check the application's functionality and reliability.
Testing areas include:
•	Registration and login
•	Weather API integration
•	Saved scouting locations
•	Offline location access
•	Shoot Planner functionality
•	Navigation between screens
•	Error handling
•	Firebase integration
Unit-test tasks are executed through Gradle, and automated builds are performed using GitHub Actions.
A successful build confirms that the project compiled and the configured test task completed. Individual test results should be reviewed separately to confirm which test cases executed.
________________________________________
⚙️ GitHub Actions
The GitHub Actions workflow is located at:
.github/workflows/build.yml
The workflow is named Android Build and Test.
It performs the following steps:
1.	Checks out the source code.
2.	Sets up Java.
3.	Configures Gradle.
4.	Restores Firebase configuration from a GitHub repository secret.
5.	Runs the Gradle unit-test task.
6.	Builds a debug APK.
The workflow runs for configured pushes and pull requests, helping confirm that the project can build outside the developer's computer.
GitHub Actions: View workflow runs
________________________________________
🧭 GitHub and Version Control
Git and GitHub are used to manage LuminaScout's source code and development history.
The repository contains Kotlin source code, application resources, Gradle configuration and the GitHub Actions workflow.
Development is recorded through commits and branches.
The final-poe-2026 branch contains the Final PoE development work.
Repository: https://github.com/Aneeqah-Fisher/LuminaScout
________________________________________
🧯 Error Handling and Logging
LuminaScout includes error handling to reduce unnecessary crashes when users provide invalid input or when network services are unavailable.
Logging is used during development to assist with identifying application errors and troubleshooting API or application behaviour.
________________________________________
📂 Project Structure
The Android project is organised into components for:
•	User interface screens
•	Application navigation
•	Data models
•	API communication
•	Local data storage
•	Firebase integration
•	Android resources
•	Gradle configuration
•	Automated build workflow
________________________________________
▶️ Running the Application
Requirements
•	Android Studio
•	Android SDK
•	Compatible JDK
•	Android emulator or physical Android device
•	Internet connection for online features
•	Firebase configuration
•	Google Maps API key
Setup
1.	Clone the repository:
git clone https://github.com/Aneeqah-Fisher/LuminaScout.git
2.	Open the project in Android Studio.
3.	Allow Gradle to synchronise.
4.	Add the Firebase configuration file to app/google-services.json.
5.	Configure MAPS_API_KEY in your local local.properties file.
6.	Connect an Android device or start an emulator.
7.	Build and run the application.
________________________________________
🚀 Improvements Since Part 2
The Final PoE version expands the original LuminaScout prototype.
Improvements include:
•	Google Sign-In
•	Firebase Authentication integration
•	Cloud Firestore synchronisation
•	Offline scouting location access
•	Interactive map improvements
•	Saved location editing
•	Shoot Planner with checklists
•	Reminders and notifications
•	Multilingual support
•	Photography advice
•	Custom app icon and splash screen
•	GitHub Actions automation
•	Signed Android App Bundle generation
•	LuminaScout AI Photography Assistant with weather-based photography recommendations and an interactive advice interface.
•	Firebase Cloud Messaging integration with successfully tested push notifications on a physical Android device.
•	Scheduled golden-hour reminders using Android WorkManager.

________________________________________
📦 Google Play Store Preparation
LuminaScout has been prepared for a potential Google Play Store release.
Completed preparation includes:
•	Successful signed release build
•	Generated Android App Bundle (app-release.aab)
The app has not been uploaded or published through Google Play Console.
Further publication requirements, including privacy documentation, account deletion support where applicable, release configuration testing , Play Console verification, customer application icon, application screenshots and play store listing information must be completed before actual publication.
________________________________________
🎥 Demonstration Video
Part 2 Prototype Demonstration
https://youtu.be/TJxMK99YYz4
Final PoE Demonstration
https://youtu.be/48xlrMmeHv8?si=QQwupa852jfKwrMo
The final demonstration covers the application's features, Google Sign-In, weather API, location scouting, shoot planning, reminders and multilingual support.
________________________________________
LuminaScout — Scout smarter. Plan better. Shoot with confidence.

