Name: Baldwin Sphesihle Mazibuko
Student Number: 402310972

===Smart Pantry Android Application===
A smart, intuitive Android application built to help users manage their pantry inventory, track food item expiration dates, minimize food waste, and streamline meal planning.

--Features--
     +Inventory Management: Track, organize, and monitor pantry and kitchen items in real time.
     +Expiration Tracking: Receive notifications and alerts for items nearing their expiration dates to reduce waste.
     +Category & Storage Organization: Categorize items by location (e.g., Pantry, Refrigerator, Freezer) or type (e.g., Produce, Dairy, Dry Goods).
     +Search & Filter: Easily find items in your pantry using built-in search and filtering functionality.
     +Modern Android UI: Clean and user-friendly interface designed according to Android Material Design guidelines.

--Tech Stack & Tools--
     +Language: Kotlin
     +Build System: Gradle (Version 9.5.0, Kotlin DSL)
     +Target OS: Android (API level configured in app/build.gradle.kts)
     +Architecture: MVVM (Model-View-ViewModel) / Clean Architecture
     +UI Framework: Android Jetpack / Material Components

--Project Structure--
    SmartPantry/
|-- app/
|   |-- build.gradle.kts     # Module-level Gradle configuration
|   |-- src/                 # Application source code and layout resources
|-- build.gradle.kts         # Top-level Gradle configuration
|-- settings.gradle.kts      # Project settings
|-- .gradle/

--Getting Started--
    Prerequisites:
      +Android Studio: Ladybug (or newer recommended)
      +JDK: Java 17 or higher
      +Android SDK: API Level 24 (Android 7.0) or higher

1.Clone the repository:
    Bash:  git clone https://github.com/mazibukobaldwin55/smart-pantry-android.git
           cd smart-pantry-android
    Powershell: git clone https://github.com/mazibukobaldwin55/smart-pantry-android.git
                cd smart-pantry-android

2. Open in Android Studio:
    +Launch Android Studio.
    +Select Open an Existing Project.
    +Navigate to the cloned directory and click OK.

3. Sync Gradle:
    +Allow Android Studio to download dependencies and sync Gradle automatically.

4. Run the Application:
    +Connect an Android device via USB (with Debugging enabled) or start an Android Emulator.
    +Click the Run button (Shift + F10 or the green play icon) in Android Studio
