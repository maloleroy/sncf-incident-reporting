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

- Node.js (v18) and npm (usually comes with Node.js)
- Expo CLI: `npm install -g expo-cli`
- For development builds: Expo Go app on your device

## Installation

1.  **Clone the repository:**
    ```bash
    git clone ssh://git@paris-digital-lab.com:2012/sncf/s2025p2-mobile-app-incidents.git
    ```

2.  **Install dependencies:**
    ```bash
    npm install
    ```
    *This command installs all the necessary JavaScript dependencies defined in `package.json`.*

3.  **Install Vosk Language Model:**
    - Download the Vosk French model (`vosk-model-small-fr-0.22` or a similar small French model) from the [Vosk models page](https://alphacephei.com/vosk/models). Look for the `tiny-fr` or `small-fr` model for mobile use.
    - The model will be loaded dynamically during runtime through Expo's asset system.

## Running the App

### With Docker

The app has full Docker support:
```bash
cd app
docker build -t sncfincidents:dev --target development .
```

### Running Locally

1.  **Start the Expo development server:**
    ```bash
    npx expo start
    ```
    *Keep this terminal window open. It serves your JavaScript code to the app.*

2.  **Choose your development environment:**
    - [Development build](https://docs.expo.dev/develop/development-builds/introduction/)
    - [Expo Go](https://expo.dev/go), a limited sandbox for trying out app development with Expo

3.  **First Launch:** The app should build and launch on your selected device. If prompted, grant microphone permissions for the speech-to-text feature.

## Project Structure

```
app/
├── assets/                 # Static assets (images, fonts, etc.)
├── components/             # Reusable React components
├── constants/             # App-wide constants and configurations
├── hooks/                 # Custom React hooks
├── lib/                   # Third-party libraries and utilities
├── services/             # Business logic and services
├── types/                # TypeScript type definitions
├── scripts/              # Build and utility scripts
├── src/                  # Main source code directory
├── .expo/                # Expo configuration files
├── app.json             # Expo app configuration
├── app.tsx              # Main app component
├── package.json         # Project dependencies
└── tsconfig.json        # TypeScript configuration
```

## Key Technologies

- React Native
- TypeScript
- Vosk (Speech-to-text)
- AsyncStorage (Local storage)
- React Navigation
- NetInfo (Network status)
- Expo

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
