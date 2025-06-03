# SNCF Incident Reports Kotlin App

## Getting started

You need to have an Android SDK installed. The easiest way is probably to install Android Studio.

## Configuration

> You first have to configure the backend (notably, the part where the backend's certificate is created) before configuring the app!

Edit [local.properties](local.properties) (create it if it does not exist) and specify the various values. For example values, please read [local.properties.example](local.properties.example).

## Running the app

You first have to sync the Gradle project.

- To run the app in Android Studio, just click on "run" on the `MainActivity` or `app` configuration
- To perform a project lint, run `./gradlew lint`
- To run tests, run `./gradlew test`
- To build a debug APK, run `./gradlew assembleDebug`