plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" // Voeg deze regel toe
}

android {
    namespace = "com.borsosistvan.hitjamtv"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.borsosistvan.hitjamtv"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    // VERWIJDER het complete oude 'composeOptions { kotlinCompilerExtensionVersion = ... }' blok!
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    //implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    
    // KÖTELEZŐ: Jetpack Compose alapvető UI és Material 3 könyvtárak
    //implementation("androidx.compose.ui:ui:1.6.1")
    //implementation("androidx.compose.material3:material3:1.2.0") // Ezt hiányolta a compiler!
    //implementation("androidx.compose.ui:ui-tooling-preview:1.6.1")
    
    // Officiële stabiele Jetpack Compose TV componenten van Google
    //implementation("androidx.tv:tv-foundation:1.0.0")
    //implementation("androidx.tv:tv-material:1.0.0")

    // ExoPlayer voor .m3u8 streaming
    implementation("androidx.media3:media3-exoplayer:1.4.0")
    implementation("androidx.media3:media3-exoplayer-hls:1.4.0")
    implementation("androidx.media3:media3-ui:1.4.0")
}

