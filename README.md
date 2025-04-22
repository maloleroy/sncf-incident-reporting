# SNCF Incidents Mobile App

A React Native mobile application for SNCF's on-board personnel to report incidents.

## Features

- View and manage incidents
- Track trips
- Speech-to-text incident reporting using Vosk
- Offline-first functionality with automatic sync when online
- User account management
- Feedback system

## Prerequisites

- Node.js (v14 or later) and npm (usually comes with Node.js)
- Android Studio (latest stable version recommended)
- Android SDK (specifically API level 31 or higher, installable via Android Studio's SDK Manager)
- Java Development Kit (JDK) (version 11 or later, usually included with Android Studio)
- React Native CLI: `npm install -g react-native-cli`
- An Android Virtual Device (AVD) set up in Android Studio, or a physical Android device with USB debugging enabled.

## Installation

1.  **Clone the repository:**
    ```bash
    git clone <repository-url> # Replace <repository-url> with the actual URL
    cd app
    ```

2.  **Install dependencies:**
    ```bash
    npm install
    ```
    *This command installs all the necessary JavaScript dependencies defined in `package.json`.*

3.  **Install Vosk Language Model:**
    - Download the Vosk French model (`vosk-model-small-fr-0.22` or a similar small French model) from the [Vosk models page](https://alphacephei.com/vosk/models). Look for the `tiny-fr` or `small-fr` model for mobile use.
    - Unzip the downloaded model archive.
    - Create the following directory structure inside the `android` folder if it doesn't exist: `android/app/src/main/assets/`.
    - Rename the unzipped model folder to `model` (if it's not already named that).
    - Move the `model` folder (containing the Vosk model files) into `android/app/src/main/assets/`. The final path should look like `app/android/app/src/main/assets/model/`.

4.  **Set up Android Development Environment:**
    - Launch Android Studio.
    - Ensure you have an Android SDK Platform installed (e.g., Android 12 - API 31). You can check this via `Tools > SDK Manager`.
    - If using an emulator: Create an AVD via `Tools > AVD Manager`. Choose a device definition and an appropriate system image (API 31 or higher).
    - If using a physical device: Enable Developer Options and USB Debugging on your device, then connect it to your computer via USB. Make sure your computer recognizes the device (`adb devices` command in the terminal should list your device).
    - **Permissions:** Ensure the app will have microphone permissions. This is typically handled by the Android OS prompting the user on the first run, but ensure the `AndroidManifest.xml` includes the `RECORD_AUDIO` permission (it should be there by default).

## Running the App Locally

1.  **Ensure Emulator/Device is Running:** Start your AVD from Android Studio or ensure your physical device is connected and recognized (`adb devices`).

2.  **Start the Metro Bundler:** Metro is the JavaScript bundler for React Native. Open a terminal in the project root (`app/`) and run:
    ```bash
    npm start
    ```
    *Keep this terminal window open. It serves your JavaScript code to the app.*

3.  **Build and Run the Android App:** Open *another* terminal window in the project root (`app/`) and run:
    ```bash
    npm run android
    ```
    *This command will compile the Android native code (including the Vosk module), install the app on your emulator/device, and launch it. The app will then connect to the Metro bundler you started in the previous step.*

4.  **First Launch:** The app should build and launch on your selected emulator or device. It might take a few minutes the first time. If prompted, grant microphone permissions for the speech-to-text feature.

## Troubleshooting

- **Build Fails:**
    - Ensure all prerequisites are correctly installed and configured.
    - Clean the Android build: `cd android && ./gradlew clean && cd ..` then try `npm run android` again.
    - Check for errors in the terminal output from `npm run android`.
- **App Crashes on Start:**
    - Verify the Vosk model was placed correctly in `android/app/src/main/assets/model/`.
    - Check the device logs using Android Studio's Logcat or `adb logcat`.
- **Metro Bundler Issues:**
    - Try resetting the cache: `npm start -- --reset-cache`.
    - Ensure no other process is using port 8081.

## Project Structure

```
app/
├── android/                 # Android native code
├── ios/                     # iOS native code
├── src/
│   ├── screens/            # App screens
│   ├── services/           # Business logic and services
│   ├── types/              # TypeScript type definitions
│   └── utils/              # Utility functions
├── App.tsx                 # Main app component
└── package.json            # Project dependencies
```

## Key Technologies

- React Native
- TypeScript
- Vosk (Speech-to-text)
- AsyncStorage (Local storage)
- React Navigation
- NetInfo (Network status)

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.