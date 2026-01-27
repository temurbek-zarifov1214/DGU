# DGU
This is for only learning purpose.

## Prerequisites
- Android Studio (2023.2.1+ recommended).
- Android SDK with API 34 installed (from **SDK Manager**).

## Run locally
1. Clone the repo.
2. Open the project folder in **Android Studio**.
3. Let **Gradle sync** finish.
4. Click **Run** (or `./gradlew assembleDebug`).

## Common build error
If you see:
```
Could not find com.github.barteksc:android-pdf-viewer:2.8.2
```
Make sure your Gradle repositories include **JitPack** (required for this dependency). The project is already configured for this in `settings.gradle` via:
```
maven { url "https://jitpack.io" }
```

## Android Studio installer
Download link: [android-studio-2023.2.1.25-windows.exe](https://redirector.gvt1.com/edgedl/android/studio/install/2023.2.1.25/android-studio-2023.2.1.25-windows.exe)
