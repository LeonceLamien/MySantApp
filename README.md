<div align="center">

# MySantApp

*Healthy lifestyle tracking for everyday progress*

![Android](https://img.shields.io/badge/Android-API%2024%2B-3DDC84?logo=android)
![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk)
![Firebase](https://img.shields.io/badge/Firebase-Realtime%20DB-FFCA28?logo=firebase)
![Mapbox](https://img.shields.io/badge/Mapbox-Maps%20SDK-3A4A5C?logo=mapbox)
![License](https://img.shields.io/badge/License-Open%20Project-green.svg)

[Overview](#overview) • [Features](#features) • [Tech Stack](#tech-stack) • [Screenshots](#screenshots) • [Installation](#installation) • [Project Structure](#project-structure)

</div>

---

## Overview

MySantApp is a mobile health and wellness application designed to help users monitor their daily physical activity, body metrics, and lifestyle trends in a simple and motivating way.

The app brings together several health-oriented features into one Android experience:
- daily step tracking with motion and GPS support
- BMI calculation and progress analysis
- personalized health dashboard
- user authentication and local profile management
- cloud-backed data storage for monitoring progress over time

This project was built as a practical health-tech application prototype, with a focus on usability, motivation, and data tracking for everyday well-being.

### Why this project

In a world where health is increasingly data-driven, I wanted to create a mobile app that turns everyday habits into measurable progress. The goal is to provide users with a clear overview of their activity and physical health without overwhelming them.

---

## Features

### 1. User authentication
- sign up and login flow
- SQLite-based validation for local user accounts
- simple onboarding experience for a first-time user

### 2. Daily step tracking
- step counter integration using Android sensors
- distance and pace estimation
- activity session start/end workflow
- map-based tracking support with geolocation

### 3. BMI and health metrics
- weight and height entry
- BMI calculation with status feedback
- historical records for comparing progress over time
- chart visualization for health evolution

### 4. Health dashboard
- summary of activity and progress
- visual insights through analytics charts
- activity-driven motivation and personalized health reporting

### 5. Firebase-backed data storage
- cloud sync for recorded BMI and running sessions
- scalable structure for future app expansion
- potential for user data persistence across devices

---

## Tech Stack

- Android Native: Java
- UI: Android XML layouts + Material Components
- Local storage: SQLite
- Real-time data: Firebase Realtime Database
- Maps: Mapbox SDK
- Charts: Highcharts Android
- Sensors: Android Sensor APIs + Location APIs
- Build: Gradle / Android Studio

---

## Screenshots

<div align="center">

![App preview](app/src/main/res/drawable/preview.jpg)

</div>

### App concept and user experience

The interface is designed around a clean, modern health dashboard with strong visual cues for activity, movement, and wellness. It targets users who want a lightweight app that feels motivating and easy to use daily.

---

## Installation

### Prerequisites

- Android Studio
- JDK 17+
- Android SDK 24+
- Firebase project configured
- Mapbox access token

### Steps

```bash
# Clone the project
git clone https://github.com/your-username/MySantApp.git

# Open in Android Studio
cd MySantApp

# Sync Gradle and run the app
./gradlew assembleDebug
```

### Firebase configuration

1. Create a Firebase project.
2. Add the Android app package name.
3. Download `google-services.json`.
4. Place it in the `app/` directory.
5. Enable Realtime Database / Authentication if needed.

### Mapbox configuration

Add your Mapbox token in the relevant Mapbox initialization code before running the app.

---

## Project Structure

```text
MySantApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/my_health_app/
│   │   │   │   ├── ui/
│   │   │   │   │   ├── home/
│   │   │   │   │   ├── imc/
│   │   │   │   │   ├── step/
│   │   │   │   │   ├── DatabaseHelper.java
│   │   │   │   │   ├── LoginActivity.java
│   │   │   │   │   ├── RegisterActivity.java
│   │   │   │   │   └── ...
│   │   │   ├── res/
│   │   │   │   ├── drawable/
│   │   │   │   ├── layout/
│   │   │   │   ├── values/
│   │   │   │   └── navigation/
│   │   └── test/
│   ├── build.gradle.kts
│   └── google-services.json
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
├── build.gradle.kts
├── README.md
└── .gitignore
```

---

## Key development highlights

- built around a health and fitness user journey
- combines sensors, geolocation, and analytics in one application
- demonstrates both local persistence and cloud-connected features
- designed to be scalable for adding meal tracking, reminders, and coaching features

---

## Future improvements

- add a more advanced user profile and health history
- integrate workout recommendations and goal setting
- improve dashboards with personalized analytics
- add notifications and reminders
- strengthen app security and data validation
- migrate to modern architecture patterns such as MVVM + Repository

---

## Authors

This project was developed as a personal mobile app project focused on health, lifestyle tracking, and digital health innovation.

---

## License

This project is currently shared for portfolio and learning purposes.

---

<div align="center">

Built with care for a healthier, more active lifestyle.

</div>
