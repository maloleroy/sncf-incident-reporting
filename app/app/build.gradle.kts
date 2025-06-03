import java.util.Properties // <-- Import ajouté
import java.io.FileInputStream // <-- Import ajouté

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
}

fun getEnvVar(propertyKey: String, default: String = ""): String {
    val properties = Properties() // Use the imported class directly
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(FileInputStream(localPropertiesFile)) // Use the imported class directly
        return properties.getProperty(propertyKey, "")
    }
    return default
}

android {
    namespace = "com.sncf.reports"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sncf.reports"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        val backendUrl = getEnvVar("BACKEND_URL", "https://10.0.2.2:8000/")
        buildConfigField("String", "BACKEND_URL", "\"$backendUrl\"")
        val backendPassword = getEnvVar("BACKEND_PASSWORD", "admin")
        buildConfigField("String", "BACKEND_PASSWORD", "\"$backendPassword\"")
        val backendAiRoute = getEnvVar("BACKEND_AI_ROUTE", "/openai")
        buildConfigField("String", "BACKEND_AI_ROUTE", "\"$backendAiRoute\"")
        val backendCompletionRoute = getEnvVar("BACKEND_COMPLETION_ROUTE", "/incident-options")
        buildConfigField("String", "BACKEND_COMPLETION_ROUTE", "\"$backendCompletionRoute\"")
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
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}