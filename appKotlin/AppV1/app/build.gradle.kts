import java.util.Properties // <-- Import ajouté
import java.io.FileInputStream // <-- Import ajouté

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

fun getBackendUrl(propertyKey: String): String {
    val properties = Properties() // Use the imported class directly
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(FileInputStream(localPropertiesFile)) // Use the imported class directly
        return properties.getProperty(propertyKey, "")
    }
    return ""
}

fun getBackendPassword(propertyKey: String): String {
    val properties = Properties() // Use the imported class directly
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(FileInputStream(localPropertiesFile)) // Use the imported class directly
        return properties.getProperty(propertyKey, "")
    }
    return ""
}

android {
    namespace = "com.example.appv1"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.appv1"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        val backendUrl = getBackendUrl("BACKEND_URL")
        buildConfigField("String", "BACKEND_URL", "\"$backendUrl\"")
        val backendPassword = getBackendPassword("BACKEND_PASSWORD")
        buildConfigField("String", "BACKEND_PASSWORD", "\"$backendPassword\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true // Ensure this is present
    }
    androidResources { noCompress += "task" }
}

dependencies {
    implementation(libs.gson)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material.icons.extended) // <= ajoute ceci
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.retrofit) // Ou version plus récente
    implementation(libs.retrofit.converter.gson) // Ou version plus récente
    implementation(libs.okhttp) // Ou version plus récente
    // Ajoutez la dépendance MediaPipe GenAI Tasks
    implementation(libs.tasks.genai) // Vérifiez la dernière version stable
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}