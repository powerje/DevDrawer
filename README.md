# DevDrawerWidget

A home screen widget for Android developers to quickly launch in-development applications.

Add regex patterns like `com.example.*` and the widget displays all matching installed apps in a scrollable list. Tap to launch, or use the menu for quick access to uninstall, app info, or copy package name.

## Features

- Filter apps by package name using regex patterns
- Scrollable widget with app icons, names, and package names
- Quick actions: Launch, Uninstall, App Info, Copy Package Name
- Auto-updates when apps are installed, removed, or replaced
- Compose-based configuration UI with inline regex validation

## Requirements

- Android 15 (API 36) or higher
- JDK 17

## Building

```bash
./gradlew assembleDebug
```

Install on a connected device:

```bash
./gradlew installDebug
```

## Testing

```bash
./gradlew test
```

## Architecture

```
com.powerje.devdrawer/
├── config/      # Configuration activity (Compose UI)
├── data/        # Pattern storage (DataStore + Repository)
├── matching/    # App matching logic
├── receiver/    # Package change broadcast receiver
└── widget/      # Glance widget and action menu
```

**Tech stack:** Jetpack Glance, Compose, DataStore, Kotlin Flow, kotlinx.serialization
