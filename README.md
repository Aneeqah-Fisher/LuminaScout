LuminaScout
SCOUT • PLAN • SHOOT
LuminaScout is an Android application designed to help outdoor photographers scout locations and plan photography sessions.
The application brings important photography information together in one place, including weather conditions, sunrise and sunset information, golden hour and blue hour information, saved locations, and photography planning features.
The goal of LuminaScout is to reduce the need for photographers to switch between multiple applications when preparing for an outdoor photography session.
________________________________________
Features
User Authentication
LuminaScout provides user registration and sign-in functionality.
The authentication system allows users to securely access their account and use the application's personalised features.
Passwords are handled using encryption/hashing mechanisms appropriate to the authentication implementation.
Settings
Users can access the application's settings and modify available preferences.
The settings interface has been designed to provide simple navigation and clear controls.
Weather Information
LuminaScout connects to a RESTful weather API to retrieve live weather information.
The Weather screen displays information such as:
•	Current temperature
•	Wind speed
•	Cloud cover
•	Precipitation probability
•	Sunrise
•	Sunset
•	Golden hour
•	Blue hour
•	Five-day weather forecast
Weather information is retrieved from the Open-Meteo API.
Solar Tracking
The application uses API-provided sunrise and sunset information to calculate useful photography periods.
Golden hour and blue hour information is presented to help photographers plan outdoor shoots around available natural light.
Saved Locations
Users can save photography locations for future reference.
Saved locations are displayed on the Home screen to provide quick access to places the user is interested in photographing.
Photography Planning
LuminaScout is designed around the workflow of:
Scout → Plan → Shoot
The application provides information that assists the photographer in deciding where and when to photograph while considering environmental conditions.
________________________________________
User Interface Design
LuminaScout uses a dark interface designed to remain readable in outdoor and low-light environments.
The design uses:
•	Deep charcoal / midnight navy backgrounds
•	Amber/golden-orange accents
•	Slate blue-grey cards
•	White primary text
•	Muted grey secondary text
•	Green status indicators
The interface uses cards, clear headings and simple navigation to make important photography information easy to identify.
The application's visual identity is based on a photography lens and location-pin concept.
________________________________________
REST API
LuminaScout uses a RESTful API to retrieve weather and solar information.
The Android application communicates with the API using Retrofit.
The API provides data including:
•	Current weather
•	Temperature
•	Wind speed
•	Cloud cover
•	Precipitation probability
•	Sunrise
•	Sunset
•	Forecast information
The API data is converted into Kotlin data models before being displayed in the application's user interface.
API Technology
•	Retrofit
•	Gson
•	Kotlin Coroutines
•	Open-Meteo Weather API
________________________________________
External Libraries and SDKs
The project makes use of external libraries and Android development tools to solve specific programming requirements.
Key technologies include:
•	Kotlin
•	Jetpack Compose
•	Android SDK
•	Retrofit
•	Gson
•	Gradle
•	GitHub
•	GitHub Actions
Retrofit is used to simplify communication between the Android application and the REST API.
________________________________________
Testing
Automated testing is used to test the main functionality of the application.
Unit tests were initially executed through Android Studio.
GitHub Actions is also configured to automatically run the project's tests and build the Android application whenever changes are pushed to the main branch.
The automated workflow performs:
1.	Checkout of the source code
2.	Java setup
3.	Gradle setup
4.	Unit testing
5.	Android application build
This provides an additional check that the project can be built outside the development computer.
GitHub Actions Status
The GitHub Actions workflow is called:
Android Build and Test
A successful workflow run confirms that the automated tests and Android build completed successfully.
________________________________________
Version Control
Git and GitHub are used for version control throughout the project.
The project is stored in a GitHub repository and development progress is recorded using commits.
Example commit:
Initial LuminaScout project
Further development changes are committed and pushed to GitHub so that the project history can be tracked.
________________________________________
Logging
Logging has been incorporated into the application to assist with monitoring application behaviour and debugging.
Log messages are used when appropriate to help identify events such as API requests, data processing and application errors.
________________________________________
Error Handling
The application is designed to handle invalid input and API-related problems without unnecessarily crashing.
Where possible, users are provided with appropriate feedback when information cannot be processed or retrieved.
________________________________________
Project Structure
The project follows an Android/Kotlin structure with separate areas for:
•	User interface
•	Data models
•	API communication
•	ViewModels
•	Application navigation
The separation of these responsibilities helps keep the application organised and maintainable.
________________________________________
GitHub Actions
GitHub Actions is used to automate testing and building.
The workflow is stored at:
.github/workflows/build.yml
The workflow automatically runs when code is pushed to the main branch or when a pull request targets main.
________________________________________
Demonstration Video
The demonstration video shows the LuminaScout prototype running on an Android mobile device.
The demonstration covers:
•	User registration
•	User login
•	Password security
•	Settings
•	REST API functionality
•	Weather information
•	Solar information
•	Golden hour and blue hour
•	Saved locations
•	Other implemented application features
•	Online authentication/API/database data where applicable
Video: 
________________________________________
Screenshots
Home Screen
<img width="738" height="1600" alt="image" src="https://github.com/user-attachments/assets/be71a897-6f98-45b4-8b33-91afdb70a1cb" />

Weather Screen
 <img width="738" height="1600" alt="image" src="https://github.com/user-attachments/assets/7ecceb94-83e5-40bf-8603-2e0101d776ae" />

Settings
<img width="738" height="1600" alt="image" src="https://github.com/user-attachments/assets/ea69dd7f-b4fe-4681-b563-1b78d71b42cb" />

Login / Registration
<img width="738" height="1600" alt="image" src="https://github.com/user-attachments/assets/c9f7899a-27b4-410e-b059-5e57a27bef63" />
<img width="738" height="1600" alt="image" src="https://github.com/user-attachments/assets/13443a63-8214-4ff0-8891-215bc91aad78" />

 
 

________________________________________
Development
Requirements
To run the project locally:
•	Android Studio
•	Android SDK
•	JDK
•	Internet connection for API functionality
Running the Project
1.	Clone the GitHub repository.
2.	Open the project in Android Studio.
3.	Allow Gradle dependencies to synchronise.
4.	Connect an Android device or start an Android emulator.
5.	Run the application.
________________________________________
References
Open-Meteo API: https://open-meteo.com/
Android Developers: https://developer.android.com/
GitHub Actions: https://github.com/features/actions
Retrofit: https://square.github.io/retrofit/
________________________________________
Author:
Aneeqah Fisher
Android application prototype developed for the Part 2 project.
Project: LuminaScout2

