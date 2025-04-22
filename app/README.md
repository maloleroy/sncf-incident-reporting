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
- Cursor IDE (latest version recommended)
- Android SDK Command Line Tools (download from [Android Studio](https://developer.android.com/studio))
- Java Development Kit (JDK) (version 11 or later)
- React Native CLI: `npm install -g react-native-cli`
- Android Debug Bridge (adb) installed and in your PATH
- An Android Virtual Device (AVD) or a physical Android device with USB debugging enabled

## Installation

1.  **Clone the repository:**
    ```bash
    git clone <repository-url> # Replace <repository-url> with the actual URL
    cd SNCFIncidents
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
    - Move the `model` folder (containing the Vosk model files) into `android/app/src/main/assets/`. The final path should look like `SNCFIncidents/android/app/src/main/assets/model/`.

4.  **Set up Android Development Environment:**
    - Install Android SDK Command Line Tools
    - Set up environment variables:
      ```bash
      export ANDROID_HOME=$HOME/Android/Sdk
      export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
      ```
    - Install required Android SDK components:
      ```bash
      sdkmanager "platform-tools" "platforms;android-33" "build-tools;33.0.0"
      ```
    - If using an emulator:
      ```bash
      sdkmanager "system-images;android-33;google_apis;x86_64"
      avdmanager create avd -n test_device -k "system-images;android-33;google_apis;x86_64"
      ```
    - If using a physical device:
      - Enable Developer Options and USB Debugging on your device
      - Connect device via USB
      - Verify connection: `adb devices`
    - **Permissions:** Ensure the app will have microphone permissions. This is typically handled by the Android OS prompting the user on the first run, but ensure the `AndroidManifest.xml` includes the `RECORD_AUDIO` permission (it should be there by default).

## Running the App Locally

1.  **Start the Metro Bundler:** Open Cursor and run in the integrated terminal:
    ```bash
    npm start
    ```
    *Keep this terminal open. It serves your JavaScript code to the app.*

2.  **Start Emulator (if using):**
    ```bash
    emulator -avd test_device
    ```
    *Wait for the emulator to fully boot.*

3.  **Build and Run the Android App:** In another Cursor terminal:
    ```bash
    npm run android
    ```
    *This command will compile the Android native code (including the Vosk module), install the app on your emulator/device, and launch it.*

4.  **First Launch:** The app should build and launch on your selected emulator or device. It might take a few minutes the first time. If prompted, grant microphone permissions for the speech-to-text feature.

## Troubleshooting

- **Build Fails:**
    - Ensure all prerequisites are correctly installed and configured.
    - Clean the Android build: `cd android && ./gradlew clean && cd ..` then try `npm run android` again.
    - Check for errors in the terminal output from `npm run android`.
- **App Crashes on Start:**
    - Verify the Vosk model was placed correctly in `android/app/src/main/assets/model/`.
    - Check the device logs: `adb logcat | grep ReactNative`
- **Metro Bundler Issues:**
    - Try resetting the cache: `npm start -- --reset-cache`.
    - Ensure no other process is using port 8081.
- **Device Connection Issues:**
    - Check device connection: `adb devices`
    - Restart adb server: `adb kill-server && adb start-server`
    - For emulator issues: `emulator -list-avds` to list available devices

## Project Structure

```
SNCFIncidents/
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
- Cursor IDE

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
