# Words 📚

**Words** is a modern, offline-first Android vocabulary learning and management app built with **Jetpack Compose** and **Clean Architecture**. It allows users to manage custom vocabulary, view rich word definitions and example sentences, switch application languages dynamically, import words from JSON, and display daily vocabulary updates on the Android home screen using a **Jetpack Glance** widget.

---

## ✨ Features

- 📖 **Vocabulary Directory**: View a list of vocabulary words with definitions, translations, phonetic pronunciations, and example usage.
- 🔍 **Search & Filter**: Find words quickly with real-time search functionality.
- ➕ **Add & Edit Words**: Add new custom words or update existing word entries.
- ⚙️ **Settings & Multi-language Support**: Support for English and Arabic locales using Android's per-app language preferences (`AppCompatDelegate`).
- 📥 **JSON Data Import**: Easily import large vocabulary datasets from external JSON files into the Room database.
- 📱 **Glance Home Screen Widget**: A custom home screen widget powered by Jetpack Glance that displays daily vocabulary words, updated automatically every 24 hours via **WorkManager**.
- 💾 **Offline-First Storage**: Local database persistence using **Room Database** pre-populated with default vocabulary on initial launch.

---

## 🏗️ Architecture

The app follows **Clean Architecture** principles and the **MVI (Model-View-Intent) / UDF (Unidirectional Data Flow)** pattern for predictable state management across screens.

```
com.moetaz.words
├── data/
│   ├── local/
│   │   ├── converter/      # Room TypeConverters for custom data types
│   │   ├── dao/            # Room Data Access Objects
│   │   ├── database/       # Room Database configuration & initializers
│   │   ├── dto/            # Data Transfer Objects for JSON parsing
│   │   └── entity/         # Room Database entities
│   └── repository/         # Implementation of domain repositories
├── di/                     # Koin Dependency Injection modules
├── domain/
│   ├── model/              # Domain models (Word, Example)
│   ├── repository/         # Repository interfaces
│   └── usecase/            # Encapsulated business logic use cases
├── presentation/
│   ├── add/                # Add/Edit Word Screen, ViewModels, and Contracts
│   ├── detail/             # Word Detail Screen, ViewModels, and Contracts
│   ├── list/               # Word List Screen, ViewModels, and Contracts
│   ├── navigation/         # Jetpack Navigation 3 setup & Navigator
│   └── settings/           # Settings Screen, Language selector, and JSON Importer
├── ui/theme/               # Material 3 Theme, Typography, and Color palettes
└── widget/                 # Jetpack Glance Home Screen Widget & WorkManager Worker
```

---

## 🛠️ Tech Stack & Libraries

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://developer.android.com/jetpack/compose/designsystems/material3)
- **Navigation**: [Navigation 3](https://developer.android.com/guide/navigation) (`androidx.navigation3`)
- **Dependency Injection**: [Koin](https://insert-koin.io/) (`koin-android`, `koin-androidx-compose`)
- **Local Database**: [Room Database](https://developer.android.com/training/data-storage/room) with KSP
- **Background Tasks**: [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- **Home Screen Widget**: [Jetpack Glance](https://developer.android.com/jetpack/compose/glance)
- **Serialization**: [KotlinX Serialization](https://github.com/Kotlin/kotlinx.serialization)
- **Asynchronous Flow**: Kotlin Coroutines & `StateFlow`
- **Testing**: JUnit 4, MockK, Turbine, and KotlinX Coroutines Test

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Ladybug / Jellyfish or newer
- **JDK**: Version 11+
- **Min SDK**: 29 (Android 10)
- **Target SDK**: 36 (Android 15/16)

### Building the Project

Clone the repository and build the project using Gradle:

```bash
# Clone the repository
git clone https://github.com/your-username/Words.git
cd Words

# Build Debug APK
./gradlew assembleDebug
```

### Running Unit Tests

Run all unit tests across data, domain, and presentation layers:

```bash
./gradlew test
```

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
